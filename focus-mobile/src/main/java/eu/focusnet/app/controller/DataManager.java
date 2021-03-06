/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import eu.focusnet.app.model.AppContentInstance;
import eu.focusnet.app.model.gson.AppContentTemplate;
import eu.focusnet.app.model.gson.FocusObject;
import eu.focusnet.app.model.gson.FocusSample;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * The DataManager is responsible for granting access to resources. They may be online or in the
 * local SQlite repository. Other classes may not have to interact with data handling objects such
 * as the {@link DatabaseAdapter} or the {@link NetworkManager}, and only use this present object.
 * It also contains the required logic to build the application content.
 */
public class DataManager implements ApplicationStatusObserver
{
	/**
	 * When performing operations on resources, it may happens that the result is not binary
	 * (sucess or failure). If there is no network available, the operation is queued by storing
	 * the altered version of the resource into the local database storage, and will therefore be
	 * pushed later to the network.
	 */
	public enum ResourceOperationStatus
	{
		/**
		 * Resource has been altered directly and successfully.
		 *
		 * @see ResourceOperationStatus
		 */
		SUCCESS,
		/**
		 * Resource has been altered locally and modification will be pushed on the network later
		 * by the periodic tasks of {@link CronService}.
		 *
		 * @see ResourceOperationStatus
		 */
		PENDING,
		/**
		 * A permanent error occured.
		 *
		 * @see ResourceOperationStatus
		 */
		ERROR
	}

	/**
	 * The object responsible for providing networking facilities
	 */
	final private NetworkManager net;

	/**
	 * Tells whether the application is ready or not.
	 */
	private boolean applicationReady;

	/**
	 * Pool for building application
	 */
	private ExecutorService appBuilderPool;

	/**
	 * Pool for retrieving data
	 */
	private ExecutorService dataRetrievalPool;

	/**
	 * An in-memory cache of {@link FocusObject}s that helps to speed up the
	 * instantiation of the application content, by avoiding to retrieve many time the same
	 * resources from the network and/or the local database. It consists of {@code FutureTask}s
	 * of {@link DataRetrievalTask}s.
	 * <p/>
	 * This cache is only used when initializing the application content instance, and is
	 * then freed, as resources are not required after that anymore.
	 * <p/>
	 * This cache mechanism is also used to avoid multiple concurrent downloads of the same
	 * resource, so it is NOT safe to free it when needing memory.
	 */
	private HashMap<String, FutureTask<FocusObject>> cache;

	/**
	 * The object responsible for providing local database storage faiclities (SQLite database)
	 */
	private DatabaseAdapter databaseAdapter;

	/**
	 * Constructor. Initialize all default values and facilities, but does not perform any
	 * operation.
	 */
	public DataManager()
	{
		this.applicationReady = false;

		// setup database
		this.databaseAdapter = new DatabaseAdapter();

		// queue for building app content in parallel
		this.appBuilderPool = DataManager.BuilderPoolFactory();

		// queue for retrieving data
		this.dataRetrievalPool = DataManager.DataRetrievalPoolFactory();

		// setup network
		this.net = new NetworkManager();

		// setup cache
		this.cache = new HashMap<>();
	}

	/**
	 * Factory for creating the priority-based pool used to retrieve data with reasonable settings.
	 *
	 * @return A new pool
	 */
	private static ExecutorService BuilderPoolFactory()
	{
		return new ThreadPoolExecutor(
				Constant.AppConfig.MAX_CONCURRENT_BUILD_TASKS,
				Constant.AppConfig.MAX_CONCURRENT_BUILD_TASKS,
				0L,
				TimeUnit.MILLISECONDS,
				new PriorityBlockingQueue<>(
						Constant.AppConfig.MAX_CONCURRENT_BUILD_TASKS,
						new PriorityTaskComparator()
				)
		);
	}

	/**
	 * Factory for creating the fifo pool used to retrieve data with reasonable settings.
	 * <p/>
	 * The prioritized {@link #BuilderPoolFactory()} will define the order in which the
	 * data are requested in this pool.
	 *
	 * @return A new pool
	 */
	private static ExecutorService DataRetrievalPoolFactory()
	{
		return new ThreadPoolExecutor(
				Constant.Networking.MAX_CONCURRENT_DOWNLOADS,
				Constant.Networking.MAX_CONCURRENT_DOWNLOADS,
				0L,
				TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(128)
		);
	}

	/**
	 * Get a FocusSample
	 *
	 * @param url The URL identifyiing the sample to retrieve
	 * @return A FocusSample
	 * @throws FocusMissingResourceException If the resource could not be found (in the local store
	 *                                       and on the network)
	 */
	public FocusSample getSample(String url) throws FocusMissingResourceException
	{
		FocusSample fs = (FocusSample) (this.get(url, FocusSample.class));
		if (fs == null) {
			throw new FocusMissingResourceException("Cannot retrieve requested FocusSample.");
		}

		return fs;
	}

	/**
	 * Get a sample history.
	 * <p/>
	 * Output:
	 * <pre>
	 * {
	 * 		url: ...,
	 * 		...,
	 * 		data: {
	 * 			timestamp: [epoch1, ..., epochN],
	 * 			prop1: [v1, ..., vN],
	 * 			...
	 * 			propN: [w1, ..., wN]
	 *      }
	 * }
	 * </pre>
	 *
	 * @param url    The URL of the resource for which we want an history
	 * @param params The parameters to be passed to the history retrieving service
	 * @return A {@link FocusSample} containing the history of intereset
	 */
	public FocusSample getHistory(String url, String params) throws FocusMissingResourceException
	{
		url = url + "?" + params;
		return this.getSample(url);
	}


	/**
	 * Get the data identified by the provided url.
	 * <p/>
	 * This method first tries to acquire the resource from the local cache or database and then
	 * from the network if not available. It also stores the object in the local database such that
	 * it can later be quickly accessed.
	 *
	 * @param url         The URL of the resource to retrieve
	 * @param targetClass The retrieved object will be converted to a Java object matching this class
	 * @return A FocusObject, or mor specifically one of its inherited classes matching {@code targetclass},
	 * or {@code null} if the object could not be retrieved, e.g. because of network error or any
	 * other exception raised in {@link DataRetrievalTask}, or if the task was interrupted.
	 */
	public FocusObject get(String url, Class targetClass)
	{
		FutureTask<FocusObject> future = getFutureForGet(url, targetClass);
		try {
			return future.get();
		}
		catch (InterruptedException | ExecutionException e) {
			return null;
		}
	}

	/**
	 * Acquire the {@code FutureTask} matching the resource to get. If the task is not created, yet,
	 * do create it and launch data retrieval on the data retrieval pool. If it already exists,
	 * simply return a pointer to the task's future such that the caller can wait for the resource
	 * to become available.
	 * <p/>
	 * This way of organizing code allows us to mark this method as synchronized and hence avoid
	 * having multiple parallel downloads of the same resource.
	 *
	 * @param url         The URL of the resource to retrieve
	 * @param targetClass The retrieved object will be converted to a Java object matching this class.
	 * @return The future of the task
	 */
	synchronized private FutureTask<FocusObject> getFutureForGet(String url, Class targetClass)
	{
		FutureTask<FocusObject> future;

		if (this.cache.get(url) == null) {
			DataRetrievalTask task = new DataRetrievalTask(url, targetClass);
			future = new FutureTask<>(task);
			this.cache.put(url, future);
			this.dataRetrievalPool.execute(future);
		}
		else {
			future = this.cache.get(url);
		}
		return future;
	}


	/**
	 * Create a new FOCUS data object and send it to the its target location on the network.
	 * <p/>
	 * The resource is also saved in the local database for later quicker use. If network is not
	 * available, it is only stored in this database and will be pushed to the network later
	 * during periodic operations performed by {@link CronService}.
	 *
	 * @param data The FocusObject representing the data to create
	 * @return {@code ResourceOperationsStatus.SUCESS} on success,
	 * {@code ResourceOperationsStatus.FAILURE} on failure, or
	 * {@code ResourceOperationsStatus.PENDING} on success but without
	 * network connectivity.
	 */
	public ResourceOperationStatus create(FocusObject data)
	{
		String url = data.getUrl();

		// store in local database, with the "toPost" flag
		Sample sample = Sample.cloneFromFocusObject(data);

		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			dao.create(sample);
		}
		finally {
			this.databaseAdapter.close();
		}

		// push on the network
		if (!NetworkManager.isNetworkAvailable()) {
			return ResourceOperationStatus.PENDING;
		}

		boolean netSuccess;
		try {
			HttpResponse r = this.net.post(url, data);
			netSuccess = r.isSuccessful();
		}
		catch (IOException ex) {
			netSuccess = false;
		}
		if (netSuccess) { // if POST on network, also remove POST flag in local db copy
			sample.setToCreate(false);
			try {
				SampleDao dao = this.databaseAdapter.getSampleDao();
				dao.update(sample);
				return ResourceOperationStatus.SUCCESS;
			}
			finally {
				this.databaseAdapter.close();
			}
		}
		return ResourceOperationStatus.ERROR;
	}

	/**
	 * Upate an existing FOCUS data object and send it to the its target location on the network.
	 * <p/>
	 * The resource is also saved in the local database for later quicker use. If network is not
	 * available, it is only stored in this database and will be pushed to the network later
	 * during periodic operations performed by {@link CronService}.
	 *
	 * @param data The FocusObject representing the data to update.
	 * @return {@code ResourceOperationsStatus.SUCESS} on success,
	 * {@code ResourceOperationsStatus.FAILURE} on failure, or
	 * {@code ResourceOperationsStatus.PENDING} on success but without
	 * network connectivity.
	 */
	public ResourceOperationStatus update(FocusObject data)
	{
		String url = data.getUrl();

		// update the object to a new version
		data.updateToNewVersion();

		// store in local database, with the "toPut" flag
		Sample sample = Sample.cloneFromFocusObject(data);
		sample.setToUpdate(true);
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			dao.update(sample);
		}
		finally {
			this.databaseAdapter.close();
		}

		// network PUT
		if (!NetworkManager.isNetworkAvailable()) {
			return ResourceOperationStatus.PENDING;
		}

		boolean netSuccess;
		try {
			netSuccess = this.net.put(url, data).isSuccessful();
		}
		catch (IOException ex) {
			netSuccess = false;
		}
		if (netSuccess) { // if PUT on network, also remove PUT flag in local db copy
			sample.setToUpdate(false);
			try {
				SampleDao dao = this.databaseAdapter.getSampleDao();
				dao.update(sample);
				return ResourceOperationStatus.SUCCESS;
			}
			finally {
				this.databaseAdapter.close();
			}
		}
		return ResourceOperationStatus.ERROR;
	}

	/**
	 * Delete an existing FOCUS data object, both locally and on its target location on the network.
	 * <p/>
	 * If network is not available, the deletion information is only stored in the local database
	 * and deletion will be performed on the network later during periodic operations performed by
	 * {@link CronService}.
	 *
	 * @param url The URL of the resource to delete.
	 * @return {@code ResourceOperationsStatus.SUCESS} on success,
	 * {@code ResourceOperationsStatus.FAILURE} on failure, or
	 * {@code ResourceOperationsStatus.PENDING} on success but without
	 * network connectivity.
	 */
	public ResourceOperationStatus delete(String url)
	{
		// remove from RAM cache
		this.deleteFromCache(url);

		// mark data for deletion in local database
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();

			// mark for deletion in local store
			dao.markForDeletion(url);

			// remove from network server
			if (!NetworkManager.isNetworkAvailable()) {
				return ResourceOperationStatus.PENDING;
			}

			boolean netSuccess;
			try {
				netSuccess = this.net.delete(url).isSuccessful();
			}
			catch (IOException ex) {
				netSuccess = false;
			}

			if (netSuccess) { // if remove on network, also remove copy in local db
				dao.delete(url);
				return ResourceOperationStatus.SUCCESS;
			}
		}
		finally {
			this.databaseAdapter.close();
		}
		return ResourceOperationStatus.ERROR;
	}

	/**
	 * Delete the specified url from the local RAM cache.
	 *
	 * @param url The resource to remove
	 */
	synchronized private void deleteFromCache(String url)
	{
		this.cache.remove(url);
	}

	/**
	 * Clean the samples table from useless entries.
	 */
	public void cleanDataStore()
	{
		if (!this.applicationReady) {
			return;
		}
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			dao.cleanTable();
		}
		finally {
			this.databaseAdapter.close();
		}
	}

	/**
	 * Retrieve the size of the current local database.
	 *
	 * @return The size in bytes.
	 */
	public long getDatabaseSize()
	{
		return this.databaseAdapter.getDatabaseSize();
	}

	/**
	 * logout. Inherited from {@link ApplicationStatusObserver} interface.
	 */
	public void handleLogout()
	{
		this.reset();

		// delete the whole database content
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			dao.deleteAll();
		}
		finally {
			this.databaseAdapter.close();
		}

		this.applicationReady = false;
	}

	/**
	 * Reset what can be reset. Used when logging out.
	 */
	private void reset()
	{
		this.cache = new HashMap<>();
	}

	/**
	 * Inherited from {@link ApplicationStatusObserver} interface.
	 *
	 * @param success Inherited
	 */
	public void onChangeStatus(boolean success)
	{
		if (!this.applicationReady && success) {
			this.applicationReady = true;
			this.databaseAdapter.makeDataPersistent();
		}
		// otherwise, keep current status
	}

	/**
	 * Acquire the last data set id that was used in the local database
	 * and assign it to our {@link DatabaseAdapter}
	 */
	public void useExistingDataSet()
	{
		this.databaseAdapter.useExistingDataSet();
	}


	/**
	 * Push local modifications to the network
	 *
	 * @throws FocusMissingResourceException If at least one error has been encountered. note that
	 *                                       this method is resilient: if an error occurs, it will continue to process the queue of
	 *                                       objects to push. This way, if the problem is temporary due to network conditions, it will
	 *                                       not have to redo everything on next run. The caller should however take this exception
	 *                                       in consideration for its logic, e.g. do not overwrite objects that are have not yet been
	 *                                       transmitted to the final network endpoint.
	 */
	public void pushPendingLocalModifications() throws FocusMissingResourceException
	{
		// a bitmask for our return value
		int reportFailure = Constant.Networking.NETWORK_REQUEST_STATUS_SUCCESS;
		int numberOfErrors = 0;

		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();

			// POST and PUT are almost the same
			HashSet<Sample> toPushSamples;
			for (String typeOfOperation : new String[]
					{
							Constant.Database.TO_CREATE,
							Constant.Database.TO_UPDATE,
							Constant.Database.TO_DELETE
					}) {
				toPushSamples = dao.getAllMarkedFocusSamples(typeOfOperation);
				for (Sample sample : toPushSamples) {
					reportFailure |= this.pushLocalModification(sample, FocusSample.class, typeOfOperation, dao);
					++numberOfErrors;
				}
			}
		}
		finally {
			this.databaseAdapter.close();
		}


		// Error reporting
		if (reportFailure != Constant.Networking.NETWORK_REQUEST_STATUS_SUCCESS) {
			throw new FocusMissingResourceException("Cannot retrieve resource - code 0x" + reportFailure + "[" + numberOfErrors + " errors]");
		}

	}

	/**
	 * push modification for a single sample of speciifed class type
	 *
	 * @param sample          the database {@link Sample} to push
	 * @param targetClass     The type of object to push
	 * @param typeOfOperation The type of operation to perform on the object, which may be
	 *                        {@link Constant.Database#TO_CREATE},
	 *                        {@link Constant.Database#TO_UPDATE} or
	 *                        {@link Constant.Database#TO_DELETE}
	 * @param dao             The Data Access Object to use for performing the operation
	 * @return A network error status: {@link Constant.Networking#NETWORK_REQUEST_STATUS_SUCCESS},
	 * {@link Constant.Networking#NETWORK_REQUEST_STATUS_NETWORK_FAILURE} or
	 * {@link Constant.Networking#NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE}
	 */
	private int pushLocalModification(Sample sample, Class targetClass, String typeOfOperation, SampleDao dao)
	{
		int reportFailure = Constant.Networking.NETWORK_REQUEST_STATUS_SUCCESS;

		String networkOperation;
		switch (typeOfOperation) {
			case Constant.Database.TO_CREATE:
				networkOperation = "POST";
				break;
			case Constant.Database.TO_UPDATE:
				networkOperation = "PUT";
				break;
			case Constant.Database.TO_DELETE:
				networkOperation = "DELETE";
				break;
			default:
				throw new FocusInternalErrorException("Invalid operation requested.");
		}


		FocusObject fo = FocusObject.factory(sample.getData(), targetClass);
		if (fo == null) {
			throw new FocusInternalErrorException("Could get the data in the database, but could not instantiate it (POST).");
		}
		try {
			String url = sample.getUrl();
			boolean netResult = this.net.pushModification(networkOperation, url, fo).isSuccessful();
			if (netResult) {
				if (typeOfOperation.equals(Constant.Database.TO_DELETE)) {
					dao.delete(url);
				}
				else {
					sample.setEditionField(typeOfOperation, false);
					dao.update(sample);
				}
			}
			else {
				reportFailure |= Constant.Networking.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
			}
		}
		catch (IOException ex) {
			reportFailure |= Constant.Networking.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
		}

		return reportFailure;
	}

	/**
	 * Same as {@link #pushLocalModification(Sample, Class, String, SampleDao)} but public,
	 * and hence we create the DAO ourselves and fetch the Sample for local store for the caller.
	 * Also, we only allow UPDATEs of data.
	 *
	 * @param url         the resource to push
	 * @param targetClass The type of object to push
	 * @return A network error status: {@link Constant.Networking#NETWORK_REQUEST_STATUS_SUCCESS},
	 * {@link Constant.Networking#NETWORK_REQUEST_STATUS_NETWORK_FAILURE} or
	 * {@link Constant.Networking#NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE}
	 */
	public int pushLocalModification(String url, Class targetClass)
	{
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();

			// get the sample corresponding to the url
			Sample sample = dao.get(url);
			// we do the update only an update is scheduled
			if (sample.isToPut()) {
				return this.pushLocalModification(sample, targetClass, Constant.Database.TO_UPDATE, dao);
			}
			else {
				// nothing to do.
				return Constant.Networking.NETWORK_REQUEST_STATUS_SUCCESS;
			}
		}
		finally {
			this.databaseAdapter.close();
		}
	}


	/**
	 * Wait for the completion of the pool that retrieves data for us
	 *
	 * @throws InterruptedException
	 */
	public void waitForCompletion() throws InterruptedException
	{
		this.appBuilderPool.shutdown();
		// Wait for everything to finish.
		//noinspection StatementWithEmptyBody
		while (!this.appBuilderPool.awaitTermination(5, TimeUnit.SECONDS)) {
			// wait silently
		}
		// create a new pool in case we need it in the future (e.g. resync)
		this.appBuilderPool = DataManager.BuilderPoolFactory();
	}

	/**
	 * Push a {@link PriorityTask} to the execution pool
	 *
	 * @param task The {@link PriorityTask} to execute.
	 */
	synchronized public void executeOnAppBuilderPool(PriorityTask task)
	{
		this.appBuilderPool.execute(task);
	}


	/**
	 * Task for retrieving data, either in our local database or on the network.
	 */
	private class DataRetrievalTask implements Callable
	{

		/**
		 * The URL of the resource to retrieve
		 */
		private String url;

		/**
		 * The target class of the object to retrieve
		 */
		private Class targetClass;


		/**
		 * Constructor
		 *
		 * @param url         Input value for instance variable
		 * @param targetClass Input value for instance variable
		 */
		private DataRetrievalTask(String url, Class targetClass)
		{
			this.url = url;
			this.targetClass = targetClass;
		}


		/**
		 * Perform data retrieval
		 *
		 * @return A FocusObject mathing {@link #targetClass}
		 * @throws IOException If a network error occurs.
		 */
		@Override
		public FocusObject call() throws IOException
		{
			FocusObject result = null;

			// try to get it from the local SQL db
			try {
				SampleDao dao = databaseAdapter.getSampleDao();
				Sample sample = dao.get(url);
				if (sample != null) {
					result = FocusObject.factory(sample.getData(), targetClass);
				}
			}
			finally {
				databaseAdapter.close();
			}

			if (result == null) {

				// try to get it from the network
				if (NetworkManager.isNetworkAvailable()) {
					HttpResponse response = net.get(url);

					if (response.isSuccessful()) {
						String json = response.getData();
						result = FocusObject.factory(json, targetClass);

						// Convert into a sample and save it into the database
						Sample sample = Sample.cloneFromFocusObject(result);

						try {
							SampleDao dao = databaseAdapter.getSampleDao();
							dao.create(sample);
						}
						finally {
							databaseAdapter.close();
						}
					}
					else {
						return null;
					}
				}
			}
			return result;
		}
	}
}

