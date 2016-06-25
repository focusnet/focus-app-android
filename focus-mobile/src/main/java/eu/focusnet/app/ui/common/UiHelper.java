package eu.focusnet.app.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DecimalFormat;

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

/**
 * This class contains static methods to help building commonly used UI elements.
 */
public class UiHelper
{
	/**
	 * Displays a toast
	 *
	 * @param context The context
	 * @param msg     The message in the toast
	 */
	public static void displayToast(Context context, CharSequence msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Displays a toast
	 *
	 * @param context The context
	 * @param msg     The message in the toast as a resource reference
	 */
	public static void displayToast(Context context, int msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Get a bitmap image
	 *
	 * @param context The context
	 * @param image   The image as a resource reference
	 * @return a {@code Bitmap} object or {@code null} on failure.
	 */
	public static Bitmap getBitmap(Context context, int image)
	{
		return BitmapFactory.decodeResource(context.getResources(), image);
	}

	/**
	 * Conversion function
	 *
	 * @param dp      The number of dps to convert from.
	 * @param context The context
	 * @return The corresponding number of pixels.
	 */
	public static int dpToPixels(int dp, Context context)
	{
		return (int) ((float) dp * context.getResources().getDisplayMetrics().density);
	}

	/**
	 * Helper function to hide the keyboard when clicking on an element.
	 * <p/>
	 * See http://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
	 *
	 * @param activity The Activity where this operation is performed
	 */
	public static void hideSoftKeyboard(Activity activity)
	{
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		View f = activity.getCurrentFocus();
		if (f != null) {
			inputMethodManager.hideSoftInputFromWindow(f.getWindowToken(), 0);
		}
	}

	/**
	 * Setup the UI to be able to click outside of keyboard to hide it.
	 * <p/>
	 * We recursively parse the content of the View and register a touch listener that will hide
	 * the keyboard on all elements that are not {@link EditText}s.
	 * <p/>
	 * See http://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
	 *
	 * also do append text to EditTexts such that the cursor for editing is at the end and not at the beginning.
	 *
	 * @param view     The view to parse
	 * @param activity The Activity where this operation is performed
	 */
	public static void setupHidableKeyboard(View view, final Activity activity)
	{
		//Set up touch listener for non-text box views to hide keyboard.
		if (!(view instanceof EditText)) {
			view.setOnTouchListener(new View.OnTouchListener()
			{
				public boolean onTouch(View v, MotionEvent event)
				{
					hideSoftKeyboard(activity);
					return false;
				}
			});
		}
		else {
			EditText e = ((EditText) view);
			String txt = e.getText().toString();
			e.setText("");
			e.append(txt);
		}

		//If a layout container, iterate over children and seed recursion.
		if (view instanceof ViewGroup) {
			for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
				View innerView = ((ViewGroup) view).getChildAt(i);
				setupHidableKeyboard(innerView, activity);
			}
		}
	}


	/**
	 * Get a user-friendly data size
	 *
	 * @param size The size of the data to evaluate
	 * @return a String such as "2.2 GB or 1.23kB
	 */
	public static String getFileSize(long size)
	{
		if (size <= 0) {
			return "0";
		}
		final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}


	/**
	 * Close the current activity and redirect the user to another one.
	 *
	 * @param targetActivityClass
	 * @param msg
	 * @param activity
	 */
	public static void redirectTo(Class targetActivityClass, String msg, Activity activity)
	{
		// FIXME no toast, other cannot be called from non-ui threads (e.g. onPrepare of async tasks
		if (msg != null) {
			UiHelper.displayToast(activity, msg);
		}
		activity.finish();
		activity.startActivity(new Intent(activity, targetActivityClass));
	}
}
