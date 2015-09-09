package eu.focusnet.app.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import eu.focusnet.app.activity.R;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.WidgetDao;
import eu.focusnet.app.manager.PageManager;
import eu.focusnet.app.model.data.Page;
import eu.focusnet.app.model.data.Widget;
import eu.focusnet.app.model.data.WidgetLinker;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.ViewFactory;

public class PageFragment extends Fragment {

    private static final String TAG  = PageFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View viewRoot =  inflater.inflate(R.layout.fragment_page, container, false);
        Bundle bundle = getArguments();
        String path = (String)bundle.get(Constant.PATH);
        new PageBuilderTask().execute(path);
        return viewRoot;
    }


    private class PageBuilderTask extends AsyncTask<String, Void, Page> {

        @Override
        protected Page doInBackground(String... params) {
            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
            String path = params[0];
            String pageId = path.substring(path.indexOf("/") + 1, path.length());
            Page page = null;
            try {
                databaseAdapter.openWritableDatabase();
                SQLiteDatabase db = databaseAdapter.getDb();
                PageManager pageManager = new PageManager(db);
                page = pageManager.findPage(pageId);
            }
            finally {
                databaseAdapter.close();
            }

            return page;
        }

        @Override
       protected void onPostExecute(Page page) {
            if (page != null) {

                LinearLayout linearLayoutHorizontal = ViewFactory.createLinearLayout(getActivity(), LinearLayout.HORIZONTAL,
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
                try {
                    databaseAdapter.openWritableDatabase();
                    WidgetDao widgetDao = new WidgetDao(databaseAdapter.getDb());
                    ArrayList<WidgetLinker> widgetLinkers = page.getWidgets();

                    //TODO enhance tis code
                    float weightSum = 0; //Default
                    for (WidgetLinker widgetLinker : widgetLinkers) {
                        Map<String, String> layouts = widgetLinker.getLayout();
                        if (widgetLinker.getLayout() != null) {
                            String width = layouts.get("width"); //TODO create a constant
                            int indexOf = width.indexOf("of");
                            weightSum += Float.valueOf(width.substring(0, indexOf).trim());
                        }
                    }
                    Log.d(TAG, "The sum of the weight: " + weightSum);

                    float weightToFill = 0;
                    if(weightSum < 4){
                        weightToFill = 4 - weightSum;
                    }

                    for (WidgetLinker widgetLinker : widgetLinkers) {
                        Widget widget = widgetDao.findWidget(widgetLinker.getWidgetid());
                        float weight = 0;
                        Map<String, String> layouts = widgetLinker.getLayout();
                        if (widgetLinker.getLayout() != null) {
                            String width = layouts.get("width"); //TODO create a constant
                            int indexOf = width.indexOf("of");
                            weight = Float.valueOf(width.substring(0, indexOf).trim());
                            //weight /= weightSum;
                            Log.d(TAG, "The weight: " + weight);
                        }


                        final TextView widgetParam = ViewFactory.createTextView(getActivity(),
                                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weight),
                                15, null);
                        widgetParam.setBackgroundColor(new Random().nextInt());
                        Map<String, String> params = widget.getParams();
                        if(params != null) {
                            String text = null;
                            for(Map.Entry<String, String> entry : params.entrySet()) {
                                text = entry.getValue();
                            }
                            widgetParam.setText(text);
                        }
                        linearLayoutHorizontal.addView(widgetParam);
                    }

                    if(weightToFill != 0) {
                        final TextView emptyText = ViewFactory.createTextView(getActivity(),
                                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weightToFill),
                                15, "Some text left");
                        emptyText.setBackgroundColor(new Random().nextInt());
                        linearLayoutHorizontal.addView(emptyText);
                    }

                }
                finally {
                    databaseAdapter.close();
                }

                //////////////END/////////////////////

                LinearLayout linearLayoutPageInfo = (LinearLayout) getView().findViewById(R.id.pageInfo);
                linearLayoutPageInfo.addView(linearLayoutHorizontal);
            }
        }
    }
}
