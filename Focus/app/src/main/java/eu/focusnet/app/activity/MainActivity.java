package eu.focusnet.app.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import eu.focusnet.app.fragment.ProjectFragment;
import eu.focusnet.app.fragment.SettingFragment;
import eu.focusnet.app.fragment.SynchronizeFragment;
import eu.focusnet.app.fragment.UserManualFragment;
import eu.focusnet.app.model.ui.HeaderDrawerListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.Util;

/**
 * Created by admin on 15.06.2015.
 */
public class MainActivity extends AppCompatActivity  {

    private static final String TAG  = MainActivity.class.getName();

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

        setContentView(R.layout.activity_main);

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
        drawerItems.add(new HeaderDrawerListItem(Util.getBitmap(this, R.drawable.focus_logo_small), "John Smith", "Company ABC", "js@example.com"));

        for(int i = 0; i < navMenuTitles.length; i++){
            String menuTitle = navMenuTitles[i];
            StandardListItem drawListItem = new StandardListItem(Util.getBitmap(this, navMenuIcons.getResourceId(i, -1)), menuTitle, null, null); //Null for Info and left icon
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
                R.string.app_name, /* "open drawer" description */
                R.string.app_name /* "close drawer" description */
        ) {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(savedTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }
            /** Called when a drawer has settled in a completely open state. */
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

        Bundle extras = getIntent().getExtras();
        if (savedInstanceState == null && extras == null) {
            // on first time display view for first nav item
            showView(1);
        }
        //if started from a notification display the appropriate fragment
        else if(extras != null && new Integer(extras.getInt("notificationId")) != null){
            showView(extras.getInt("notificationId"));
        }
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
        //Navigate back to the ProjectFragment or exit the application
        Log.d(TAG, "Back button pressed");
        FragmentManager fragmentMng = getFragmentManager();

        int fragmentBackStackNumber = fragmentMng.getBackStackEntryCount();
        //If we have 2 fragments in the stack remove the last inserted
        if(fragmentBackStackNumber == 2){
            Log.d(TAG, "Number of fragment on the stack: " + fragmentBackStackNumber);
            //remove the last inserted fragment from the stack
            fragmentMng.popBackStack();
            // Highlight the item (ProjectFragment)
             highlightSelectedMenuItem(Constant.PROJECT_FRAGMENT);
            //Set title
            setTitle(navMenuTitles[Constant.PROJECT_FRAGMENT - 1]);
            drawerLayout.closeDrawer(drawerListMenu);
        }
        else{
            Log.d(TAG, "There are NOT fragments on the stack or only 1");
            //Exit the application
            super.onBackPressed();
        }
    }

    private void showView(int position) {
        Fragment fragment = null;
        switch (position) {
            case Constant.PROJECT_FRAGMENT:
                fragment = new ProjectFragment();
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

        //TODO this should be done in a better way
        FragmentManager fragmentManager = getFragmentManager();
        int fragmentBackStackNumber = fragmentManager.getBackStackEntryCount();
        //If we have two fragments in the stack remove the last inserted
        //so that we only have one fragment in the stack(the ProjectFragment)
        if(fragmentBackStackNumber == 2) {
            //remove the last inserted fragment from the stack
            fragmentManager.popBackStack();
        }

        if(fragment != null) {
            FragmentTransaction fragTrans = fragmentManager.beginTransaction();
            fragTrans.replace(R.id.frame_container, fragment).addToBackStack("FragmentBackStack").commit();

            // Highlight the selected item
            highlightSelectedMenuItem(position);
            //set title
            setTitle(navMenuTitles[position - 1]);
            drawerLayout.closeDrawer(drawerListMenu);
        }
    }

    /**
     * Slide menu item click listener
     * */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            showView(position);
        }
    }

    private void highlightSelectedMenuItem(int position){
        // Highlight the selected item
        drawerListMenu.setItemChecked(position, true);
        drawerListMenu.setSelection(position);
    }
}
