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

package eu.focusnet.app.ui.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;

import eu.focusnet.app.R;

/**
 * Helper class for handling {@code Fragment}s
 */
final public class FragmentManager
{
	/**
	 * Replace a {@code Fragment} with a smooth animation.
	 *
	 * @param containerViewId The target container
	 * @param newFragment The new {@code Fragment}
	 * @param fragmentManager The Android {@code FragmentManager}
	 */
	public static void replaceFragment(int containerViewId, Fragment newFragment, android.app.FragmentManager fragmentManager)
	{
		String fragementName = newFragment.getClass().getName();
		boolean isFragementPoped = fragmentManager.popBackStackImmediate(fragementName, 0);

		if (!isFragementPoped) {
			FragmentTransaction fragTrans = fragmentManager.beginTransaction();
			fragTrans.setCustomAnimations(R.animator.in_from_down, R.animator.out_to_down);
			fragTrans.replace(containerViewId, newFragment, fragementName);
			fragTrans.addToBackStack(fragementName);
			fragTrans.commit();
		}
	}

	/**
	 * Add a {@code Fragment} to the container
	 *
	 * @param containerViewId Target container
	 * @param newFragment The new {@code Fragment}
	 * @param fragmentManager The Android {@code FragmentManager}
	 */
	public static void addFragment(int containerViewId, Fragment newFragment, android.app.FragmentManager fragmentManager)
	{
		FragmentTransaction fragTrans = fragmentManager.beginTransaction();
		fragTrans.add(containerViewId, newFragment, null);
		fragTrans.commit();
	}
}
