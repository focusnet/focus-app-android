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

package eu.focusnet.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.focusnet.app.FocusAppLogic;
import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.util.ApplicationHelper;


// TOOD FIXME also clean SQL db from old entries (i.e. ones that are not refered by fully loaded instances (? to check ?)

// FIXME synchronized functions
// FIXME how to interrupt (schedulor AND manually launched sync)

/**
 * This class is a service responsible for calling periodic tasks:
 * - cleaning of SQL database every N hours
 * - retrieving new version of data every N minutes
 * <p/>
 * Schedulor behavior regarding sleep/wake:
 * - will not wake the device up
 * - we use wake locks to ensure that started tasks are indeed finished
 * - when waking up, the device will wait for CRON_SERVICE_POLLING_PERIOD_IN_MINUTES before executing the tasks again
 * - in tasks, we then make sure we do now execute the sync/clean tasks too often
 * (CRON_SERVICE_CLEAN_SQL_PERIOD, CRON_SERVICE_REFRESH_DATA_PERIOD)
 * - this way, we don't expect the user to work with the app for CRON_SERVICE_REFRESH_DATA_PERIOD minutes,
 * the periodic tasks are run as long as he is on the app for more than CRON_SERVICE_POLLING_PERIOD_IN_MINUTES minutes
 */
public class CronService extends Service implements ApplicationStatusObserver
{


	public static final int CRON_SERVICE_MINIMUM_DURATION_BETWEEN_SYNC_DATA_IN_MS = 10 * 60 * 1_000; // 10 minutes in milliseconds, does not apply to db cleaning
	private static final int CRON_SERVICE_POLLING_PERIOD_IN_MINUTES = 2; // 2 minutes
	private static final int CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MINUTES = 30; // 30 min
	private static final int CRON_SERVICE_DURATION_TO_WAIT_BEFORE_FIRST_SYNC_IN_MINUTES = 30;
	private static final String CRON_WAKE_LOCK_NAME = "FOCUS_CRON_TASK";
	private final IBinder cronBinder = new CronBinder();
	PowerManager powerManager;
	private long lastSync;
	private ScheduledExecutorService scheduleTaskExecutor;
	private int failures;
	private boolean syncInProgress; // does not apply to db cleaning
	private boolean applicationReady;

	@Override
	public void onCreate()
	{
		super.onCreate();
		this.applicationReady = false;
		this.scheduleTaskExecutor = Executors.newScheduledThreadPool(3);
		this.powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		this.syncInProgress = false;

		HashMap<String, String> prefs = ApplicationHelper.getPreferences();
		String l = prefs.get(ApplicationHelper.SHARED_PREFERENCES_LAST_SYNC);
		this.lastSync = l == null ? 0 : Long.parseLong(l);
		this.failures = 0;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		this.periodicallySyncData();
		// FIXME enable this.periodicallyCleanDatabase();
		return START_REDELIVER_INTENT;
	}


	/**
	 * Sync data operations
	 * <p/>
	 * * return false if already in progress OR application nto ready, true otherwise
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
				CRON_WAKE_LOCK_NAME);
		wakeLock.acquire();

		// perform the sync
		boolean success = false;
		try {
			// FIXME how to make it NOT static? not really possible
			FocusAppLogic.getInstance().sync();
			success = true;
		}
		catch (FocusMissingResourceException ex) {
			// report once in a while, but do not crash the app
			// perhaps on next run it will be better?
			// {success} is left to false
			++this.failures;
			if (failures % 10 == 0) {
				FocusApplication.reportError(ex);
			}
		}

		// Update information about the last sync
		this.lastSync = System.currentTimeMillis();
		HashMap<String, String> toSave = new HashMap<>();
		toSave.put(ApplicationHelper.SHARED_PREFERENCES_LAST_SYNC, Long.toString(this.lastSync));
		ApplicationHelper.savePreferences(toSave);

		// release CPU lock
		wakeLock.release();

		// And finally release the synchronization lock
		setSyncInProgress(false);

		// Everything went fine
		return success;
	}

	/**
	 * Sync data with remote backends
	 */
	private void periodicallySyncData()
	{
		this.scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{

				if (!applicationReady) {
					return;
				}

				// last sync is too recent, let's wait more
				long limit = System.currentTimeMillis() - (CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MINUTES * 60 * 1_000);
				if (lastSync == 0 || lastSync >= limit) {
					return;
				}

				syncData();

			}
		}, CRON_SERVICE_DURATION_TO_WAIT_BEFORE_FIRST_SYNC_IN_MINUTES, CRON_SERVICE_POLLING_PERIOD_IN_MINUTES, TimeUnit.MINUTES);
	}


	/**
	 * Kill any current sync data operation
	 * - from scheduler
	 * - from explicit synching
	 */
	public void killPendingSync()
	{
		//
	}


	/**
	 * Get the work-in-progress flag
	 *
	 * @return
	 */
	synchronized public boolean getSyncInProgress()
	{
		return this.syncInProgress;
	}

	/**
	 * Set the work-in-progress flag
	 */
	synchronized private boolean setSyncInProgress(boolean flag)
	{
		if (this.syncInProgress == flag) {
			return false;
		}

		this.syncInProgress = flag;
		return true;
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
	 * Get the last sync date
	 * <p/>
	 * NOTE: only available within a single application instance run. We do not permanently save
	 * this information (e.g. in the db)
	 *
	 * @return
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

	// If the service was not bound when this method is called, then our applicationReady flag
	// may be wrong.
	// FIXME to circumvent that, call this method often?
	@Override
	public void observeApplicationStatus(boolean appStatus)
	{
		this.applicationReady = appStatus;
	}

	// FIXME check what happens if called when not bound.
	@Override
	public void handleLogout()
	{
		// FIXME TODO kill any pending process
		// no need to commit the modification in the SharedPreferences. This is the job of UserManager.logout(), which will clear the whole prefs
		this.lastSync = 0;
		this.failures = 0;
	}


	/**
	 * Class used for the client Binder.  Because we know this service always
	 * runs in the same process as its clients, we don't need to deal with IPC.
	 */
	public class CronBinder extends Binder
	{
		public CronService getService()
		{
			// Return this instance of LocalService so clients can call public methods
			return CronService.this;
		}
	}


}



