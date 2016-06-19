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

package eu.focusnet.focus_mobile.ui.activity;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import eu.focusnet.focus_mobile.R;
import eu.focusnet.focus_mobile.ui.util.Constant;
import eu.focusnet.focus_mobile.ui.util.FragmentManager;

/**
 * The ToolbarEnabledActivity is used as the basis for Activities that have
 * a title bar and a content below.
 */
public abstract class ToolbarEnabledActivity extends AppCompatActivity
{
	protected Toolbar toolbar;
	protected Fragment fragment;
	protected String title;
	protected ActionBar actionBar;
	private String path;

	/**
	 * Override creation method. Add a toolbar.
	 *
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set the view
		setContentView(getTargetView());

		// add the toolbar
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// configure
		this.actionBar = getSupportActionBar();
		if (this.actionBar != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true); // FIXME not same behavior as BACK button.
		}

		//Get the project title and project id either from the
		//saved instance or from the received calling Intent
		if (savedInstanceState == null) {
			this.title = getIntent().getStringExtra(Constant.UI_EXTRA_TITLE);
			//Path is the same as projectId
			this.path = getIntent().getStringExtra(Constant.UI_EXTRA_PATH);
		}
		else {
			this.title = savedInstanceState.getString(Constant.UI_EXTRA_TITLE);
			//Path is the same as projectId
			this.path = savedInstanceState.getString(Constant.UI_EXTRA_PATH);
		}

		setTitle(this.title);

		// our activity is then ready to accept customization by subclasses
		this.setupSpecificUiElements();

		// and run the initial UI loading (in page operations and fragment loading)
		this.applyUiChanges();
	}


	/**
	 * Retrieve the content of the view
	 *
	 * @return a valid Content View id
	 */
	protected abstract int getTargetView();


	protected abstract int getTargetLayoutContainer();

	/**
	 * Method that may be overriden to setup Activity-specific UI elements, such as a Drawer or
	 * Action buttons.
	 */
	protected void setupSpecificUiElements()
	{

	}

	protected void prepareNewFragment()
	{

	}

	/**
	 * All operations that do not infert
	 */
	protected void doInPageUiOperations()
	{

	}

	@Override
	protected void onSaveInstanceState(Bundle saveInstanceState)
	{
		super.onSaveInstanceState(saveInstanceState);
		saveInstanceState.putString(Constant.UI_EXTRA_TITLE, this.title);
		saveInstanceState.putString(Constant.UI_EXTRA_PATH, this.path);
	}

	final protected void applyUiChanges()
	{
		// all operations that do not provoke the loading of a new fragment
		// should be done here
		this.doInPageUiOperations();

		// in case we need to load a new fragment, setup operations are done in this
		// method.
		this.fragment = null;
		this.prepareNewFragment();

		// and replace the fragment
		this.replaceFragment();
	}


	/**
	 * Method for replacing the existing fragment with the newly selected one.
	 */
	private void replaceFragment()
	{
		if (this.fragment != null) {

			Bundle bundle = new Bundle();
			bundle.putString(Constant.UI_EXTRA_PATH, this.path);
			this.fragment.setArguments(bundle);

			FragmentManager.replaceFragment(getTargetLayoutContainer(), this.fragment, getFragmentManager());
		}

	}

	// make the home button behave like the back button.
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}

		return(super.onOptionsItemSelected(item));
	}


}
