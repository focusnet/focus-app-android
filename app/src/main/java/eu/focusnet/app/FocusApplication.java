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

package eu.focusnet.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.view.MenuItem;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.network.NetworkManager;
import eu.focusnet.app.service.CronService;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.util.FocusApplicationActivityLifecycleHandler;
import eu.focusnet.app.util.ConfigurationHelper;

/**
 * FOCUS Application
 * <p/>
 * This is the entry point of our application and direct subclass of the default Android
 * Application.
 * <p/>
 * In this class, we override the default behavior by:
 * - Defining a custom error reporting system (ACRA)
 * - Defining a custom Activity handler used to
 * <p/>
 * This class also:
 * - provides most application-wide instances, and/or methods for accesing them. It is a Singleton
 * and therefore allows us to refer to these instances efficiently. In fact many single-instance
 * objects are stored in the DataManager, but this class contains helper functions that allow
 * accessing these objects more quickly as it is the only Singleton of the application.
 * <p/>
 * FIXME review all methods and set synchronize / volatile when appropriate
 * FIXME ACRA make sure it works in production. I noticed that sometimes the interactive dialog is not shown emulator (real-time issue?) / Probably only when debugger is attached.
 */
public class FocusApplication extends Application
{
	/**
	 * ACRA configuration keys, from the focus.properties file
	 */
	private static final String PROPERTY_ACRA_FORM_URI = "acra.form-uri",
			PROPERTY_ACRA_USERNAME = "acra.username",
			PROPERTY_ACRA_PASSWORD = "acra.password";

	private static FocusApplication instance;
	private DataManager dataManager;
	private FocusApplicationActivityLifecycleHandler activityHandler;
	private Thread.UncaughtExceptionHandler originalUncaughtExceptionHandler;

	/**
	 * Acquire the Singleton instance
	 *
	 * @return the Singleton instance
	 */
	public static FocusApplication getInstance()
	{
		return instance;
	}

	/**
	 * This function triggers a silent ACRA report to the reporting server.
	 * <p/>
	 * This is used when we think we have somehow end up in a strange/crashed state, but we
	 * were still able to recover. We however want to inspect the reasons of this state and
	 * therefore expect to receive a bug report.
	 * <p/>
	 * This also allows us to send reports even if no exception is triggered. We may for example
	 * monitor the application performance and send a report if it is too slow, for knowing what
	 * happens.
	 * <p/>
	 * FIXME the report may contain sensitive information. It should be anonymized.
	 *
	 * @param e The exception to be transmitted on the remote reporting server.
	 */
	public static void reportError(Exception e)
	{
		if (!BuildConfig.DEBUG) {
			ACRA.getErrorReporter().handleSilentException(e);
		}
		else {
			e.printStackTrace();
		}
	}

	/**
	 * Application-wide current DataManager
	 *
	 * @return the current DataManager for the application
	 */
	public DataManager getDataManager()
	{
		return this.dataManager;
	}

	/**
	 * Attach the base Context
	 * <p/>
	 * This implementation does:
	 * - register our Singleton instance
	 * - Enable MultiDex
	 * - Enable ACRA when not in DEBUG build mode
	 * - Register a custom uncaught exception handler
	 *
	 * @param base inherited parameter
	 */
	@Override
	protected void attachBaseContext(Context base)
	{
		super.attachBaseContext(base);

		// Singleton reference saving
		instance = this;

		// Enable MultiDex
		MultiDex.install(this);

		// ACRA init, only in release mode
		if (!BuildConfig.DEBUG) {
			// Get ACRA's configuration parameters from the focus.properties file
			String form_uri;
			String user;
			String pass;
			try {
				form_uri = ConfigurationHelper.getProperty(PROPERTY_ACRA_FORM_URI, this);
				user = ConfigurationHelper.getProperty(PROPERTY_ACRA_USERNAME, this);
				pass = ConfigurationHelper.getProperty(PROPERTY_ACRA_PASSWORD, this);
			}
			catch (IOException ex) {
				throw new FocusInternalErrorException("Cannot get property in focus.properties");
			}

			// fully programmatic ACRA configuration (no annotation)
			try {
				final ACRAConfiguration config = new ConfigurationBuilder(this)
						.setFormUri(form_uri)
						.setFormUriBasicAuthLogin(user)
						.setFormUriBasicAuthPassword(pass)
						.setReportType(org.acra.sender.HttpSender.Type.JSON)
						.setHttpMethod(org.acra.sender.HttpSender.Method.PUT)
						.setReportingInteractionMode(ReportingInteractionMode.DIALOG)
						.setResToastText(R.string.focus_crash_toast_text)
						.setResDialogText(R.string.focus_crash_dialog_text)
						.setResDialogTitle(R.string.focus_crash_dialog_title)
						.setResDialogCommentPrompt(R.string.focus_crash_dialog_comment_prompt)
						.setResDialogOkToast(R.string.focus_crash_dialog_ok_toast)
						.build();
				ACRA.init(this, config);
			}
			catch (ACRAConfigurationException e) {
				e.printStackTrace();
			}
		}


		/*
		 * We create a custom uncaught exception handler that will delete any local data if any
		 * exception is not caught.
		 *
		 * We enforce this such that the application does not end up in a strange state from which we
		 * cannot recover without updating. By doing this, we can assume that on next application
		 * execution, it will download data from the server(s) again and hopefully things will be
		 * better. If this is not the case, it is easier to alter data on the server than on the client
		 * anyway.
		 *
		 * But it will also call the ACRA handler for nice bug reports, so let's save it first
		 * and then setup this new handler
		 */
		this.originalUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler()
		{
			@Override
			public void uncaughtException(Thread thread, Throwable ex)
			{
				if (FocusApplication.getInstance() != null && FocusApplication.getInstance().getDataManager() != null) {
					FocusApplication.getInstance().getDataManager().logout();
				}

				if (BuildConfig.DEBUG) {
					ex.printStackTrace();
				}

				// The original handler is in fact ACRA's one, which ha been configured in onAttachBaseContext()
				originalUncaughtExceptionHandler.uncaughtException(thread, ex);
			}
		};
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	/**
	 * Perform operations when creating the Application
	 * <p/>
	 * This is called after attachBaseContext(). We do the following:
	 * - Register a custom activity handler that will help us keep track of the currently active
	 * Activity
	 * - Enable debugging checks in DEBUG build mode
	 * - Setup our DataManager and CronService
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();

		this.activityHandler = new FocusApplicationActivityLifecycleHandler();
		this.registerActivityLifecycleCallbacks(activityHandler);

		// Safety checks in DEBUG mode
		if (BuildConfig.DEBUG) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectAll()
					.penaltyLog()
					.build());
		}

		// setup DataManager
		this.dataManager = new DataManager();

		// start the CronService
		// We should not start the service if the current proces is ACRA's one.
		// https://github.com/ACRA/acra/wiki/BasicSetup
		if (!ACRA.isACRASenderServiceProcess()) {
			this.startService(new Intent(this, CronService.class));
		}
	}

	/**
	 * Replace the current data manager with a new one. The old one will therefore be
	 * garbage collected. This is part of the data synchronization procedure.
	 *
	 * @param new_dm A new DataManager
	 * @see CronService The periodic tasks runner
	 * @see eu.focusnet.app.ui.activity.ProjectsListingActivity#onOptionsItemSelected(MenuItem)
	 * Where the synchronization task can be manually triggered
	 */
	public void replaceDataManager(DataManager new_dm)
	{
		this.dataManager = new_dm;
	}

	/**
	 * Use the custom FocusApplicationActivityLifecycleHandler to restart the current activity.
	 */
	public void restartCurrentActivity()
	{
		this.activityHandler.restartCurrentActivity();
	}

	/**
	 * Retrieve the NetworkManager
	 *
	 * @return the current unique NetworkManager of the application
	 */
	public NetworkManager getNetworkManager()
	{
		return this.getDataManager().getNetworkManager();
	}

	/**
	 * Explictly free memory when requested to do so.
	 *
	 * @param level inherited
	 */
	@Override
	public void onTrimMemory(int level)
	{
		super.onTrimMemory(level);
		this.dataManager.freeMemory();
	}

	/**
	 * Explicitly free memory when requested to do so.
	 */
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		this.dataManager.freeMemory();
	}


}
