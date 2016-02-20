package eu.focusnet.app.ui.fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.activity.FocusActivity;
import eu.focusnet.app.DEPRECATED.DataProviderManager;
import eu.focusnet.app.DEPRECATED.DataProviderManager.ResponseData;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.ViewFactory;
import eu.focusnet.app.ui.util.ViewUtil;


/**
 * Fragment used to displays the setting of the application
 */
public class SettingFragment extends Fragment
{

	private String[] httpMethods;
	private String selectedHttpMethod;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_settings, container, false);

		httpMethods = getResources().getStringArray(R.array.http_methods);
		Spinner spinner = (Spinner) viewRoot.findViewById(R.id.http_method_spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, httpMethods);
		spinner.setAdapter(adapter);

		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				selectedHttpMethod = httpMethods[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});

		Button getResourceButton = (Button) viewRoot.findViewById(R.id.get_resource_button);
		getResourceButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				EditText webserviceUrlText = (EditText) getView().findViewById(R.id.webservice_textView);
				String url = webserviceUrlText.getText().toString();
				new PreferenceDataReaderTask().execute(url, selectedHttpMethod); //selectedHttpMethod no needed anymore

			}
		});

		return viewRoot;
	}

	//This is only for test purpose
	private class PreferenceDataReaderTask extends AsyncTask<String, Void, String>
	{

		private ProgressDialog progressDialog;

		@Override
		protected void onPreExecute()
		{
			progressDialog = ViewFactory.createProgressDialog(getActivity(), "Retrieving the data", "Please wait...");
			progressDialog.show();
		}

		@Override
		protected String doInBackground(String... urls)
		{
			String data = null;
			try {
				ResponseData responseData = DataProviderManager.retrieveData(urls[0]);
				if (responseData != null) {
					data = responseData.getData();
				}
			}
			catch (Exception ex) {

			}

			return data;
		}

		@Override
		protected void onPostExecute(String result)
		{
			int id = Constant.UI_FRAGMENT_SYNCHRONIZE; //The id of the notification and the navigation id to display the appropriate fragment ()
			TextView prefTextView = (TextView) getView().findViewById(R.id.settings);
			prefTextView.setText(result);
			ViewUtil.displayNotification(getActivity(), FocusActivity.class, R.drawable.ic_tree, "Title", "Content", id);
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}
}