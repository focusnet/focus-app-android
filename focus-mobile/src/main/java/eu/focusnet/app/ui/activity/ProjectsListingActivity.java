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
import eu.focusnet.app.ui.common.DrawerListAdapter;
import eu.focusnet.app.ui.common.FocusDialogBuilder;
import eu.focusnet.app.ui.common.SimpleListItem;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.ui.fragment.BookmarkFragment;
import eu.focusnet.app.ui.fragment.ProjectsListingFragment;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

/**
 * This {@code Activity} contains the listing of all projects, and is therefore the welcome screen
 * of our application after the login and loading screens.
 */
public class ProjectsListingActivity extends ToolbarEnabledActivity
{
	/**
	 * The layout containing our drawer menu.
	 */
	protected DrawerLayout drawerLayout;

	/**
	 * The {@code ListView} defining the menu entries of our drawer menu.
	 */
	protected ListView drawerListMenu;

	/**
	 * Facility for linking the menu drawer and the action bar.
	 */
	protected ActionBarDrawerToggle drawerToggle;

	/**
	 * List of items to include in the menu drawer.
	 */
	protected ArrayList<SimpleListItem> drawerItems;

	/**
	 * Previously selected {@code Fragment} identifier.
	 */
	private int previouslySelectedSectionToRender;

	/**
	 * Currently selected {@code Fragment} identifier.
	 */
	private int sectionToRender;

	/**
	 * {@link CronService} to link to for allowing the user to trigger data synchronization
	 */
	private CronService cronService;

	/**
	 * Tells whether the {@link CronService} is bound or not.
	 */
	private boolean cronBound = false;

	/**
	 * Defines callbacks for {@link CronService} binding, passed to bindService()
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
			// Not likely, though.
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
	 * - the parent {@code onCreate()} method will setup and render the UI
	 * - we bind to {@link CronService}
	 *
	 * @param savedInstanceState Inherited.
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

	/**
	 * Destroy the activity, and unbind the registered {@link CronService}.
	 */
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
	 * Defines the layout of this activity.
	 *
	 * @return Inherited.
	 */
	@Override
	protected int getTargetView()
	{
		return R.layout.activity_projects_listing;
	}

	/**
	 * Defines the target container of this Activity.
	 *
	 * @return Inherited.
	 */
	@Override
	protected int getTargetLayoutContainer()
	{
		return R.id.focus_drawer_content_container;
	}


	/**
	 * Prepare the view. This is called in {@link #onCreate(Bundle)}, before actually rendering the
	 * UI.
	 * <p/>
	 * See {@link super#onCreate(Bundle)}.
	 */
	@Override
	protected void setupSpecificUiElements()
	{
		LayoutInflater inflater = getLayoutInflater();

		// set the default section that will initially be loaded
		this.sectionToRender = this.previouslySelectedSectionToRender = Constant.Ui.UI_MENU_ENTRY_PROJECTS_LISTING;

		// create a Drawer
		this.drawerItems = this.getDrawerItems();
		this.drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		this.drawerListMenu = (ListView) findViewById(R.id.focus_drawer);
		if (this.drawerListMenu == null) {
			throw new FocusInternalErrorException("Cannot retrieve drawer view.");
		}

		// get the header and add it to the ListView
		// FIXME pass something else as the Context
		LinearLayout menuHeader = (LinearLayout) inflater.inflate(R.layout.include_drawer_header, null);
		menuHeader.setEnabled(false);
		menuHeader.setOnClickListener(null);
		this.drawerListMenu.addHeaderView(menuHeader, null, false);
		// adjust content of header
		/** @deprecated for production version, we should put the name/email of the user */
		String appVersion = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME;
		TextView appInfo = (TextView) findViewById(R.id.drawer_app_info);
		if (appInfo != null) {
			appInfo.setText(appVersion);
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

	/**
	 * Do any UI operation that do not imply creating and including a new Fragment in the UI.
	 * The following is performed depending on which menu item is selected:
	 * - if projects or bookmarks listing: highlight the appropriate menu item
	 * - if About, then display a dialog with the information about the app
	 * - If Logout, then reset the application
	 * <p/>
	 * See {@link super#applyUiChanges()}.
	 */
	@Override
	protected void doInPageUiOperations()
	{
		switch (this.sectionToRender) {
			case Constant.Ui.UI_MENU_ENTRY_PROJECTS_LISTING:
			case Constant.Ui.UI_MENU_ENTRY_BOOKMARK:
				highlightSelectedMenuItem(this.sectionToRender);
				break;
			case Constant.Ui.UI_MENU_ENTRY_ABOUT:
				LayoutInflater inflater = LayoutInflater.from(this);
				// FIXME set a better Context than null
				final WebView dialogContent = (WebView) inflater.inflate(R.layout.dialog_content_about, null);
				dialogContent.loadUrl("file:///android_asset/" + Constant.AppConfig.ASSETS_ABOUT_PAGE);
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
			case Constant.Ui.UI_MENU_ENTRY_LOGOUT:
				final Thread logoutThread = new Thread()
				{
					public void run()
					{
						// reset all when logging out
						FocusAppLogic.getInstance().reset();
						// redirect to the EntryPointActivity
						try {
							Intent i = new Intent(ProjectsListingActivity.this, EntryPointActivity.class);
							i.putExtra(Constant.Extra.UI_EXTRA_LOADING_INFO_TEXT, getString(R.string.wiping_user_data_logout_msg));
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

		// close the drawer and cleanup
		if (this.sectionToRender != 0) {
			drawerLayout.closeDrawer(this.drawerListMenu);

			// do not keep selection on the current item
			switch (this.sectionToRender) {
				case Constant.Ui.UI_MENU_ENTRY_ABOUT:
					highlightSelectedMenuItem(this.previouslySelectedSectionToRender);
					break;
			}
		}
	}

	/**
	 * Defines the Fragment to include in the container for this Activity.
	 * Either the project listing fragment or the bookmarks listing fragment.
	 * <p/>
	 * See {@link super#applyUiChanges()}.
	 */
	@Override
	protected void prepareNewFragment()
	{
		switch (this.sectionToRender) {
			case Constant.Ui.UI_MENU_ENTRY_PROJECTS_LISTING:
				this.fragment = new ProjectsListingFragment();
				if (this.actionBar != null) {
					this.actionBar.setTitle(R.string.project_listing_title);
				}
				break;
			case Constant.Ui.UI_MENU_ENTRY_BOOKMARK:
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
	 * Create a click listener tailored for the Drawer menu.
	 *
	 * @return A click listener.
	 */
	private ListView.OnItemClickListener getOnClickDrawerItemListener()
	{
		return new ListView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				if (position != Constant.Ui.UI_MENU_ENTRY_HEADER_NON_CLICKABLE) {
					boolean doIt = true;
					switch (position) {
						case Constant.Ui.UI_MENU_ENTRY_PROJECTS_LISTING:
						case Constant.Ui.UI_MENU_ENTRY_BOOKMARK:
							if (position == sectionToRender) {
								doIt = false;
							}
							break;
						default:
							break;
					}
					if (doIt) {
						previouslySelectedSectionToRender = sectionToRender;
						sectionToRender = position;
						applyUiChanges();
					}
					drawerLayout.closeDrawer(drawerListMenu);
				}
			}
		};
	}

	/**
	 * Listener for the back button. We have 2 menu entries in the drawer menu that load their
	 * fragment in the main content container.
	 * <p/>
	 * In {@link #getOnClickDrawerItemListener()}, we make sure we do not reload the same page
	 * indefinitely such that the below logic can work.
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

			// hard coded things is ok as we are only playing with 2 possible targets, and
			// hence hitting the BACK button will always make us go back to the home page
			// (if we hit the BACK button on the home page, we exit the app)
			this.actionBar.setTitle(R.string.project_listing_title);
			highlightSelectedMenuItem(Constant.Ui.UI_MENU_ENTRY_PROJECTS_LISTING);

			// close the drawer
			this.drawerLayout.closeDrawer(this.drawerListMenu);
		}
		else {
			// Exit the application
			super.onBackPressed();
		}
	}


	/**
	 * Highlight the currently selected menu item
	 *
	 * @param position 1-based position of the menu item to highlight.
	 */
	private void highlightSelectedMenuItem(int position)
	{
		this.drawerListMenu.setItemChecked(position, true);
		this.drawerListMenu.setSelection(position);
	}

	/**
	 * Required by {@link ActionBarDrawerToggle}.
	 *
	 * @param newConfig Inherited.
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		this.drawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Required by {@link ActionBarDrawerToggle}.
	 *
	 * @param savedInstanceState Inherited.
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		this.drawerToggle.syncState();
	}


	/**
	 * When clicking the action bar buttons:
	 * - display an alert dialog that allows to trigger data synchronization or displays the current
	 * status of the synchronization.
	 *
	 * @param item Inherited.
	 * @return Inherited.
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.action_sync:

				if (!this.cronBound) {
					return false;
				}

				LayoutInflater inflater = getLayoutInflater();
				// FIXME pass a better Context than null.
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
				final TextView statusField = (TextView) status.findViewById(R.id.dialog_sync_status_field);

				// update dynamic content (last sync and last data volume)
				String lastSync;
				if (this.cronService.getLastSync() == 0) {
					lastSync = getString(R.string.n_a);
				}
				else {
					DateFormat dateFormat = SimpleDateFormat.getDateTimeInstance();
					lastSync = dateFormat.format(new Date(this.cronService.getLastSync()));
				}
				TextView lastSyncField = (TextView) instructions.findViewById(R.id.dialog_sync_last_sync_field);
				lastSyncField.setText(getString(R.string.last_sync_label) + lastSync);
				TextView lastDataVolumeField = (TextView) instructions.findViewById(R.id.dialog_sync_data_volume_field);

				long rawDbSize = FocusAppLogic.getDataManager().getDatabaseSize();
				lastDataVolumeField.setText(getString(R.string.last_sync_data_volume_label) + (rawDbSize == 0 ? "N/A" : UiHelper.getFileSize(rawDbSize)));


				// too early since last sync
				// display instructions with custom message, disable START
				if (cronService.getLastSync() >= System.currentTimeMillis() - Constant.AppConfig.CRON_SERVICE_MINIMUM_DURATION_BETWEEN_SYNC_DATA_IN_MILLISECONDS) {
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
							new SyncTask(builder, dialogContent, dialog).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
				switch (item.getItemId()) {
					default:
						return super.onOptionsItemSelected(item);
				}
		}
	}


	/**
	 * Task responsible for synchronizing data
	 * <p/>
	 * Called when the user explicitly launches data sync via the dialog of the action bar.
	 */
	private class SyncTask extends AsyncTask<Void, Void, Void>
	{

		/**
		 * Layout containing the content of the dialog
		 */
		final private LinearLayout dialogContent;

		/**
		 * Dialog builder
		 */
		final private FocusDialogBuilder builder;

		/**
		 * The dialog being displayed.
		 */
		final private AlertDialog dialog;

		/**
		 * Constructor.
		 *
		 * @param builder       Input value for setting instance variable.
		 * @param dialogContent Input value for setting instance variable.
		 * @param dialog        Input value for setting instance variable.
		 */
		public SyncTask(FocusDialogBuilder builder, LinearLayout dialogContent, AlertDialog dialog)
		{
			this.builder = builder;
			this.dialogContent = dialogContent;
			this.dialog = dialog;
		}

		/**
		 * Perform the data synchronization in the background.
		 *
		 * @param params Nothing
		 * @return Nothing
		 */
		@Override
		protected Void doInBackground(Void... params)
		{
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

		/**
		 * After execution, update the dialog to reflect success or failure.
		 *
		 * @param v Nothing
		 */
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
			this.builder.getNegativeButton().setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.dismiss();
					finish();
					startActivity(new Intent(ProjectsListingActivity.this, ProjectsListingActivity.class));
				}
			});
		}
	}


}
