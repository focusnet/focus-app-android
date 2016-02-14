package eu.focusnet.app.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.fragment.PageFragment;
import eu.focusnet.app.ui.util.FragmentManager;
import eu.focusnet.app.ui.util.Constant;


/**
 * This class displays (loading the PageFragment) the characteristics of a page after
 * the it was selected in the ProjectActivity
 */
public class PageActivity extends BaseActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		String pageTitle = getIntent().getStringExtra(Constant.UI_EXTRA_TITLE);
		String pageId = getIntent().getStringExtra(Constant.UI_EXTRA_PAGE_PATH);
		String projectId = getIntent().getStringExtra(Constant.UI_EXTRA_PROJECT_PATH);
		setTitle(pageTitle);

		Fragment fragment = new PageFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.UI_EXTRA_PAGE_PATH, pageId);
		bundle.putString(Constant.UI_EXTRA_PROJECT_PATH, projectId);
		fragment.setArguments(bundle);
		FragmentManager.replaceFragment(R.id.page_container, fragment, getFragmentManager());
	}

	@Override
	protected int getContentView()
	{
		return R.layout.activity_page;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
