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

package eu.focusnet.focus_mobile;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import eu.focusnet.focus_mobile.exception.FocusInternalErrorException;
import eu.focusnet.focus_mobile.exception.FocusMissingResourceException;
import eu.focusnet.focus_mobile.model.internal.AppContentInstance;
import eu.focusnet.focus_mobile.model.json.AppContentTemplate;
import eu.focusnet.focus_mobile.service.network.NetworkManager;
import eu.focusnet.focus_mobile.service.CronService;
import eu.focusnet.focus_mobile.service.DataManager;
import eu.focusnet.focus_mobile.service.UserManager;
import eu.focusnet.focus_mobile.ui.adapter.DateTypeAdapter;
import eu.focusnet.focus_mobile.util.ApplicationHelper;

// Singleton (the only one)

/**
 * singleton : only static methods if the operation affects the current state of the app. E.g. triggerSync, replaceDataManager(), getAppStatus, etc.
 * <p/>
 * hence all methods called from UI are static or via getInstance
 */
public class FocusAppLogic
{
	/**
	 * singleton instance
	 */
	private static FocusAppLogic instance = new FocusAppLogic();

	/**
	 * The GSON object with which we do all our Java Object &lt;-&gt; JSON transformations
	 */
	private Gson gson;
	private DataManager dataManager;
	private Context context;
	private UserManager userManager;
	private CronService cronService;
	private boolean cronBound = false;
	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection cronServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			// We've bound to LocalService, cast the IBinder and get CronService instance
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
	 * The {@link AppContentInstance} is the instanciation of the {@link AppContentTemplate}
	 * referenced above. It means that the template will be evaluated and expanded with actual data.
	 */
	private AppContentInstance currentApplicationContent;

	/**
	 * Singleton constructor
	 */
	private FocusAppLogic()
	{

	}

	public static FocusAppLogic getInstance()
	{
		return instance;
	}

	/**
	 * Get the GSON object used for data conversion in our app
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


	public static DataManager getDataManager()
	{
		return instance.dataManager;
	}


	/**
	 * We need the context for some static method operations, such as NetworkManager.isNetworkAvailable()
	 *
	 * @return the current ApplicationContext
	 */
	public static Context getApplicationContext()
	{
		return instance.context;
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

	public static UserManager getUserManager()
	{
		return instance.userManager;
	}

	/**
	 * Initialize app logic.
	 */
	public void init(Context context)
	{
		this.context = context;

		// setup GSON
		this.gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();

		// setup DataManager
		this.dataManager = new DataManager();
		// if at first run there is already something in the database, then we should use it
		// and not overwrite it by refetching data.
		// FIXME runs on main thread, warnings in logs
		// D/StrictMode: StrictMode policy violation; ~duration=147 ms: android.os.StrictMode$StrictModeDiskReadViolation: policy=31 violation=2
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
	 * do the full login.
	 * <p/>
	 * Get the 3 basic objects, and then instantiate the Application content as a
	 * {@link AppContentInstance}, based on the Application template ({@link AppContentTemplate}).
	 *
	 * @throws FocusMissingResourceException If any of the involved resources could not be found
	 */
	public void acquireApplicationData() throws FocusMissingResourceException
	{
		// Get data from access control handler.
		this.userManager.getUserData();

		this.currentApplicationContent = this.dataManager.retrieveApplicationData();
		if (this.currentApplicationContent != null) {
			this.applicationReady = true;
		}
		else {
			this.userManager.handleLogout();
			this.dataManager.handleLogout();
		}

		this.dataManager.onApplicationLoad(this.applicationReady);
		this.userManager.onApplicationLoad(this.applicationReady);
		if (cronBound) {
			this.cronService.onApplicationLoad(this.applicationReady);
		}

		this.dataManager.freeMemory();
	}

	/**
	 * We do a local copy of the whole initialization procedure and then copy back into
	 * the real object in case of success
	 *
	 * @throws FocusMissingResourceException if any error happens
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
			throw new FocusMissingResourceException("Cannot retrieve user object when syncing data");
		}

		// throws FocusMissingResourceException if any error
		AppContentInstance newApplicationContent = newDataManager.retrieveApplicationData();

		if (newApplicationContent != null) {
			// advertise that we are ready to our new objects
			newUserManager.onApplicationLoad(true);
			newDataManager.onApplicationLoad(true);

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
	 * Reset whole app (delete everything)
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

		// any other service/memory eater?
	}
}
