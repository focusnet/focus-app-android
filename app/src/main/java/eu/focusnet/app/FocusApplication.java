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

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.config.ACRAConfiguration;
import org.acra.config.ACRAConfigurationException;
import org.acra.config.ConfigurationBuilder;

import java.io.IOException;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.service.CronService;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.util.FocusApplicationActivityLifecycleHandler;
import eu.focusnet.app.ui.util.PropertiesHelper;

/**
 * FOCUS Application
 * <p/>
 * FIXME TODO review all methods and 'synchronized' them if necessary, but only if necessary.
 * <p/>
 * This is a Singleton, which allows us to access the application context and DataManager from anywhere.
 */
public class FocusApplication extends Application
{

	private static final String PROPERTY_ACRA_FORM_URI = "acra.form-uri",
			PROPERTY_ACRA_USERNAME = "acra.username",
			PROPERTY_ACRA_PASSWORD = "acra.password";

	/**
	 * Static instance variable, hence our Singleton instanciatio
	 */
	private static FocusApplication instance;

	/**
	 * DataManager
	 */
	private DataManager dataManager;
	private FocusApplicationActivityLifecycleHandler activityHandler;
	private Thread.UncaughtExceptionHandler originalUncaughtExceptionHandler;
	/**
	 * A custom exception handler that will reset() the app if any exception is not caught.
	 */
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler()
	{
		@Override
		public void uncaughtException(Thread thread, Throwable ex)
		{
			if (FocusApplication.getInstance() != null && FocusApplication.getInstance().getDataManager() != null) {
				FocusApplication.getInstance().getDataManager().reset();
			}

			if (BuildConfig.DEBUG) {
				ex.printStackTrace();
			}
			originalUncaughtExceptionHandler.uncaughtException(thread, ex);
		}
	};

	/**
	 * Acquire the instance of this app
	 *
	 * @return
	 */
	public static FocusApplication getInstance()
	{
		return instance;
	}

	/**
	 * R
	 * helper function that allows reporting uncaught errors via ACRA
	 *
	 * @param e
	 */
	public static void reportError(Exception e)
	{
		// FIXME TODO we should alter the report to remove sensitive information!

		if (!BuildConfig.DEBUG) {
			ACRA.getErrorReporter().handleSilentException(e);
		}
		else {
			e.printStackTrace();
		}
	}

	/**
	 * R
	 * etrieve application-wide context
	 *
	 * @return
	 */
	public Context getContext()
	{
		return instance;
	}

	/**
	 * A
	 * etrieve the DataManager
	 *
	 * @return
	 */
	public DataManager getDataManager()
	{
		return this.dataManager;
	}

	/**
	 * ACRA programmatic initialization.
	 *
	 * @param base
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
			// prepopulated with values set in the annotation
			String form_uri;
			String user;
			String pass;
			try {
				form_uri = PropertiesHelper.getProperty(PROPERTY_ACRA_FORM_URI, this.getContext());
				user = PropertiesHelper.getProperty(PROPERTY_ACRA_USERNAME, this.getContext());
				pass = PropertiesHelper.getProperty(PROPERTY_ACRA_PASSWORD, this.getContext());
			}
			catch (IOException ex) {
				throw new FocusInternalErrorException("Cannot get property in focus.properties");
			}

			try {
				// ACRA configuration is fully programmatic, no annotation
				// so everything at the same location
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

		// We create a custom handler that will clean the application state
		// but it will also call the ACRA handler for nice bug reports
		this.originalUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	/**
	 * On Application creation, do initialize the DataManager, CronService and ACRA
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
	 * garbage collected.
	 *
	 * @param new_dm
	 */
	public void replaceDataManager(DataManager new_dm)
	{
		this.dataManager = new_dm;
	}

	/**
	 * Use the registered FocusApplicationActivityLifecycleHandler to restart the current activity.
	 */
	public void restartCurrentActivity()
	{
		this.activityHandler.restartCurrentActivity();
	}

}
