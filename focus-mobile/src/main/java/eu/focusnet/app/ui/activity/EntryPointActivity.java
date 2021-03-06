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

package eu.focusnet.app.ui.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import eu.focusnet.app.BuildConfig;
import eu.focusnet.app.R;
import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.controller.UserManager;
import eu.focusnet.app.ui.common.FocusDialogBuilder;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * Entry point activity
 * <p/>
 * Start screen of the application. If the application is not configured, yet, the user is
 * redirected to the {@link LoginActivity} (or {@link DemoUseCaseSelectionActivity} as long as
 * we are working with a prototype of the application). If the application is configured, then
 * the basic application data are loaded and the user is then redirected to the
 * {@link }ProjectsListingActivity}.
 */
public class EntryPointActivity extends Activity
{

	/**
	 * Instantiate the activity by defining the UI and launching the background task responsible
	 * for initializing the application.
	 *
	 * @param savedInstanceState Inherited.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_point);

		// Give more information about the currently pending operation
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String loadingText = (String) extras.get(Constant.Extra.UI_EXTRA_LOADING_INFO_TEXT);
			if (loadingText != null) {
				TextView loadInfo = (TextView) findViewById(R.id.splashscreen_loading_info);
				loadInfo.setText(loadingText);
			}
		}

		TextView version = (TextView) findViewById(R.id.splashscreen_version_number);
		version.setText(BuildConfig.VERSION_NAME);

		// Initialize the application
		new InitTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	/**
	 * This class is used for testing if the given credential are correct
	 */
	private class InitTask extends AsyncTask<Void, Void, Void>
	{
		/**
		 * Current Android context.
		 */
		private Context context;

		/**
		 * The splash screen must be displayed at least until this epoch
		 */
		private long runUntil;

		/**
		 * Tells whether we must display the remediation dialog or not (i.e. if there has been
		 * a network error or a missing resource).
		 */
		private boolean remediationDialog;

		/**
		 * Tells whether the task is finished or not.
		 */
		private boolean finished;

		/**
		 * Tells whether we should try again, depending on what the user has decided with the
		 * remediation dialog.
		 */
		private boolean tryAgain;

		/**
		 * Constructor.
		 *
		 * @param context Input value for instance variable.
		 */
		public InitTask(Context context)
		{
			this.context = context;
			this.remediationDialog = false;
			this.finished = false;
			this.tryAgain = false;
		}

		/**
		 * Set the minimum duration of displaying of the splash screen.
		 */
		@Override
		protected void onPreExecute()
		{
			this.runUntil = System.currentTimeMillis() + Constant.AppConfig.SPLASHSCREEN_MINIMUM_DISPLAY_DURATION_IN_MILLISECONDS;
		}

		/**
		 * Do application initialization.
		 *
		 * @param data Nothing
		 * @return Nothing
		 */
		@Override
		protected Void doInBackground(Void... data)
		{
			UserManager accessControl = FocusAppLogic.getUserManager();
			accessControl.quickLoginInfoAcquisition();
			if (accessControl.isLoggedIn()) {
				// acquire CPU lock such that the operation will be done until the end even if the device
				// enters sleep mode
				PowerManager powerManager = (PowerManager) ApplicationHelper.getSystemService(Service.POWER_SERVICE);
				PowerManager.WakeLock wakeLock = powerManager.newWakeLock(
						PowerManager.PARTIAL_WAKE_LOCK,
						Constant.AppConfig.CRON_WAKE_LOCK_NAME
				);
				wakeLock.acquire();
				try {
					FocusAppLogic.getInstance().acquireApplicationData();
				}
				catch (FocusMissingResourceException ex) {
					// this may occur when no data has been previously loaded even though the login information are available
					// (e.g. no network, and the app content has not been previously loaded -> User + UserPreferences are created, but the app content
					// still must be loaded. )
					// if this is the case, we mark the current instance for it to display a remediation dialog in the UI
					// This remediation dialog will ask the user to connect to the network.
					this.remediationDialog = true;
					// leave this.finished to false
					return null;
				}
				finally {
					// release the wakelock
					wakeLock.release();
				}
			}

			this.finished = true;
			return null;
		}

		/**
		 * After execution, wait until the minimum amount of time to display the splash screen
		 * and if appropriate, display the remediation dialog. If everything went fine, redirect to
		 * {@link ProjectsListingActivity}. If the application could not be initialized, redirect
		 * to the login activity.
		 *
		 * @param v Nothing
		 */
		@Override
		protected void onPostExecute(Void v)
		{
			// sleep() is blocking, so let's create a new thread
			final Thread waitAndRedirect = new Thread()
			{
				public void run()
				{
					try {
						while (System.currentTimeMillis() < runUntil || !finished) {
							sleep(500);
						}
						// we either redirect to the login page or the projects listing
						if (!tryAgain) {
							if (FocusAppLogic.getUserManager().isLoggedIn()) {
								ApplicationHelper.changeLanguage(FocusAppLogic.getCurrentApplicationContent().getLanguage());
								startActivity(new Intent(EntryPointActivity.this, ProjectsListingActivity.class));
							}
							else {
								startActivity(new Intent(EntryPointActivity.this, Constant.AppConfig.LOGIN_ACTIVITY));
							}
							finish();
						}
					}
					catch (InterruptedException ex) {
						// empty, but that's ok. Ignore failure.
						finish();
					}
					// restart this activity if necessary
					if (tryAgain) {
						finish();
						startActivity(getIntent());
					}
				}
			};
			waitAndRedirect.start();

			// display the remediation dialog if necessary
			if (this.remediationDialog) {
				LayoutInflater inflater = LayoutInflater.from(context);
				// FIXME define a better Context than null. this.context? To be tested.
				TextView dialogContent = (TextView) inflater.inflate(R.layout.dialog_content_simpletext, null);
				dialogContent.setText(getString(R.string.connected_to_web));

				FocusDialogBuilder builder = new FocusDialogBuilder(this.context)
						.setTitle(getString(R.string.fail_load_content_title))
						.insertContent(dialogContent)
						.removeNegativeButton()
						.removeNeutralButton()
						.setCancelable(false)
						.setPositiveButtonText(getString(R.string.try_again));
				final AlertDialog dialog = builder.create();
				builder.getPositiveButton().setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						finished = true;
						tryAgain = true;
						// force reloading of activity on next iteration of the waiting thread(
						runUntil = System.currentTimeMillis() - Constant.AppConfig.SPLASHSCREEN_MINIMUM_DISPLAY_DURATION_IN_MILLISECONDS;
						dialog.dismiss();
					}
				});
				dialog.show();
			}
			else {
				this.finished = true;
			}

		}
	}

}