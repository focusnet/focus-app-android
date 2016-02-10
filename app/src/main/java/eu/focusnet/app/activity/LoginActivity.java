package eu.focusnet.app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import eu.focusnet.app.R;
import eu.focusnet.app.manager.DataManager;

/**
 * Login Activity: this activity displays the login screen
 * and log the user in the application
 */
public class LoginActivity extends Activity
{

	private static final String TAG = LoginActivity.class.getName();
	private Context context = null;

	/**
	 * Instantiate the activity UI
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		this.context = this.getApplicationContext();

		// FIXME TODO: if no network then disable SUBMIT button and mention the problem
		// to the end user. adapt Activity such that it changes depending on connectivity:
		// http://developer.android.com/training/basics/network-ops/managing.html
	}

	/**
	 * When the user clicks the login button, the provided username, password and server name
	 * are used to authenticate.
	 * <p/>
	 * This logic requires a network connection.
	 */
	public void onClick(View view)
	{
		// network calls -> run in another Thread
		Thread loginThread = new Thread()
		{
			public void run()
			{

				// FIXME TODO progress indicator? disable all controls?

				DataManager dm = DataManager.getInstance();
				try {

					String username = ((EditText) findViewById(R.id.login_username_textView)).getText().toString();
					String password = ((EditText) findViewById(R.id.login_password_textView)).getText().toString();
					// TODO also a field for server name
					String server = "server";

					if (dm.login(username, password, server)) {
						Intent i = new Intent(LoginActivity.this, EntryPointActivity.class);
						startActivity(i);
						finish();
					}
					else {
						// TODO
						// adapt current activity to highlight the fact that the login failed.
						// e.g. "try again" message
					}
				}
				catch (RuntimeException e) {
					e.printStackTrace();
				}


			}
		};
		loginThread.start();
	}
}
