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

package eu.focusnet.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import eu.focusnet.app.service.CronService;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.util.FocusApplicationActivityLifecycleHandler;

/**
 * ACRA configuration
 *
 * FIXME TODO YANDY move credentials / formUri to a properties file, which is not committed to git! and failsafe with defaults
 * see assets/acra.properties
 * we can use the programmatic way of settings params of ACRA if necessary, into onCreate()
 */
@ReportsCrashes(
		formUri = "https://focus.cloudant.com/acra-focus-app/_design/acra-storage/_update/report",
		reportType = org.acra.sender.HttpSender.Type.JSON,
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
		formUriBasicAuthLogin = "USER", // FIXME TODO JULIEN must request API key in cloudant
		formUriBasicAuthPassword = "KEY",
		mode = ReportingInteractionMode.DIALOG,
		resToastText = R.string.focus_crash_toast_text,
		resDialogText = R.string.focus_crash_dialog_text,
		resDialogTitle = R.string.focus_crash_dialog_title,
		resDialogCommentPrompt = R.string.focus_crash_dialog_comment_prompt,
		resDialogOkToast = R.string.focus_crash_dialog_ok_toast
)

/**
 * FOCUS Application
 *
 * FIXME TODO review all methods and 'synchronized' them if necessary, but only if necessary.
 *
 * This is a Singleton, which allows us to access the application context and DataManager from anywhere.
 */
public class FocusApplication extends Application
{

	/**
	 * Static instance variable, hence our Singleton instanciatio
	 */
	private static FocusApplication instance;

	/**
	 * DataManager
	 */
	private DataManager dataManager;
	private FocusApplicationActivityLifecycleHandler activityHandler;

	/**
	 * Acquire the instance of this app
	 *
	 * @return
	 */
	public static FocusApplication getInstance() {
		return instance;
	}

	/**
	 * Retrieve application-wide context
	 *
	 * @return
	 */
	public Context getContext(){
		return instance.getApplicationContext();
	}

	/**
	 * Retrieve the DataManager
	 *
	 * @return
	 */
	public DataManager getDataManager()
	{
		return this.dataManager;
	}

	/**
	 * A helper function that allows reporting uncaught errors via ACRA
	 *
	 * @param e
	 */
	public static final void reportError(Exception e)
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
	 * On Application creation, do initialize the DataManager, CronService and ACRA
	 */
	@Override
	public void onCreate()
	{
		// Singleton reference saving
		instance = this;

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
		// FIXME TODO YANDY: I moved the service creation here because it makes more sense (Service is relevant for whole app)
		// Answer: It is ok, the principal thing is that we start the service only once and stop it when the app is closed or get on the background (this is not done here)
		// but is that a problem regarding sleep/wake? we could detect the sleeping into the service itself (?)
		//Answer: we can detect the sleep/wake with a broadcast receiver
		this.startService(new Intent(this, CronService.class));
		// setup ACRA, only in release mode
		if (!BuildConfig.DEBUG) {
			ACRA.init(this);
		}
	}

	/**
	 * When we are running low on memory, let's recycle what we can
	 */
	@Override
	public void onLowMemory()
	{
		super.onLowMemory();
		this.dataManager.freeMemory();
	}

	/**
	 * When the system requests so, let's recycle what we can
	 *
	 * @param level The higher, the more critical memory recycling is important.
	 */
	@Override
	public void onTrimMemory(int level)
	{
		super.onTrimMemory(level);
		this.dataManager.freeMemory();
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
