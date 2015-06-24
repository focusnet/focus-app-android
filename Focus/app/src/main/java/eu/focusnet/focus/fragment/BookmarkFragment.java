package eu.focusnet.focus.fragment;

import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import eu.focusnet.focus.activity.MainActivity;
import eu.focusnet.focus.activity.R;
import eu.focusnet.focus.service.DataProviderService;
import eu.focusnet.focus.util.Util;


/**
 * Created by admin on 15.06.2015.
 */
public class BookmarkFragment extends Fragment {

    private String[] httpMethods;
    private String selectedHttpMethod;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_bookmark, container, false);

        httpMethods = getResources().getStringArray(R.array.http_methods);
        Spinner spinner = (Spinner)viewRoot.findViewById(R.id.http_method_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, httpMethods);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedHttpMethod = httpMethods[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button getResourceButton = (Button) viewRoot.findViewById(R.id.get_resource_button);
        getResourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText webserviceUrlText = (EditText) getView().findViewById(R.id.webservice_textView);
                String url = webserviceUrlText.getText().toString();
                new PreferenceDataReaderTask().execute(url, selectedHttpMethod);

            }
        });

        return viewRoot;
    }


    private class PreferenceDataReaderTask extends AsyncTask<String, Void, String> {

        private final ProgressDialog progressDialog;

        public PreferenceDataReaderTask(){
            progressDialog = Util.createProgressDialog(getActivity(), "Retrieving the data", "Please wait...");
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            return DataProviderService.retrieveData(urls[0], urls[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            //TODO uncomment this for the UserPreference
//            Gson gson = new Gson();
//            if(result != null)
//               userPref = gson.fromJson(result, UserPreference.class);

            int id = 3; //The id of the notification and the navigation id to display the appropriate fragment ()
            TextView prefTextView = (TextView)getView().findViewById(R.id.preference);
            prefTextView.setText(result);
            Util.displayNotification(getActivity(), MainActivity.class, R.mipmap.ic_launcher, "Title", "Content", id);
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        }
    }
}
