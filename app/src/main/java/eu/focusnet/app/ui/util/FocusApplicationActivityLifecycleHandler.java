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
package eu.focusnet.app.ui.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;


public class FocusApplicationActivityLifecycleHandler implements Application.ActivityLifecycleCallbacks
{

	private Activity currentActivity;
	private Bundle currentSavedInstanceState; // FIXME TODO YANDY do we need this?

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState)
	{
		this.currentActivity = activity;
		this.currentSavedInstanceState = savedInstanceState;
	}

	@Override
	public void onActivityStarted(Activity activity)
	{


	}

	@Override
	public void onActivityResumed(Activity activity)
	{

	}

	@Override
	public void onActivityPaused(Activity activity)
	{

	}

	@Override
	public void onActivityStopped(Activity activity)
	{

	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState)
	{

	}


	@Override
	public void onActivityDestroyed(Activity activity)
	{
		this.currentActivity = null;
		this.currentSavedInstanceState = null;
	}

	/**
	 * Restart the currently active activity.
	 */
	public void restartCurrentActivity()
	{
		if (this.currentActivity != null) {
			Intent intent = this.currentActivity.getIntent();
			this.currentActivity.finish();
			this.currentActivity.startActivity(intent);
		}
	}
}