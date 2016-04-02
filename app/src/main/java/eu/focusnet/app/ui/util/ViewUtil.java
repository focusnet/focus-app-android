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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.model.internal.PageInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.model.internal.widgets.BarChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.CameraWidgetInstance;
import eu.focusnet.app.model.internal.widgets.ExternalAppWidgetInstance;
import eu.focusnet.app.model.internal.widgets.FormWidgetInstance;
import eu.focusnet.app.model.internal.widgets.GPSWidgetInstance;
import eu.focusnet.app.model.internal.widgets.Html5WidgetInstance;
import eu.focusnet.app.model.internal.widgets.LineChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.PieChartWidgetInstance;
import eu.focusnet.app.model.internal.widgets.SubmitWidgetInstance;
import eu.focusnet.app.model.internal.widgets.TableWidgetInstance;
import eu.focusnet.app.model.internal.widgets.TextWidgetInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.ui.fragment.widget.BarChartWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.CameraWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.EmptyWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.FormWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.GPSWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.Html5WidgetFragment;
import eu.focusnet.app.ui.fragment.widget.LineChartWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.PieChartWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.SubmitWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.TableWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.TextWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.WidgetFragment;

/**
 * Util class for displaying android specific messages
 */
public class ViewUtil
{

	public static final String WIDGET_LAYOUT_OF = "of";
	public static final String LAYOUT_WIDTH = "layoutWidth";
	public static final String LAYOUT_HEIGHT = "layoutHeight";
	public static final String LAYOUT_WEIGHT = "layoutWeight";




	/**
	 * @param context
	 * @param orientation
	 * @param layoutParams
	 * @return
	 */
	public static LinearLayout createLinearLayout(Context context, int orientation, LinearLayout.LayoutParams layoutParams)
	{
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(orientation);
		layout.setLayoutParams(layoutParams);
		return layout;
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


	/**
	 * Get the WidgetFragment subtype depending on the input parameter
	 */
	public static WidgetFragment getWidgetFragmentByType(WidgetInstance wi)
	{
		if (wi instanceof TextWidgetInstance) {
			return new TextWidgetFragment();
		}
		else if (wi instanceof TableWidgetInstance) {
			return new TableWidgetFragment();
		}
		else if (wi instanceof PieChartWidgetInstance) {
			return new PieChartWidgetFragment();
		}
		else if (wi instanceof BarChartWidgetInstance) {
			return new BarChartWidgetFragment();
		}
		else if (wi instanceof LineChartWidgetInstance) {
			return new LineChartWidgetFragment();
		}
		else if (wi instanceof CameraWidgetInstance) {
			return new CameraWidgetFragment();
		}
		else if (wi instanceof GPSWidgetInstance) {
			return new GPSWidgetFragment();
		}
		else if (wi instanceof FormWidgetInstance) {
			return new FormWidgetFragment();
		}
		else if (wi instanceof ExternalAppWidgetInstance) {
			return null;
		}
		else if (wi instanceof SubmitWidgetInstance) {
			return new SubmitWidgetFragment();
		}
		else if (wi instanceof Html5WidgetInstance) {
			return new Html5WidgetFragment();
		}
		return null;
	}

	/**
	 * Build a page consisting of widgets
	 *
	 * @param projectInstance
	 * @param pageInstance
	 * @param linearLayoutPageInfo
	 * @param activity
	 */
	public static void buildPageView(ProjectInstance projectInstance, PageInstance pageInstance, LinearLayout linearLayoutPageInfo, Activity activity)
	{

		final int screenSize = 4;
		int sizeLeft = screenSize;
		int lastSizeLeft = sizeLeft;

		int layoutID = 13334;

		LinkedHashMap<String, WidgetInstance> widgetInstances = pageInstance.getWidgets();

		//Create layout horizontal
		LinearLayout linearLayoutHorizontal = ViewUtil.createLinearLayout(activity, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayoutHorizontal.setId(layoutID);

		for (Map.Entry<String, WidgetInstance> entry : widgetInstances.entrySet()) {
			WidgetInstance widgetInstance = entry.getValue();

			int weight = 0;

			//Get the width of the widget (e.g. 2of4)
			String width = widgetInstance.getLayoutAttribute(WidgetInstance.WIDGET_LAYOUT_WIDTH_LABEL);
			int indexOf = width.indexOf(WIDGET_LAYOUT_OF);
			weight = Integer.valueOf(width.substring(0, indexOf).trim());

			int tempLastSizeLeft = lastSizeLeft;
			if (lastSizeLeft == 0) {
				lastSizeLeft = screenSize;
			}

			if ((screenSize - lastSizeLeft) + weight > screenSize) {
				WidgetFragment emptyWidgetFragment = new EmptyWidgetFragment();
				Bundle widgetBundle = new Bundle();
				widgetBundle.putInt(LAYOUT_WIDTH, 0);
				widgetBundle.putInt(LAYOUT_HEIGHT, LinearLayout.LayoutParams.WRAP_CONTENT);
				widgetBundle.putInt(LAYOUT_WEIGHT, tempLastSizeLeft);
				emptyWidgetFragment.setArguments(widgetBundle);
				FragmentManager.addFragment(linearLayoutHorizontal.getId(), emptyWidgetFragment, activity.getFragmentManager());
				lastSizeLeft = screenSize;
			}

			sizeLeft = Math.abs((lastSizeLeft - weight) % screenSize);

			int linearLayoutWidth = LinearLayout.LayoutParams.MATCH_PARENT;

			if (weight != 0 && weight != screenSize) {
				linearLayoutWidth = 0;
			}

			if (lastSizeLeft == 0 || lastSizeLeft == screenSize) {
				linearLayoutHorizontal = ViewUtil.createLinearLayout(activity, LinearLayout.HORIZONTAL,
						new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				linearLayoutHorizontal.setId(++layoutID);
			}

			linearLayoutPageInfo.removeView(linearLayoutHorizontal);


			WidgetFragment widgetFragment = ViewUtil.getWidgetFragmentByType(widgetInstance);
			Bundle widgetBundle = new Bundle();
			widgetBundle.putString(Constant.UI_EXTRA_PATH, FocusApplication.getInstance().getDataManager().getAppContentInstance().buildPath(projectInstance, pageInstance, widgetInstance));
			widgetBundle.putInt(LAYOUT_WIDTH, linearLayoutWidth);

			// FIXME FIXME FIXME:
			// access the instance, that may contain information about the height: widgetInstance.getHeightAdvice() = small | full | medium
			// TODO TODO
			// or another strategy: height is ALWAYS 90% of viewport height, execpt if TEXT / TABLE ?
			if (widgetFragment instanceof TableWidgetFragment
					|| widgetFragment instanceof PieChartWidgetFragment
					|| widgetFragment instanceof BarChartWidgetFragment
					|| widgetFragment instanceof LineChartWidgetFragment) // FIXME FIXME height may depend on widget type and content !
			{
				widgetBundle.putInt(LAYOUT_HEIGHT, 500);
			}
			else {
				widgetBundle.putInt(LAYOUT_HEIGHT, LinearLayout.LayoutParams.WRAP_CONTENT);
			}
			widgetBundle.putInt(LAYOUT_WEIGHT, weight);

			widgetFragment.setArguments(widgetBundle);

			FragmentManager.addFragment(linearLayoutHorizontal.getId(), widgetFragment, activity.getFragmentManager());

			lastSizeLeft = sizeLeft;

			linearLayoutPageInfo.addView(linearLayoutHorizontal);
		}

		if (lastSizeLeft != 0) {
			WidgetFragment emptyWidgetFragment = new EmptyWidgetFragment();
			Bundle widgetBundle = new Bundle();
			widgetBundle.putInt(LAYOUT_WIDTH, 0);
			widgetBundle.putInt(LAYOUT_HEIGHT, LinearLayout.LayoutParams.WRAP_CONTENT);
			widgetBundle.putInt(LAYOUT_WEIGHT, lastSizeLeft);
			emptyWidgetFragment.setArguments(widgetBundle);
			FragmentManager.addFragment(linearLayoutHorizontal.getId(), emptyWidgetFragment, activity.getFragmentManager());
		}
	}
}
