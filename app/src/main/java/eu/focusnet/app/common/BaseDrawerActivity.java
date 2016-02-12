package eu.focusnet.app.common;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.adapter.DrawerListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;

/**
 * Created by yandypiedra on 17.11.15.
 */
public abstract class BaseDrawerActivity extends BaseActivity
{

	protected DrawerLayout drawerLayout;
	protected ListView drawerListMenu;
	protected ActionBarDrawerToggle drawerToggle;
	protected ArrayList<AbstractListItem> drawerItems;
	// nav drawer title
	private CharSequence drawerTitle;
	// used to store app title
	private CharSequence savedTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		drawerLayout = (DrawerLayout) findViewById(getDrawerLayout());
		drawerListMenu = (ListView) findViewById(getDrawerList());
		drawerItems = getDrawerItems();
		drawerListMenu.setOnItemClickListener(getOnClickListener());

		// setting the nav drawer list adapter
		DrawerListAdapter adapter = new DrawerListAdapter(getApplicationContext(), drawerItems);
		drawerListMenu.setAdapter(adapter);

		//save the title
		savedTitle = drawerTitle = getTitle();

		drawerToggle = new ActionBarDrawerToggle(
				this,               /* host Activity */
				drawerLayout,      /* DrawerLayout object */
				toolbar,
				R.string.app_name, /* "openWritableDatabase drawer" description */
				R.string.app_name /* "close drawer" description */
		)
		{
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				setTitle(savedTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu();
			}

			/** Called when a drawer has settled in a completely openWritableDatabase state. */
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				setTitle(drawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu();
			}
		};

		drawerToggle.setDrawerIndicatorEnabled(true);

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(drawerToggle);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// toggle nav drawer on selecting action bar app icon/title
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		//TODO analise this
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerListMenu);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void setTitle(CharSequence title)
	{
		savedTitle = title;
		toolbar.setTitle(savedTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	protected abstract int getContentView();


	@Override
	protected boolean isDisplayHomeAsUpEnabled()
	{
		return false;
	}

	@Override
	protected boolean isHomeButtonEnabled()
	{
		return false;
	}

	protected abstract ArrayList<AbstractListItem> getDrawerItems();

	protected abstract int getDrawerLayout();

	protected abstract int getDrawerList();

	protected abstract ListView.OnItemClickListener getOnClickListener();
}
