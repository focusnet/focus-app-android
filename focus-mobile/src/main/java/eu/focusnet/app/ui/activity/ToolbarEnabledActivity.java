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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.fragment.FragmentManager;
import eu.focusnet.app.util.Constant;

/**
 * The ToolbarEnabledActivity is used as the basis for Activities that have
 * a title bar and a content below.
 */
public abstract class ToolbarEnabledActivity extends AppCompatActivity
{
	/**
	 * The toolbar, i.e. the top bar with title and action buttons
	 */
	protected Toolbar toolbar;

	/**
	 * The ActionBar of this activity. We operate in compatibility activity, so we set a Toolbar
	 * and must perform actions such as setting titles on the corresponding ActionBar.
	 */
	protected ActionBar actionBar;

	/**
	 * Currently active fragment
	 */
	protected Fragment fragment;

	/**
	 * Title of the activity
	 */
	protected String title;

	/**
	 * Path of the activity currently being displayed.
	 */
	private String path;

	/**
	 * Override creation method:
	 * - Add a toolbar
	 * - setup the title and path depending on incoming extra
	 * - setup the UI
	 * - load the UI
	 *
	 * @param savedInstanceState Inherited.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// set the view
		setContentView(getTargetView());

		// add the toolbar
		this.toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(this.toolbar);

		// configure
		this.actionBar = getSupportActionBar();
		if (this.actionBar != null) {
			this.actionBar.setDisplayHomeAsUpEnabled(true);
		}

		//Get the project title and project id either from the
		//saved instance or from the received calling Intent
		if (savedInstanceState == null) {
			this.title = getIntent().getStringExtra(Constant.Extra.UI_EXTRA_TITLE);
			//Path is the same as projectId
			this.path = getIntent().getStringExtra(Constant.Extra.UI_EXTRA_PATH);
		}
		else {
			this.title = savedInstanceState.getString(Constant.Extra.UI_EXTRA_TITLE);
			//Path is the same as projectId
			this.path = savedInstanceState.getString(Constant.Extra.UI_EXTRA_PATH);
		}

		setTitle(this.title);

		// our activity is then ready to accept customization by subclasses (setup)
		this.setupSpecificUiElements();

		// and run the initial UI loading
		this.applyUiChanges();
	}




	/**
	 * Method that may be overriden to setup Activity-specific UI elements, such as a Drawer or
	 * Action buttons. This method is only called once, in {@link #onCreate(Bundle)}.
	 */
	protected void setupSpecificUiElements()
	{

	}

	/**
	 * Load the UI, based on the current Activity configuration as defined by
	 * {@link #onCreate(Bundle)}, including via {@link #setupSpecificUiElements()}.
	 *
	 * This method is called from {@link #onCreate(Bundle)} and whenever the UI must be refreshed.
	 */
	final protected void applyUiChanges()
	{
		// All operations that do not provoke the loading of a new fragment
		// should be done here
		this.doInPageUiOperations();

		// In case we need to load a new fragment, setup operations are done in this
		// method.
		this.fragment = null;
		this.prepareNewFragment();

		// and replace the fragment
		this.replaceFragment();
	}

	/**
	 * Retrieve the content of the view
	 *
	 * @return a valid View id
	 */
	protected abstract int getTargetView();


	/**
	 * Retrieve
	 * @return a valid layout id
	 */
	protected abstract int getTargetLayoutContainer();

	/**
	 * Do any UI operation that do not imply creating and including a new Fragment in the UI, such
	 * as displaying an AlertDialog.
	 *
	 * See {@link #applyUiChanges()}.
	 */
	protected void doInPageUiOperations()
	{

	}

	/**
	 * Prepare a new Fragment before its inclusion in the UI.
	 *
	 * See {@link #applyUiChanges()}.
	 */
	protected void prepareNewFragment()
	{

	}


	/**
	 * Method for replacing the existing fragment with the newly selected one.
	 *
	 * See {@link #applyUiChanges()}.
	 */
	private void replaceFragment()
	{
		if (this.fragment != null) {

			Bundle bundle = new Bundle();
			bundle.putString(Constant.Extra.UI_EXTRA_PATH, this.path);
			this.fragment.setArguments(bundle);

			FragmentManager.replaceFragment(getTargetLayoutContainer(), this.fragment, getFragmentManager());
		}
	}

	/**
	 * Pass Activity parameters when saving the instance.
	 *
	 * @param saveInstanceState Inherited.
	 */
	@Override
	protected void onSaveInstanceState(Bundle saveInstanceState)
	{
		super.onSaveInstanceState(saveInstanceState);
		saveInstanceState.putString(Constant.Extra.UI_EXTRA_TITLE, this.title);
		saveInstanceState.putString(Constant.Extra.UI_EXTRA_PATH, this.path);
	}

	/**
	 * When the home button is pressed, behave like if the back button of the device was pressed.
	 *
	 * @param item Inherited.
	 * @return Inherited.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
		}
		return (super.onOptionsItemSelected(item));
	}

}
