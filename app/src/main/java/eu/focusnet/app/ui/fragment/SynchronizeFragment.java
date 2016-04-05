/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import eu.focusnet.app.R;
import eu.focusnet.app.service.CronService;

/**
 * The synchronized fragment is used to refresh the data
 * getting the new data from the webservice
 *
 * FIXME TODO FIXME use CronService's logic to trigger -> cronService.doNotDoNextSync()
 * and remove useless codce
 */
public class SynchronizeFragment extends Fragment
{

	private static final String TAG = SynchronizeFragment.class.getName();
	private static final String CHECK_FRESHNESS_PATH = "http://focus.yatt.ch/resources-server/services/check-freshness";
	private TableLayout tableLayout;

	private CronService cronService;
	private boolean boundService;
	/** Defines callbacks for service binding, passed to bindService() */
	private ServiceConnection mConnection = new ServiceConnection()
	{

		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service)
		{
			// We've bound to LocalService, cast the IBinder and get LocalService instance
			CronService.CronBinder binder = (CronService.CronBinder) service;
			cronService = binder.getService();
			boundService = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			boundService = false;
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_synchronize, container, false);
		tableLayout = (TableLayout) viewRoot.findViewById(R.id.synchronize_paths);
		// FIXME	new SynchronizeDataTask().execute();
		return viewRoot;
	}

	@Override
	public void onStart()
	{
		super.onStart();

		Intent intent = new Intent(this.getActivity(), CronService.class);
		this.getActivity().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		boundService = false;
	}

	@Override
	public void onStop()
	{
		super.onStop();
		// Unbind from the service
		if (boundService) {
			this.getActivity().unbindService(mConnection);
			boundService = false;
		}
	}


// FIXME FIXME TODO TO BE TESTED

/*
	private class SynchronizeDataTask extends AsyncTask<Void, String, ArrayList<String>>
	{
// FIXME TODO - do execute the cron service method.
		// bind to cron service
		@Override
		protected ArrayList<String> doInBackground(Void... params)
		{
			ArrayList<String> resourcesToRefresh = null;
			try {
				ArrayList<RequestData> requestDatas = new ArrayList<>();
				//TODO hold the url for the resource and the version
				final String version = "/v1";
				RequestData requestData = new RequestData("http://focus.yatt.ch/resources-server/data/user/123/app-content-definition" + version);
				requestDatas.add(requestData);
				resourcesToRefresh = DataProviderManager.checkDataFreshness(CHECK_FRESHNESS_PATH, requestDatas);

			}
			catch (IOException e) {
				e.printStackTrace();
			}

			return resourcesToRefresh;
		}

		@Override
		protected void onPostExecute(final ArrayList<String> resourcesToRefresh)
		{
			int textSize = 15;
			final TextView text = ViewFactory.createTextView(getActivity(),
					R.style.Base_TextAppearance_AppCompat,
					new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
							TableRow.LayoutParams.WRAP_CONTENT), "There were not resources to refresh."); //TODO internationalize

			final TableRow tableRow = ViewFactory.createTableRow(getActivity(), new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

			if (resourcesToRefresh == null || resourcesToRefresh.isEmpty()) {
				tableRow.addView(text);
				tableLayout.addView(tableRow);
			}
			else {
				for (String resource : resourcesToRefresh) {
					text.setText("Resource: " + resource); //TODO internationalize
					tableRow.addView(text);
					tableLayout.addView(tableRow);
				}
				final Button button = ViewFactory.createButton(getActivity(), new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT),
						"Refresh Resources");//TODO internationalize

				button.setOnClickListener(new View.OnClickListener()
										  {
											  @Override
											  public void onClick(View v)
											  {
												  // FIXME FIXME FIXME TODO, that we also has to use the IventBus pattern)

												  // FIXME FIXM FIXME TODO call DataManager to rebuild the database, posssibly with the ApplicationConntentInstance.
												  // FIXME FIXME FIXME TODO will be done in bg.
												  // no need to access the database anymore.


												  DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
												  try {
													  databaseAdapter.openWritableDatabase();
													  AppContentDao appContentDao = new AppContentDao(databaseAdapter.getDb());
													  //TODO set the appContent id dynamic
													  appContentDao.deleteAppContent(new Long(123));
													  String resource = resourcesToRefresh.get(0);
													  //TODO what is there are more resorces of different types?
													  try {
														  Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
														  DataProviderManager.ResponseData responseData = DataProviderManager.retrieveData(resource);
														  Log.d(TAG, "Creating the new App Content");
														  AppContentTemplate appContentTemplate = gson.fromJson(responseData.getData(), AppContentTemplate.class);
														  appContentTemplate.setId(Long.valueOf(appContentTemplate.getOwner()));
														  appContentDao.createAppContent(appContentTemplate);
														  Log.i(TAG, "The new App Content was created successfully");
														  ViewUtil.displayToast(getActivity(), "Resource refreshed successfully");
														  tableLayout.removeAllViews();
														  text.setText("Resource refreshed successfully!"); //TODO internationalize
														  tableLayout.addView(tableRow);
													  }
													  catch (IOException e) {
														  e.printStackTrace();
													  }
												  }
												  finally {
													  databaseAdapter.close();
												  }

											  }
										  }

				);
				tableLayout.addView(button);
			}
		}
	}*/

}
