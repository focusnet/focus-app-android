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
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

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

    public static LinearLayout buildDialogLayout(Context context){

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setTextAppearance(context, android.R.attr.textAppearanceLarge);
        textView.setText("Hallo Welt!");

        EditText editText = new EditText(context);
        editText.setLayoutParams(layoutParams);

        linearLayout.addView(textView);
       // linearLayout.addView(editText);

//        setContentView(layout);
//
//
//        LinearLayout linearLayout = new LinearLayout(this);
//        LL.setBackgroundColor(Color.CYAN);
//        LL.setOrientation(LinearLayout.VERTICAL);
//
//        LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//
//        LL.setWeightSum(6f);
//        LL.setLayoutParams(LLParams);
//
//
//        ImageView ladder = new ImageView(this);
//        ladder.setImageResource(R.drawable.ic_launcher);
//
//        FrameLayout.LayoutParams ladderParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
//        ladder.setLayoutParams(ladderParams);
//
//        FrameLayout ladderFL = new FrameLayout(this);
//        LinearLayout.LayoutParams ladderFLParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
//        ladderFLParams.weight = 5f;
//        ladderFL.setLayoutParams(ladderFLParams);
//        ladderFL.setBackgroundColor(Color.GREEN);
//        View dummyView = new View(this);
//
//        LinearLayout.LayoutParams dummyParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
//        dummyParams.weight = 1f;
//        dummyView.setLayoutParams(dummyParams);
//        dummyView.setBackgroundColor(Color.RED);
//
//
//
//        ladderFL.addView(ladder);
//        LL.addView(ladderFL);
//        LL.addView(dummyView);
//        RelativeLayout rl=((RelativeLayout) findViewById(R.id.screenRL));
//        rl.addView(LL);
        return  linearLayout;
    }
}
