package eu.focusnet.app.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.Toast;

/**
 * Util class for displaying android specific messages
 */
public class ViewUtil {

    private static final String TAG = ViewUtil.class.getName();

    /**
     * Displays a toast
     * @param context the context
     * @param msg the message in the toast
     */
    public static void displayToast(Context context, CharSequence msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displays a notification
     * @param context the context
     * @param cls the class (Activity which will be started when user click in the notification)
     * @param icon the icon
     * @param title the title
     * @param content the content
     * @param notificationId Represent the notification id and the navigation id to display the appropriate fragment
     */
    public static void displayNotification(Context context, Class<?> cls, int icon, CharSequence title, CharSequence content, int notificationId) {

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
        NotificationManager notifMng = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMng.notify(notificationId, notif);
    }

    //TODO find a good place for this method
    public static Bitmap getBitmap(Context context, int image) {
        return BitmapFactory.decodeResource(context.getResources(), image);
    }
}
