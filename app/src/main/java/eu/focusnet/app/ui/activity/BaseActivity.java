/**
 *
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

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
