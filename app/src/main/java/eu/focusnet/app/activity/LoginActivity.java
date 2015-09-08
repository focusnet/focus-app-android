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

import java.util.ArrayList;
import java.util.Date;

import eu.focusnet.app.adapter.DateTypeAdapter;
import eu.focusnet.app.db.AppContentDao;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.LinkerDao;
import eu.focusnet.app.db.PageDao;
import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.db.ProjectDao;
import eu.focusnet.app.db.UserDao;
import eu.focusnet.app.db.WidgetDao;
import eu.focusnet.app.db.WidgetLinkerDao;
import eu.focusnet.app.manager.AppContentManager;
import eu.focusnet.app.manager.PreferenceManager;
import eu.focusnet.app.manager.UserManager;
import eu.focusnet.app.model.data.AppContent;
import eu.focusnet.app.model.data.Linker;
import eu.focusnet.app.model.data.Page;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.model.data.Project;
import eu.focusnet.app.model.data.User;
import eu.focusnet.app.model.data.Widget;
import eu.focusnet.app.model.data.WidgetLinker;
import eu.focusnet.app.manager.DataProviderManager;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.GuiUtil;
import eu.focusnet.app.util.NetworkUtil.*;

/**
 * Created by admin on 16.06.2015.
 */
public class LoginActivity extends Activity {

    private static final String TAG  = LoginActivity.class.getName();
    private int userId = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onClick(View view){

        new DataReaderTask(this).execute("http://focus.yatt.ch/resources-server/data/user/"+userId+"/user-information",
                "http://focus.yatt.ch/resources-server/data/user/"+userId+"/app-user-preferences",
                "http://focus.yatt.ch/resources-server/data/user/"+userId+"/app-content-definition");
    }


    private class DataReaderTask extends AsyncTask<String, String, User> {

        private ProgressDialog progressDialog;
        private Context context;

        public DataReaderTask(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = GuiUtil.createProgressDialog(context, "Authentication process running", "Please wait...");
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
                                UserManager userManager = new UserManager(database);
                                userManager.saveUser(user);
                            }
                            else if (i == 1) {
                                Log.d(TAG, "Creating Preferences");
                                Preference preference = gson.fromJson(data, Preference.class);
                                preference.setId(new Long(userId));
                                PreferenceManager preferenceManager = new PreferenceManager(database);
                                preferenceManager.savePreference(preference);
                            }
                            else {
                                Log.d(TAG, "Creating App Content");

                                AppContent appContent = gson.fromJson(data, AppContent.class);
                                appContent.setId(Long.valueOf(appContent.getOwner()));
                                AppContentManager appContentManager = new AppContentManager(database);
                                appContentManager.saveAppContent(appContent);
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
            GuiUtil.displayToast(context, values[0]);
        }

        @Override
        protected void onPostExecute(User user) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            Intent i = new Intent("eu.focusnet.app.activity.MainActivity");
            i.putExtra(Constant.USER_DATA, user);
            startActivity(i);
            finish();
        }
    }
}
