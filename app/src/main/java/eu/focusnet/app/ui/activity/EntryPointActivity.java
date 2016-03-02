/**
 *
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
 *
 */

package eu.focusnet.app.ui.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.ui.util.ViewFactory;

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
	 * Instantiate the activity.
	 *
	 * @param savedInstanceState past Activity state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Intent loader = null;

		/*
		 * If we don't have login information, yet, let's redirect to the LoginActivity.
		 * Otherwise, load the basic application configuration and redirect to FocusActivity
		 */
		if (FocusApplication.getInstance().getDataManager().hasLoginInformation()) {
			new ConfigLoaderTask(this).execute();
			loader = new Intent(new Intent(EntryPointActivity.this, FocusActivity.class));
		}
		else {
			loader = new Intent(EntryPointActivity.this, LoginActivity.class);
		}

		startActivity(loader);
		finish();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		// FIXME TODO YANDY: AppIndexing - is that useful? if so, let's do it properly (i.e. with real configuration).
		//Answer: This is new for me, I think it could be interesting but we have to define for which uses cases we need that. I suggest you
		//to let it there at the moment and when we are finish with the project(everything works good), we can take a look at it.
		// FIXME TODO otherwise, let's completely remove it.
		//	client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	/*
	* This thread will retrieve the application configuration.
	*/
	private class  ConfigLoaderTask extends AsyncTask<Void, Void, Void>{

		private ProgressDialog progressDialog;
		private Context context;

		public ConfigLoaderTask(Context context){
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			progressDialog = ViewFactory.createProgressDialog(context, "Loading app configuration", "Please wait..."); //TODO internationalize this message
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				FocusApplication.getInstance().getDataManager().retrieveApplicationData();
			}
			catch (FocusMissingResourceException e) {
				// this may occur when no data has been previously loaded
				// FIXME TODO YANDY what do we do? Toast-like message + exit
				// or rather redirect to new Activity that explains the problem and proposes to try again later (button)
				//Answer: What is exactly: no data has been previously loaded
				/*
		 		* If we don't have login information, yet, let's redirect to the LoginActivity.
		 		* Otherwise, load the basic application configuration and redirect to FocusActivity
		 		*/
				//are you retrieving the login information from a database, config file or
				// from an object in memory(this could bring some problems if the android OS needs this memory for something else, then the object will be destroyed ...)
				// and after that making a call to the webservice to retrieve the data?

			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			if(progressDialog.isShowing())
				progressDialog.dismiss();
		}
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