package eu.focusnet.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


import eu.focusnet.app.R;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.manager.DataManager;

/**
 * Entry point activity
 * <p/>
 * Start screen of the application. If the application is not configured, yet, he is redirected
 * to the LoginActivity. If the application is configured, then the basic application data are
 * loaded and the user is then redirected to the FocusActivity.
 *
 */
public class EntryPointActivity extends Activity
{
	private static final String TAG = EntryPointActivity.class.getName();

	private Context context = null;

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
		this.context = this.getApplicationContext();

		/*
		 * If we don't have login information, yet, let's redirect to the LoginActivity.
		 * Otherwise, load the basic application configuration and redirect to FocusActivity
		 *
		 * The Context must be set once.
		 */
		DataManager dm = DataManager.getInstance();
		dm.setContext(this.getApplicationContext());

		//Create the database
		//TODO this is only for test purpose. This call should be done somewhere else
		DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplication());
		databaseAdapter.openWritableDatabase();
		//End

		if (dm.hasLoginInformation()) {
			/*
			 * Do everything in a different Thread
			 */
			final Thread splashScreen = new Thread()
			{
				public void run()
				{
					try {

						sleep(2000); // FIXME TODO sleep for at least 2seconds, even if below tasks are already done

						// FIXME TODO  display a progress wheel when we are executing this

						DataManager dm = DataManager.getInstance();
						dm.retrieveApplicationData();
						startActivity(new Intent(EntryPointActivity.this, FocusActivity.class));
					}
					catch (InterruptedException e) {
						e.printStackTrace();
					}
					finally {
						finish();
					}
				}
			};

			splashScreen.start();
		}
		else {
			startActivity(new Intent(EntryPointActivity.this, LoginActivity.class));
			finish();
		}

	}
}