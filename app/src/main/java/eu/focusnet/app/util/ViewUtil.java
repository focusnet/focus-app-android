package eu.focusnet.app.util;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.focusnet.app.fragment.widgets.WidgetFragment;
import eu.focusnet.app.fragment.widgets.BarChartWidgetFragment;
import eu.focusnet.app.fragment.widgets.CameraWidgetFragment;
import eu.focusnet.app.fragment.widgets.EmptyWidgetFragment;
import eu.focusnet.app.fragment.widgets.FormWidgetFragment;
import eu.focusnet.app.fragment.widgets.GPSWidgetFragment;
import eu.focusnet.app.fragment.widgets.Html5WidgetFragment;
import eu.focusnet.app.fragment.widgets.LineChartWidgetFragment;
import eu.focusnet.app.fragment.widgets.PieChartWidgetFragment;
import eu.focusnet.app.fragment.widgets.SubmitWidgetFragment;
import eu.focusnet.app.fragment.widgets.TableWidgetFragment;
import eu.focusnet.app.fragment.widgets.TextWidgetFragment;
import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.manager.FragmentManager;
import eu.focusnet.app.model.internal.PageInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;

/**
 * Util class for displaying android specific messages
 */
public class ViewUtil
{

	public static final String WIDTH = "width";
	public static final String OF = "of";
	public static final String LAYOUT_WIDTH = "layoutWidth";
	public static final String LAYOUT_HEIGHT = "layoutHeight";
	public static final String LAYOUT_WEIGHT = "layoutWeight";
	private static final String TAG = ViewUtil.class.getName();
	private static final String TYPE_TEXT = "#/definitions/widget/visualize/text";
	private static final String TYPE_TABLE = "#/definitions/widget/visualize/table";
	private static final String TYPE_PIE_CHART = "#/definitions/widget/visualize/piechart";
	private static final String TYPE_BAR_CHART = "#/definitions/widget/visualize/barchart";
	private static final String TYPE_LINE_CHART = "#/definitions/widget/visualize/linechart";
	private static final String TYPE_CAMERA = "#/definitions/widget/collect/camera";
	private static final String TYPE_GPS = "#/definitions/widget/collect/gps";
	private static final String TYPE_FORM = "#/definitions/widget/collect/form";
	private static final String TYPE_EXTERNAL_APP = "#/definitions/widget/visualize/external-app";
	private static final String TYPE_SUBMIT = "#/definitions/widget/collect/submit";
	private static final String TYPE_HTML5 = "#/definitions/widget/visualize/html5-widget";

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
		intent.putExtra(Constant.NOTIFICATION_ID, notificationId);
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

	//TODO find a good place for this method
	public static Bitmap getBitmap(Context context, int image)
	{
		return BitmapFactory.decodeResource(context.getResources(), image);
	}


	public static WidgetFragment getWidgetFragmentByType(String type)
	{
		WidgetFragment widgetFragment = null;
		switch (type) {
			case TYPE_TEXT:
				widgetFragment = new TextWidgetFragment();
				break;
			case TYPE_TABLE:
				widgetFragment = new TableWidgetFragment();
				break;
			case TYPE_PIE_CHART:
				widgetFragment = new PieChartWidgetFragment();
				break;
			case TYPE_BAR_CHART:
				widgetFragment = new BarChartWidgetFragment();
				break;
			case TYPE_LINE_CHART:
				widgetFragment = new LineChartWidgetFragment();
				break;
			case TYPE_CAMERA:
				widgetFragment = new CameraWidgetFragment();
				break;
			case TYPE_GPS:
				widgetFragment = new GPSWidgetFragment();
				break;
			case TYPE_FORM:
				widgetFragment = new FormWidgetFragment();
				break;
			case TYPE_EXTERNAL_APP:
				//TODO
				break;
			case TYPE_SUBMIT:
				widgetFragment = new SubmitWidgetFragment();
				break;
			case TYPE_HTML5:
				widgetFragment = new Html5WidgetFragment();
				break;
		}
		return widgetFragment;
	}

	public static void buildPageView(ProjectInstance projectInstance, PageInstance pageInstance, LinearLayout linearLayoutPageInfo, Activity activity)
	{

		final int screenSize = 4;
		int sizeLeft = screenSize;
		int lastSizeLeft = sizeLeft;

		int layoutID = 13334;

		LinkedHashMap<String, WidgetInstance> widgetInstances = pageInstance.getWidgets();

		//Create layout horizontal
		LinearLayout linearLayoutHorizontal = ViewFactory.createLinearLayout(activity, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayoutHorizontal.setId(layoutID);

		for (Map.Entry<String, WidgetInstance> entry : widgetInstances.entrySet()) {
			WidgetInstance widgetInstance = entry.getValue();

			int weight = 0;

			//Get the width of the widget (e.g. 2of4)
			String width = widgetInstance.getLayoutAttribute(WIDTH);
			int indexOf = width.indexOf(OF);
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
			Log.d(TAG, "The weight: " + weight);
			Log.d(TAG, "The size left: " + sizeLeft);

			int linearLayoutWidth = LinearLayout.LayoutParams.MATCH_PARENT;

			if (weight != 0 && weight != screenSize) {
				linearLayoutWidth = 0;
			}

			if (lastSizeLeft == 0 || lastSizeLeft == screenSize) {
				linearLayoutHorizontal = ViewFactory.createLinearLayout(activity, LinearLayout.HORIZONTAL,
						new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				linearLayoutHorizontal.setId(++layoutID);
			}

			linearLayoutPageInfo.removeView(linearLayoutHorizontal);


			WidgetFragment widgetFragment = ViewUtil.getWidgetFragmentByType(widgetInstance.getType());
			Bundle widgetBundle = new Bundle();
			widgetBundle.putString(Constant.PATH, DataManager.getInstance().getAppContentInstance().buildPath(projectInstance, pageInstance, widgetInstance));
			widgetBundle.putInt(LAYOUT_WIDTH, linearLayoutWidth);

			// FIXME FIXME FIXME:
			// access the instance, that may contain information about the height: widgetInstance.getHeightAdvice() = small | full | medium
			// TODO TODO
			// or another strategy: height is ALWAYS 90% of viewport height, execpt if TEXT / TABLE ?
			if (widgetFragment instanceof TableWidgetFragment || widgetFragment instanceof PieChartWidgetFragment) // FIXME FIXME height may depend on widget type and content !
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
