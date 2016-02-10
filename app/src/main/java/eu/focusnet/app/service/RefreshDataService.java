package eu.focusnet.app.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import eu.focusnet.app.util.Constant;


public class RefreshDataService extends Service
{

	private static final String TAG = RefreshDataService.class.getName();

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
		scheduleRefreshData(intent.getExtras().getInt(Constant.DELAY_INTERVAL));
		return START_REDELIVER_INTENT;
	}

	public void scheduleRefreshData(int delay)
	{
		scheduleTaskExecutor.scheduleWithFixedDelay(new Runnable()
		{
			@Override
			public void run()
			{
				Log.i(TAG, "Refreshing data right now!");
			}
		}, 0, delay, TimeUnit.MINUTES);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		Log.d(TAG, "Destroying Service");
		scheduleTaskExecutor.shutdown();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
}
