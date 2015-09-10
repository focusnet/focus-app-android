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

                LinearLayout linearLayoutPageInfo = (LinearLayout) getView().findViewById(R.id.pageInfo);
                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
                try {
                    databaseAdapter.openWritableDatabase();
                    WidgetDao widgetDao = new WidgetDao(databaseAdapter.getDb());
                    ArrayList<WidgetLinker> widgetLinkers = page.getWidgets();

                    //TODO enhance tis code
                    int lastWeightLeftToFill = 0;
                    int weightLeftToFill = 4;
                    boolean needNewLayout = false;
                    boolean isMaxWeightReached = false;
                    LinearLayout linearLayoutHorizontal = ViewFactory.createLinearLayout(getActivity(), LinearLayout.HORIZONTAL,
                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                    for(WidgetLinker widgetLinker : widgetLinkers) {
                        Widget widget = widgetDao.findWidget(widgetLinker.getWidgetid());
                        int weight = 0;
                        Map<String, String> layouts = widgetLinker.getLayout();
                        if (widgetLinker.getLayout() != null) {
                            String width = layouts.get("width"); //TODO create a constant
                            int indexOf = width.indexOf("of");
                            weight = Integer.valueOf(width.substring(0, indexOf).trim());
                            weightLeftToFill -= weight;
                            Log.d(TAG, "The weight: " + weight);
                        }

                        int linearLayoutWidth = LinearLayout.LayoutParams.MATCH_PARENT;
                        if (weight != 0 && weight != 4){
                            linearLayoutWidth = 0;
                            if(needNewLayout || isMaxWeightReached){
                                linearLayoutHorizontal = ViewFactory.createLinearLayout(getActivity(), LinearLayout.HORIZONTAL,
                                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                           //     linearLayoutPageInfo.addView(linearLayoutHorizontal);
                                needNewLayout = false;
                                isMaxWeightReached = false;
                            }

                            linearLayoutPageInfo.removeView(linearLayoutHorizontal); //TODO do this in a better way
                            linearLayoutPageInfo.addView(linearLayoutHorizontal);
                        }
                        else {
                            if(lastWeightLeftToFill != 0) {
                                final TextView emptyText = ViewFactory.createTextView(getActivity(),
                                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weightLeftToFill),
                                15, null);
                                 emptyText.setBackgroundColor(new Random().nextInt());
                                linearLayoutHorizontal.addView(emptyText);
                                linearLayoutPageInfo.addView(linearLayoutHorizontal);
                            }

                            weight = 0;
                            weightLeftToFill = 0;
                            linearLayoutHorizontal = ViewFactory.createLinearLayout(getActivity(), LinearLayout.HORIZONTAL,
                                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                            linearLayoutPageInfo.addView(linearLayoutHorizontal);
                            needNewLayout = true;
                        }


                        final TextView widgetParam = ViewFactory.createTextView(getActivity(),
                                new LinearLayout.LayoutParams(linearLayoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT, weight),
                                15, null);
                        widgetParam.setBackgroundColor(new Random().nextInt());
                        Map<String, String> params = widget.getParams();
                        if (params != null) {
                            String text = null;
                            for (Map.Entry<String, String> entry : params.entrySet()) {
                                text = entry.getValue();
                            }
                            widgetParam.setText(text);
                        }

                        linearLayoutHorizontal.addView(widgetParam);

                        lastWeightLeftToFill = weightLeftToFill;

                         if(weightLeftToFill == 0) {
                             weightLeftToFill = 4;
                             isMaxWeightReached = true;
                         }
                    }

                    if(lastWeightLeftToFill != 0) {
                        final TextView emptyText = ViewFactory.createTextView(getActivity(),
                                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, weightLeftToFill),
                                15, null);
                        emptyText.setBackgroundColor(new Random().nextInt());
                        linearLayoutHorizontal.addView(emptyText);
                    }

                }
                finally {
                    databaseAdapter.close();
                }

                //////////////END///////////////
            }
        }
    }
}
