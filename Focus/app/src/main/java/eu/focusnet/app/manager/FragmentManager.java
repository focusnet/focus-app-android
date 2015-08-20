package eu.focusnet.app.manager;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;

/**
 * Created by admin on 17.08.2015.
 */
public class FragmentManager {

    private static final String TAG = FragmentManager.class.getName();

    public static void replaceFragment(int containerViewId, Fragment fragment, android.app.FragmentManager fragmentManager){
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

    public static Fragment getCurrentFragment(android.app.FragmentManager fragmentManager){
        //Get the top fragment's tag from the stack
        String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
        Log.d(TAG, "Current fragment's tag: "+fragmentTag);
        //Get the fragment with this fragment's name and return it
        return fragmentManager.findFragmentByTag(fragmentTag);
    }

}
