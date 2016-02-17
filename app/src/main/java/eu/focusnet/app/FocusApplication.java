package eu.focusnet.app;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

// Configure ACRA
@ReportsCrashes(
		// formUri = "http://yourserver.com/yourscript", // FIXME TODO we would need to setup a backend, which may not be that difficult
		mailTo = "julien.kuenzi@bfh.ch",
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
