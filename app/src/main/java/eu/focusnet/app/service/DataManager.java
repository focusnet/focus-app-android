package eu.focusnet.app.service;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusNotImplementedException;
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
	private static final String FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX = "http://localhost/FOCUS-INTERNAL/";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION = FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX + "focus-internal-configuration";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER = "login-server";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME = "login-username";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD = "login-password";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS = "user-infos";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS = "application-settings";
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT = "application-content";

	private static DataManager ourInstance = new DataManager();

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

	/**
	 * Initialize the Singleton.
	 */
	private DataManager()
	{
		this.isInitialized = false;
	}

	/**
	 * Get the Singleton instance
	 *
	 * @return a pointer to the Singleton
	 */
	public static DataManager getInstance()
	{
		return ourInstance;
	}


	/**
	 * Finish initializing the DataManager
	 *
	 * @param context The Application Context this DataManager lives in
	 */
	public void init(Context context)
	{
		if (this.isInitialized) {
			return;
		}

		// setup GSON
		this.gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

		// setup network
		this.net = NetworkManager.getInstance();
		this.net.setContext(context);

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
	 * Login and if successful, save the login information in the permanent store.
	 * <p/>
	 * This method relies on network connectivity.
	 *
	 * @param user     The login user
	 * @param password The login password
	 * @param server   The login server
	 * @return true on successful authentication, false otherwise (403 error)
	 * @exception IOException if a network error occurs
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
	 * FIXME FIXME TODO YANDY: add logout button in settings fragment that triggers this function and then
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
			// FIXME should not happen as this is local resource (internal)
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
	public Preference getUserPreferences() throws RuntimeException, FocusMissingResourceException
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
	public AppContentTemplate getAppContentTemplate() throws RuntimeException, FocusMissingResourceException
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
	 */
	public void retrieveApplicationData() throws FocusMissingResourceException
	{
		this.getUser();
		this.getUserPreferences();
		AppContentTemplate template = this.getAppContentTemplate();
		this.appContentInstance = new AppContentInstance(template);
	}

	/**
	 * Get a FocusSample
	 *
	 * @param url The URL identifyiing the sample to retrieve
	 * @return A FocusSample
	 */
	public FocusSample getSample(String url) throws RuntimeException, FocusMissingResourceException
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
	 * Get the appropriate copy of the data identified by the provided url.
	 */
	private FocusObject get(String url, Class targetClass) throws IOException
	{
		// do we have it in the cache?
		// FIXME TODO check if not too old ? (see sample.lastUsage)
		if (this.cache.get(url) != null) {
			FocusObject result = this.cache.get(url);
			result.updateLastUsage();
			return result;
		}

		FocusObject result = null;

		// try to get it from the local SQL db
		try {
			this.databaseAdapter.openWritableDatabase();
			SampleDao sampleDao = new SampleDao(this.databaseAdapter.getDb());
			Sample sample = sampleDao.get(url);
			if (sample != null) {
				result = (FocusObject) this.gson.fromJson(sample.getData(), targetClass);
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
					result = (FocusObject) this.gson.fromJson(json, targetClass);

					// Convert into a sample and save it into the database
					Sample sample = new Sample();
					sample.cloneFromFocusObject(result, json);

					try {
						this.databaseAdapter.openWritableDatabase();
						SampleDao sampleDao = new SampleDao(this.databaseAdapter.getDb());
						sampleDao.create(sample);
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
			result.updateLastUsage();
			this.cache.put(url, result);
		}

		return result;
	}

	/**
	 * Create a new data entry
	 *
	 * @param url The URL identifying the resource to POST
	 * @param data The FocusObject representing the data to POST
	 */
	public void post(String url, FocusObject data) throws IOException
	{
		boolean is_special_url = url.startsWith(FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX);

		// make it accessible through the cache
		this.cache.put(url, data);
		data.updateLastUsage();

		// store in local database, with the "toPost" flag
		Sample sample = new Sample();
		sample.cloneFromFocusObject(data);
		if (!is_special_url) {
			sample.setToPost(true);
		}

		try {
			this.databaseAdapter.openWritableDatabase();
			SampleDao sample_dao = new SampleDao(this.databaseAdapter.getDb());
			sample_dao.create(sample);
		}
		finally {
			this.databaseAdapter.close();
		}

		if (is_special_url) {
			return;
		}

		// push on the network
		if (this.net.post(url, data).isSuccessful()) { // if POST on network, also remove POST flag in local db copy
			try {
				this.databaseAdapter.openWritableDatabase();
				SampleDao sample_dao = new SampleDao(this.databaseAdapter.getDb());
				sample.setToPost(false);
				sample_dao.update(sample);
			}
			finally {
				this.databaseAdapter.close();
			}
		}
	}

	/**
	 * PUT some data to a remote server.
	 *
	 * @param url The URL identifying the resource to PUT
	 * @param data The FocusObject data to PUT
	 */
	public void put(String url, FocusObject data) throws IOException
	{
		/*
		 * PUT = new entry, updated, with version += 1
		 *
		 * 1. update local cache
		 * 2. update local store, mark as to push
		 * 3. update remote store NetworkManager.post();
		 * 4. if net success, unmark push flag
		 */
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
			this.databaseAdapter.openWritableDatabase();
			SampleDao dao = new SampleDao(this.databaseAdapter.getDb());

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
	 * Free memory from our in-RAM data structure, where this is possible
	 */
	public void freeMemory()
	{
		// likely to browser all DataContext's and free memory there
		return;
	}

}
