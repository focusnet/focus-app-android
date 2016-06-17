/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.internal.AbstractInstance;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.json.AppContentTemplate;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.service.datastore.DatabaseAdapter;
import eu.focusnet.app.service.datastore.Sample;
import eu.focusnet.app.service.datastore.SampleDao;
import eu.focusnet.app.service.network.HttpResponse;
import eu.focusnet.app.service.network.NetworkManager;
import eu.focusnet.app.util.ApplicationHelper;

/**
 * The DataManager is responsible for building the application content and data management.
 * <p/>
 * Its specific tasks are:
 * <ul>
 * <li>Serve as a single contact entity for data CRUD operations.</li>
 * <li>Initialize and construct the application content</li>
 * </ul>
 * <p/>
 * The DataManager is hence the Model of the application. It does not provide any UI-related
 * feature, and does not run tasks in the background. This is left to the View aspect of
 * the application.
 * <p/>
 * TODO move user/login-related operations to a standalone {@code AccessControlManager}
 * <p/>
 * FIXME we instantiate all application contentn parts (projects, pages, widgets) at start-time. Should we rather do it on-demand?
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

	boolean applicationReady;
	/**
	 * An in-memory cache of {@link FocusObject}s that helps to speed up the
	 * instanciation of the application content, by avoiding to retrieve many time the same
	 * resources from the network and/or the local database.
	 * <p/>
	 * This cache is only used when initializing the application content instance, and is
	 * then freed, as resources are not required after that anymore.
	 */
	private HashMap<String, FocusObject> cache;
	/**
	 * The object responsible for providing networking facilities
	 */
	private NetworkManager net;
	/**
	 * The object responsible for providing local database storage faiclities (SQLite database)
	 */
	private DatabaseAdapter databaseAdapter;
	/**
	 * This List keeps track of the currently active instances
	 * <p/>
	 * We use this such that the instances are not garbage collected
	 * <p/>
	 * FIXME check that last sentence, I have a doubt now. Not really useful?
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	private ArrayList<AbstractInstance> activeInstances;
	private AppContentTemplate appContentTemplate;

	/**
	 * Constructor. Initialize all default values and facilities, but does not perform any
	 * operation.
	 */
	public DataManager()
	{

		this.applicationReady = false;
		this.activeInstances = new ArrayList<>();

		// setup network
		this.net = new NetworkManager();

		// setup cache
		this.cache = new HashMap<>();

		// setup database
		this.databaseAdapter = new DatabaseAdapter();

	}


	/**
	 * The {@link AppContentTemplate} is one of the 3 mandatory objects for the application to run.
	 * This method retrieves this object based on the URI that has been obtained during the
	 * login procedure. If the object cannot be found, this is considered as a permanent failure.
	 * <p/>
	 * The application cannot live without this object and will therefore crash if it does not
	 * succeed in retrieving this object.
	 *
	 * @return A {@link AppContentTemplate} object
	 * @throws FocusMissingResourceException If the object could not be found
	 */
	public AppContentTemplate getAppContentTemplate(String templateUri) throws FocusMissingResourceException
	{
		if (this.appContentTemplate != null) {
			return this.appContentTemplate;
		}
		try {
			this.appContentTemplate = (AppContentTemplate) (this.get(templateUri, AppContentTemplate.class));
		}
		catch (IOException ex) {
			// we cannot survive without an appcontent
			// this case is triggered if there was network + no appcontent in local db + network outage while retrieving object of interest
			throw new FocusInternalErrorException("Object may exist on network but network error occurred");
		}
		if (this.appContentTemplate == null) {
			throw new FocusMissingResourceException("Cannot retrieve ApplicationTemplate object.");
		}

		return this.appContentTemplate;
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
		FocusSample fs;
		try {
			fs = (FocusSample) (this.get(url, FocusSample.class));
		}
		catch (IOException ex) {
			throw new FocusMissingResourceException("FocusSample may exist on network but network error occurred");
		}
		if (fs == null) {
			throw new FocusMissingResourceException("Cannot retrieve requested FocusSample.");
		}

		return fs;
	}

	/**
	 * Get a sample history.
	 * <p/>
	 * FIXME give a proper description of the output
	 *
	 * @param url    The URL of the resource for which we want an history
	 * @param params The parameters to be passed to the history retrieving service
	 * @return A {@link FocusSample} containing the history of intereset
	 * <p/>
	 *
	 * FIXME params are passed without any validation.
	 *
	 * FIXME important: the returned FocusSample MUST have a url property that is set to the
	 * same one as the accessed url.
	 */
	public FocusSample getHistory(String url, String params) throws FocusMissingResourceException
	{
		url = url + "?" + params;
		return this.getSample(url);
	}


	/**
	 * Get the appropriate copy of the data identified by the provided url.
	 * <p/>
	 * This method first tries to acquire the resource from the local database and then from the
	 * network if not available. It also stores the object in the local database such that it
	 * can later be quickly accessed.
	 *
	 * @param url         The URL of the resource to retrieve
	 * @param targetClass The retrieved object will be converted to a Java object matching this class
	 * @return A FocusObject, or mor specifically one of its inherited classes matching {@code targetclass}
	 * @throws IOException If an inrecoverable netowrk error occurs
	 */
	FocusObject get(String url, Class targetClass) throws IOException
	{
		// do we have it in the cache?
		if (this.cache.get(url) != null) {
			return this.cache.get(url);
		}

		FocusObject result = null;

		// try to get it from the local SQL db
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			Sample sample = dao.get(url);
			if (sample != null) {
				result = FocusObject.factory(sample.getData(), targetClass);
			}
		}
		finally {
			this.databaseAdapter.close();
		}

		if (result == null) {

			// try to get it from the network
			if (NetworkManager.isNetworkAvailable()) {
				HttpResponse response = this.net.get(url);
				if (response.isSuccessful()) {
					String json = response.getData();
					result = FocusObject.factory(json, targetClass);

					// Convert into a sample and save it into the database
					Sample sample = Sample.cloneFromFocusObject(result);

					try {
						SampleDao dao = this.databaseAdapter.getSampleDao();
						dao.create(sample);
					}
					finally {
						this.databaseAdapter.close();
					}
				}
				else {
					return null;
				}
			}
		}

		if (result != null) {
			this.cache.put(url, result);
		}

		return result;
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
		this.cache.remove(url);

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
	 * Adds the specified instance to the list of currently active instances.
	 *
	 * @param i The instance to add
	 */
	public void registerActiveInstance(AbstractInstance i)
	{
		this.activeInstances.add(i);
	}

	/**
	 * Removes the specified instance from the list of active instances.
	 *
	 * @param i the instance to remove
	 */
	public void unregisterActiveInstance(AbstractInstance i)
	{
		this.activeInstances.remove(i);
	}

	/**
	 * Clean the samples table from useless entries.
	 * <p/>
	 * FIXME we should not do anything if app not ready.
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
	 * return new app instance if success or null on failure.
	 *
	 * @return
	 * @throws FocusMissingResourceException FIXME this should rather go elsewhere, in FocusAppLogic
	 */

	public AppContentInstance retrieveApplicationData() throws FocusMissingResourceException
	{
		// then get the user data
		// the SHARED_PREFERENCES_APPLICATION_CONTENT preference was filled during the
		// login procedure.
		HashMap<String, String> prefs = ApplicationHelper.getPreferences();
		String templateUri = prefs.get(ApplicationHelper.SHARED_PREFERENCES_APPLICATION_CONTENT);
		if (templateUri == null) {
			throw new FocusInternalErrorException("Template is not yet available. Error in the application workflows.");
		}
		AppContentTemplate template = this.getAppContentTemplate(templateUri);
		// FIXME useless ? ???this.registerActiveInstance(this.appContentInstance);

		return new AppContentInstance(template, this);
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
	 * Delete non-mandatory data structure for freeing memory.
	 * <p/>
	 * The application will continue to work, but might require more disk accesses and will
	 * then be slower.
	 */
	public void freeMemory()
	{
		// the in-memory cache is anyway delete after the building of the application content
		// instance, but it is not required for the this task to complete, so we may garbabe collect
		// its content, and the application will then fallback to the local database when accessing
		// resources.
		this.cache = new HashMap<>();
	}

	/**
	 * logout
	 */
	public void handleLogout()
	{
		if (this.applicationReady) {
			this.appContentTemplate = null;

			this.freeMemory();

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
	}

	public void onApplicationLoad(boolean success)
	{
		this.applicationReady = success;
	}

	/**
	 * Acquire the last data set id that was used in the local database
	 * and assign it to our DatabaseAdapter
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
		int reportFailure = NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS;

		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();

			// POST and PUT are almost the same
			HashSet<Sample> toPushSamples;
			for (String typeOfOperation : new String[]{Constant.TO_CREATE, Constant.TO_UPDATE, Constant.TO_DELETE}) {
				toPushSamples = dao.getAllMarkedFocusSamples(typeOfOperation);
				for (Sample sample : toPushSamples) {
					reportFailure |= this.pushLocalModification(sample, FocusSample.class, typeOfOperation, dao);
				}
			}
		}
		finally {
			this.databaseAdapter.close();
		}


		// Error reporting
		if (reportFailure != NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS) {
			throw new FocusMissingResourceException("Cannot retrieve resource - code 0x" + reportFailure);
		}

	}

	/**
	 * push modification for a single sample of speciifed class type
	 *
	 * @param sample
	 * @param targetClass
	 * @param typeOfOperation
	 * @param dao
	 * @return
	 */
	private int pushLocalModification(Sample sample, Class targetClass, String typeOfOperation, SampleDao dao)
	{
		int reportFailure = NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS;


		String networkOperation;
		switch (typeOfOperation) {
			case Constant.TO_CREATE:
				networkOperation = "POST";
				break;
			case Constant.TO_UPDATE:
				networkOperation = "PUT";
				break;
			case Constant.TO_DELETE:
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
				if (typeOfOperation.equals(Constant.TO_DELETE)) {
					dao.delete(url);
				}
				else {
					sample.setEditionField(typeOfOperation, false);
					dao.update(sample);
				}
			}
			else {
				reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
			}
		}
		catch (IOException ex) {
			reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
		}

		return reportFailure;
	}

	/**
	 * same but public, and hence we create the dao ourselves and fetch the Sample for local store for the caller
	 *
	 *
	 * also, we only allow UPDATE of data.
	 */
	public int pushLocalModification(String url, Class targetClass)
	{
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();

			// get the sample corresponding to the url
			Sample sample = dao.get(url);
			// we do the update only an update is scheduled
			if (sample.isToPut()) {
				return this.pushLocalModification(sample, targetClass, Constant.TO_UPDATE, dao);
			}
			else {
				// nothing to do.
				return NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS;
			}
		}
		finally {
			this.databaseAdapter.close();
		}
	}

}

