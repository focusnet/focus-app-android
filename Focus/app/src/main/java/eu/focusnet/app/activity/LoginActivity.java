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

/**
 * Created by admin on 16.06.2015.
 */
public class LoginActivity extends Activity {

    private static final String TAG  = LoginActivity.class.getName();
    private int userId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onClick(View view){

        new DataReaderTask(this).execute("http://focus.yatt.ch/resources-server/data/users/" + userId + "/information",
                "http://focus.yatt.ch/resources-server/data/users/" + userId + "/focus-mobile-app-pref",
                "http://focus.yatt.ch/data-model/documentation/examples/focus-mobile-app-content-definition/001-test.json");
                                         // "http://focus.yatt.ch/resources-server/data/users/1/focus-mobile-app-content"); //TODO app-content
    }


    private class DataReaderTask extends AsyncTask<String, String, Void> {

        private ProgressDialog progressDialog;
        private Gson gson;
        private Context context;
        private DatabaseAdapter databaseAdapter;
        private User user;

        public DataReaderTask(Context context){
            this.context = context;
            gson = new GsonBuilder().registerTypeAdapter(Date.class, new DateTypeAdapter()).create();
            progressDialog = GuiUtil.createProgressDialog(context, "Authentication process running", "Please wait...");
            databaseAdapter = new DatabaseAdapter(context);
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... urls) {
            Log.d(TAG, "Number of path to retrieve the resources: " + urls.length);

            databaseAdapter.openWritableDatabase();
            SQLiteDatabase database = databaseAdapter.getDb();

            for(int i = 0; i < urls.length; i++) {
                Log.d(TAG, "Url: " + urls[i]);
                Log.d(TAG, "Counter: " + (i));

                String value = DataProviderManager.retrieveData(urls[i]);

                if(i == 0) {
                    Log.d(TAG, "Creating User");
                    user = gson.fromJson(value, User.class);

                    UserDao userDao = new UserDao(database);
                    user.setId(new Long(userId));
                    userDao.createUser(user);
                }
                else if(i == 1){
                    Log.d(TAG, "Creating Preferences");

                    Preference preference = gson.fromJson(value, Preference.class);
                    preference.setId(new Long(userId));
                    PreferenceDao preferenceDAO = new PreferenceDao(database);
                    preferenceDAO.createPreference(preference);
                }
                else{
                    Log.d(TAG, "Creating App Content");

                    AppContent appContent = gson.fromJson(value, AppContent.class);
                    appContent.setId(new Long(userId));
                    AppContentDao appContentDao = new AppContentDao(database);
                    appContentDao.createAppContent(appContent);

                    ProjectDao projectDao = new ProjectDao(database);
                    ArrayList<Project> projects = appContent.getProjects();
                    if(projects != null ) {
                        for (Project project : projects) {
                            projectDao.createProject(project, appContent.getId());
                            String projectId = project.getGuid();

                            ArrayList<Widget> widgets = project.getWidgets();
                            if (widgets != null) {
                                WidgetDao widgetDao = new WidgetDao(database);
                                for (Widget w : widgets)
                                    widgetDao.createWidget(w, projectId);
                            }

                            ArrayList<Page> pages = project.getPages();
                            if (pages != null) {
                                PageDao pageDao = new PageDao(database);
                                for (Page p : pages) {
                                    pageDao.createPage(p, projectId);

                                    WidgetLinkerDao widgetLinkerDao = new WidgetLinkerDao(database);
                                    ArrayList<WidgetLinker> widgetLinkers = p.getWidgets();
                                    if(widgetLinkers != null) {
                                        for (WidgetLinker wl : widgetLinkers)
                                            widgetLinkerDao.createWidgetLinker(wl, p.getGuid());
                                    }
                                }
                            }

                            LinkerDao linkerDao = new LinkerDao(database);
                            ArrayList<Linker> dashboards = project.getDashboards();
                            if (dashboards != null) {
                                for (Linker l : dashboards)
                                    linkerDao.createLinker(l, projectId, LinkerDao.LINKER_TYPE.DASHBOARD);
                            }

                            ArrayList<Linker> tools = project.getTools();
                            if (tools != null) {
                                for (Linker l : tools)
                                    linkerDao.createLinker(l, projectId, LinkerDao.LINKER_TYPE.TOOL);
                            }
                        }
                    }
                }

                publishProgress(value); //TODO remove this, when the app is finished
            }

            databaseAdapter.close();

            return null;
        }

        //TODO remove this, when the app is finished
        @Override
        protected void onProgressUpdate(String... values) {
            GuiUtil.displayToast(context, values[0]);
        }

        @Override
        protected void onPostExecute(Void unused) {
            if(progressDialog.isShowing())
                progressDialog.dismiss();
            Intent i = new Intent("eu.focusnet.app.activity.MainActivity");
            i.putExtra(Constant.USER_DATA, user);
            startActivity(i);
            finish();
        }
    }
}
