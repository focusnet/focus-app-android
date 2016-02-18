package eu.focusnet.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import eu.focusnet.app.R;
import eu.focusnet.app.service.DataManager;

/**
 * Login Activity: this activity displays the login screen
 * and log the user in the application
 */
public class LoginActivity extends Activity
{

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

		// FIXME TODO YANDY: if no network then disable SUBMIT button and mention the problem
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

				// FIXME TODO YANDY progress indicator? disable all controls? Or just a Toast-like thing that says "Connecting. Please wait..."

				try {

					String username = ((EditText) findViewById(R.id.login_username_textView)).getText().toString();
					String password = ((EditText) findViewById(R.id.login_password_textView)).getText().toString();
					// TODO FIXME YANDY also a field for server name
					String server = "server";

					if (DataManager.getInstance().login(username, password, server)) {
						Intent i = new Intent(LoginActivity.this, EntryPointActivity.class);
						startActivity(i);
						finish();
					}
					else {
						Toast toast = Toast.makeText(getApplicationContext(), R.string.focus_login_error, Toast.LENGTH_SHORT);
						toast.show();
					}
				}
				catch (RuntimeException e) {
					// FIXME TODO
				}
			}
		};
		loginThread.start();
	}
}
