package eu.focusnet.app.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import eu.focusnet.app.activity.R;

/**
 * Created by admin on 15.06.2015.
 */
public class Util {

    private static final String TAG = Util.class.getName();

    public static void displayToast(Context context, CharSequence msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     *
     * @param context
     * @param cls
     * @param icon
     * @param title
     * @param content
     * @param notificationId Represent the notification id and the navigation id to display the appropriate fragment
     */
    public static void displayNotification(Context context, Class<?> cls, int icon, CharSequence title, CharSequence content ,int notificationId) {

        // Intent to be triggered when the notification is selected
        Intent intent = new Intent(context, cls);
        intent.putExtra(Constant.NOTIFICATION_ID, notificationId);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Notification.Builder mBuilder = new Notification.Builder(context);
        mBuilder.setSmallIcon(icon);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(content);
        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setWhen(System.currentTimeMillis());
     //   mBuilder.setVibrate(new long[]{100, 250, 100, 500});
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(notificationSound);
        Notification notif = mBuilder.build();
        // hide the notification after its selected
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notifMng = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifMng.notify(notificationId, notif);
    }

    public static ProgressDialog createProgressDialog(Context context, CharSequence title, CharSequence message){
        final ProgressDialog progDialog = new ProgressDialog(context);
        progDialog.setTitle(title);
        progDialog.setMessage(message);
        return progDialog;
    }

    public static Bitmap getBitmap(Context context, int image) {
        return BitmapFactory.decodeResource(context.getResources(), image);
    }

    public static void replaceFragment(Fragment fragment, FragmentManager fragmentManager){
        String backStateName = fragment.getClass().getName();
        Log.d(TAG, "The backStateName: " + backStateName);
        boolean fragmentInBackStack = fragmentManager.popBackStackImmediate(backStateName, 0);
        Log.d(TAG, "Is poped: "+fragmentInBackStack);

        if(!fragmentInBackStack) {
            FragmentTransaction fragTrans = fragmentManager.beginTransaction();
            fragTrans.replace(R.id.frame_container, fragment, backStateName);
            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragTrans.addToBackStack(backStateName);
            fragTrans.commit();
        }
    }

    public static Fragment getCurrentFragment(FragmentManager fragmentManager){
        //Get the top fragment's tag from the stack
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Log.d(TAG, "Current fragment's tag: "+fragmentTag);
        //Get the fragment with this fragment's name and return it
        return fragmentManager.findFragmentByTag(fragmentTag);
    }

}
