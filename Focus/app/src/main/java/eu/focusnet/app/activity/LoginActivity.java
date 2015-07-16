package eu.focusnet.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;

import eu.focusnet.app.adapter.DateTypeAdapter;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDAO;
import eu.focusnet.app.db.UserDAO;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.model.data.User;
import eu.focusnet.app.service.DataProviderService;
import eu.focusnet.app.util.Util;

/**
 * Created by admin on 16.06.2015.
 */
public class LoginActivity extends Activity {

    private static final String TAG  = LoginActivity.class.getName();
    private ArrayList<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onClick(View view){
        //TODO
        new DataReaderTask(this).execute("http://focus.yatt.ch/resources-server/data/users/1/information",
                                         "http://focus.yatt.ch/resources-server/data/users/1/focus-mobile-app-pref");
//        "http://focus.yatt.ch/resources-server/data/users/1/focus-mobile-app-content" //TODO app-content
    }


    private class DataReaderTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog;

        private Gson gson;
        private Context context;
        private int counter;
        private DatabaseAdapter databaseAdapter;

        public DataReaderTask(Context context){
            this.context = context;
            progressDialog = Util.createProgressDialog(context, "Authentication process running", "Please wait...");
            gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
            databaseAdapter = new DatabaseAdapter(context);

        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            Log.d(TAG, "Number of path to retrieve the resources: " + urls.length);
            for (int i = 0; i < urls.length; i++) {
                Log.d(TAG, "Url: "+urls[i]);
                publishProgress(DataProviderService.retrieveData(urls[i]));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            Log.d(TAG, "Counter: " + (counter));
            String value = values[0];
            if(counter == 0){
                Log.d(TAG, "Creating User");
                User user = gson.fromJson(value, User.class);
                databaseAdapter.open();
                UserDAO userDao = new UserDAO(databaseAdapter.getDb());
                user.setId(new Long(1));
                userDao.createUser(user);
                databaseAdapter.close();
            }
            else if(counter == 1){
                Log.d(TAG, "Creating Preferences");

                Preference preference = gson.fromJson(value, Preference.class);
                preference.setId(new Long(1));
                databaseAdapter.open();
                PreferenceDAO preferenceDAO = new PreferenceDAO(databaseAdapter.getDb());
                preferenceDAO.createPreference(preference); //TODO bookmark and setting's id
                databaseAdapter.close();
            }
            else{
                //TODO content
            }
            counter++;
            data.add(value);
            Util.displayToast(context, value);
        }

        @Override
        protected void onPostExecute(Void unused) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            Intent i = new Intent("eu.focusnet.app.activity.MainActivity");
            startActivity(i);
            finish();
        }
    }
}
