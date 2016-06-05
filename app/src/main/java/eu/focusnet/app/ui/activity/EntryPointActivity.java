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

package eu.focusnet.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import eu.focusnet.app.BuildConfig;
import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.service.DataManager;

/**
 * Entry point activity
 * <p/>
 * Start screen of the application. If the application is not configured, yet, he is redirected
 * to the LoginActivity. If the application is configured, then the basic application data are
 * loaded and the user is then redirected to the ProjectsListingActivity.
 * <p/>
 * TODO consider setting up AppIndex API
 * <p/>
 * FIXME why did Yandy alter the logic -> to display progress dialog.
 */
public class EntryPointActivity extends Activity
{
	/**
	 * Instantiate the activity.
	 *
	 * @param savedInstanceState past Activity state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_entry_point);

		TextView version = (TextView) findViewById(R.id.versionNumber);
		version.setText(BuildConfig.VERSION_NAME);

		final Thread init_thread = new Thread()
		{
			public void run()
			{
				DataManager dm = FocusApplication.getInstance().getDataManager();
				dm.init();
				if (dm.hasLoginInformation()) {
					try {
						dm.retrieveApplicationData();
					}
					catch (FocusMissingResourceException ex) {
						// this may occur when no data has been previously loaded even though the login information are available
						// (e.g. no network, and the app content has not been previously completely loaded)
						// ViewUtil.displayToast(FocusApplication.getInstance(), R.string.focus_error_no_configuration_available);
						// FIXME TODO custom activity instead (that requests "try again later")

					}
				}
			}
		};



		/*
		 * If we don't have login information, yet, let's redirect to the LoginActivity.
		 * Otherwise, load the basic application configuration and redirect to ProjectsListingActivity
		 */

		// don't do anything while not init();

		/*
			 * This thread will simply sleep for a minimum amount of time, waiting for the
			 * app_acquisition Thread to complete.
			 */
		final Thread splash_screen = new Thread()
		{
			public void run()
			{
				try {
					int min_splashscreen_display_time = 2000;
					sleep(1000);
					min_splashscreen_display_time -= 1000;
					while (init_thread.isAlive() || min_splashscreen_display_time > 0) {
						sleep(500);
						min_splashscreen_display_time -= 500;
					}
					if (FocusApplication.getInstance().getDataManager().hasLoginInformation()) {
						startActivity(new Intent(EntryPointActivity.this, ProjectsListingActivity.class));
					}
					else {
						startActivity(new Intent(EntryPointActivity.this, LoginActivity.class));
					}
				}
				catch (InterruptedException ex) {
					// empty, but that's ok. Ignore failure.
				}
				finally {
					finish();
				}
			}
		};

		// start threads
		init_thread.start();
		splash_screen.start();

	}

}