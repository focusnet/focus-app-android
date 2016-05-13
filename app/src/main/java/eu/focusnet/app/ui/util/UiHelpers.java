package eu.focusnet.app.ui.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.Display;
import android.widget.Toast;

import eu.focusnet.app.FocusApplication;

/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class UiHelpers
{

	private static Float scale;

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

	public static Bitmap getBitmap(Context context, int image)
	{
		return BitmapFactory.decodeResource(context.getResources(), image);
	}


	public static int dp_to_pixels(int dp, Context context)
	{
		if (scale == null) {
			scale = context.getResources().getDisplayMetrics().density;
		}
		return (int) ((float) dp * scale);
	}


	/**
	 * Displays a notification
	 *
	 * @param context        the context
	 * @param cls            the class (Activity which will be started when user click in the notification)
	 * @param icon           the icon
	 * @param title          the title
	 * @param content        the content
	 * @param notificationId Represent the notification id and the navigation id to display the appropriate fragment
	 */
	public static void displayNotification(Context context, Class<?> cls, int icon, CharSequence title, CharSequence content, int notificationId)
	{

		// Intent to be triggered when the notification is selected
		Intent intent = new Intent(context, cls);
		intent.putExtra(Constant.UI_EXTRA_NOTIFICATION_ID, notificationId);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

		//   mBuilder.setVibrate(new long[]{100, 250, 100, 500});
		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Notification.Builder mBuilder = new Notification.Builder(context)
				.setSmallIcon(icon)
				.setContentTitle(title)
				.setContentText(content)
				.setContentIntent(pendingIntent)
				.setWhen(System.currentTimeMillis())
				.setSound(notificationSound);
		Notification notif = mBuilder.build();
		// hide the notification after its selected
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		NotificationManager notifMng = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notifMng.notify(notificationId, notif);
	}

}
