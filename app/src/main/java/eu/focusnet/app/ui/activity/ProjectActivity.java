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

package eu.focusnet.app.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.fragment.ProjectFragment;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.FragmentManager;

/**
 * This class displays (loading the ProjectFragment) the characteristics of a project after
 * the it was selected in the ProjectsListingActivity
 */
public class ProjectActivity extends BaseActivity
{

	private String projectTitle, projectId;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//Get the project title and project id either from the
		//saved instance or from the received calling Intent
		if (savedInstanceState == null) {
			projectTitle = getIntent().getStringExtra(Constant.UI_EXTRA_TITLE);
			//Path is the same as projectId
			projectId = getIntent().getStringExtra(Constant.UI_EXTRA_PROJECT_PATH);
		}
		else {
			projectTitle = savedInstanceState.getString(Constant.UI_EXTRA_TITLE);
			//Path is the same as projectId
			projectId = savedInstanceState.getString(Constant.UI_EXTRA_PROJECT_PATH);
		}

		setTitle(projectTitle);
		Fragment fragment = new ProjectFragment();
		Bundle bundle = new Bundle();
		bundle.putString(Constant.UI_EXTRA_PROJECT_PATH, projectId);
		fragment.setArguments(bundle);
		FragmentManager.replaceFragment(R.id.project_container, fragment, getFragmentManager());
	}

	@Override
	protected int getContentView()
	{
		return R.layout.activity_project;
	}

	@Override
	public void onSaveInstanceState(Bundle saveInstanceState)
	{
		super.onSaveInstanceState(saveInstanceState);
		saveInstanceState.putString(Constant.UI_EXTRA_TITLE, projectTitle);
		saveInstanceState.putString(Constant.UI_EXTRA_PROJECT_PATH, projectId);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_project, menu);
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
		//	if (id == R.id.action_settings) {
		//		return true;
		//	}

		return super.onOptionsItemSelected(item);
	}

}
