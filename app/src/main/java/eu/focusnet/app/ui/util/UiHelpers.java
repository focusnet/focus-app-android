package eu.focusnet.app.ui.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

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
public class UiHelpers
{

	/**
	 * Displays a toast
	 *
	 * @param context the context
	 * @param msg     the message in the toast
	 */
	public static void displayToast(Context context, CharSequence msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Displays a toast
	 *
	 * @param context the context
	 * @param msg     the message in the toast as integer
	 */
	public static void displayToast(Context context, int msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}


	//TODO find a good place for this method
	public static Bitmap getBitmap(Context context, int image)
	{
		return BitmapFactory.decodeResource(context.getResources(), image);
	}


}
