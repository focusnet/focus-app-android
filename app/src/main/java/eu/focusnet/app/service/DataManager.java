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

import android.content.Context;
import android.provider.Settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.internal.AbstractInstance;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.json.AppContentTemplate;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.json.User;
import eu.focusnet.app.model.json.UserPreferences;
import eu.focusnet.app.model.store.DatabaseAdapter;
import eu.focusnet.app.model.store.Sample;
import eu.focusnet.app.model.store.SampleDao;
import eu.focusnet.app.network.HttpResponse;
import eu.focusnet.app.network.NetworkManager;
import eu.focusnet.app.ui.adapter.DateTypeAdapter;
import eu.focusnet.app.ui.util.PropertiesHelper;

/**
 * This follows a Singleton pattern.
 * <p/>
 * FIXME this class is probably responsible for too many things.
 * <p/>
 * <p/>
 * Created by julien on 07.01.16.
 */
public class DataManager
{
	public enum ResourceOperationStatus
	{
		SUCCESS, // resource altered directly
		PENDING, // pending network operation
		ERROR // permanent error
	}

	public static final String FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX = "http://localhost/FOCUS-INTERNAL/";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION = FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX + "focus-internal-configuration";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER = "login-server";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME = "login-username";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD = "login-password";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS = "user-infos";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS = "application-settings";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT = "application-content";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_DEMO_USE_CASE = "demo-user-case";
	private static final String PROPERTY_TARGET_PERMANENT_STORAGE_SERVER = "resource-server.endpoint";

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
	private UserPreferences userPreferences;
	private AppContentTemplate appContentTemplate;
	private AppContentInstance appContentInstance;
	// for samples, the cache is read-only. It helps speed up the process of building the application content
	private HashMap<String, FocusObject> cache;

	private Gson gson;
	private NetworkManager net;
	private DatabaseAdapter databaseAdapter;

	private ArrayList<AbstractInstance> activeInstances;
	private boolean applicationReady;
	private boolean loggedIn;

	// FIXME only for demo purpose
	private String demoUseCase;

	/**
	 * Constructor
	 */
	public DataManager()
	{
		this.isInitialized = false;
		this.loggedIn = false;

		Context context = FocusApplication.getInstance().getContext();

		this.activeInstances = new ArrayList<>();
		this.applicationReady = false;

		// setup GSON
		this.gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

		// setup network
		this.net = new NetworkManager();

		// setup cache
		this.cache = new HashMap<>();

		// setup database
		this.databaseAdapter = new DatabaseAdapter(context);
	}

	/**
	 * Finish initializing the DataManager
	 */
	public void init()
	{
		if (this.isInitialized) {
			return;
		}

		// get login infos from local store, and use it as the default
		FocusSample internal_config = null;
		try {
			internal_config = (FocusSample) (this.get(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION, FocusSample.class));
		}
		catch (IOException ex) {
			// ok to ignore, no network access for internal configuration (saved in local db)
		}

		if (internal_config != null) {
			this.loginUser = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME);
			this.loginPassword = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD);
			this.loginServer = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER);
			this.userUrl = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS);
			this.prefUrl = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS);
			this.appContentUrl = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT);
			this.demoUseCase = internal_config.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_DEMO_USE_CASE);
			this.loggedIn = true;
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
		if (1 == 1) {
			throw new FocusNotImplementedException("DataManager.login()");
		}

		// FIXME most of the logic below is OK, except for the FIXME part


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

		this.loginServer = server;
		this.loginUser = user;
		this.loginPassword = password;

		/* FIXME */
		// how to get proper URLs?
		this.userUrl = "FIXME";
		this.prefUrl = "FIXME";
		this.appContentUrl = "FIXME";
		/* end of FIXME */

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
		this.post(fs);

		this.loggedIn = true;
		return true;
	}

	/**
	 * A temporary and more simple login function for the demonstration prototype
	 */
	public boolean demoLogin(String use_case) throws IOException
	{
		// if there is no network available, trigger a failure right away
		if (!this.net.isNetworkAvailable()) {
			throw new IOException("No network");
		}

		// Demo URIs:
		// one Use URI per user
		// one UserPreferences URI per user x use case combination
		// one AppContentTemplate per use case
		String user_id = "user-" + Settings.Secure.getString(FocusApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

		this.loginServer = "";
		this.loginUser = "";
		this.loginPassword = "";

		// find the demo use case idx based on the
		this.demoUseCase = use_case;

		// user and preferences URIs
		String test_server = PropertiesHelper.getProperty(PROPERTY_TARGET_PERMANENT_STORAGE_SERVER, FocusApplication.getInstance());
		this.userUrl = test_server + "/data/focus-user/" + user_id;
		this.prefUrl = test_server + "/data/focus-user/" + user_id + "/focus-mobile-app-preferences/" + this.demoUseCase;

		// infer app content URI
		String[] use_cases_ids = FocusApplication.getInstance().getResources().getStringArray(R.array.demo_use_cases_values);
		int found_idx = -1;
		for (int i = 0; i < use_cases_ids.length; ++i) {
			if (this.demoUseCase.equals(use_cases_ids[i])) {
				found_idx = i;
			}
		}
		if (found_idx == -1) {
			throw new FocusInternalErrorException("Invalid demo identifier");
		}

		String[] uris = FocusApplication.getInstance().getResources().getStringArray(R.array.demo_use_cases_app_cntent_uris);
		this.appContentUrl = uris[found_idx];


		// if all ok, save info to local database for later loading
		FocusSample fs = new FocusSample(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME, this.loginUser);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD, this.loginPassword);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER, this.loginServer);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS, this.userUrl);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS, this.prefUrl);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT, this.appContentUrl);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_DEMO_USE_CASE, this.demoUseCase);

		// and save in the local SQLite database (it won't be sent on the network)
		this.delete(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION); // delete existing configuration, just in case
		this.post(fs);

		this.loggedIn = true;
		return true;
	}

	/**
	 * Delete login information, and reset the content of the whole application
	 */
	public void reset()
	{
		this.loginUser = null;
		this.loginPassword = null;
		this.loginServer = null;
		this.userUrl = null;
		this.prefUrl = null;
		this.appContentUrl = null;
		this.user = null;
		this.userPreferences = null;
		this.appContentTemplate = null;
		this.demoUseCase = null;
		this.cache = new HashMap<>();

		// delete the whole database content
		SampleDao dao = this.databaseAdapter.getSampleDao();
		dao.deleteAll();

		this.loggedIn = false;
	}


	/**
	 * Do we have login information?
	 *
	 * @return true if we have all required login information, false otherwise
	 */
	public boolean isLoggedIn()
	{
		return this.loggedIn;
	}


	/**
	 * Acquire personal information about the user of the application
	 * <p/>
	 * User is mandatory object, app cannot live without it, so failure to retrieve it throws a crash.
	 */
	public User getUser()
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.user != null) {
			return this.user;
		}
		User user = null;
		try {
			user = (User) this.get(this.userUrl, User.class);
		}
		catch (IOException ex) {
			// we cannot survive without a user
			// this case is triggered if there was network + no user in local db + network outage while retrieving object of interest
			throw new FocusInternalErrorException("Object may exist on the network but network error occurred.");
		}
		if (user == null) {
			user = new User(this.userUrl, "first", "last", "email@email.com", "company"); // FIXME information not used for now
			ResourceOperationStatus ret = this.post(user);
			if (ret == ResourceOperationStatus.ERROR) {
				throw new FocusInternalErrorException("Cannot create User object.");
			}
		}
		this.user = user;
		return this.user;
	}

	/**
	 * Acquire the application's user preferences.
	 * <p/>
	 * * UserPreferences is mandatory object, app cannot live without it, so failure to retrieve it throws a crash.
	 *
	 * @return A UserPreferences object
	 */
	public UserPreferences getUserPreferences()
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.userPreferences != null) {
			return this.userPreferences;
		}
		UserPreferences user_preference = null;
		try {
			user_preference = (UserPreferences) (this.get(this.prefUrl, UserPreferences.class));
		}
		catch (IOException ex) {
			// we cannot survive without userpreferences
			// this case is triggered if there was network + no userprerferences in local db + network outage while retrieving object of interest
			throw new FocusInternalErrorException("Object may exist on network but network error occurred");
		}
		if (user_preference == null) {
			user_preference = new UserPreferences(this.prefUrl);
			ResourceOperationStatus ret = this.post(user_preference);
			if (ret == ResourceOperationStatus.ERROR) {
				throw new FocusInternalErrorException("Cannot create UserPreferences object.");
			}
		}
		this.userPreferences = user_preference;
		return this.userPreferences;
	}

	/**
	 * Acquire the application content object
	 *
	 * @return An AppContentTemplate object (not an instance, it still must be processed)
	 */
	public AppContentTemplate getAppContentTemplate() throws FocusMissingResourceException
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.appContentTemplate != null) {
			return this.appContentTemplate;
		}
		try {
			this.appContentTemplate = (AppContentTemplate) (this.get(this.appContentUrl, AppContentTemplate.class));
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
	 * <p/>
	 * When building the different instances (AppContent, Project, PAge, Widget), we silently ignore errors, but we log them, just in case.
	 */
	public void retrieveApplicationData() throws FocusMissingResourceException
	{
		this.getUser();
		this.getUserPreferences();
		AppContentTemplate template = this.getAppContentTemplate();
		this.appContentInstance = new AppContentInstance(template);
		this.registerActiveInstance(this.appContentInstance);

		// we don't need the cache anymore
		this.cache = new HashMap<>();

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
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		FocusSample fs = null;
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
	 * Retrieve a sample that is stored in the local database (or in the cache), but do not get it from the network
	 *
	 * @param url
	 * @return
	 */
	public FocusObject getLocalObject(String url)
	{
		if (!this.isLoggedIn()) {
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
	 * @param data The FocusObject representing the data to POST
	 */
	public ResourceOperationStatus post(FocusObject data)
	{
		String url = data.getUrl();
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
// 		this.cache.put(url, data);

		// push on the network
		if (is_special_url) {
			return ResourceOperationStatus.SUCCESS;
		}

		if (!this.net.isNetworkAvailable()) {
			return ResourceOperationStatus.PENDING;
		}

		boolean net_success = false;
		try {
			HttpResponse r = this.net.post(url, data);
			net_success = r.isSuccessful();
		}
		catch (IOException ex) {
			net_success = false;
		}
		if (net_success) { // if POST on network, also remove POST flag in local db copy
			sample.setToPost(false);
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
	 * @param data The FocusObject data to PUT
	 */
	public ResourceOperationStatus put(FocusObject data)
	{
		String url = data.getUrl();

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
			dao.update(sample);
		}
		finally {
			this.databaseAdapter.close();
		}

		// make it accessible through the cache
		this.cache.put(url, data);

		// network PUT
		if (!this.net.isNetworkAvailable()) {
			return ResourceOperationStatus.PENDING;
		}

		boolean net_success = false;
		try {
			net_success = this.net.put(url, data).isSuccessful();
		}
		catch (IOException ex) {
			net_success = false;
		}
		if (net_success) { // if PUT on network, also remove PUT flag in local db copy
			sample.setToPut(false);
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
	 * DELETE a resource
	 *
	 * @param url The URL identifying the resource to DELETE.
	 */
	public ResourceOperationStatus delete(String url)
	{
		// remove from RAM cache
		this.cache.remove(url);

		// mark data for deletion in local database
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();

			// no network for special urls
			if (url.startsWith(FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX)) {
				dao.delete(url);
				return ResourceOperationStatus.SUCCESS;

			}

			dao.markForDeletion(url);

			// remove from network server
			if (!this.net.isNetworkAvailable()) {
				return ResourceOperationStatus.PENDING;
			}

			boolean net_success = false;
			try {
				net_success = this.net.delete(url).isSuccessful();
			}
			catch (IOException ex) {
				net_success = false;
			}

			if (net_success) { // if remove on network, also remove copy in local db
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
		this.put(this.userPreferences);
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
		int report_failure = NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS; // report_failure is a bit mask
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
						report_failure |= NetworkManager.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
					}
				}
				catch (IOException e) {
					report_failure |= NetworkManager.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
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
						report_failure |= NetworkManager.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
					}
				}
				catch (IOException e) {
					report_failure |= NetworkManager.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
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
						report_failure |= NetworkManager.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
					}
				}
				catch (IOException e) {
					report_failure |= NetworkManager.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
				}
			}
		}
		finally {
			this.databaseAdapter.close();
		}
		if (report_failure != NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS) {
			throw new FocusMissingResourceException("Cannot retrieve resource - code 0x" + report_failure);
		}
	}


	/**
	 * Get latest versions of all resources required for running the application, and replace the Application DataManager
	 * <p/>
	 * No need to take care of activeInstances. They will simply be rebuilt on next Activity loading.
	 * <p/>
	 * FIXME FIXME FIXME are we really loading new data, or do they come from the local cache? !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 * <p/>
	 * <p/>
	 * FIXME also, what happens if user / preferences / app content have changed? should be reload full app? -> should be ok like this as we change the data manager being used.
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

	public long getDatabaseSize()
	{
		return this.databaseAdapter.getDatabaseSize();
	}
}
