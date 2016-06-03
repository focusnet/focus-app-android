/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.activity;

import android.app.Fragment;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.json.User;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.ui.common.DrawerListItem;
import eu.focusnet.app.ui.common.HeaderDrawerListItem;
import eu.focusnet.app.ui.fragment.AboutFragment;
import eu.focusnet.app.ui.fragment.BookmarkFragment;
import eu.focusnet.app.ui.fragment.FocusFragment;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.FragmentManager;
import eu.focusnet.app.ui.util.UiHelpers;

/**
 * This Activity contains the list of available projects.
 */
public class ProjectsListingActivity extends BaseDrawerActivity
{
	private String[] navMenuTitles;

	/**
	 * Create the activity.
	 *
	 * @param savedInstanceState past state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		showView(Constant.UI_FRAGMENT_FOCUS);
	}

	/**
	 * Get the current activity's view
	 *
	 * @return The view
	 */
	@Override
	protected int getContentView()
	{
		return R.layout.activity_focus;
	}

	/**
	 * This method gives a list of items to display in the left drawer.
	 *
	 * @return An array list of items to display in the drawer
	 */
	@Override
	protected ArrayList<AbstractListItem> getDrawerItems()
	{
		// load menu items
		navMenuTitles = getResources().getStringArray(R.array.drawer_items);

		// load menu icons
		TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.drawer_icons);

		ArrayList<AbstractListItem> drawerItems = new ArrayList<AbstractListItem>();

		User user = null;
		try {
			user = FocusApplication.getInstance().getDataManager().getUser();
		}
		catch (FocusMissingResourceException ex) {
			throw new FocusInternalErrorException("User object cannot be accessed.");
		}

		drawerItems.add(new HeaderDrawerListItem(UiHelpers.getBitmap(this, R.drawable.focus_logo_small), user.getFirstName() + " " + user.getLastName(), user.getCompany(), user.getEmail()));
		for (int i = 0; i < navMenuTitles.length; i++) {
			String menuTitle = navMenuTitles[i];
			DrawerListItem drawListItem = new DrawerListItem(UiHelpers.getBitmap(this, navMenuIcons.getResourceId(i, -1)), menuTitle, null); //Null for info
			drawerItems.add(drawListItem);
		}

		// Recycle the typed array
		navMenuIcons.recycle();

		return drawerItems;
	}

	/**
	 * Retrieve the drawer layout
	 *
	 * @return the drawer layout ID
	 */
	@Override
	protected int getDrawerLayout()
	{
		return R.id.drawer_layout;
	}

	/**
	 * Retrieve the drawer list
	 *
	 * @return the drawer list ID
	 */
	@Override
	protected int getDrawerList()
	{
		return R.id.drawer_list_menu;
	}

	/**
	 * Register a click listener for drawer items
	 *
	 * @return a ListView
	 */
	@Override
	protected ListView.OnItemClickListener getOnClickListener()
	{
		return new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				showView(position);
			}
		};
	}


	/**
	 * Create the option menu of the app
	 * @param menu the menu
	 * @return true
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_projects_listing, menu);
		return true;
	}

	/**
	 * Listener for the back button
	 *
	 */
	@Override
	public void onBackPressed()
	{
		//Navigate back to the last Fragment or exit the application
		android.app.FragmentManager fragmentManager = getFragmentManager();
		int fragmentBackStackNumber = fragmentManager.getBackStackEntryCount();
		// If we have more than one fragments in the stack remove the last inserted
		if (fragmentBackStackNumber > 1) {
			//remove the last inserted fragment from the stack
			fragmentManager.popBackStackImmediate();

			//Get the current fragment
			Fragment fragment = FragmentManager.getCurrentFragment(fragmentManager);
			Bundle bundle = fragment.getArguments();

			// Set title
			String title = (String) bundle.get(Constant.UI_BUNDLE_FRAGMENT_TITLE);
			setTitle(title);

			// Highlight the item
			int position = (int) bundle.get(Constant.UI_BUNDLE_FRAGMENT_POSITION);
			highlightSelectedMenuItem(position);

			// close the drawer if the user clicks the back button
			drawerLayout.closeDrawer(drawerListMenu);
		}
		else {
			// Exit the application
			super.onBackPressed();
		}
	}

	/**
	 * Show the selected fragment when the user click on the drawer layout
	 *
	 * @param position position click in the drawer layout
	 */
	private void showView(int position)
	{
		Fragment fragment = null;

		switch (position) {
			case Constant.UI_FRAGMENT_FOCUS:
				fragment = new FocusFragment();
				break;
			case Constant.UI_FRAGMENT_BOOKMARK:
				fragment = new BookmarkFragment();
				break;
			case Constant.UI_FRAGMENT_ABOUT:
				AboutFragment f = new AboutFragment();
				f.show(getSupportFragmentManager(), "About FOCUS");
				drawerLayout.closeDrawer(drawerListMenu);
			default:
				break;
		}

		if (fragment != null) {
			int effectivePosition = position - 1;
			String title = navMenuTitles[effectivePosition];
			Bundle bundle = new Bundle();
			bundle.putString(Constant.UI_BUNDLE_FRAGMENT_TITLE, title);
			bundle.putInt(Constant.UI_BUNDLE_FRAGMENT_POSITION, effectivePosition);
			fragment.setArguments(bundle);
			FragmentManager.replaceFragment(R.id.frame_container, fragment, getFragmentManager());

			highlightSelectedMenuItem(position);
			setTitle(title);

			drawerLayout.closeDrawer(drawerListMenu);
		}
	}

	/**
	 * Highlight the selected menu item
	 *
	 * @param position
	 */
	private void highlightSelectedMenuItem(int position)
	{
		// Highlight the selected item
		drawerListMenu.setItemChecked(position, true);
		drawerListMenu.setSelection(position);
	}
}
