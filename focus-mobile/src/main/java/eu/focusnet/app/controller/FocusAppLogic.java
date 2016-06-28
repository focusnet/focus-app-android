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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import eu.focusnet.app.model.AppContentInstance;
import eu.focusnet.app.model.gson.AppContentTemplate;
import eu.focusnet.app.model.gson.DateTypeAdapter;
import eu.focusnet.app.ui.FocusApplication;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.util.FocusMissingResourceException;


/**
 * This object is the entry point of the mail application logic. In the MVC paradygm, it would be
 * the Controller.
 * <p/>
 * This class is a Singleton (the only one of the application). We only access static methods
 * when it comes to access or alter the state of the currently running application, e.g. when
 * we want to replace the current {@link DataManager}. Following this logic, the UI always acts on
 * static methods (or accessed via {@link #getInstance}).
 */
public class FocusAppLogic
{
	/**
	 * Singleton instance
	 */
	final private static FocusAppLogic instance = new FocusAppLogic();

	/**
	 * Android's context from which this object has been created.
	 * See {@link FocusApplication}.
	 */
	private Context context;

	/**
	 * The GSON object with which we do all our Java Object &lt;-&gt; JSON transformations
	 */
	private Gson gson;

	/**
	 * The currently active {@link DataManager} for the application.
	 */
	private DataManager dataManager;

	/**
	 * Current {@link UserManager} for the application.
	 */
	private UserManager userManager;

	/**
	 * Current periodic task operator. See {@link CronService}.
	 */
	private CronService cronService;

	/**
	 * Tells whether the {@link CronService} is bound.
	 */
	private boolean cronBound = false;

	/**
	 * Defines callbacks for {@link CronService} binding, passed to {@code bindService()}.
	 */
	private ServiceConnection cronServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			CronService.CronBinder binder = (CronService.CronBinder) service;
			cronService = binder.getService();
			cronBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			cronBound = false;
		}
	};

	/**
	 * Tells whether the application is ready to be used
	 */
	private boolean applicationReady;

	/**
	 * The {@link AppContentInstance} is the model of the application content. It's content is
	 * used to generate the UI and interact with the model (e.g. create, upate, delete, etc.).
	 */
	private AppContentInstance currentApplicationContent;

	/**
	 * Singleton constructor. It is empty, we are only interested in the object reference here.
	 */
	private FocusAppLogic()
	{

	}

	/**
	 * Singleton accessor
	 *
	 * @return A static reference to our Singleton.
	 */
	public static FocusAppLogic getInstance()
	{
		return instance;
	}

	/**
	 * We need the context for some static method operations,
	 * such as {@link ApplicationHelper#getSystemService(String)}}.
	 *
	 * @return the current {@code ApplicationContext}
	 */
	public static Context getApplicationContext()
	{
		return instance.context;
	}

	/**
	 * Get the GSON object used for data conversion in our app.
	 * <p/>
	 * The GSON configuration never changes, so its safe to refer to it statically.
	 *
	 * @return a pointer to the GSON object, which is already properly configured for our
	 * application case.
	 */
	public static Gson getGson()
	{
		return instance.gson;
	}

	/**
	 * Get the current {@link DataManager} of the application.
	 *
	 * @return The current {@link DataManager}.
	 */
	public static DataManager getDataManager()
	{
		return instance.dataManager;
	}

	/**
	 * Get the current {@link UserManager} of the application.
	 *
	 * @return The current {@link UserManager}.
	 */
	public static UserManager getUserManager()
	{
		return instance.userManager;
	}

	/**
	 * Get the application content instance, i.e. the application content with all projects,
	 * pages and widgets and their content being initialized with proper values.
	 *
	 * @return An ApplicationInstance object reflecting the current content of the app
	 */
	public static AppContentInstance getCurrentApplicationContent()
	{
		return instance.currentApplicationContent;
	}

	/**
	 * Initialize app logic.
	 *
	 * @param context Current {@code ApplicationContext}.
	 */
	public void init(Context context)
	{
		this.context = context;

		// setup GSON
		this.gson = new GsonBuilder().serializeNulls().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

		// setup DataManager
		this.dataManager = new DataManager();

		// if at first run there is already something in the database, then we should use it
		// and not overwrite it by refetching data.
		this.dataManager.useExistingDataSet();

		// Access control facility
		this.userManager = new UserManager(this.dataManager);

		// bind to cron service
		// bind cron service
		Intent intent = new Intent(this.context, CronService.class);
		this.context.bindService(intent, this.cronServiceConnection, Context.BIND_AUTO_CREATE);

		// we are not fully ready, yet.
		this.applicationReady = false;
	}

	/**
	 * Do the full login.
	 * <p/>
	 * Get the 3 basic objects
	 * ({@link eu.focusnet.app.model.gson.User},
	 * {@link eu.focusnet.app.model.gson.UserPreferences},
	 * {@link AppContentTemplate}),
	 * and then instantiate the Application content as a
	 * {@link AppContentInstance}, based on the Application template ({@link AppContentTemplate}).
	 *
	 * @throws FocusMissingResourceException If any of the involved resources could not be found.
	 */
	public void acquireApplicationData() throws FocusMissingResourceException
	{
		// Get data from access control handler.
		this.userManager.getUserData();

		// Try to create the app content instance
		this.currentApplicationContent = this.dataManager.retrieveApplicationData();

		// if success, do set the current application content instance. Otherwise, announce logout
		if (this.currentApplicationContent != null) {
			this.applicationReady = true;
		}
		else {
			this.userManager.handleLogout();
			this.dataManager.handleLogout();
		}

		this.dataManager.onChangeStatus(this.applicationReady);
		this.userManager.onChangeStatus(this.applicationReady);
		if (cronBound) {
			this.cronService.onChangeStatus(this.applicationReady);
		}

		// be nice and willingly free memory that we don't need anymore
		this.dataManager.freeMemory();
	}

	/**
	 * Data synchronization: we do a local copy of the whole initialization procedure and then
	 * register the reference of the new object as the current object in case of success.
	 *
	 * @throws FocusMissingResourceException if any error happens.
	 */
	public void sync() throws FocusMissingResourceException
	{
		if (!this.applicationReady) {
			return;
		}

		if (!NetworkManager.isNetworkAvailable()) {
			return;
		}

		// first, push pending modifications
		// if an error occurs, FocusMissingResourceException is thrown by these methods,
		// and this will be propagated to the caller
		// So no need for error handling here.
		this.userManager.pushPendingocalUserData();
		this.dataManager.pushPendingLocalModifications();

		// New objects creation
		DataManager newDataManager = new DataManager();
		UserManager newUserManager = new UserManager(newDataManager);

		// Data acquisition
		// Get login information from SharedPreferences, no need to network-login
		newUserManager.quickLoginInfoAcquisition();
		if (!newUserManager.isLoggedIn()) {
			throw new FocusInternalErrorException("Inconsistent state reached: we should have enough information for logging in at this point.");
		}
		try {
			newUserManager.getUserData();
		}
		catch (FocusInternalErrorException ex) {
			// if we crash, this is because the user objects could not be retrieved and they are mandatory.
			// Let the application survive anyway (we are in a background job, invisible to end user..
			throw new FocusMissingResourceException("Cannot retrieve user object when syncing data", "newUserManager.getUserData()");
		}

		// throws FocusMissingResourceException if any error
		AppContentInstance newApplicationContent = newDataManager.retrieveApplicationData();

		if (newApplicationContent != null) {
			// advertise that we are ready to our new objects
			newUserManager.onChangeStatus(true);
			newDataManager.onChangeStatus(true);

			// everything went fine, then replace the current objects by the new ones
			this.userManager = newUserManager;
			this.dataManager = newDataManager;
			this.currentApplicationContent = newApplicationContent;

			// Also announce change of DataManager to observers, such as access control handler.
			this.userManager.registerNewDataManager(this.dataManager);

			// do some cleaning
			this.dataManager.freeMemory();
			this.dataManager.cleanDataStore();
		}
	}

	/**
	 * Reset whole app. This deletes everything that is locally stored.
	 * <p/>
	 * Note: this method called when crashing, so bugs in there won't be reportedby ACRA. Pay extra attention to it.
	 */
	public void reset()
	{
		this.userManager.handleLogout();
		this.dataManager.handleLogout();
		if (this.cronBound) {
			this.cronService.handleLogout();
		}

		ApplicationHelper.resetLanguage();
	}

	/**
	 * Recycle memory
	 */
	public void freeMemory()
	{
		this.dataManager.freeMemory();
	}
}
