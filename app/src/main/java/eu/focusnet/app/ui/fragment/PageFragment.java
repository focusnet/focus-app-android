package eu.focusnet.app.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import eu.focusnet.app.R;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.model.internal.AppContentInstance;
import eu.focusnet.app.model.internal.PageInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.ViewUtil;

/**
 * This fragment will be loaded from the PageActivity and displays
 * the characteristics of a page
 */
public class PageFragment extends Fragment
{

	private PageInstance pageInstance;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState)
	{

		// Inflate the layout for this fragment
		View viewRoot = inflater.inflate(R.layout.fragment_page, container, false);
		Bundle bundle = getArguments();
		String projectPath = (String) bundle.get(Constant.UI_EXTRA_PROJECT_PATH);
		String pagePath = (String) bundle.get(Constant.UI_EXTRA_PAGE_PATH);

//        new PageBuilderTask().execute(projectPath, pagePath);

		AppContentInstance appContentInstance = DataManager.getInstance().getAppContentInstance();
		ProjectInstance projectInstance = appContentInstance.getProjectFromPath(projectPath);
		this.pageInstance = appContentInstance.getPageFromPath(pagePath);

		// useful for our custom garbage collection in DataManager
		DataManager.getInstance().registerActiveInstance(this.pageInstance);

		LinearLayout linearLayoutPageInfo = (LinearLayout) viewRoot.findViewById(R.id.pageInfo);

		ViewUtil.buildPageView(projectInstance, pageInstance, linearLayoutPageInfo, getActivity());

//        LinkedHashMap<String, WidgetInstance> widgetInstances = pageInstance.getWidgets();

//        for(Map.Entry<String, WidgetInstance> entry : widgetInstances.entrySet()){
//            WidgetInstance widgetInstance = entry.getValue();
//            WidgetFragment widgetFragment = ViewUtil.getWidgetFragmentByType(widgetInstance.getType());
//            Bundle widgetBundle = new Bundle();
//            widgetBundle.putString(Constant.UI_EXTRA_PATH, appContentInstance.buildPath(projectInstance, pageInstance, widgetInstance));
//            widgetFragment.setArguments(widgetBundle);
//            //Test
//            LinearLayout linearLayoutPageInfo = (LinearLayout) viewRoot.findViewById(R.id.pageInfo);
//            FragmentManager.addFragment(linearLayoutPageInfo.getId(), widgetFragment, getFragmentManager());
//        }

		return viewRoot;
	}

	@Override
	public void onDestroyView()
	{
		// useful for our custom garbage collection in DataManager
		DataManager.getInstance().unregisterActiveInstance(this.pageInstance);
	}


//    /**
//     * This class loads the page characteristics from the database
//     */
//    private class PageBuilderTask extends AsyncTask<String, Void, PageInstance> {
//
//        @Override
//        protected PageInstance doInBackground(String... config) {
//            DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
//            String projectPath = config[0];
//            String pagePath = config[1];
//
////           String pageId = path.substring(path.indexOf("/") + 1, path.length());
//
//            PageInstance pageInstance  = DataManager.getInstance().getAppContentInstance().getPageFromPath(pagePath);
//
//
//
////            PageTemplate page = null;
////            try {
////                databaseAdapter.openWritableDatabase();
////                SQLiteDatabase db = databaseAdapter.getDb();
////                PageDao pageDao = new PageDao(db);
////                page = pageDao.findPage(pageId);
////            }
////            finally {
////                databaseAdapter.close();
////            }
////
////            return page;
//            return  pageInstance;
//        }
//
//        @Override
//        protected void onPostExecute(PageInstance page) {
//
//            LinkedHashMap<String, WidgetInstance> widgetInstances = page.getWidgets();
//
//            for(Map.Entry<String, WidgetInstance> entry : widgetInstances.entrySet()){
//                WidgetInstance widgetInstance = entry.getValue();
//
//                WidgetFragment widgetFragment = NavigationUtil.checkWidgetType(widgetInstance.getType());
//                Bundle bundle = new Bundle();
//                bundle.putString(Constant.UI_EXTRA_PATH, DataManager.getInstance().getAppContentInstance().buildPath(project , page, widgetInstance));
//
//            }
//
//
//            if (page != null) {
//                LinearLayout linearLayoutPageInfo = (LinearLayout) getView().findViewById(R.id.pageInfo);
//                DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
//
//                try {
//                    databaseAdapter.openWritableDatabase();
//                    WidgetDao widgetDao = new WidgetDao(databaseAdapter.getDb());
//                    ArrayList<WidgetLinker> widgetLinkers = page.getWidgets();
//
//                    final int screenSize = 4;
//                    int sizeLeft = screenSize;
//                    int lastSizeLeft = sizeLeft;
//
//
//                    LinearLayout linearLayoutHorizontal = ViewFactory.createLinearLayout(getActivity(), LinearLayout.HORIZONTAL,
//                            new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//                    for (WidgetLinker widgetLinker : widgetLinkers) {
//                        WidgetTemplate widget = widgetDao.findWidget(widgetLinker.getWidgetid());
//                        int weight = 0;
//                        Map<String, String> layouts = widgetLinker.getLayout();
//
//                        if (widgetLinker.getLayout() != null) {
//                            String width = layouts.get("width"); //TODO create a constant
//                            int indexOf = width.indexOf("of");
//                            weight = Integer.valueOf(width.substring(0, indexOf).trim());
//
//                            int tempLastSizeLeft = lastSizeLeft;
//                            if(lastSizeLeft == 0)
//                                lastSizeLeft = screenSize;
//
//                            if((screenSize - lastSizeLeft) + weight > screenSize){
//                                final TextView emptyText = ViewFactory.createTextView(getActivity(), R.style.Base_TextAppearance_AppCompat,
//                                        new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, tempLastSizeLeft),
//                                        null);
////                                emptyText.setBackgroundColor(new Random().nextInt());
//                                linearLayoutHorizontal.addView(emptyText);
//                                lastSizeLeft = screenSize;
//                            }
//
//                            sizeLeft = (lastSizeLeft - weight) % screenSize;
//                            Log.d(TAG, "The weight: " + weight);
//                        }
//                        else {
//                            sizeLeft = (lastSizeLeft - screenSize) % screenSize;
//                        }
//
//                        sizeLeft = Math.abs(sizeLeft);
//
//
//                        int linearLayoutWidth = LinearLayout.LayoutParams.MATCH_PARENT;
//                        if (weight != 0 && weight != screenSize)
//                              linearLayoutWidth = 0;
//
//
//                        if (lastSizeLeft == 0 || lastSizeLeft == screenSize) {
//                            linearLayoutHorizontal = ViewFactory.createLinearLayout(getActivity(), LinearLayout.HORIZONTAL,
//                                    new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//
//                        }
//
//                        linearLayoutPageInfo.removeView(linearLayoutHorizontal); //TODO do this in a better way
//                        linearLayoutPageInfo.addView(linearLayoutHorizontal);
//
//
//                        final TextView widgetParam = ViewFactory.createTextView(getActivity(), R.style.Base_TextAppearance_AppCompat,
//                                new LinearLayout.LayoutParams(linearLayoutWidth, LinearLayout.LayoutParams.WRAP_CONTENT, weight),
//                                null);
//                        widgetParam.setBackgroundColor(new Random().nextInt());
//                        FocusSampleDataMap config = widget.getConfig();
//
//                        if (config != null) {
//                            String text = null;
//                            for (Map.Entry<String, Object> entry : config.entrySet()) {
//                                text = entry.getValue().toString();
//                            }
//                            widgetParam.setText(text);
//                        }
//
//                        linearLayoutHorizontal.addView(widgetParam);
//
//                        lastSizeLeft = sizeLeft;
//
//                    }
//
//                    if (lastSizeLeft != 0) {
//                            final TextView emptyText = ViewFactory.createTextView(getActivity(), R.style.Base_TextAppearance_AppCompat,
//                                    new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, lastSizeLeft),
//                                    null);
////                            emptyText.setBackgroundColor(new Random().nextInt());
//                            linearLayoutHorizontal.addView(emptyText);
//
//                    }
//                }
//                finally {
//                    databaseAdapter.close();
//                }
//            }
//        }
//    }
}
