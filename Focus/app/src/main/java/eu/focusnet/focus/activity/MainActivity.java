package eu.focusnet.focus.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import eu.focusnet.focus.adapter.DrawerListAdapter;
import eu.focusnet.focus.fragment.BookmarkFragment;
import eu.focusnet.focus.fragment.ProjectFragment;
import eu.focusnet.focus.fragment.SettingFragment;
import eu.focusnet.focus.fragment.SynchronizeFragment;
import eu.focusnet.focus.fragment.UserManualFragment;
import eu.focusnet.focus.model.DrawerListItem;

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

    private ArrayList<DrawerListItem> drawerItems;
    private DrawerListAdapter adapter;

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

        drawerItems = new ArrayList<DrawerListItem>();

        for(int i = 0; i < navMenuTitles.length; i++){
            String menuTitle = navMenuTitles[i];
            DrawerListItem drawListItem = new DrawerListItem(getBitmap(navMenuIcons.getResourceId(i, -1)), menuTitle);
            //find out the synchronize menu
            if(menuTitle.equals(getResources().getString(R.string.drawer_menu_synchronize))) {
                //TODO set the synchronized info
                String lastUpdate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
                drawListItem.setInfo(getResources().getString(R.string.drawer_menu_update) + " " + lastUpdate);
                drawListItem.setIsInfoVisible(true);
            }
            drawerItems.add(drawListItem);
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        //set the listener to the list view
        drawerListMenu.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new DrawerListAdapter(getApplicationContext(), drawerItems);

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
                getSupportActionBar().setTitle(savedTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(drawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        drawerToggle.setDrawerIndicatorEnabled(true);

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            showView(0);
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


    private void showView(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ProjectFragment();
                break;
            case 1:
                fragment = new BookmarkFragment();
                break;
            case 2:
                fragment = new SynchronizeFragment();
                break;
            case 3:
                fragment = new SettingFragment();
                break;
            case 4:
                fragment = new UserManualFragment();
                break;
            default:
                break;
        }

        if(fragment != null){

            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, fragment).commit();

             // Highlight the selected item, update the title, and close the drawe
              drawerListMenu.setItemChecked(position, true);
              drawerListMenu.setSelection(position);
              setTitle(navMenuTitles[position]);
              drawerLayout.closeDrawer(drawerListMenu);
        }
        else {
            Log.e(TAG, "Error creating Intent");
        }
    }

    private Bitmap getBitmap(int image) {
        return BitmapFactory.decodeResource(getResources(), image);
    }

}
