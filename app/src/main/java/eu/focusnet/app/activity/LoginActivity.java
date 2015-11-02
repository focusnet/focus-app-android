package eu.focusnet.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import eu.focusnet.app.adapter.DateTypeAdapter;
import eu.focusnet.app.db.AppContentDao;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.db.UserDao;
import eu.focusnet.app.model.data.AppContent;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.model.data.User;
import eu.focusnet.app.manager.DataProviderManager;
import eu.focusnet.app.manager.DataProviderManager.*;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.ViewFactory;
import eu.focusnet.app.util.ViewUtil;

/**
 * Login Activity, this activity displays the login screen
 * and log the user in the application
 */
public class LoginActivity extends Activity {

    private static final String TAG  = LoginActivity.class.getName();
    private int userId = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    /**
     * This method will be called when the user click
     * in the login button
     */
    public void onClick(View view){
        new DataReaderTask(this).execute("http://focus.yatt.ch/resources-server/data/user/"+userId+"/user-information",
                "http://focus.yatt.ch/resources-server/data/user/"+userId+"/app-user-preferences",
                "http://focus.yatt.ch/resources-server/data/user/"+userId+"/app-content-definition");
    }


    /**
     * Class used to connect to the webservice and retrieve the user and projects data
     */
    private class DataReaderTask extends AsyncTask<String, String, User> {

        private ProgressDialog progressDialog;
        private Context context;

        public DataReaderTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ViewFactory.createProgressDialog(context, "Authentication process running", "Please wait...");
            progressDialog.show();
        }

        @Override
        protected User doInBackground(String... urls) {
            Log.d(TAG, "Number of path to retrieve the resources: " + urls.length);

            DatabaseAdapter databaseAdapter = new DatabaseAdapter(context);
            User user = null;

            try {
                Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
                databaseAdapter.openWritableDatabase();
                SQLiteDatabase database = databaseAdapter.getDb();
                for (int i = 0; i < urls.length; i++) {
                    Log.d(TAG, "Url: " + urls[i]);
                    Log.d(TAG, "Counter: " + (i));
                    try {
                        ResponseData responseData = DataProviderManager.retrieveData(urls[i]);
                        if (responseData != null) {
                            String data = responseData.getData();
                            if (i == 0) {
                                Log.d(TAG, "Creating User");
                                user = gson.fromJson(data, User.class);
                                UserDao userDao = new UserDao(database);
                                userDao.createUser(user);
                            }
                            else if (i == 1) {
                                Log.d(TAG, "Creating Preferences");
                                Preference preference = gson.fromJson(data, Preference.class);
                                preference.setId(new Long(userId));
                                PreferenceDao preferenceDao = new PreferenceDao(database);
                                preferenceDao.createPreference(preference);
                            }
                            else {
                                Log.d(TAG, "Creating App Content");

                                AppContent appContent = gson.fromJson(data, AppContent.class);
                                appContent.setId(Long.valueOf(appContent.getOwner()));
                                AppContentDao appContentManager = new AppContentDao(database);
                                appContentManager.createAppContent(appContent);
                            }

                            publishProgress(data); //TODO remove this, when the app is finished
                        }
                    } catch (Exception ex) {
                        //TODO
                        ex.printStackTrace();
                    }
                }
            }
            finally {
                databaseAdapter.close();
            }

            return user;
        }

        //TODO remove this, when the app is finished
        @Override
        protected void onProgressUpdate(String... values) {
            ViewUtil.displayToast(context, values[0]);
        }

        @Override
        protected void onPostExecute(User user) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            Intent i = new Intent(LoginActivity.this, FocusActivity.class);
            i.putExtra(Constant.USER_DATA, user);
            startActivity(i);
            finish();
        }
    }
}
