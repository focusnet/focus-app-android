package eu.focusnet.app.activity;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import eu.focusnet.app.R;

import java.util.Date;

import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.common.BaseDrawerActivity;
import eu.focusnet.app.fragment.BookmarkFragment;
import eu.focusnet.app.fragment.FocusFragment;
import eu.focusnet.app.fragment.SettingFragment;
import eu.focusnet.app.fragment.SynchronizeFragment;
import eu.focusnet.app.fragment.UserManualFragment;
import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.model.focus.User;
import eu.focusnet.app.model.ui.DrawerListItem;
import eu.focusnet.app.model.ui.HeaderDrawerListItem;
import eu.focusnet.app.manager.FragmentManager;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.ViewUtil;

/**
 * This Activity contains the list of available projects.
 */
public class FocusActivity extends BaseDrawerActivity
{

	private static final String TAG = FocusActivity.class.getName();
	private String[] navMenuTitles;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//TODO remove this when the application is finished
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectAll()   // or .detectAll() for all detectable problems
				.penaltyLog()
				.build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
				.detectAll()
				.penaltyLog()
				.build());


		//TODO change this   //////////////////////////////////////////////////////////////////////////////////////////////
//        if (savedInstanceState == null) {
//            // on first time display view for first nav item
		showView(Constant.FOCUS_FRAGMENT);
//                Util.displayToast(this, "First name: " + user.getFirstName() + ", last name :" + user.getLastName());
//        }
//
		//TODO change this
		//     else if(extras != null){
//            if(extras.get(Constant.USER_DATA) != null){
//                ArrayList<String> data = (ArrayList)extras.get(Constant.USER_DATA);
//                Log.d(TAG, data.get(0));
//                Util.displayToast(this, data.get(0));
//                showView(Constant.FOCUS_FRAGMENT);
//            }
//            else {
		//if started from a notification display the appropriate fragment
		//              showView(extras.getInt(Constant.NOTIFICATION_ID));
//            }
//        }
		//////////////////////////////////////////////////////////////////////////////////////////////
	}


	@Override
	protected int getContentView()
	{
		return R.layout.activity_focus;
	}

	@Override
	protected ArrayList<AbstractListItem> getDrawerItems()
	{
		// load menu items
		navMenuTitles = getResources().getStringArray(R.array.drawer_items);

		// load menu icons
		TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.drawer_icons);

		ArrayList<AbstractListItem> drawerItems = new ArrayList<AbstractListItem>();

		//Get the user data from the bundle
		//	Bundle extras = getIntent().getExtras();
		//User user = (User) extras.getSerializable(Constant.USER_DATA);

		DataManager dm = DataManager.getInstance();
		User user = dm.getUser();


		drawerItems.add(new HeaderDrawerListItem(ViewUtil.getBitmap(this, R.drawable.focus_logo_small), user.getFirstName() + " " + user.getLastName(), user.getCompany(), user.getEmail()));

		ViewUtil.displayToast(this, "First name: " + user.getFirstName() + ", last name :" + user.getLastName()); //TODO remove this when app is finished

		for (int i = 0; i < navMenuTitles.length; i++) {
			String menuTitle = navMenuTitles[i];
			DrawerListItem drawListItem = new DrawerListItem(ViewUtil.getBitmap(this, navMenuIcons.getResourceId(i, -1)), menuTitle, null); //Null for info
			//find out the synchronize menu
			if (menuTitle.equals(getResources().getString(R.string.drawer_menu_synchronize))) {
				//TODO set the synchronized info
				String lastUpdate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
				drawListItem.setInfo(getResources().getString(R.string.drawer_menu_update) + " " + lastUpdate);
			}
			drawerItems.add(drawListItem);
		}

		// Recycle the typed array
		navMenuIcons.recycle();

		return drawerItems;
	}

	@Override
	protected int getDrawerLayout()
	{
		return R.id.drawer_layout;
	}

	@Override
	protected int getDrawerList()
	{
		return R.id.drawer_list_menu;
	}

	@Override
	protected ListView.OnItemClickListener getOnClickListener()
	{
		return new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				//display view for selected nav drawer item
				//  ((TextView)view.findViewById(R.id.title)).setTextColor(getResources().getColor(R.color.app_color));
				showView(position);
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}


	@Override
	public void onBackPressed()
	{
		//Navigate back to the last Fragment or exit the application
		Log.d(TAG, "Back button pressed");
		android.app.FragmentManager fragmentMng = getFragmentManager();
		int fragmentBackStackNumber = fragmentMng.getBackStackEntryCount();
		// If we have more than one fragments in the stack remove the last inserted
		if (fragmentBackStackNumber > 1) {
			Log.d(TAG, "Number of fragment on the stack: " + fragmentBackStackNumber);
			//remove the last inserted fragment from the stack
			fragmentMng.popBackStackImmediate();

			//Get the current fragment
			Fragment fragment = (Fragment) FragmentManager.getCurrentFragment(fragmentMng);

			Log.d(TAG, "Getting the fragment's argument");
			Bundle bundle = fragment.getArguments();

			String title = (String) bundle.get(Constant.FRAGMENT_TITLE);
			Log.d(TAG, "Title :" + title);
			// Set title
			setTitle(title);

			int position = (int) bundle.get(Constant.FRAGMENT_POSITION);
			Log.d(TAG, "Position :" + position);
			// Highlight the item
			highlightSelectedMenuItem(position);

			//In case the drawer menu is openWritableDatabase and the user click the back button,
			// the drawer menu will be closed
			drawerLayout.closeDrawer(drawerListMenu);
		}
		else {
			Log.d(TAG, "There is only one fragment in the stack, the application will exit");
			// Exit the application
			super.onBackPressed();
		}
	}

//    @Override
//    protected void onResume() {
//        super.onResume();
//    }

//    @Override
//    protected void onPause() {
//    //   databaseAdapter.close();
//        super.onPause();
//    }

//    @Override
//    protected void onDestroy() {
////        databaseAdapter.openWritableDatabase();
////        userDao = new UserDAO(databaseAdapter.getDb());
////        PreferenceDAO preferenceDAO = new PreferenceDAO(databaseAdapter.getDb());
////        if(userDao.deleteUser(new Long(1)) && preferenceDAO.deletePreference(new Long(1))) {
////            Log.d(TAG, "USER AND PREFERENCES DELETED");
////        }
////        else {
////            Log.d(TAG, "EITHER USER OR PREFERENCES NOT DELETE");
////        }
////        databaseAdapter.close();
//
//        super.onDestroy();
//    }


	/**
	 * Show the selected fragment when the user click on the drawer layout
	 *
	 * @param position position click in the drawer layout
	 */
	private void showView(int position)
	{
		Fragment fragment = null;

		switch (position) {
			case Constant.FOCUS_FRAGMENT:
				fragment = new FocusFragment();
				break;
			case Constant.BOOKMARK_FRAGMENT:
				fragment = new BookmarkFragment();
				break;
			case Constant.SYNCHRONIZE_FRAGMENT:
				fragment = new SynchronizeFragment();
				break;
			case Constant.SETTING_FRAGMENT:
				fragment = new SettingFragment();
				break;
			case Constant.USER_MANUAL_FRAGMENT:
				fragment = new UserManualFragment();
				break;
			default:
				break;
		}

		if (fragment != null) {
			int effectivePosition = position - 1;
			String title = navMenuTitles[effectivePosition];
			Bundle bundle = new Bundle();
			bundle.putString(Constant.FRAGMENT_TITLE, title);
			bundle.putInt(Constant.FRAGMENT_POSITION, effectivePosition);
			fragment.setArguments(bundle);
			FragmentManager.replaceFragment(R.id.frame_container, fragment, getFragmentManager());
			// Highlight the selected item
			highlightSelectedMenuItem(position);
			//set title
			setTitle(title);
			drawerLayout.closeDrawer(drawerListMenu);
		}
	}

	private void highlightSelectedMenuItem(int position)
	{
		// Highlight the selected item
		drawerListMenu.setItemChecked(position, true);
		drawerListMenu.setSelection(position);


	}
}
