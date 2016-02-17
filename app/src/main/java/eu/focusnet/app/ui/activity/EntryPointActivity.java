package eu.focusnet.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import eu.focusnet.app.R;
import eu.focusnet.app.service.DataManager;

/**
 * Entry point activity
 * <p/>
 * Start screen of the application. If the application is not configured, yet, he is redirected
 * to the LoginActivity. If the application is configured, then the basic application data are
 * loaded and the user is then redirected to the FocusActivity.
 */
public class EntryPointActivity extends Activity
{
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	// FIXME TODO YANDY: AppIndexing - is that useful? if so, let's do it properly.
	// private GoogleApiClient client;

	/**
	 * Instantiate the activity.
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		/*
		 * If we don't have login information, yet, let's redirect to the LoginActivity.
		 * Otherwise, load the basic application configuration and redirect to FocusActivity
		 *
		 * The Context must be set once.
		 */
		DataManager dm = DataManager.getInstance();
		dm.init(this.getApplicationContext());

		if (dm.hasLoginInformation()) {

			/*
			 * This thread will retrieve the application configuration.
			 */
			final Thread app_acquisition = new Thread()
			{
				public void run()
				{
					DataManager dm = DataManager.getInstance();
					dm.retrieveApplicationData();
				}
			};

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
						while (app_acquisition.isAlive() || min_splashscreen_display_time > 0) {
							sleep(500);
							min_splashscreen_display_time -= 500;
						}
						startActivity(new Intent(EntryPointActivity.this, FocusActivity.class));
					}
					catch (InterruptedException ex) {
					}
					finally {
						finish();
					}

				}
			};

			// start threads
			splash_screen.start();
			app_acquisition.start();

		}
		else {
			startActivity(new Intent(EntryPointActivity.this, LoginActivity.class));
			finish();
		}

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		// FIXME TODO YANDY: AppIndexing - is that useful? if so, let's do it properly.
		//	client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

/*
	@Override
	public void onStart()
	{
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"EntryPoint Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://eu.focusnet.app.ui.activity/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop()
	{
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"EntryPoint Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app deep link URI is correct.
				Uri.parse("android-app://eu.focusnet.app.ui.activity/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
	*/
}