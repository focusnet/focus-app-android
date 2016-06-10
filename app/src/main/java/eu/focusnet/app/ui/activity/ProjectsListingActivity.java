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
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
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
public class ProjectsListingActivity extends BaseDrawerActivity
{
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
		super.onCreate(savedInstanceState);
		showView(Constant.UI_MENU_ENTRY_PROJECTS_LISTING);

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
	protected int getContentView()
	{
		return R.layout.activity_projects_listing;
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

		String app_version = getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME;

		// FIXME if not in Demo mode we should use be the below line - but we should rewrite this UI component anyway
		// User user = FocusApplication.getInstance().getDataManager().getUser();
		// drawerItems.add(new HeaderDrawerListItem(UiHelper.getBitmap(this, R.drawable.focus_logo_small), user.getFirstName() + " " + user.getLastName(), user.getCompany(), user.getEmail()));
		drawerItems.add(new HeaderDrawerListItem(UiHelper.getBitmap(this, R.drawable.focus_logo_small), app_version, "", ""));

		for (int i = 0; i < navMenuTitles.length; i++) {
			String menuTitle = navMenuTitles[i];
			DrawerListItem drawListItem = new DrawerListItem(UiHelper.getBitmap(this, navMenuIcons.getResourceId(i, -1)), menuTitle, null); //Null for info
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
	 * Listener for the back button
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
			case Constant.UI_MENU_ENTRY_PROJECTS_LISTING:
				fragment = new ProjectsListingFragment();
				getSupportActionBar().setSubtitle(
						FocusApplication.getInstance().getDataManager().getAppContentInstance().getTitle()
				);
				break;
			case Constant.UI_MENU_ENTRY_BOOKMARK:
				fragment = new BookmarkFragment(); // FIXME clicking on BOOKMARKS in Menu -> MY FOCUS is displayed, but not the subtitle
				getSupportActionBar().setSubtitle(null);
				break;
			case Constant.UI_MENU_ENTRY_ABOUT:
				AboutFragment f = new AboutFragment();
				f.show(getSupportFragmentManager(), getString(R.string.about_focus));
				drawerLayout.closeDrawer(drawerListMenu); // FIXME would be better to have a proper page.
				break;
			case Constant.UI_MENU_ENTRY_LOGOUT:
				final Thread logout_thread = new Thread()
				{
					public void run()
					{
						FocusApplication.getInstance().getDataManager().reset();
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
				logout_thread.start();
				break;
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


	@Override
	public boolean onOptionsItemSelected(final MenuItem item)
	{
		switch (item.getItemId()) {
			case R.id.action_sync:

				if (!this.cronBound) {
					return false;
				}

				LayoutInflater inflater = LayoutInflater.from(this);
				final LinearLayout dialog_content = (LinearLayout) inflater.inflate(R.layout.dialog_content_synchronization, null);

				final Context context = this;
				final FocusDialogBuilder builder = new FocusDialogBuilder(context)
						.removeNeutralButton()
						.setNegativeButtonText(getString(R.string.cancel))
						.setPositiveButtonText(getString(R.string.start))
						.setCancelable(false)
						.insertContent(dialog_content)
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

				final View instructions = dialog_content.findViewById(R.id.dialog_sync_instructions);
				final View progress = dialog_content.findViewById(R.id.dialog_sync_progress);
				final View status = dialog_content.findViewById(R.id.dialog_sync_status);
				final TextView instructionsField = (TextView) instructions.findViewById(R.id.dialog_sync_instructions_msg);
				final TextView progressField = (TextView) progress.findViewById(R.id.dialog_sync_progress_field);
				final TextView statusField = (TextView) status.findViewById(R.id.dialog_sync_status_field);

				// update dynamic content (last sync and last data volume)
				String last_sync;
				if (this.cronService.getLastSync() == 0) {
					last_sync = getString(R.string.n_a);
				}
				else {
					SimpleDateFormat dateFormat = new SimpleDateFormat(eu.focusnet.app.model.util.Constant.DATE_FORMAT);
					last_sync = dateFormat.format(new Date(this.cronService.getLastSync()));
				}
				TextView lastSyncField = (TextView) instructions.findViewById(R.id.dialog_sync_last_sync_field);
				lastSyncField.setText(getString(R.string.last_sync_label) + last_sync);
				TextView lastDataVolumeField = (TextView) instructions.findViewById(R.id.dialog_sync_data_volume_field);
				long raw_db_size = FocusApplication.getInstance().getDataManager().getDatabaseSize();
				lastDataVolumeField.setText(getString(R.string.last_sync_data_volume_label) + (raw_db_size == 0 ? "N/A" : UiHelper.getFileSize(raw_db_size)));


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

							// we careful, cannot start if already started !!! FIXME
							new SyncTask(builder, dialog_content).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}
					});
				}
				return true;

			default:
				// If we got here, the user's action was not recognized.
				// Invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);

		}
	}

	/**
	 * Task responsible for synchronizing data
	 *
	 * Called when the user explicitly launches data sync via the dialog
	 */
	private class SyncTask extends AsyncTask<Void, Void, Void>
	{

		private final LinearLayout dialogContent;
		private final FocusDialogBuilder builder;

		public SyncTask(FocusDialogBuilder builder, LinearLayout dialog_content)
		{
			this.builder = builder;
			this.dialogContent = dialog_content;
		}

		@Override
		protected Void doInBackground(Void... params)
		{
			//if (1==1) return null;
			if (!cronBound) {
				return null;
			}
			boolean new_sync = cronService.manuallySyncData();

			// if already started (periodic execution), let's just wait for completion
			if (!new_sync) {
				while(cronService.getSyncInProgress()) {
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
