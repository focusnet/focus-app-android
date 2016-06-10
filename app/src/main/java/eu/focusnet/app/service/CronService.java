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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusMissingResourceException;


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
public class CronService extends Service
{


	public static final int CRON_SERVICE_MINIMUM_DURATION_BETWEEN_SYNC_DATA_IN_MS = 10 * 60 * 1_000; // 10 minutes in milliseconds, does not apply to db cleaning
	private static final int CRON_SERVICE_POLLING_PERIOD_IN_MINUTES = 200; // 2 minutes FIXME
	private static final int CRON_SERVICE_CLEAN_SQL_PERIOD_IN_MS = 6 * 60 * 60 * 1_000; // 6 hours
	private static final int CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MS = 30 * 60 * 1_000;//30; // 30 min
	private final IBinder cronBinder = new CronBinder();
	PowerManager powerManager;
	private long lastSync;
	private long lastClean;
	private ScheduledExecutorService scheduleTaskExecutor;
	private int failures;
	private boolean syncInProgress; // does not apply to db cleaning
	private boolean dbCleaningInProgress;

	@Override
	public void onCreate()
	{
		super.onCreate();
		this.scheduleTaskExecutor = Executors.newScheduledThreadPool(3);
		this.powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		this.syncInProgress = false;
		this.lastSync = 0;
		this.lastClean = 0;
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
	 * User-triggered sync of data
	 * <p/>
	 * return false if already in progress, true otherwise
	 */
	public boolean manuallySyncData()
	{


		// DEBUG FIXME
		if (1==1) {
			FocusApplication.getInstance().getDataManager().cleanDataStore(); // DEPRECATED

			return true;
		}

		if (!setSyncInProgress(true)) {
			// already work in progress, let's discard this execution
			return false;
		}

		// acquire CPU lock
		PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
		PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				"FOCUS_EXPLICIT_SYNC_DATA");
		wakeLock.acquire();


		this.syncData();


		// release CPU lock
		wakeLock.release();

		setSyncInProgress(false);
		return true;
	}

	/**
	 * Sync data operations
	 * <p/>
	 * no CPU lock or work in progress flag set here.
	 */
	private void syncData()
	{
		if (1 == 1) {

			try {
				Thread.sleep(1000 * 60); // 1 min
			}
			catch (InterruptedException ignored) {

			}
			this.lastSync = System.currentTimeMillis();

			return;
		}


		// FIXME also we may not be in the application yet (e.g. login screen) -> DataManager->getAppRead()
		// Acquire a CPU lock to avoid our service to stop running when going into sleep mode

		DataManager old_dm = FocusApplication.getInstance().getDataManager();

		try {
			old_dm.syncData();
		}
		catch (FocusMissingResourceException e) {
			++this.failures;
			if (this.failures % 10 == 0) {
				// if too many failures, let's report them, just in case that may me an important problem.
				FocusApplication.reportError(e);
			}
		}

		if (old_dm != FocusApplication.getInstance().getDataManager()) { // if the DataManager has changed (= a new sync has been correctly performed), then reload UI
			FocusApplication.getInstance().restartCurrentActivity(); // FIXME YANDY is that working? //Answer: I have to take a look at it in details
			// FIXME what happens if current path does not exist anymore? We may redirect to projects listing in this case (and display sth to user)
		}

		this.lastSync = System.currentTimeMillis();
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

				if (!FocusApplication.getInstance().getDataManager().isApplicationReady()) {
					return;
				}

				// last sync is too recent, let's wait more
				long limit = System.currentTimeMillis() - CRON_SERVICE_REFRESH_DATA_PERIOD_IN_MS;
				if (lastSync == 0 || lastSync >= limit) {
					return;
				}

				if (!setSyncInProgress(true)) {
					// already work in progress, let's discard this execution
					return;
				}

				PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						"FOCUS_SYNC_DATA");
				wakeLock.acquire();


				syncData();


				// release CPU lock
				wakeLock.release();

				setSyncInProgress(false);
			}
		}, CRON_SERVICE_POLLING_PERIOD_IN_MINUTES, CRON_SERVICE_POLLING_PERIOD_IN_MINUTES, TimeUnit.MINUTES);
	}

	/**
	 * Clean the database
	 */
	private void periodicallyCleanDatabase()
	{
		this.scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable()
														 {
															 @Override
															 public void run()
															 {


																 if (!FocusApplication.getInstance().getDataManager().isApplicationReady()) {
																	 return;
																 }

																 // last db cleaning is too recent, let's wait more
																 if (lastClean == 0 || lastClean >= System.currentTimeMillis() - CRON_SERVICE_CLEAN_SQL_PERIOD_IN_MS) {
																	 return;
																 }

																 if (!setDbCleaningInProgress(true)) {
																	 // already work in progress, let's discard this execution
																	 return;
																 }

																 // Acquire a CPU lock to avoid our service to stop running when going into sleep mode
																 PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
																		 "FOCUS_CLEAN_DB");

																 wakeLock.acquire();

																 FocusApplication.getInstance().getDataManager().cleanDataStore();

																 lastClean = System.currentTimeMillis();

																 // release CPU lock
																 wakeLock.release();

																 setDbCleaningInProgress(false);
															 }

														 }

				, CRON_SERVICE_POLLING_PERIOD_IN_MINUTES, CRON_SERVICE_POLLING_PERIOD_IN_MINUTES, TimeUnit.MINUTES);
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
	 * Set the work-in-progress flag
	 */
	synchronized private boolean setDbCleaningInProgress(boolean flag)
	{
		if (this.dbCleaningInProgress == flag) {
			return false;
		}

		this.dbCleaningInProgress = flag;
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
	 *
	 * NOTE: only available within a single application instance run. We do not permanently save
	 * this information (e.g. in the db)
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



