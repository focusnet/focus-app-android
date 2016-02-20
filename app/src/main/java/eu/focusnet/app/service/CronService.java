package eu.focusnet.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusMissingResourceException;


// TOOD FIXME also clean SQL db from old entries (i.e. ones that are not refered by fully loaded instances (? to check ?)

/**
 * This class is a service responsible for calling periodic tasks:
 * - cleaning of SQL database every N hours
 * - retrieving new version of data every N minutes
 *
 * It must make sure not to consume too much battery (by waking up the device inappropriately)
 *
 * We save the times of last execution in the DataManager, as this is a Singleton and it never dies. FIXME is the service killed? I don't think so.
 */
public class CronService extends Service
{
	private static final int CRON_SERVICE_CLEAN_SQL_PERIOD = 6 * 60; // 6 hours
	private static final int CRON_SERVICE_REFRESH_DATA_PERIOD = 30; // 30 min

	private ScheduledExecutorService scheduleTaskExecutor;
	private int failures;

	@Override
	public void onCreate()
	{
		super.onCreate();
		this.scheduleTaskExecutor = Executors.newScheduledThreadPool(3);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		periodicallySyncData();
		periodicallyCleanDatabase();
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
				try {
					DataManager.getInstance().syncData();
				}
				catch (FocusMissingResourceException e) {
					++failures;
					if (failures == 10 || failures == 50) {
						// if too many failures, let's report them, just in case that may me a more important problem.
						FocusApplication.reportError(e);
					}
				}
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
				DataManager.getInstance().cleanDataStore();
			}
		}, 0, CRON_SERVICE_CLEAN_SQL_PERIOD, TimeUnit.MINUTES);
	}

	@Override
	public void onDestroy()
	{
		// FIXME TODO also flush database modifications - DataManager ... flush()

		super.onDestroy();
	//	Log.d(TAG, "Destroying Service");
		scheduleTaskExecutor.shutdown();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
