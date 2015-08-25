package eu.focusnet.app.activity;

import android.app.Fragment;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eu.focusnet.app.adapter.DrawerListAdapter;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.fragment.BookmarkFragment;
import eu.focusnet.app.fragment.FocusFragment;
import eu.focusnet.app.fragment.SettingFragment;
import eu.focusnet.app.fragment.SynchronizeFragment;
import eu.focusnet.app.fragment.UserManualFragment;
import eu.focusnet.app.model.data.User;
import eu.focusnet.app.model.ui.HeaderDrawerListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.manager.FragmentManager;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.GuiUtil;

/**
 * Created by admin on 15.06.2015.
 */
public class FocusActivity extends AppCompatActivity  {

    private static final String TAG  = FocusActivity.class.getName();

    private DrawerLayout drawerLayout;
    private ListView drawerListMenu;
    private ActionBarDrawerToggle drawerToggle;
    // nav drawer title
    private CharSequence drawerTitle;
    // used to store app title
    private CharSequence savedTitle;
    // slide menu items
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;
    private ArrayList<AbstractListItem> drawerItems;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()   // or .detectAll() for all detectable problems
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build());


        setContentView(R.layout.activity_focus);

        //save the title
        savedTitle = drawerTitle = getTitle();

        // load menu items
        navMenuTitles = getResources().getStringArray(R.array.drawer_items);

        // load menu icons
        navMenuIcons = getResources().obtainTypedArray(R.array.drawer_icons);

        // get the drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // get the list for the drawer menu
        drawerListMenu = (ListView) findViewById(R.id.drawer_list_menu);

        drawerItems = new ArrayList<AbstractListItem>();

        Bundle extras = getIntent().getExtras();
        User user = (User) extras.getSerializable(Constant.USER_DATA);

        drawerItems.add(new HeaderDrawerListItem(GuiUtil.getBitmap(this, R.drawable.focus_logo_small), user.getFirstName() + " " + user.getLastName(), user.getCompany(), user.getEmail()));
        GuiUtil.displayToast(this, "First name: " + user.getFirstName() + ", last name :" + user.getLastName()); //TODO remove this when app is finished

        for(int i = 0; i < navMenuTitles.length; i++){
            String menuTitle = navMenuTitles[i];
            StandardListItem drawListItem = new StandardListItem(null, GuiUtil.getBitmap(this, navMenuIcons.getResourceId(i, -1)), menuTitle, null); //Null for id and info
            //find out the synchronize menu
            if(menuTitle.equals(getResources().getString(R.string.drawer_menu_synchronize))) {
                //TODO set the synchronized info
                String lastUpdate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                drawListItem.setInfo(getResources().getString(R.string.drawer_menu_update) + " " + lastUpdate);
            }
            drawerItems.add(drawListItem);
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        //set the listener to the list view
        drawerListMenu.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        DrawerListAdapter adapter = new DrawerListAdapter(getApplicationContext(), drawerItems);

        drawerListMenu.setAdapter(adapter);

        toolbar = (Toolbar)findViewById(R.layout.toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerToggle = new ActionBarDrawerToggle(
                this,               /* host Activity */
                drawerLayout,      /* DrawerLayout object */
                toolbar,
                R.string.app_name, /* "openWritableDatabase drawer" description */
                R.string.app_name /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(savedTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
            /** Called when a drawer has settled in a completely openWritableDatabase state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setTitle(drawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action bar actions click
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = drawerLayout.isDrawerOpen(drawerListMenu);
        menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        savedTitle = title;
        getSupportActionBar().setTitle(savedTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggle
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        //Navigate back to the last Fragment or exit the application
        Log.d(TAG, "Back button pressed");
        android.app.FragmentManager fragmentMng = getFragmentManager();
        int fragmentBackStackNumber = fragmentMng.getBackStackEntryCount();
        // If we have more than one fragments in the stack remove the last inserted
        if(fragmentBackStackNumber > 1){
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
        else{
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

    private void showView(int position) {
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

        if(fragment != null) {
            int effectivePosition = position - 1;
            String title = navMenuTitles[effectivePosition];
            Bundle bundle = new Bundle();
            bundle.putString(Constant.FRAGMENT_TITLE, title);
            bundle.putInt(Constant.FRAGMENT_POSITION, effectivePosition);
            fragment.setArguments(bundle);
            FragmentManager.replaceFragment(R.id.frame_container, fragment, getFragmentManager());
            // Highlight the selected item
            highlightSelectedMenuItem(effectivePosition);
            //set title
            setTitle(title);
            drawerLayout.closeDrawer(drawerListMenu);
        }
    }

    private void highlightSelectedMenuItem(int position){
        // Highlight the selected item
        drawerListMenu.setItemChecked(position + 1, true);
        drawerListMenu.setSelection(position);


    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //display view for selected nav drawer item
            showView(position);
        }
    }
}
