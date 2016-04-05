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

package eu.focusnet.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusMissingResourceException;


// TOOD FIXME also clean SQL db from old entries (i.e. ones that are not refered by fully loaded instances (? to check ?)
// FIXME synchronized functions

/**
 * This class is a service responsible for calling periodic tasks:
 * - cleaning of SQL database every N hours
 * - retrieving new version of data every N minutes
 *
 * It must make sure not to consume too much battery (by waking up the device inappropriately)
 *
 * FIXME TODO Detect the sleep/wake with a broadcast receiver
 * -> shutdown and restart tasks (but save last time of execution to avoid too frequent tasks calls)
 * This way the service is never killed, but it does not do anything if in sleep mode.
 */
public class CronService extends Service
{

	private static final int CRON_SERVICE_CLEAN_SQL_PERIOD = 6 * 60; // 6 hours
	private static final int CRON_SERVICE_REFRESH_DATA_PERIOD = 30; // 30 min

	private final IBinder mBinder = new CronBinder();

	// FIXME TODO if manual sync ->
	// if currently being run
	private ScheduledExecutorService scheduleTaskExecutor;
	private int failures;
	private boolean workInProgress;

	@Override
	public void onCreate()
	{
		super.onCreate();
		this.scheduleTaskExecutor = Executors.newScheduledThreadPool(3);
		this.workInProgress = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		this.periodicallySyncData();
		this.periodicallyCleanDatabase();
		return START_REDELIVER_INTENT;
	}

	/**
	 * Sync data with remote backends
	 */
	public void periodicallySyncData()
	{
		this.scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
				if (getWorkInProgress()) {
					return;
				}

				setWorkInProgress(true);
				DataManager old_dm = FocusApplication.getInstance().getDataManager();

				try {
					old_dm.syncData();
				}
				catch (FocusMissingResourceException e) {
					++failures;
					if (failures == 10 || failures == 50) {
						// if too many failures, let's report them, just in case that may me a more important problem.
						FocusApplication.reportError(e);
						setWorkInProgress(false);
					}
				}

				if (old_dm != FocusApplication.getInstance().getDataManager()) { // if the DataManager has changed (= a new sync has been correctly performed), then reload UI
					FocusApplication.getInstance().restartCurrentActivity(); // FIXME YANDY is that working? //Answer: I have to take a look at it in details
				}
				setWorkInProgress(false);

			}
		}, 0, CRON_SERVICE_REFRESH_DATA_PERIOD, TimeUnit.MINUTES);
	}

	/**
	 * Clean the database
	 */
	public void periodicallyCleanDatabase()
	{
		scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
				if (!getWorkInProgress()) {
					setWorkInProgress(true);
					FocusApplication.getInstance().getDataManager().cleanDataStore();
					setWorkInProgress(false);
				}
			}
		}, 0, CRON_SERVICE_CLEAN_SQL_PERIOD, TimeUnit.MINUTES);
	}

	/**
	 * Get the work-in-progress flag
	 * @return
	 */
	synchronized public boolean getWorkInProgress()
	{
		return this.workInProgress;
	}

	/**
	 * Set the work-in-progress flag
	 */
	synchronized public void setWorkInProgress(boolean flag)
	{
		this.workInProgress = flag;
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
	 * Get reference to service
	 */
	@Override
	public IBinder onBind(Intent intent)
	{
		return this.mBinder;
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



