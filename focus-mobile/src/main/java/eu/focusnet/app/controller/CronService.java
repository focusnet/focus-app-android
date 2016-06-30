/*
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
 */

package eu.focusnet.app.controller;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.focusnet.app.ui.FocusApplication;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * This class is a service responsible for calling periodic tasks:
 * - retrieving new version of data every N minutes
 * <p/>
 * Schedulor behavior regarding sleep/wake:
 * - will not wake the device up
 * - we use wake locks to ensure that started tasks are indeed finished
 * - when waking up, the device will wait for
 * {@link Constant.AppConfig#CRON_SERVICE_POLLING_INTERVAL_IN_MINUTES}
 * before trying to execute the tasks again
 * - When an execution is triggered, we then make sure we do not execute the sync/clean tasks too
 * often by comparing the
 * last execution time with {@link Constant.AppConfig#CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MINUTES}.
 * - this way, we don't expect the user to work with the app for
 * {@link Constant.AppConfig#CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MINUTES} minutes,
 * the periodic tasks are launched as long as he is on the app for more than
 * {@link Constant.AppConfig#CRON_SERVICE_POLLING_INTERVAL_IN_MINUTES}  minutes
 */
public class CronService extends Service implements ApplicationStatusObserver
{

	/**
	 * Link used to bind against this service
	 */
	final private IBinder cronBinder = new CronBinder();

	/**
	 * Last synchronization time (epoch in milliseconds)
	 */
	private long lastSync;

	/**
	 * Execution pool for our task
	 */
	private ScheduledExecutorService scheduleTaskExecutor;

	/**
	 * Number of encountered consecutive failures
	 */
	private int failures;

	/**
	 * Tells whether the synchronization process is in progress
	 */
	private boolean syncInProgress;

	/**
	 * Tells whether the applicatin is in the ready status or if it still needs configuration
	 */
	private boolean applicationReady;

	/**
	 * Create the service, setting reasonable defaults.
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		this.applicationReady = false;
		this.scheduleTaskExecutor = Executors.newScheduledThreadPool(2);
		this.syncInProgress = false;

		HashMap<String, String> prefs = ApplicationHelper.getPreferences();
		String l = prefs.get(Constant.SharedPreferences.SHARED_PREFERENCES_LAST_SYNC);
		this.lastSync = l == null ? 0 : Long.parseLong(l);
		this.failures = 0;
	}

	/**
	 * Start the service
	 *
	 * @param intent  Inherited
	 * @param flags   Inherited
	 * @param startId Inherited
	 * @return Inherited
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		this.periodicallySyncData();
		return START_REDELIVER_INTENT;
	}

	/**
	 * Kill the scheduler on service destruction
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		scheduleTaskExecutor.shutdown();
	}

	/**
	 * Synchronize data periodically
	 */
	private void periodicallySyncData()
	{
		this.scheduleTaskExecutor.scheduleWithFixedDelay(
				new Runnable()
				{
					@Override
					public void run()
					{

						if (!applicationReady) {
							return;
						}

						// last sync is too recent, let's wait more
						long limit = System.currentTimeMillis() - (Constant.AppConfig.CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MINUTES * 60 * 1_000);
						if (lastSync == 0 || lastSync >= limit) {
							return;
						}

						syncData();

					}
				},
				Constant.AppConfig.CRON_SERVICE_DURATION_TO_WAIT_BEFORE_FIRST_SYNC_IN_MINUTES,
				Constant.AppConfig.CRON_SERVICE_POLLING_INTERVAL_IN_MINUTES,
				TimeUnit.MINUTES
		);
	}


	/**
	 * Sync data operation. In this method, we prepare everything for the sync to happen in good
	 * conditions (wake lock, progress flag), but the actual synchronization is made by calling
	 * {@link FocusAppLogic#sync()}.
	 *
	 * @return {@code false} if already in progress OR application not ready,
	 * {@code true} otherwise.
	 */
	public boolean syncData()
	{
		// Applicaiton is not ready, nothing to sync
		if (!this.applicationReady) {
			return false;
		}

		// acquire the synchronization lock
		if (!setSyncInProgress(true)) {
			return false;
		}

		// acquire CPU lock
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				Constant.AppConfig.CRON_WAKE_LOCK_NAME);
		wakeLock.acquire();

		// perform the sync
		boolean success = false;
		try {
			// Call the controller and ask him to start syncing
			FocusAppLogic.getInstance().sync();
			success = true;
			failures = 0;
		}
		catch (FocusMissingResourceException ex) {
			// report once in a while, but do not crash the app
			// perhaps on next run it will be better?
			// {success} is left to false
			// do not crash. Let the caller decide that.
			++this.failures;
			if (failures % 10 == 0) {
				FocusApplication.reportError(ex);
			}
		}

		// Update information about the last sync
		this.lastSync = System.currentTimeMillis();
		HashMap<String, String> toSave = new HashMap<>();
		toSave.put(Constant.SharedPreferences.SHARED_PREFERENCES_LAST_SYNC, Long.toString(this.lastSync));
		ApplicationHelper.savePreferences(toSave);

		// release CPU lock
		wakeLock.release();

		// And finally release the synchronization lock
		setSyncInProgress(false);

		// Everything went fine
		return success;
	}

	/**
	 * Get the work-in-progress flag
	 *
	 * @return {@code true} if the sync operation is in progress, {@code false} otherwise
	 */
	public boolean getSyncInProgress()
	{
		return this.syncInProgress;
	}

	/**
	 * Set the work-in-progress flag
	 *
	 * @param flag {@code true} if the sync operation is in progress, {@code false} otherwise
	 */
	private boolean setSyncInProgress(boolean flag)
	{
		if (this.syncInProgress == flag) {
			return false;
		}

		this.syncInProgress = flag;
		return true;
	}

	/**
	 * Get the last sync date
	 *
	 * @return The last sync epoch in milliseconds
	 */
	public long getLastSync()
	{
		return this.lastSync;
	}

	/**
	 * Get reference to service
	 */
	@Override
	public IBinder onBind(Intent intent)
	{
		return this.cronBinder;
	}

	/**
	 * Inherited
	 *
	 * @param appStatus {@code true} if the application is now ready, {@code false} otherwise.
	 */
	@Override
	public void onChangeStatus(boolean appStatus)
	{
		this.applicationReady = appStatus;
	}

	/**
	 * Inherited
	 */
	@Override
	public void handleLogout()
	{
		// no need to commit the modification in the SharedPreferences. This is the job of
		// UserManager.logout(), which will clear the whole prefs
		this.lastSync = 0;
		this.failures = 0;
	}

	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class CronBinder extends Binder
	{
		/**
		 * Get the service.
		 *
		 * @return The service.
		 */
		public CronService getService()
		{
			return CronService.this;
		}
	}

}



