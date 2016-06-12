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
public class DataManager
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
	 * Prefix for internal configuration resources.
	 *
	 * @see DataManager#FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION
	 */
	public static final String FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX = "http://localhost/FOCUS-INTERNAL/";
	/**
	 * The local database storage contains a special entry that contains the basics of application
	 * configuration. This is identified by the {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION} resource
	 * URI. More generally, any URI starting with{@link #FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX} should
	 * never be transmitted over the network.
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION = FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX + "focus-internal-configuration";
	/**
	 * Label for server name as stored in the internal configuration identified by
	 * {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION}
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER = "login-server";
	/**
	 * Label for username for authentication as stored in the internal configuration identified by
	 * {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION}
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME = "login-username";
	/**
	 * Label for password for authentication as stored in the internal configuration identified by
	 * {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION}
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD = "login-password";
	/**
	 * Label for URI to user information as stored in the internal configuration identified by
	 * {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION}
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS = "user-infos";
	/**
	 * Label for URI to application preferences as stored in the internal configuration identified by
	 * {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION}
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS = "user-preferences";
	/**
	 * Label for URI to application content as stored in the internal configuration identified by
	 * {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION}
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT = "application-content";
	/**
	 * Label for URI to application preferences as stored in the internal configuration identified by
	 * {@link #FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION}
	 *
	 * @deprecated Used only in the prototype. Can be safely removed in production version.
	 */
	private static final String FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_DEMO_USE_CASE = "demo-use-case";

	/**
	 * This property defines the endpoint where our application-specific data are stored
	 * (User information, User preferences)
	 */
	private static final String PROPERTY_TARGET_PERMANENT_STORAGE_SERVER = "resource-server.endpoint";

	/**
	 * Login-related input information: username
	 */
	private String loginUser;
	/**
	 * Login-related input information: password
	 */
	private String loginPassword;
	/**
	 * Login-related input information: connection server
	 */
	private String loginServer;

	/**
	 * The currently selected use case identifier for the prototype. This is one of the values
	 * listed in R.string.demo_use_cases_values.
	 *
	 * @deprecated This is only used in the prototype of the application.
	 */
	private String demoUseCase;

	/**
	 * URI to resource holding user information.
	 * <p/>
	 * URIs to resources that are used for building the application. 3 objects are of special
	 * importance and mandatory for the application to work properly:
	 * - userUrl points to a {@link User} object, which contains basic information such as the
	 * user's name
	 * - prefUrl points to a {@link UserPreferences} object, which contains e.g. bookmarks
	 * - appContentUrl points to a {@link AppContentTemplate} object, which contains the template
	 * used to generate the content of the application.
	 */
	private String userUrl;
	/**
	 * URI to resource holding user information.
	 *
	 * @see #userUrl;
	 */
	private String prefUrl;
	/**
	 * URI to resource holding user information.
	 *
	 * @see #userUrl;
	 */
	private String appContentUrl;

	/**
	 * Java object holding the content of {@link #userUrl}
	 */
	private User user;
	/**
	 * Java object holding the content of {@link #prefUrl}
	 */
	private UserPreferences userPreferences;
	/**
	 * Java object holding the content of {@link #appContentUrl}
	 */
	private AppContentTemplate appContentTemplate;

	/**
	 * The {@link AppContentInstance} is the instanciation of the {@link AppContentTemplate}
	 * referenced above. It means that the template will be evaluated and expanded with actual data.
	 */
	private AppContentInstance appContentInstance;

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
	 * The GSON object with which we do all our Java Object &lt;-&gt; JSON transformations
	 */
	private Gson gson;

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

	/**
	 * Tells whether the application is ready to be used
	 */
	private boolean applicationReady;

	/**
	 * Tells whether the user is properly logged in.
	 */
	private boolean loggedIn;

	/**
	 * Constructor. Initialize all default values and facilities, but does not perform any
	 * operation.
	 */
	public DataManager()
	{
		this.loggedIn = false;

		Context context = FocusApplication.getInstance();

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
	 * Finish initializing the DataManager by retrieving the internal configuration containing
	 * credentials, URIs to basic resources, etc.
	 * <p/>
	 * If this initialization fails, {@code this.login} will not be set to {@code true} and the UI
	 * will act consequently.
	 */
	public void init()
	{

		// get login infos from local store, and use it as the default
		FocusSample internalConfig = null;
		try {
			internalConfig = (FocusSample) (this.get(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION, FocusSample.class));
		}
		catch (IOException ignored) {
			// ok to ignore, no network access for internal configuration (saved in local db)
		}

		if (internalConfig != null) {
			this.loginUser = internalConfig.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME);
			this.loginPassword = internalConfig.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD);
			this.loginServer = internalConfig.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER);
			this.userUrl = internalConfig.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS);
			this.prefUrl = internalConfig.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS);
			this.appContentUrl = internalConfig.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT);
			this.demoUseCase = internalConfig.getString(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_DEMO_USE_CASE);
			this.loggedIn = true;
		}
	}

	/**
	 * Login and if successful, save the login information in the permanent store, as the internal
	 * configuration resource.
	 * <p/>
	 * FIXME TODO implementation to be completed once we have an authentication server.
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

		// FIXME logic below is OK, except for the FIXME part

		// if there is no network available, trigger a failure right away
		if (!this.net.isNetworkAvailable()) {
			throw new IOException("No network");
		}

		// do network login
		boolean loginResult = this.net.login(user, password, server);
		if (!loginResult) { // 403 error
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

		// if all ok, save description to local database for later loading
		FocusSample fs = new FocusSample(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_USERNAME, this.loginUser);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_PASSWORD, this.loginPassword);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_LOGIN_SERVER, this.loginServer);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_USER_INFOS, this.userUrl);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_SETTINGS, this.prefUrl);
		fs.add(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION_APPLICATION_CONTENT, this.appContentUrl);

		// and save in the local SQLite database (it won't be sent on the network)
		this.delete(FOCUS_DATA_MANAGER_INTERNAL_CONFIGURATION); // delete existing configuration, just in case
		this.create(fs);

		this.loggedIn = true;
		return true;
	}

	/**
	 * A temporary and more simple login-like function that will retrieve all information from
	 * resources ({@code R.string.demo_use_cases_labels, R.string.demo_use_cases_app_content_uris}).
	 * These resources are stored as string-arrays, and the selected use case properties share the
	 * same array index as the one of the selected {@param useCase}  as it can be found in
	 * {@code R.string#demo_use_cases_values}.
	 *
	 * @param useCase the identifier of the use case selected by the user, as it can be found in
	 *                {@code R.string#demo_use_cases_values}.
	 * @return true on success, false otherwise
	 * @throws IOException If a network error occured
	 * @deprecated Only valid for the prototype
	 */
	public boolean demoLogin(String useCase) throws IOException
	{
		// if there is no network available, trigger a failure right away
		if (!this.net.isNetworkAvailable()) {
			throw new IOException("No network");
		}

		// Demo URIs:
		// - one Use URI per user
		// - one UserPreferences URI per user x use case combination
		// - one AppContentTemplate per use case
		String userId = "user-" + Settings.Secure.getString(FocusApplication.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

		// no need to set credentials, we won't use them.
		this.loginServer = "";
		this.loginUser = "";
		this.loginPassword = "";

		// find the demo use case idx based on the
		this.demoUseCase = useCase;

		// user and preferences URIs
		String testServer = PropertiesHelper.getProperty(PROPERTY_TARGET_PERMANENT_STORAGE_SERVER, FocusApplication.getInstance());
		this.userUrl = testServer + "/data/focus-user/" + userId;
		this.prefUrl = testServer + "/data/focus-user/" + userId + "/focus-mobile-app-preferences/" + this.demoUseCase;

		// infer app content URI
		String[] useCasesIds = FocusApplication.getInstance().getResources().getStringArray(R.array.demo_use_cases_values);
		int foundIdx = -1;
		for (int i = 0; i < useCasesIds.length; ++i) {
			if (this.demoUseCase.equals(useCasesIds[i])) {
				foundIdx = i;
				break;
			}
		}
		if (foundIdx == -1) {
			throw new FocusInternalErrorException("Invalid demo identifier");
		}
		String[] uris = FocusApplication.getInstance().getResources().getStringArray(R.array.demo_use_cases_app_content_uris);
		this.appContentUrl = uris[foundIdx];

		// if all ok, save description to local database for later loading
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
		this.create(fs);

		this.loggedIn = true;
		return true;
	}

	/**
	 * Delete login information and any locally stored data
	 */
	public void logout()
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
	 * Tells whether the user has already logged in.
	 *
	 * @return {@code true} if we have all required login information, {@code false} otherwise
	 */
	public boolean isLoggedIn()
	{
		return this.loggedIn;
	}

	/**
	 * Get the 3 basic objects, and then instantiate the Application content as a
	 * {@link AppContentInstance}, based on the Application template ({@link AppContentTemplate}).
	 *
	 * @throws FocusMissingResourceException If any of the involved resources could not be found
	 */
	public void retrieveApplicationData() throws FocusMissingResourceException
	{
		this.getUser();
		this.getUserPreferences();
		AppContentTemplate template = this.getAppContentTemplate();
		this.appContentInstance = new AppContentInstance(template);
		this.registerActiveInstance(this.appContentInstance);

		// we don't need the cache anymore
		this.cache = new HashMap<>(); // FIXME should we keep it anyway? maybe useful for periodic operations?

		this.applicationReady = true;
	}

	/**
	 * Get the application content instance, i.e. the application content with all projects,
	 * pages and widgets and their content being initialized with proper values.
	 *
	 * @return An ApplicationInstance object reflecting the current content of the app
	 */
	public AppContentInstance getAppContentInstance()
	{
		return this.appContentInstance;
	}

	/**
	 * Tells whether the application is ready for being run, i.e. all 3 basic objects have been
	 * loaded and the application content has been built.
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isApplicationReady()
	{
		return this.applicationReady;
	}

	/**
	 * Get latest versions of all resources required for running the application, and replace
	 * the application-wide DataManager on success.
	 * <p/>
	 * Note: we do not need to take care of currently active instances ({@see #activeInstances}),
	 * this list will be rebuilt on next Activity loading. FIXME sure of that?
	 * <p/>
	 * FIXME bug: it looks like the data come from the local databse, not from the network. To check.
	 * FIXME behavior: what if one of the 3 basic types has changed? relaod full app?
	 */
	public void rebuildApplicationData()
	{
		boolean mustRecover = false;
		DataManager newDm = new DataManager();
		try {
			newDm.retrieveApplicationData();
		}
		catch (FocusMissingResourceException ex) {
			mustRecover = true;
		}
		if (!mustRecover) {
			FocusApplication.getInstance().replaceDataManager(newDm);
		}
	}

	/**
	 * The {@link User} is one of the 3 mandatory objects for the application to run. This method
	 * retrieves this object based on the URI that has been obtained during the login procedure.
	 * If the object does not exist, a new one is created on the network.
	 * <p/>
	 * The application cannot live without this object and will therefore crash if it does not
	 * succeed in retrieving or creating this object.
	 *
	 * @return A {@link User} object
	 */
	public User getUser()
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.user != null) {
			return this.user;
		}
		User user;
		try {
			user = (User) this.get(this.userUrl, User.class);
		}
		catch (IOException ex) {
			// we cannot survive without a user
			// this case is triggered if there was network + no user in local db + network outage
			// while retrieving object of interest
			throw new FocusInternalErrorException("Object may exist on the network but network error occurred.");
		}
		if (user == null) {
			// FIXME information not used for now, must still must be valid to pass JSON Schema valiation on the server-side
			user = new User(this.userUrl, "first", "last", "email@email.com", "company");
			ResourceOperationStatus ret = this.create(user);
			if (ret == ResourceOperationStatus.ERROR) {
				throw new FocusInternalErrorException("Cannot create User object.");
			}
		}
		this.user = user;
		return this.user;
	}

	/**
	 * The {@link UserPreferences} is one of the 3 mandatory objects for the application to run.
	 * This method retrieves this object based on the URI that has been obtained during the
	 * login procedure. If the object does not exist, a new one is created on the network.
	 * <p/>
	 * The application cannot live without this object and will therefore crash if it does not
	 * succeed in retrieving or creating this object.
	 *
	 * @return A {@link UserPreferences} object
	 */
	public UserPreferences getUserPreferences()
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.userPreferences != null) {
			return this.userPreferences;
		}
		UserPreferences userPreferences = null;
		try {
			userPreferences = (UserPreferences) (this.get(this.prefUrl, UserPreferences.class));
		}
		catch (IOException ex) {
			// we cannot survive without userpreferences
			// this case is triggered if there was network + no userprerferences in local db + network outage while retrieving object of interest
			throw new FocusInternalErrorException("Object may exist on network but network error occurred");
		}
		if (userPreferences == null) {
			userPreferences = new UserPreferences(this.prefUrl);
			ResourceOperationStatus ret = this.create(userPreferences);
			if (ret == ResourceOperationStatus.ERROR) {
				throw new FocusInternalErrorException("Cannot create UserPreferences object.");
			}
		}
		this.userPreferences = userPreferences;
		return this.userPreferences;
	}

	/**
	 * Trigger saving of user preferences.
	 */
	public void saveUserPreferences()
	{
		this.update(this.userPreferences);
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
	 * Get a FocusSample
	 *
	 * @param url The URL identifyiing the sample to retrieve
	 * @return A FocusSample
	 * @throws FocusMissingResourceException If the resource could not be found (in the local store
	 *                                       and on the network)
	 */
	public FocusSample getSample(String url) throws FocusMissingResourceException
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

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
	 * TODO to implement
	 */
	public FocusSample getHistory(String url, String params)
	{
		throw new FocusNotImplementedException("DataManager.getHistory()");
	}

	/**
	 * Lookup for data based on their type.
	 * <p/>
	 * FIXME give a proper description of the output
	 *
	 * @param type The FOCUS object type of interest,
	 *             e.g. http://reference.focusnet.eu/schemas/focus-data-sample/v1.0
	 * @return A {@link FocusSample} containing references to resources matching the lookup.
	 */
	public FocusSample getLookup(String type)
	{
		throw new FocusNotImplementedException("DataManager.getLookup()");
	}

	/**
	 * Retrieve a sample that is stored in the local database (or in the cache), but do not
	 * get it from the network
	 *
	 * @param url the URL of the resource to get
	 * @return a FocusObject
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
		boolean isSpecialUrl = url.startsWith(FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX);

		// store in local database, with the "toPost" flag
		Sample sample = Sample.cloneFromFocusObject(data);
		if (!isSpecialUrl) {
			sample.setToPost(true);
		}

		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			dao.create(sample);
		}
		finally {
			this.databaseAdapter.close();
		}

		// push on the network
		if (isSpecialUrl) {
			return ResourceOperationStatus.SUCCESS;
		}

		if (!this.net.isNetworkAvailable()) {
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

		// network PUT
		if (!this.net.isNetworkAvailable()) {
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
	 *
	 * @deprecated may not be necessary anymore as this will be done by the SyncData cron service -- TODO FIXME
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
	 *
	 * @return true on successful completion, false if the operation could not be performed at all.
	 * @throws FocusMissingResourceException if any of the data could not FIXME check why we have this exception. I should catch it and return false?
	 */
	public boolean syncData() throws FocusMissingResourceException
	{
		if (!this.isApplicationReady()) {
			return false;
		}

		if (!this.net.isNetworkAvailable()) {
			return false;
		}

		this.pushLocalModifications(); // FIXME this one returns the FocusMissingResourceException
		this.rebuildApplicationData();
		return true;
	}

	/**
	 * Push local modifications to the network
	 *
	 * @throws FocusMissingResourceException FIXME complete documentation
	 */
	private void pushLocalModifications() throws FocusMissingResourceException
	{
		// POST
		String[] toPostUrls;
		int reportFailure = NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS; // report_failure is a bit mask
		try {
			SampleDao dao = this.databaseAdapter.getSampleDao();
			toPostUrls = dao.getAllMarkedForPost();
			for (String url : toPostUrls) {
				FocusObject fo = this.getLocalObject(url); // FIXME opens a dao by itself, so 2 dao acquired, which may not be good and we close the db in this method and in the called method
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
						reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
					}
				}
				catch (IOException e) {
					reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
				}
			}

			// PUT
			String[] toPutUrls;
			toPutUrls = dao.getAllMarkedForPut();
			for (String url : toPutUrls) {
				FocusObject fo = this.getLocalObject(url); // FIXME opens a dao by itself, so 2 dao acquired, which may not be good and we close the db in this method and in the called method
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
						reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
					}
				}
				catch (IOException e) {
					reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
				}
			}

			// DELETE
			String[] toDeleteUrls;
			toDeleteUrls = dao.getAllMarkedForDeletion();
			for (String url : toDeleteUrls) {
				try {
					if (this.net.delete(url).isSuccessful()) {
						dao.delete(url);
					}
					else {
						reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NON_SUCCESSFUL_RESPONSE;
					}
				}
				catch (IOException e) {
					reportFailure |= NetworkManager.NETWORK_REQUEST_STATUS_NETWORK_FAILURE;
				}
			}
		}
		finally {
			this.databaseAdapter.close();
		}
		if (reportFailure != NetworkManager.NETWORK_REQUEST_STATUS_SUCCESS) {
			throw new FocusMissingResourceException("Cannot retrieve resource - code 0x" + reportFailure);
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
	 * Get the GSON object used for data conversion in our app
	 *
	 * @return a pointer to the GSON object, which is already properly configured for our
	 * application case.
	 */
	public Gson getGson()
	{
		return this.gson;
	}

	/**
	 * Get the network manager.
	 *
	 * @return The current NetworkManager.
	 */
	public NetworkManager getNetworkManager()
	{
		return this.net;
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
}

