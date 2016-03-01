/**
 *
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
 *
 */

package eu.focusnet.app.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.internal.AbstractInstance;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.json.AppContentTemplate;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.json.Preference;
import eu.focusnet.app.model.json.User;
import eu.focusnet.app.model.store.DatabaseAdapter;
import eu.focusnet.app.model.store.Sample;
import eu.focusnet.app.model.store.SampleDao;
import eu.focusnet.app.network.HttpResponse;
import eu.focusnet.app.network.NetworkManager;
import eu.focusnet.app.ui.adapter.DateTypeAdapter;

/**
 * This follows a Singleton pattern.
 * <p/>
 * Created by julien on 07.01.16.
 */
public class DataManager
{
	public static final String FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX = "http://localhost/FOCUS-INTERNAL/";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION = FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX + "focus-internal-configuration";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER = "login-server";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME = "login-username";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD = "login-password";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS = "user-infos";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS = "application-settings";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT = "application-content";

	private boolean isInitialized;

	// other information regarding login?
	private String loginUser;
	private String loginPassword;
	private String loginServer;
	private String userUrl;
	private String prefUrl;
	private String appContentUrl;

	// java objects
	private User user;
	private Preference userPreferences;
	private AppContentTemplate appContentTemplate;
	private AppContentInstance appContentInstance;


	// for samples
	private HashMap<String, FocusObject> cache;

	private Gson gson;
	private NetworkManager net;
	private DatabaseAdapter databaseAdapter;

	private ArrayList<AbstractInstance> activeInstances;
	private boolean applicationReady;

	/**
	 * Constructor
	 */
	public DataManager()
	{
		this.isInitialized = false;
		this.init();
	}


	/**
	 * Finish initializing the DataManager
	 *
	 */
	public void init()
	{
		if (this.isInitialized) {
			return;
		}

		Context context = FocusApplication.getInstance().getContext();

		this.activeInstances = new ArrayList<>();
		this.applicationReady = false;

		// setup GSON
		this.gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

		// setup network
		this.net = new NetworkManager(context);

		// setup cache
		this.cache = new HashMap<>();

		// setup database
		this.databaseAdapter = new DatabaseAdapter(context);

		// get login infos from local store, and use it as the default
		FocusSample internal_config = null;
		try {
			internal_config = (FocusSample) (this.get(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION, FocusSample.class));
		}
		catch (IOException ex) {
			// not a network resource, ok to ignore
		}
		// FIXME TODO YANDY: would that be better to use SharedPreferences? Or is that way of doing ok? Basically I store everything in an entry of our samples SQLlite table
		if (internal_config != null) {
			this.loginUser = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME);
			this.loginPassword = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD);
			this.loginServer = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER);
			this.userUrl = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS);
			this.prefUrl = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS);
			this.appContentUrl = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT);
		}

		this.isInitialized = true;
	}


	/**
	 * Get the GSON object used for data conversion in our app
	 *
	 * @return a pointer to the GSON object, which is already properly configured for our application
	 */
	public Gson getGson()
	{
		return this.gson;
	}

	/**
	 * Get the network manager
	 *
	 * @return
	 */
	public NetworkManager getNetworkManager()
	{
		return this.net;
	}

	/**
	 * Tells whether the application is ready for being run, i.e. all basic objects have been loaded
	 *
	 * @return
	 */
	public boolean isApplicationReady()
	{
		return this.applicationReady;
	}

	/**
	 * Login and if successful, save the login information in the permanent store.
	 * <p/>
	 * This method relies on network connectivity.
	 *
	 * @param user     The login user
	 * @param password The login password
	 * @param server   The login server
	 * @return true on successful authentication, false otherwise (403 error)
	 * @throws IOException if a network error occurs
	 */
	public boolean login(String user, String password, String server) throws IOException
	{
		// if there is no network available, trigger a failure right away
		if (!this.net.isNetworkAvailable()) {
			throw new IOException("No network");
		}

		// do network login
		boolean login_result = this.net.login(user, password, server);
		if (!login_result) { // 403 error
			this.delete(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION);
			return false;
		}

		// then acquire application basic content
		// FIXME how do we retrieve the proper URLs? lookup() service ? context = user, but what is user url/identifier?
		// FIXME FIXME TODO when we have data from Jussi
		// and save the urls for later uses (no need to actually get the information at this point)
		int userId = 123; // FIXME hard-coded. should be obtained from network.
		this.loginServer = server;
		this.loginUser = user;
		this.loginPassword = password;
		this.userUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/user-information";
		this.prefUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/app-user-preferences";
		// this.appContentUrl = "http://focus.yatt.ch/resources-server/data/user/" + userId + "/app-content-definition";
		this.appContentUrl = "http://focus.yatt.ch/debug/app-content-3.json"; // FIXME hard-coded for testing.

		// if all ok, save info to local database for later loading
		FocusSample fs = new FocusSample(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME, this.loginUser);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD, this.loginPassword);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER, this.loginServer);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS, this.userUrl);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS, this.prefUrl);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT, this.appContentUrl);

		// and save in the local SQLite database (it won't be sent on the network)
		this.delete(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION); // delete existing configuration, just in case
		this.post(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION, fs);

		return true;
	}

	/**
	 * Delete login information, and reset the content of the whole application
	 * <p/>
	 * FIXME FIXME TODO YANDY: register logout button in settings fragment that triggers this function and then
	 * redirects to the Entrypoint activity (user will redo the whole login process).
	 */
	public void logout()
	{
		this.loginUser = "";
		this.loginPassword = "";
		this.loginServer = "";
		this.userUrl = "";
		this.prefUrl = "";
		this.appContentUrl = "";
		this.user = null;
		this.userPreferences = null;
		this.appContentTemplate = null;
		this.cache = new HashMap<>();

		// delete SQL db
		try {
			this.delete(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION);
		}
		catch (IOException ex) {
			// will not happen as this is local resource (internal)
		}
		// delete the whole database content FIXME TODO
	}


	/**
	 * Do we have login information?
	 *
	 * @return true if we have all required login information, false otherwise
	 */
	public boolean hasLoginInformation()
	{
		return this.loginUser != null
				&& this.loginPassword != null
				&& this.loginServer != null
				&& this.userUrl != null
				&& this.prefUrl != null
				&& this.appContentUrl != null;
	}


	/**
	 * Acquire personal information about the user of the application
	 */
	public User getUser() throws FocusInternalErrorException, FocusMissingResourceException
	{
		if (!this.hasLoginInformation()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.user != null) {
			return this.user;
		}
		try {
			this.user = (User) (this.get(this.userUrl, User.class));
		}
		catch (IOException ex) {
			throw new FocusMissingResourceException("Cannot retrieve User object (network problem).");
		}
		if (this.user == null) {
			throw new FocusMissingResourceException("Cannot retrieve User object.");
		}
		return this.user;
	}

	/**
	 * Acquire the application's user preferences.
	 *
	 * @return A Preference object
	 */
	public Preference getUserPreferences() throws FocusMissingResourceException
	{
		if (!this.hasLoginInformation()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.userPreferences != null) {
			return this.userPreferences;
		}
		try {
			this.userPreferences = (Preference) (this.get(this.prefUrl, Preference.class));
		}
		catch (IOException ex) {
			throw new FocusMissingResourceException("Cannot retrieve Preference object (network problem).");
		}
		if (this.userPreferences == null) {
			throw new FocusMissingResourceException("Cannot retrieve Preference object.");
		}
		return this.userPreferences;
	}

	/**
	 * Acquire the application content object
	 *
	 * @return An AppContentTemplate object (not an instance, it still must be processed)
	 */
	public AppContentTemplate getAppContentTemplate() throws FocusMissingResourceException
	{
		if (!this.hasLoginInformation()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.appContentTemplate != null) {
			return this.appContentTemplate;
		}
		try {
			this.appContentTemplate = (AppContentTemplate) (this.get(this.appContentUrl, AppContentTemplate.class));
		}
		catch (IOException ex) {
			throw new FocusMissingResourceException("Cannot retrieve ApplicationTemplate object (network problem).");
		}
		if (this.appContentTemplate == null) {
			throw new FocusMissingResourceException("Cannot retrieve ApplicationTemplate object.");
		}
		return this.appContentTemplate;
	}

	/**
	 * Get the application content instance, i.e. with data context and iterator having beeen resolved.
	 *
	 * @return An ApplicationInstance object reflecting the current content of the app
	 * @throws RuntimeException
	 */
	public AppContentInstance getAppContentInstance()
	{
		return this.appContentInstance;
	}

	/**
	 * Get the three basic informations that are required to build the application UI
	 *
	 * When building the different instances (AppContent, Project, PAge, Widget), we silently ignore errors, but we log them, just in case.
	 */
	public void retrieveApplicationData() throws FocusMissingResourceException
	{
		this.getUser();
		this.getUserPreferences();
		AppContentTemplate template = this.getAppContentTemplate();
		this.appContentInstance = new AppContentInstance(template, true);
		this.registerActiveInstance(this.appContentInstance);
		this.applicationReady = true;
	}

	/**
	 * Get a FocusSample
	 *
	 * @param url The URL identifyiing the sample to retrieve
	 * @return A FocusSample
	 */
	public FocusSample getSample(String url) throws FocusMissingResourceException
	{
		if (!this.hasLoginInformation()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}
		FocusSample fs;
		try {
			fs = (FocusSample) (this.get(url, FocusSample.class));
			if (fs == null) {
				throw new FocusMissingResourceException("Cannot retrieve requested FocusSample.");
			}
		}
		catch (IOException ex) {
			throw new FocusMissingResourceException("Cannot retrieve requested FocusSample (network problem).");
		}
		return fs;
	}


	/**
	 * Retrieve a sample that is stored in the local database (or in the cache), but do not get it from the network
	 *
	 * @param url
	 * @return
	 */
	public FocusObject getLocalObject(String url)
	{
		if (!this.hasLoginInformation()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.cache.get(url) != null) {
			return this.cache.get(url);
		}

		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			Sample sample = dao.get(url);
			if (sample != null) {
				return FocusObject.factory(sample.getData(), FocusObject.class); // FIXME TODO check if that works for UserPreference object, for example, and for FocusSample's
			}
		}
		finally {
			// FIXME nested finally -> problem will close the dao but outside we are not finished.
		}
		return null;
	}


	/**
	 * Get the appropriate copy of the data identified by the provided url.
	 */
	private FocusObject get(String url, Class targetClass) throws IOException
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
			// special case: internal configuration
			if (url.startsWith(FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX)) {
				return null;
			}

			// try to get it from the network
			if (this.net.isNetworkAvailable()) {
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
	 * Create a new data entry
	 * <p/>
	 * a POST is seen as a data commit, so we call data.commit()
	 *
	 * @param url  The URL identifying the resource to POST
	 * @param data The FocusObject representing the data to POST
	 */
	public void post(String url, FocusObject data) throws IOException
	{
		boolean is_special_url = url.startsWith(FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX);

		// store in local database, with the "toPost" flag
		Sample sample = Sample.cloneFromFocusObject(data);
		if (!is_special_url) {
			sample.setToPost(true);
		}

		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			dao.create(sample);
		}
		finally {
			this.databaseAdapter.close();
		}

		// make it accessible through the cache
		this.cache.put(url, data);
		data.commit();

		// push on the network
		if (is_special_url) {
			return;
		}

		if (this.net.post(url, data).isSuccessful()) { // if POST on network, also remove POST flag in local db copy
			sample.setToPost(false);
			try {
				SampleDao dao = this.databaseAdapter.getSampleDao();
				dao.update(sample);
			}
			finally {
				this.databaseAdapter.close();
			}
		}
	}

	/**
	 * PUT some data to a remote server.
	 * <p/>
	 * Methods calling this object do not have to take care of the FocusObject attributes, e.g.
	 * version, creationTime, editionTime, owner, editor, etc. They should only modify the object's
	 * specific attributes, such as the content of the data Map for FocusSample objects.
	 * <p/>
	 * We consider that we are provided with a *copy* of the FocusObject we update. At the end of
	 * this method, we replace the old object in our RAM cache with the new one, such that the old
	 * one will be garbage collected.
	 * <p/>
	 * a PUT is seen as a data commit, so we call data.commit()
	 *
	 * @param url  The URL identifying the resource to PUT
	 * @param data The FocusObject data to PUT
	 */
	public void put(String url, FocusObject data) throws IOException
	{
		// special internal data are NOT to be PUT, ever.
		if (url.startsWith(FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX)) {
			throw new FocusInternalErrorException("Cannot PUT internal data");
		}

		// update the object to a new version
		data.updateToNewVersion();

		// store in local database, with the "toPut" flag
		Sample sample = Sample.cloneFromFocusObject(data);
		sample.setToPut(true);
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			dao.create(sample); // we create a new sample of the object identified by url, and this is ok
		}
		finally {
			this.databaseAdapter.close();
		}

		// make it accessible through the cache
		this.cache.put(url, data);
		data.commit();

		// network PUT
		if (this.net.put(url, data).isSuccessful()) { // if PUT on network, also remove PUT flag in local db copy
			sample.setToPut(false);
			try {
				SampleDao dao = this.databaseAdapter.getSampleDao();
				dao.update(sample);

				// FIXME TODO we should delete the old versions of the resource
			}
			finally {
				this.databaseAdapter.close();
			}
		}
	}

	/**
	 * DELETE a resource
	 *
	 * @param url The URL identifying the resource to DELETE.
	 */
	public void delete(String url) throws IOException
	{
		// remove from RAM cache
		this.cache.remove(url);

		// mark data for deletion in local database
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();

			// no network for special urls
			if (url.startsWith(FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX)) {
				dao.delete(url);
				return;
			}

			dao.markForDeletion(url);

			// remove from network server
			if (this.net.delete(url).isSuccessful()) { // if remove on network, also remove copy in local db
				dao.delete(url);
			}
		}
		finally {
			this.databaseAdapter.close();
		}
	}

	/**
	 * Get the history for a url, according to provided parameters. This will call a remote service.
	 * <p/>
	 * HistorySample = { data: field1: [], field2: []}
	 *
	 * @param url
	 * @param params
	 * @return
	 */
	public FocusSample getHistorySample(String url, String params)
	{
		return null;
		// 	throw new FocusNotImplementedException("getHistorySample");
	}

	/**
	 * Look for objects that are of the specified type and context. This will call a remote service.
	 * <p/>
	 * LookupSample = { data: urls: []}
	 *
	 * @param context
	 * @param type
	 * @return
	 */
	public FocusSample getLookupSample(String context, String type)
	{
		return null;
// 		throw new FocusNotImplementedException("getLookupSample");
	}

	/**
	 * Trigger saving of user preferences
	 */
	public void saveUserPreferences()
	{
		throw new FocusNotImplementedException("saveuserpref");
	}

	/**
	 * Adds the specified AbstractInstance to the list of currently active instances.
	 *
	 * @param i
	 */
	public void registerActiveInstance(AbstractInstance i)
	{
		this.activeInstances.add(i);
	}

	/**
	 * Removes the specified AbstractInstance from the list of active instances.
	 *
	 * @param i
	 */
	public void unregisterActiveInstance(AbstractInstance i)
	{
		this.activeInstances.remove(i);
	}

	/**
	 * Free memory from our in-RAM data structure, where this is possible
	 */
	public void freeMemory()
	{
		Set<String> used_urls = new HashSet<>();
		for (AbstractInstance ai : this.activeInstances) {
			Map<String, String> m = ai.getDataContext();
			for (Map.Entry<String, String> e : m.entrySet()) {
				used_urls.add(e.getValue());
			}
		}

		for (Map.Entry<String, FocusObject> e : this.cache.entrySet()) {
			String url = e.getKey();
			if (!used_urls.contains(url)) {
				this.cache.remove(url);
			}
		}
	}

	/**
	 * Clean the samples table from useless entries.
	 */
	public void cleanDataStore()
	{
		if (!this.isApplicationReady()) {
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
	 * Sync data currently on the client side with the ones of the backends,
	 * and then retrieve latest versions of resources.
	 */
	public boolean syncData() throws FocusMissingResourceException
	{
		if (!this.isApplicationReady()) {
			return false;
		}

		if (!this.net.isNetworkAvailable()) {
			return false;
		}

		this.pushLocalModifications();
		this.rebuildApplicationData();
		return true;
	}

	/**
	 * Push local modifications to the backends
	 */
	private void pushLocalModifications() throws FocusMissingResourceException
	{
		// POST
		String[] to_post_urls;
		int report_failure = 0; // 1 = network failure only, 2 = at some point a HTTP request had a failed status
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			to_post_urls = dao.getAllMarkedForPost();
			for (String url : to_post_urls) {
				FocusObject fo = this.getLocalObject(url);
				if (fo == null) {
					throw new FocusInternalErrorException("Could get the data in the database, but could not instantiate it (POST).");
				}
				Sample sample = Sample.cloneFromFocusObject(fo);
				try {
					if (this.net.post(url, fo).isSuccessful()) {
						sample.setToPost(false);
						dao.update(sample);
					}
					else {
						report_failure = 2;
					}
				}
				catch (IOException e) {
					report_failure = report_failure == 2 ? 2 : 1;
				}
			}

			// PUT
			String[] to_put_urls;
			to_put_urls = dao.getAllMarkedForPut();
			for (String url : to_put_urls) {
				FocusObject fo = this.getLocalObject(url);
				if (fo == null) {
					throw new FocusInternalErrorException("Could get the data in the database, but could not instantiate it (PUT).");
				}
				Sample sample = Sample.cloneFromFocusObject(fo);
				try {
					if (this.net.put(url, fo).isSuccessful()) {
						sample.setToPut(false);
						dao.update(sample);
					}
					else {
						report_failure = 2;
					}
				}
				catch (IOException e) {
					report_failure = report_failure == 2 ? 2 : 1;
				}
			}

			// DELETE
			String[] to_delete_urls;
			to_delete_urls = dao.getAllMarkedForDeletion();
			for (String url : to_delete_urls) {
				try {
					if (this.net.delete(url).isSuccessful()) {
						dao.delete(url);
					}
					else {
						report_failure = 2;
					}
				}
				catch (IOException e) {
					report_failure = report_failure == 2 ? 2 : 1;
				}
			}

		}
		finally {
			this.databaseAdapter.close();
		}
		if (report_failure > 0) {
			throw new FocusMissingResourceException("Cannot retrieve resource - " + (report_failure == 2 ? "At least one unsuccessful request" : "Network failure only"));
		}
	}


	/**
	 * Get latest versions of all resources required for running the application, and replace the Application DataManager
	 *
	 * No need to take care of activeInstances. They will simply be rebuilt on next Activity loading.
	 */
	public void rebuildApplicationData()
	{
		boolean must_recover = false;
		DataManager new_dm = new DataManager();
		try {
			new_dm.retrieveApplicationData();
		}
		catch (FocusMissingResourceException ex) {
			must_recover = true;
		}
		if (!must_recover) {
			FocusApplication.getInstance().replaceDataManager(new_dm);
		}
	}
}
