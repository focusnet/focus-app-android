package eu.focusnet.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


// TOOD FIXME also clean SQL db from old entries (i.e. ones that are not refered by fully loaded instances (? to check ?)

public class SyncDataService extends Service
{

	public static final String SERVICE_EXTRA_DELAY_INTERVAL = "eu.focusnet.extra.DELAY_INTERVAL";

	private ScheduledExecutorService scheduleTaskExecutor;

	@Override
	public void onCreate()
	{
		super.onCreate();
		scheduleTaskExecutor = Executors.newScheduledThreadPool(2);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		scheduleRefreshData(intent.getExtras().getInt(SERVICE_EXTRA_DELAY_INTERVAL));
		return START_REDELIVER_INTENT;
	}

	public void scheduleRefreshData(int delay)
	{
		scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
			// 	Log.i(TAG, "Refreshing data right now!");
			}
		}, 0, delay, TimeUnit.MINUTES);
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
