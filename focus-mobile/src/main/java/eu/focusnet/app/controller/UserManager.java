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

package eu.focusnet.app.controller;

import java.io.IOException;
import java.util.HashMap;

import eu.focusnet.app.R;
import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.util.FocusMissingResourceException;
import eu.focusnet.app.util.FocusNotImplementedException;
import eu.focusnet.app.model.UserInstance;
import eu.focusnet.app.model.UserPreferencesInstance;
import eu.focusnet.app.model.gson.AppContentTemplate;
import eu.focusnet.app.model.gson.User;
import eu.focusnet.app.model.gson.UserPreferences;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

public class UserManager implements ApplicationStatusObserver
{

	private DataManager dataManager;
	private UserInstance user;
	private UserPreferencesInstance userPreferences;
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
	 * URI to resource holding application template.
	 */
	private String appContentUrl;


	private boolean loggedIn;
	private boolean applicationReady;
	private String userIdentification;

	public UserManager(DataManager dm)
	{
		this.dataManager = dm;
		this.loggedIn = false;
	}


	/**
	 * Try to login without network, just by looking at existin glogin information in shared prefs.
	 */
	public void quickLoginInfoAcquisition()
	{
		HashMap<String, String> prefs = ApplicationHelper.getPreferences();

		this.loginUser = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_USERNAME);
		this.loginPassword = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_PASSWORD);
		this.loginServer = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_SERVER);
		this.userUrl = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_USER_INFOS);
		this.prefUrl = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_APPLICATION_SETTINGS);
		this.demoUseCase = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_DEMO_USE_CASE);
		this.appContentUrl = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_APPLICATION_CONTENT);

		// no need to test all of them
		if (this.appContentUrl != null) {
			this.loggedIn = true;
		}
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
		if (!NetworkManager.isNetworkAvailable()) {
			throw new IOException("No network");
		}

		// do network login
//		boolean loginResult = this.net.login(user, password, server); FIXME solve this when network login will be implemented
		// we could pass the NetworkManager object, or create a new NetworkManager for that purpose. To be checked.
		boolean loginResult = false;
		if (!loginResult) { // 403 error
			ApplicationHelper.resetPreferences();
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
		HashMap<String, String> prefs = new HashMap<>();
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_USERNAME, this.loginUser);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_PASSWORD, this.loginPassword);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_SERVER, this.loginServer);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_USER_INFOS, this.userUrl);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_APPLICATION_SETTINGS, this.prefUrl);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_APPLICATION_CONTENT, this.appContentUrl);
		ApplicationHelper.savePreferences(prefs);

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
		if (!NetworkManager.isNetworkAvailable()) {
			throw new IOException("No network");
		}

		// Demo URIs:
		// - one Use URI per user
		// - one UserPreferences URI per user x use case combination
		// - one AppContentTemplate per use case
		String userId = "user-" + ApplicationHelper.getAndroidId();

		// no need to set credentials, we won't use them.
		this.loginServer = "";
		this.loginUser = "";
		this.loginPassword = "";

		// find the demo use case idx based on the
		this.demoUseCase = useCase;

		// user and preferences URIs
		String testServer = ApplicationHelper.getProperty(Constant.AppConfig.PROPERTY_TARGET_PERMANENT_STORAGE_SERVER);
		this.userUrl = testServer + "/data/focus-user/" + userId;
		this.prefUrl = testServer + "/data/focus-user/" + userId + "/focus-mobile-app-preferences/" + this.demoUseCase;

		// infer app content URI
		String[] useCasesIds = ApplicationHelper.getResources().getStringArray(R.array.demo_use_cases_values);
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
		String[] uris = ApplicationHelper.getResources().getStringArray(R.array.demo_use_cases_app_content_uris);
		this.appContentUrl = uris[foundIdx];


		// if all ok, save description to local database for later loading
		HashMap<String, String> prefs = new HashMap<>();
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_USERNAME, this.loginUser);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_PASSWORD, this.loginPassword);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_LOGIN_SERVER, this.loginServer);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_USER_INFOS, this.userUrl);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_APPLICATION_SETTINGS, this.prefUrl);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_APPLICATION_CONTENT, this.appContentUrl);
		prefs.put(Constant.SharedPreferences.SHARED_PREFERENCES_DEMO_USE_CASE, this.demoUseCase);
		ApplicationHelper.savePreferences(prefs);

		this.loggedIn = true;
		return true;
	}


	@Override
	public void onApplicationLoad(boolean appStatus)
	{
		this.applicationReady = appStatus;
	}

	public void handleLogout()
	{
		if (this.applicationReady) {
			this.loginUser = null;
			this.loginPassword = null;
			this.loginServer = null;
			this.userUrl = null;
			this.prefUrl = null;
			this.appContentUrl = null;
			this.user = null;
			this.userPreferences = null;
			this.demoUseCase = null;

			// and clear the internal config
			ApplicationHelper.resetPreferences();

			this.loggedIn = false;
			this.applicationReady = false;
		}
	}

	/**
	 * Delete login information and any locally stored data
	 */
	public void logout()
	{
		this.handleLogout();
		this.dataManager.handleLogout(); // in case it needs to do something.
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
	public synchronized User getUser()
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.user != null) {
			return this.user;
		}
		UserInstance user;
		try {
			// UserInstance is child of User, so safe to cast
			user = (UserInstance) this.dataManager.get(this.userUrl, UserInstance.class);
		}
		catch (IOException ex) {
			// we cannot survive without a user
			// this case is triggered if there was network + no user in local db + network outage
			// while retrieving object of interest
			throw new FocusInternalErrorException("Object may exist on the network but network error occurred.");
		}
		if (user == null) {
			// FIXME information not used for now, must still must be valid to pass JSON Schema valiation on the server-side
			user = new UserInstance(this.userUrl, "first", "last", "email@email.com", "company", this); // FIXME all information should come from login step -> only pass UserManager to User()
			DataManager.ResourceOperationStatus ret = this.dataManager.create(user);
			if (ret == DataManager.ResourceOperationStatus.ERROR) {
				throw new FocusInternalErrorException("Cannot create User object.");
			}
		}
		this.user = user;
		return this.user;
	}

	/**
	 * Return the user in its current state, it may be null
	 * <p/>
	 * FIXME this is a little trick because we need the user in the FocusObject initiatilization.
	 * FIXME must fix the logic.
	 *
	 * @return
	 */
	public UserInstance getUserAsIs()
	{
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
	public synchronized UserPreferencesInstance getUserPreferences()
	{
		if (!this.isLoggedIn()) {
			throw new FocusInternalErrorException("No login information. Cannot continue.");
		}

		if (this.userPreferences != null) {
			return this.userPreferences;
		}
		UserPreferencesInstance userPreferences = null;
		try {
			userPreferences = (UserPreferencesInstance) this.dataManager.get(this.prefUrl, UserPreferencesInstance.class);
		}
		catch (IOException ex) {
			// we cannot survive without userpreferences
			// this case is triggered if there was network + no userprerferences in local db + network outage while retrieving object of interest
			throw new FocusInternalErrorException("Object may exist on network but network error occurred");
		}
		if (userPreferences == null) {
			userPreferences = new UserPreferencesInstance(this.prefUrl);
			DataManager.ResourceOperationStatus ret = this.dataManager.create(userPreferences);
			if (ret == DataManager.ResourceOperationStatus.ERROR) {
				throw new FocusInternalErrorException("Cannot create UserPreferences object.");
			}
		}
		this.userPreferences = userPreferences;
		return this.userPreferences;
	}


	/**
	 * Those 2 objects are mandatory, and fatal exception is triggered if sth goes wrong.
	 * <p/>
	 * Access control setup should come here, too (if not already done in login() )
	 * <p/>
	 * FIXME if fail, crash. not very user-friendly. should gracefully fallback to entry point activity for example.
	 */
	public void getUserData()
	{
		this.getUser();
		this.getUserPreferences();
	}

	public void registerNewDataManager(DataManager dataManager)
	{
		this.dataManager = dataManager;
	}

	public void pushPendingocalUserData() throws FocusMissingResourceException
	{
		// Acquire user and preferencee Dao -> how to to do that?
		this.dataManager.pushLocalModification(this.userUrl, User.class);
		this.dataManager.pushLocalModification(this.prefUrl, UserPreferences.class);
	}

	/**
	 * Information obtained from login, such as a user id on the network.
	 *
	 * @return
	 */
	public String getUserIdentification()
	{
		return this.userUrl;
	}
}
