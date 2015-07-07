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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import eu.focusnet.app.adapter.DateTypeAdapter;
import eu.focusnet.app.db.UserDAO;
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
        new DataReaderTask(this).execute("http://focus.yatt.ch/resources-server/data/users/1/information");//,
                                  //       "http://focus.yatt.ch/resources-server/data/users/1/focus-mobile-app-pref");
//        "http://focus.yatt.ch/resources-server/data/users/1/focus-mobile-app-content" //TODO app-content
    }


    private class DataReaderTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog;
        private UserDAO userDbAdapter;
        private Gson gson;

        public DataReaderTask(Context context){
            progressDialog = Util.createProgressDialog(context, "Authentication process running", "Please wait...");
            userDbAdapter = new UserDAO(context);
            gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            Log.d(TAG, "Number of path to retrieve the resources: "+urls.length);
            for (int i = 0; i < urls.length; i++) {
                Log.d(TAG, "Url: "+urls[i]);
                publishProgress(DataProviderService.retrieveData(urls[i]));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            data.add(values[0]);
            User user = gson.fromJson(values[0], User.class);
            try {
                userDbAdapter.open();
                userDbAdapter.insertUser(user);
                userDbAdapter.close();
            } catch (SQLException e) {
                e.printStackTrace(); //TODO
            }

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
