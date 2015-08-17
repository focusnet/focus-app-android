package eu.focusnet.app.service;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import eu.focusnet.app.activity.R;

/**
 * Created by admin on 17.08.2015.
 */
public class FragmentService {

    private static final String TAG = FragmentService.class.getName();

    public static void replaceFragment(int containerViewId, Fragment fragment, FragmentManager fragmentManager){
        String fragementName = fragment.getClass().getName();
        Log.d(TAG, "The fragment's name: " + fragementName);
        boolean isFragementPoped = fragmentManager.popBackStackImmediate(fragementName, 0);
        Log.d(TAG, "Is poped: "+isFragementPoped);

        if(!isFragementPoped) {
            FragmentTransaction fragTrans = fragmentManager.beginTransaction();
            fragTrans.replace(containerViewId, fragment, fragementName);
            fragTrans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragTrans.addToBackStack(fragementName);
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
