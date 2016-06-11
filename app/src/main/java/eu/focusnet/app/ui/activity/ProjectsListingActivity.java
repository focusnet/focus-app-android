/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.activity;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eu.focusnet.app.BuildConfig;
import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.service.CronService;
import eu.focusnet.app.ui.adapter.DrawerListAdapter;
import eu.focusnet.app.ui.common.AbstractListItem;
import eu.focusnet.app.ui.common.DrawerListItem;
import eu.focusnet.app.ui.common.FocusDialogBuilder;
import eu.focusnet.app.ui.common.HeaderDrawerListItem;
import eu.focusnet.app.ui.fragment.AboutFragment;
import eu.focusnet.app.ui.fragment.BookmarkFragment;
import eu.focusnet.app.ui.fragment.ProjectsListingFragment;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.FragmentManager;
import eu.focusnet.app.ui.util.UiHelper;

/**
 * This Activity contains the list of available projects.
 */
public class ProjectsListingActivity extends ToolbarEnabledActivity
{
	protected DrawerLayout drawerLayout;
	protected ListView drawerListMenu;
	protected ActionBarDrawerToggle drawerToggle;
	protected ArrayList<AbstractListItem> drawerItems;
	private CharSequence drawerTitle;
	private CharSequence savedTitle;
	private int sectionToRender;
	private String[] navMenuTitles;
	private CronService cronService;
	private boolean cronBound = false;
	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection cronServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className,
									   IBinder service)
		{
			// We've bound to LocalService, cast the IBinder and get CronService instance
			CronService.CronBinder binder = (CronService.CronBinder) service;
			cronService = binder.getService();
			cronBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			cronBound = false;
		}
	};


	/**
	 * Create the activity.
	 *
	 * @param savedInstanceState past state
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// this method will take care of building the UI and rendering, using later
		// overriden methods
		super.onCreate(savedInstanceState);

		// bind cron service
		Intent intent = new Intent(this, CronService.class);
		bindService(intent, this.cronServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		// unbind cron service
		if (this.cronBound) {
			unbindService(this.cronServiceConnection);
			this.cronBound = false;
		}
	}


	/**
	 * Get the current activity's view
	 *
	 * @return The view
	 */
	@Override
	protected int getTargetView()
	{
		return R.layout.activity_projects_listing;
	}


	@Override
	protected int getTargetLayoutContainer()
	{
		return R.id.focus_drawer_content_container;
	}


	// FIXME problems with the title. to be checked.
	@Override
	public void setTitle(CharSequence title)
	{
		savedTitle = title;
		toolbar.setTitle(savedTitle);
	}



	// called just after setting view
	@Override
	protected void setupSpecificUiElements()
	{
		// set the default section that will initially be loaded
		this.sectionToRender = Constant.UI_MENU_ENTRY_PROJECTS_LISTING;

		// create a Drawer
		this.drawerItems = this.getDrawerItems();
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerListMenu = (ListView) findViewById(R.id.focus_drawer);
		this.drawerLayout.bringToFront();
		this.drawerListMenu.bringToFront();
		this.drawerListMenu.setOnItemClickListener(this.getOnClickDrawerItemListener());

		// Assign a ListAdapter to our Drawer
		DrawerListAdapter adapter = new DrawerListAdapter(getApplicationContext(), this.drawerItems);
		drawerListMenu.setAdapter(adapter);

		// Save the title // FIXME problems with title
		this.savedTitle = this.drawerTitle = getTitle();

		this.drawerToggle = new ActionBarDrawerToggle(
				this,
				this.drawerLayout,
				this.toolbar,
				R.string.focus_drawer_open_description,
				R.string.focus_drawer_close_description
		);


		// overrides getSupportActionBar().setDisplayHomeAsUpEnabled
		this.drawerToggle.setDrawerIndicatorEnabled(true);

		// Set the drawer toggle as the DrawerListener
		this.drawerLayout.addDrawerListener(drawerToggle);

		// set a subtitle
		this.actionBar.setSubtitle(
				FocusApplication.getInstance().getDataManager().getAppContentInstance().getTitle()
		);
	}

	@Override
	protected void doInPageUiOperations()
	{
		switch (this.sectionToRender) {
			case Constant.UI_MENU_ENTRY_ABOUT: // this fragment will not replace
				AboutFragment f = new AboutFragment();
				f.show(getSupportFragmentManager(), getString(R.string.about_focus));
				drawerLayout.closeDrawer(drawerListMenu); // FIXME would be better to have a proper page.
				break;
			case Constant.UI_MENU_ENTRY_LOGOUT:
				final Thread logoutThread = new Thread()
				{
					public void run()
					{
						FocusApplication.getInstance().getDataManager().logout();
						try {
							Intent i = new Intent(ProjectsListingActivity.this, EntryPointActivity.class);
							i.putExtra(Constant.UI_EXTRA_LOADING_INFO_TEXT, getString(R.string.wiping_user_data_logout_msg));
							startActivity(i);
						}
						finally {
							finish();
						}
					}
				};
				logoutThread.start();
				break;
			default:
				break;
		}

		highlightSelectedMenuItem(this.sectionToRender);
		drawerLayout.closeDrawer(drawerListMenu); // should be after replaceFragment

	}

	@Override
	protected void prepareNewFragment()
	{
		switch (this.sectionToRender) {
			case Constant.UI_MENU_ENTRY_PROJECTS_LISTING:
				this.fragment = new ProjectsListingFragment();
				if (this.actionBar != null) {
					this.actionBar.setTitle(R.string.project_listing_title);
				}
				break;
			case Constant.UI_MENU_ENTRY_BOOKMARK:
				this.fragment = new BookmarkFragment(); // FIXME clicking on BOOKMARKS in Menu -> MY FOCUS is displayed, but not the subtitle
				if (this.actionBar != null) {
					this.actionBar.setTitle(R.string.bookmarks_title);
				}
				break;
			default:
				break;
		}
	}





	/**
	 * Register a click listener for drawer items
	 *
	 * @return a ListView
	 *
	 * // FIXME the header (image + infos) is clickable, which may not be good.
	 */
	private ListView.OnItemClickListener getOnClickDrawerItemListener()
	{
		return new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				sectionToRender = position;
				applyUiChanges();
			}
		};
	}


	/**
	 * Listener for the back button
	 *
	 * FIXME if we have more than 2 menu entries in the Drawer that load their fragments in the
	 * main content, that may be more complicated.
	 */
	@Override
	public void onBackPressed()
	{
		//Navigate back to the last Fragment or exit the application
		android.app.FragmentManager fragmentManager = getFragmentManager();
		// If we have more than one fragments in the stack remove the last inserted
		// This is the case where we move from Bookmarks back to the home page via the BACK button
		if (fragmentManager.getBackStackEntryCount() > 1) {
			//remove the last inserted fragment from the stack
			fragmentManager.popBackStackImmediate();

			// hard coded things is ok as we are only playing with 2 possible targets, and
			// hence hitting the BACK button will always make us go back to the home page
			// (if we hit the BACK button on the home page, we exit the app)
			this.actionBar.setTitle(R.string.project_listing_title);
			highlightSelectedMenuItem(Constant.UI_MENU_ENTRY_PROJECTS_LISTING);

			// close the drawer
			this.drawerLayout.closeDrawer(drawerListMenu);
		}
		else {
			// Exit the application
			super.onBackPressed();
		}
	}





	/**
	 * Highlight the selected menu item
	 *
	 * @param position
	 */
	private void highlightSelectedMenuItem(int position)
	{
		this.drawerListMenu.setItemChecked(position, true);
		this.drawerListMenu.setSelection(position);
	}


	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged().
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		this.drawerToggle.onConfigurationChanged(newConfig);
	}


	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged().
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		this.drawerToggle.syncState();
	}



	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.action_sync:

				if (!this.cronBound) {
					return false;
				}

				LayoutInflater inflater = LayoutInflater.from(this);
				final LinearLayout dialogContent = (LinearLayout) inflater.inflate(R.layout.dialog_content_synchronization, null);

				final Context context = this;
				final FocusDialogBuilder builder = new FocusDialogBuilder(context)
						.removeNeutralButton()
						.setNegativeButtonText(getString(R.string.cancel))
						.setPositiveButtonText(getString(R.string.start))
						.setCancelable(false)
						.insertContent(dialogContent)
						.setTitle(getString(R.string.data_sync_title));
				final AlertDialog dialog = builder.create();
				dialog.show();

				// Cancelation listener
				builder.getNegativeButton().setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						builder.setNegativeButtonText(getString(R.string.sync_continue_background));
						dialog.dismiss();
					}
				});

				final View instructions = dialogContent.findViewById(R.id.dialog_sync_instructions);
				final View progress = dialogContent.findViewById(R.id.dialog_sync_progress);
				final View status = dialogContent.findViewById(R.id.dialog_sync_status);
				final TextView instructionsField = (TextView) instructions.findViewById(R.id.dialog_sync_instructions_msg);
				final TextView progressField = (TextView) progress.findViewById(R.id.dialog_sync_progress_field);
				final TextView statusField = (TextView) status.findViewById(R.id.dialog_sync_status_field);

				// update dynamic content (last sync and last data volume)
				String lastSync;
				if (this.cronService.getLastSync() == 0) {
					lastSync = getString(R.string.n_a);
				}
				else {
					SimpleDateFormat dateFormat = new SimpleDateFormat(eu.focusnet.app.model.util.Constant.DATE_FORMAT);
					lastSync = dateFormat.format(new Date(this.cronService.getLastSync()));
				}
				TextView lastSyncField = (TextView) instructions.findViewById(R.id.dialog_sync_last_sync_field);
				lastSyncField.setText(getString(R.string.last_sync_label) + lastSync);
				TextView lastDataVolumeField = (TextView) instructions.findViewById(R.id.dialog_sync_data_volume_field);
				long rawDbSize = FocusApplication.getInstance().getDataManager().getDatabaseSize();
				lastDataVolumeField.setText(getString(R.string.last_sync_data_volume_label) + (rawDbSize == 0 ? "N/A" : UiHelper.getFileSize(rawDbSize)));


				// too early since last sync
				// display instructions with custom message, disable START
				if (cronService.getLastSync() >= System.currentTimeMillis() - CronService.CRON_SERVICE_MINIMUM_DURATION_BETWEEN_SYNC_DATA_IN_MS) {
					instructionsField.setText(R.string.sync_too_recent);
					builder.removePositiveButton();
					builder.setNegativeButtonText(getString(R.string.close));
				}
				// ongoing sync
				// display status and remove START
				else if (this.cronService.getSyncInProgress()) {
					builder.removePositiveButton();
					builder.setNegativeButtonText(getString(R.string.close));

					statusField.setText(R.string.sync_already_in_progress);
					statusField.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));

					instructions.setVisibility(View.GONE);
					status.setVisibility(View.VISIBLE);
				}
				// Otherwise, set information about last synchronization and data volume and expect the user to start a manual synchronization
				else {
					// create START listener
					builder.getPositiveButton().setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							builder.removePositiveButton();
							builder.setNegativeButtonText(getString(R.string.sync_continue_background));
							instructions.setVisibility(View.GONE);
							progress.setVisibility(View.VISIBLE);

							// we careful, cannot start if already started !!! FIXME
							new SyncTask(builder, dialogContent).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
					});
				}
				return true;

			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				// toggle nav drawer on selecting action bar app icon/title
				if (drawerToggle.onOptionsItemSelected(item)) {
					return true;
				}
				// Handle action bar actions click
				//TODO YANDY analyze this
				switch (item.getItemId()) {
					default:
						return super.onOptionsItemSelected(item);
				}
		}
	}

	/**
	 * Create the option menu of the app
	 *
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
	 * Task responsible for synchronizing data
	 * <p/>
	 * Called when the user explicitly launches data sync via the dialog
	 */
	private class SyncTask extends AsyncTask<Void, Void, Void>
	{

		private final LinearLayout dialogContent;
		private final FocusDialogBuilder builder;

		public SyncTask(FocusDialogBuilder builder, LinearLayout dialogContent)
		{
			this.builder = builder;
			this.dialogContent = dialogContent;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			//if (1==1) return null;
			if (!cronBound) {
				return null;
			}
			boolean newSync = cronService.manuallySyncData();

			// if already started (periodic execution), let's just wait for completion
			if (!newSync) {
				while (cronService.getSyncInProgress()) {
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException ignored) {

					}
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void v)
		{
			// setStatus()
			final View progress = this.dialogContent.findViewById(R.id.dialog_sync_progress);
			final View status = this.dialogContent.findViewById(R.id.dialog_sync_status);
			final TextView statusField = (TextView) status.findViewById(R.id.dialog_sync_status_field);
			progress.setVisibility(View.GONE);
			status.setVisibility(View.VISIBLE);
			statusField.setText(R.string.done);
			statusField.setTextColor(getResources().getColor(R.color.colorPrimary));

			this.builder.getNegativeButton().setText(R.string.close);
		}
	}


	/**
	 * This method gives a list of items to display in the left drawer.
	 *
	 * @return An array list of items to display in the drawer
	 *
	 * FIXME I don't liek this method. see if can be improved.
	 */
	private ArrayList<AbstractListItem> getDrawerItems()
	{
		// load menu items
		navMenuTitles = getResources().getStringArray(R.array.drawer_items);

		// load menu icons
		TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.drawer_icons);

		ArrayList<AbstractListItem> drawerItems = new ArrayList<>();

		String appVersion = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME;

		// FIXME if not in Demo mode we should use be the below line - but we should rewrite this UI component anyway
		// User user = FocusApplication.getInstance().getDataManager().getUser();
		// drawerItems.add(new HeaderDrawerListItem(UiHelper.getBitmap(this, R.drawable.focus_logo_small), user.getFirstName() + " " + user.getLastName(), user.getCompany(), user.getEmail()));
		HeaderDrawerListItem header = new HeaderDrawerListItem(UiHelper.getBitmap(this, R.drawable.focus_logo_small), appVersion, "", "");
		drawerItems.add(header);

		for (int i = 0; i < navMenuTitles.length; i++) {
			String menuTitle = navMenuTitles[i];
			DrawerListItem drawListItem = new DrawerListItem(UiHelper.getBitmap(this, navMenuIcons.getResourceId(i, -1)), menuTitle, null); //Null for info
			drawerItems.add(drawListItem);
		}

		// Recycle the typed array
		navMenuIcons.recycle();

		return drawerItems;
	}



}
