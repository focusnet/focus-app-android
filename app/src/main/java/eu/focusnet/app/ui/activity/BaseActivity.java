package eu.focusnet.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import eu.focusnet.app.R;

/**
 * The BaseActivity is used as the basis for Activities that have a title bar and a content below
 */
public abstract class BaseActivity extends AppCompatActivity
{
	protected Toolbar toolbar;
	protected Intent cronServiceIntent;

	/**
	 * Override creation method. Add a toolbar.
	 *
	 * @param savedInstanceState
	 */
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

	/**
	 * Retrieve the content of the view
	 *
	 * @return a valid Content View id
	 */
	protected abstract int getContentView();

	/**
	 * Do we FIXME documentation
	 *
	 * @return True if we do
	 */
	protected boolean isDisplayHomeAsUpEnabled()
	{
		return true;
	}

	/**
	 * Do we FIXME documentation
	 *
	 * @return True if we do
	 */
	protected boolean isHomeButtonEnabled()
	{
		return true;
	}

	//TODO starting an stoping the service in each activity is not a good idea
	// NOTE: why? except the fact that this is a pain to maintain (easy to forget to add the service to an activity)

}
