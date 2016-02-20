package eu.focusnet.app;

import android.app.Application;
import android.content.Intent;
import android.os.StrictMode;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import eu.focusnet.app.service.CronService;
import eu.focusnet.app.service.DataManager;

/**
 * ACRA configuration
 */
@ReportsCrashes(
		// formKey = "",
		formUri = "https://focus.cloudant.com/acra-focus-app/_design/acra-storage/_update/report",
		reportType = org.acra.sender.HttpSender.Type.JSON,
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
		formUriBasicAuthLogin = "felicalliestedisfallatur",
		formUriBasicAuthPassword = "55b877fb01347d01d9dec53b54fa33e107d16b9e",
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
 * FIXME ideally we would start/stop the sync service in the Application, but there is no onResume() or onPause() at the Application level
 *
 * FIXME TODO review all methods and 'synchronized' them if necessary, but only if necessary.
 */
public class FocusApplication extends Application
{

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
	 * On Application creation, do initialize the DataManager and ACRA
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();

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
		DataManager dm = DataManager.getInstance();
		dm.init(this.getApplicationContext());

		// start the CronService
		// FIXME TODO YANDY: I moved the service creation here because it makes more sense (Service is relevant for whole app)
		// but is that a problem regarding sleep/wake? we could detect the sleeping into the service itself (?)
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

		DataManager.getInstance().freeMemory();
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

		DataManager.getInstance().freeMemory();
	}
}
