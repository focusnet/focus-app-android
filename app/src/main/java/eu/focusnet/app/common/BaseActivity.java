package eu.focusnet.app.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import eu.focusnet.app.R;
import eu.focusnet.app.service.RefreshDataService;
import eu.focusnet.app.util.Constant;

/**
 * Created by yandypiedra on 17.11.15.
 */
public abstract class BaseActivity extends AppCompatActivity
{
	//Set the toolbar
	protected Toolbar toolbar;
	protected Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(getContentView());
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled());
		getSupportActionBar().setHomeButtonEnabled(isHomeButtonEnabled());
	}

	protected abstract int getContentView();

	protected boolean isDisplayHomeAsUpEnabled()
	{
		return true;
	}

	protected boolean isHomeButtonEnabled()
	{
		return true;
	}


	//TODO starting an stoping the service in each activity is not a good idea
	@Override
	protected void onResume() {
		super.onResume();
		intent = new Intent(this, RefreshDataService.class);
		//TODO delay interval is hardcoded
		intent.putExtra(Constant.DELAY_INTERVAL, 1);
		startService(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopService(intent);
	}

}
