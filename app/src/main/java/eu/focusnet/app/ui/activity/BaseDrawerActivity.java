/**
 *
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package eu.focusnet.app.ui.activity;

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
import eu.focusnet.app.ui.adapter.DrawerListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;

/**
 * This Activity is the common frame for activities that do contain a left drawer.
 */
public abstract class BaseDrawerActivity extends BaseActivity
{
	protected DrawerLayout drawerLayout;
	protected ListView drawerListMenu;
	protected ActionBarDrawerToggle drawerToggle;
	protected ArrayList<AbstractListItem> drawerItems;
	private CharSequence drawerTitle;
	private CharSequence savedTitle;

	/**
	 * Create the Activity. Create the UI.
	 * @param savedInstanceState past state
	 */
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
				this,
				drawerLayout,
				toolbar,
				R.string.focus_drawer_open_description,
				R.string.focus_drawer_close_description
		)
		{
			/**
			 * Called when a drawer has settled in a completely closed state.
			 */
			public void onDrawerClosed(View view)
			{
				super.onDrawerClosed(view);
				setTitle(savedTitle);
				// calling onPrepareOptionsMenu() to show action bar icons // FIXME YANDY still something to do here, or can we delete this comment?
				invalidateOptionsMenu();
			}

			/**
			 * Called when a drawer has settled in a completely open state.
			 */
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				setTitle(drawerTitle);
				// calling onPrepareOptionsMenu() // FIXME YANDY still something to do here, or can we delete this comment?
				invalidateOptionsMenu();
			}
		};

		drawerToggle.setDrawerIndicatorEnabled(true);

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(drawerToggle);
	}

	/**
	 * Triggered when an item is selected
	 *
	 * @param item the item being selected
	 * @return Return false to allow normal menu processing to
	 *         proceed, true to consume it here.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// toggle nav drawer on selecting action bar app icon/title
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		//TODO YANDY analyze this
		switch (item.getItemId()) {
			case R.id.action_settings:
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/**
	 *  Called whenever we call invalidateOptionsMenu()
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = drawerLayout.isDrawerOpen(drawerListMenu);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Set the drawer title
	 *
	 * @param title Title to set
	 */
	@Override
	public void setTitle(CharSequence title)
	{
		savedTitle = title;
		toolbar.setTitle(savedTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged().
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		drawerToggle.syncState();
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged().
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggle
		drawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Get the view to pass to the content part of the Activity
	 *
	 * @return the view ID
	 */
	@Override
	protected abstract int getContentView();


	/**
	 * Is the "up" affordance enabled?
	 * @return true if so, false otherwise
	 *
	 */
	@Override
	protected boolean isDisplayHomeAsUpEnabled()
	{
		return false;
	}

	/**
	 * Tells whether the home button is enabled.
	 *
	 * @return true if so, false otherwise
	 */
	@Override
	protected boolean isHomeButtonEnabled()
	{
		return false;
	}

	/**
	 * Retrieve the list of drawer items
	 *
	 * @return the list of drawer items
	 */
	protected abstract ArrayList<AbstractListItem> getDrawerItems();

	/**
	 * Get the drawer layout
	 *
	 * @return the layout ID
	 */
	protected abstract int getDrawerLayout();

	/**
	 * Get the drawer list
	 *
	 * @return the list ID
	 */
	protected abstract int getDrawerList();

	/**
	 * The listener being triggered when clicking an item.
	 *
	 * @return an OnItemClickListener
	 */
	protected abstract ListView.OnItemClickListener getOnClickListener();
}
