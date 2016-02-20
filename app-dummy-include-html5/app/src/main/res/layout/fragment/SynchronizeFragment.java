package layout.fragment;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import eu.focusnet.app.ui.activity.R;
import eu.focusnet.app.ui.adapter.DateTypeAdapter;
import eu.focusnet.app.model.store.db.AppContentDao;
import eu.focusnet.app.model.store.db.DatabaseAdapter;
import eu.focusnet.app.manager.DataProviderManager;
import eu.focusnet.app.manager.DataProviderManager.*;
import eu.focusnet.app.model.data.AppContent;
import eu.focusnet.app.util.ViewFactory;
import eu.focusnet.app.util.ViewUtil;

/**
 * The synchronized fragment is used to refresh the data
 * getting the new data from the webservice
 */
public class SynchronizeFragment extends Fragment {

    private static final String TAG = SynchronizeFragment.class.getName();
    private static final String CHECK_FRESHNESS_PATH = "http://focus.yatt.ch/resources-server/services/check-freshness";
    private TableLayout tableLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_synchronize, container, false);
        tableLayout = (TableLayout) viewRoot.findViewById(R.id.synchronize_paths);
        new SynchronizeDataTask().execute();
        return viewRoot;
    }

    private class SynchronizeDataTask extends AsyncTask<Void, String, ArrayList<String>> {

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            ArrayList<String> resourcesToRefresh = null;
            try {
                ArrayList<RequestData> requestDatas = new ArrayList<>();
                //TODO hold the url for the resource and the version
                final String version = "/v1";
                RequestData requestData = new RequestData("http://focus.yatt.ch/resources-server/data/user/123/app-content-definition"+version);
                requestDatas.add(requestData);
                resourcesToRefresh =  DataProviderManager.checkDataFreshness(CHECK_FRESHNESS_PATH, requestDatas);
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return resourcesToRefresh;
        }

        @Override
        protected void onPostExecute(final ArrayList<String> resourcesToRefresh) {
            int textSize = 15;
            final TextView text = ViewFactory.createTextView(getActivity(),
                    R.style.Base_TextAppearance_AppCompat,
                    new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT), "There were not resources to refresh."); //TODO internationalize

            final TableRow tableRow = ViewFactory.createTableRow(getActivity(), new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

            if(resourcesToRefresh == null || resourcesToRefresh.isEmpty()){
                tableRow.addView(text);
                tableLayout.addView(tableRow);
            }
            else{
                for(String resource : resourcesToRefresh){
                    text.setText("Resource: " + resource); //TODO internationalize
                    tableRow.addView(text);
                    tableLayout.addView(tableRow);
                }
                final Button button = ViewFactory.createButton(getActivity(), new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT),
                        "Refresh Resources");//TODO internationalize

                button.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  ViewUtil.displayToast(getActivity(), "Refreshing resource");
                                                  //database (it may be, that we also has to use the IventBus pattern)
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
                                                          AppContent appContent = gson.fromJson(responseData.getData(), AppContent.class);
                                                          appContent.setId(Long.valueOf(appContent.getOwner()));
                                                          appContentDao.createAppContent(appContent);
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
    }

}