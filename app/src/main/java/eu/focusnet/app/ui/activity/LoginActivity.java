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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;

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
		setContentView(R.layout.activity_login);

		UiHelper.setupHidableKeyboard(findViewById(R.id.activity_view_parent), this);
	}

	/**
	 * When the user clicks the login button, the provided username, password and server name
	 * are used to authenticate.
	 * <p/>
	 * This logic requires a network connection.
	 */
	public void onClick(View view)
	{
		//test If the device is connected to the internet, if true
		// test then the given credentials otherwise display an error message(toast) to the user

		if (FocusApplication.getInstance().getNetworkManager().isNetworkAvailable()) {
			String username = ((EditText) findViewById(R.id.login_username_edit)).getText().toString();
			String password = ((EditText) findViewById(R.id.login_password_editText)).getText().toString();
			String server = ((EditText) findViewById(R.id.login_server_editText)).getText().toString();

			// FIXME input validation

			new LoginTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, username, password, server);
		}
		else {
			UiHelper.displayToast(this, R.string.focus_login_error_no_network);
		}
	}

	/**
	 * This class is used for testing if the given credential are correct
	 */
	private class LoginTask extends AsyncTask<String, Void, Boolean>
	{

		private ProgressDialog progressDialog;
		private Context context;

		public LoginTask(Context context)
		{
			this.context = context;
		}

		@Override
		protected void onPreExecute()
		{
			Resources res = getResources();
			progressDialog = new ProgressDialog(this.context);
			progressDialog.setTitle(res.getString(R.string.focus_login_progress_message));
			progressDialog.setMessage(res.getString(R.string.progress_wait_message));
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(String... data)
		{
			String username = data[0];
			String password = data[1];
			String server = data[2];

			Boolean hasAccess;
			try {
				hasAccess = FocusApplication.getInstance().getDataManager().login(username, password, server);
			}
			catch (IOException ex) {
				hasAccess = false;
			}
			return hasAccess;
		}

		@Override
		protected void onPostExecute(Boolean hasAccess)
		{
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			//If the given credential were correct start the EntryPointActivity otherwise display an error message(toast) to the user
			if (hasAccess) {
				Intent i = new Intent(LoginActivity.this, EntryPointActivity.class);
				i.putExtra(Constant.UI_EXTRA_LOADING_INFO_TEXT, getString(R.string.init_app_first_run));
				startActivity(i);
				finish();
			}
			else {
				UiHelper.displayToast(this.context, R.string.focus_login_error_bad_credentials);
				//TODO maybe we should also show some text in red or something like this ...?
			}
		}
	}

}
