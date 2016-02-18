package eu.focusnet.app;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

// Configure ACRA
@ReportsCrashes(
		// formKey = "",
		formUri = "https://focus.cloudant.com/acra-focus-app/_design/acra-storage/_update/report",
		reportType = org.acra.sender.HttpSender.Type.JSON,
		httpMethod = org.acra.sender.HttpSender.Method.PUT,
		formUriBasicAuthLogin="felicalliestedisfallatur",
		formUriBasicAuthPassword="55b877fb01347d01d9dec53b54fa33e107d16b9e",
		// mailTo = "julien.kuenzi@bfh.ch",
		mode = ReportingInteractionMode.DIALOG,
		resToastText = R.string.focus_crash_toast_text,
		resDialogText = R.string.focus_crash_dialog_text,
		resDialogTitle = R.string.focus_crash_dialog_title,
		resDialogCommentPrompt = R.string.focus_crash_dialog_comment_prompt,
		resDialogOkToast = R.string.focus_crash_dialog_ok_toast
)

/**
 * FOCUS Application
 */
public class FocusApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		ACRA.init(this);
	}
}
