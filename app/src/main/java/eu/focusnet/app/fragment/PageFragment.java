package eu.focusnet.app.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.activity.R;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PageDao;
import eu.focusnet.app.manager.PageManager;
import eu.focusnet.app.model.data.Page;
import eu.focusnet.app.model.data.WidgetLinker;
import eu.focusnet.app.util.Constant;

public class PageFragment extends Fragment {


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

            Page page = new Page();
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
            View view = getView();
            ((TextView)view.findViewById(R.id.pageId)).setText("Page's id :"+page.getGuid());
            ((TextView)view.findViewById(R.id.pageTitle)).setText("Page's title :" + page.getTitle());
            ((TextView)view.findViewById(R.id.pageDescription)).setText("Page's description :" + page.getDescription());

            ArrayList<WidgetLinker> widgetLinkers = page.getWidgets();

            ((TextView) view.findViewById(R.id.numberOfWidgets)).setText("Number of widgets :" + widgetLinkers.size());

            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.pageInfo);

            for(WidgetLinker widgetLinker : widgetLinkers){

                TextView widgetId = new TextView(getActivity());
                widgetId.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                widgetId.setText("Widget's id: " + widgetLinker.getWidgetid());

                TextView widgetOrder = new TextView(getActivity());
                widgetOrder.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                widgetOrder.setText("Widget's order: "+widgetLinker.getOrder());

//                TextView widgetLayout = new TextView(getActivity());
//                widgetLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                linearLayout.addView(widgetId);
                linearLayout.addView(widgetOrder);

            }
        }
    }

}
