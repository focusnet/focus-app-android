package eu.focusnet.focus.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by admin on 15.06.2015.
 */
public class Util {

    public static void makeToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
