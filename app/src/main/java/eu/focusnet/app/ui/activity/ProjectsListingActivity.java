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

import android.app.FragmentManager;
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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eu.focusnet.app.BuildConfig;
import eu.focusnet.app.FocusAppLogic;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.service.CronService;
import eu.focusnet.app.ui.adapter.DrawerListAdapter;
import eu.focusnet.app.ui.common.FocusDialogBuilder;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.fragment.BookmarkFragment;
import eu.focusnet.app.ui.fragment.ProjectsListingFragment;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.UiHelper;
import eu.focusnet.app.util.ApplicationHelper;

/**
 * This Activity contains the list of available projects.
 * <p/>
 * FIXME a bit messy, to be reorganized.
 */
public class ProjectsListingActivity extends ToolbarEnabledActivity
{
	private static final String ASSETS_ABOUT_PAGE = "documentation/about.html";

	protected DrawerLayout drawerLayout;
	protected ListView drawerListMenu;
	protected ActionBarDrawerToggle drawerToggle;
	protected ArrayList<SimpleListItem> drawerItems;
	private int previouslySelectedToRender;
	private int sectionToRender;
	private CronService cronService;
	private boolean cronBound = false;
	/**
	 * Defines callbacks for service binding, passed to bindService()
	 */
	private ServiceConnection cronServiceConnection = new ServiceConnection()
	{
		@Override
		public void onServiceConnected(ComponentName className, IBinder service)
		{
			// We've bound to LocalService, cast the IBinder and get CronService instance
			CronService.CronBinder binder = (CronService.CronBinder) service;
			cronService = binder.getService();
			cronBound = true;
			// if we are in this Activity, this means that the application is ready to display
			// something, so let's update the cron's applicationReady flag that may have not been
			// set if the service was not bound in FocusAppLogic when we tried to do it.
			cronService.onApplicationLoad(true);
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


	// called just after setting view
	@Override
	protected void setupSpecificUiElements()
	{
		LayoutInflater inflater = getLayoutInflater();

		// set the default section that will initially be loaded
		this.sectionToRender = this.previouslySelectedToRender = Constant.UI_MENU_ENTRY_PROJECTS_LISTING;

		// create a Drawer
		this.drawerItems = this.getDrawerItems();
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerListMenu = (ListView) findViewById(R.id.focus_drawer);
		if (this.drawerListMenu == null) {
			throw new FocusInternalErrorException("Cannot retrieve drawer view.");
		}

		// get the header and add it to the ListView
		LinearLayout menuHeader = (LinearLayout) inflater.inflate(R.layout.include_drawer_header, null); // FIXME context?
		menuHeader.setEnabled(false);
		menuHeader.setOnClickListener(null);
		this.drawerListMenu.addHeaderView(menuHeader, null, false);
		// adjust content of header
		String appVersion = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME;
		TextView appInfo = (TextView) findViewById(R.id.drawer_app_info);
		if (appInfo != null) {
			appInfo.setText(appVersion); // FIXME for production version, we should put the name/email of the user
		}
		// scroll the menu to the very top (at the top of the header)
		// set in a runnable such that this is called when the system decides, an not too early.
		this.drawerListMenu.post(new Runnable()
								 {
									 @Override
									 public void run()
									 {
										 drawerListMenu.smoothScrollToPositionFromTop(0, 0);
									 }
								 }
		);

		this.drawerLayout.bringToFront();
		this.drawerListMenu.bringToFront();
		this.drawerListMenu.setOnItemClickListener(this.getOnClickDrawerItemListener());

		// Assign a ListAdapter to our Drawer
		DrawerListAdapter adapter = new DrawerListAdapter(ApplicationHelper.getApplicationContext(), this.drawerItems);
		drawerListMenu.setAdapter(adapter);

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
				FocusAppLogic.getCurrentApplicationContent().getTitle()
		);
	}

	@Override
	protected void doInPageUiOperations()
	{
		switch (this.sectionToRender) {
			case Constant.UI_MENU_ENTRY_PROJECTS_LISTING:
			case Constant.UI_MENU_ENTRY_BOOKMARK:
				highlightSelectedMenuItem(this.sectionToRender);
				break;
			case Constant.UI_MENU_ENTRY_ABOUT:
				LayoutInflater inflater = LayoutInflater.from(this);
				// LayoutInflater inflater = getLayoutInflater();
				final WebView dialogContent = (WebView) inflater.inflate(R.layout.dialog_content_about, null);
				dialogContent.loadUrl("file:///android_asset/" + ASSETS_ABOUT_PAGE);
				final FocusDialogBuilder builder = new FocusDialogBuilder(this)
						.removeNeutralButton()
						.removeNegativeButton()
						.removePositiveButton()
						.setCancelable(true)
						.insertContent(dialogContent)
						.setTitle(getString(R.string.about_focus_title));
				AlertDialog dialog = builder.create();
				dialog.show();
				break;
			case Constant.UI_MENU_ENTRY_LOGOUT:
				final Thread logoutThread = new Thread()
				{
					public void run()
					{
						// reset all when logging out
						FocusAppLogic.getInstance().reset();

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

		if (this.sectionToRender != 0) {
			drawerLayout.closeDrawer(this.drawerListMenu);

			// do not keep selection on the current item
			switch (this.sectionToRender) {
				case Constant.UI_MENU_ENTRY_ABOUT:
					highlightSelectedMenuItem(this.previouslySelectedToRender);
					break;
			}
		}
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
				this.fragment = new BookmarkFragment();
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
	 * <p/>
	 */
	private ListView.OnItemClickListener getOnClickDrawerItemListener()
	{
		return new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (position != Constant.UI_MENU_ENTRY_HEADER_NON_CLICKABLE) {
					boolean doIt = true;
					switch (position) {
						case Constant.UI_MENU_ENTRY_PROJECTS_LISTING:
						case Constant.UI_MENU_ENTRY_BOOKMARK:
							if (position == sectionToRender) {
								doIt = false;
							}
							break;
						default:
							break;
					}
					if (doIt) {
						previouslySelectedToRender = sectionToRender;
						sectionToRender = position;
						applyUiChanges();
					}
					drawerLayout.closeDrawer(drawerListMenu);
				}
			}
		};
	}


	/**
	 * Listener for the back button
	 * <p/>
	 * FIXME if we have more than 2 menu entries in the Drawer that load their fragments in the
	 * main content, that may be more complicated.
	 * <p/>
	 * FIXME no animation? init.setFlags ( ANIMATION ) may help
	 * <p/>
	 * also, we block reloading the same page in the clicklistener of the menu such that we don't have problems here.
	 *
	 * this is the behavior for navigation between fragments of the proejctslistingactivity.
	 *
	 * FIXME the logic here is buggy. to be reviewed. for example the selected element is not updated.
	 */
	@Override
	public void onBackPressed()
	{
		//Navigate back to the last Fragment or exit the application
		FragmentManager fragmentManager = getFragmentManager();
		// If we have more than one fragments in the stack remove the last inserted
		// This is the case where we move from Bookmarks back to the home page via the BACK button
		if (fragmentManager.getBackStackEntryCount() > 1) {
			//remove the last inserted fragment from the stack
			fragmentManager.popBackStackImmediate();

			int b = fragmentManager.getBackStackEntryCount();
			// hard coded things is ok as we are only playing with 2 possible targets, and
			// hence hitting the BACK button will always make us go back to the home page
			// (if we hit the BACK button on the home page, we exit the app)
			this.actionBar.setTitle(R.string.project_listing_title);
			highlightSelectedMenuItem(Constant.UI_MENU_ENTRY_PROJECTS_LISTING);

			// close the drawer
			this.drawerLayout.closeDrawer(this.drawerListMenu);
		}
		else {
			// Exit the application
			super.onBackPressed();
		}
	}


	/**
	 * Highlight the selected menu item
	 *
	 * @param position FIXME does not have any effect
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

				LayoutInflater inflater = getLayoutInflater();
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
					DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
					// FIXME set the correct locale. no, apparnetly is correct, but i18n is broken ???
					// DateFormat dateformat = SimpleDateFormat.getDateTimeInstance(SimpleDateFormat.DEFAULT, SimpleDateFormat.DEFAULT, Locale locale);
					lastSync = dateFormat.format(new Date(this.cronService.getLastSync()));
				}
				TextView lastSyncField = (TextView) instructions.findViewById(R.id.dialog_sync_last_sync_field);
				lastSyncField.setText(getString(R.string.last_sync_label) + lastSync);
				TextView lastDataVolumeField = (TextView) instructions.findViewById(R.id.dialog_sync_data_volume_field);

				long rawDbSize = FocusAppLogic.getDataManager().getDatabaseSize();
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
					statusField.setTextColor(getResources().getColor(R.color.orange));

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

							// Run the periodic task
							// FIXME pass the MenuItem item, such that we can change its drawablet to an animation (or start the animation)
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
	 * This method gives a list of items to display in the left drawer.
	 *
	 * @return An array list of items to display in the drawer
	 */
	private ArrayList<SimpleListItem> getDrawerItems()
	{
		// load menu items and icons
		String[] navMenuTitles = getResources().getStringArray(R.array.drawer_items);
		TypedArray navMenuIcons = getResources().obtainTypedArray(R.array.drawer_icons);

		ArrayList<SimpleListItem> drawerItems = new ArrayList<>();

		for (int i = 0; i < navMenuTitles.length; i++) {
			String menuTitle = navMenuTitles[i];
			SimpleListItem drawListItem = new SimpleListItem(UiHelper.getBitmap(this, navMenuIcons.getResourceId(i, -1)), menuTitle);
			drawerItems.add(drawListItem);
		}

		// Release the typed array
		navMenuIcons.recycle();

		return drawerItems;
	}

	/**
	 * Task responsible for synchronizing data
	 * <p/>
	 * Called when the user explicitly launches data sync via the dialog
	 * <p/>
	 * FIXME: if we pass the MenuItem, we can change its icon to an animation (Drawable) or start an animation.
	 * FIXME: we must then stop/revert when the operation is completed, and we would then need a Listener on the Service
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
			boolean newSync = cronService.syncData();

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


}
