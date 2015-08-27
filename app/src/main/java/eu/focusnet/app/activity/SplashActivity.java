package eu.focusnet.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import eu.focusnet.app.activity.R;

/**
 * Entry point of the application
 *
 * Display a simple splashscreen for a few seconds.
 *
 * 2 possible courses of action:
 *
 * 1. The application is not configured, yet (fresh start). In this case, the splashscreen is
 * displayed for a few seconds, nothing is done, and then the user is redirected to the login page.
 *
 * FIXME TODO:
 * 2. The application is configured (login details already provided). In this case, the splashscreen
 * is displayed and the application content is updated, downloading the latest data on the remote
 * server. When this is done, the user is redirected to the application welcome screen. This
 * splashscreen must be displayed for at least 3 seconds.
 *
 */
public class SplashActivity extends Activity {

    private static final String TAG = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread SplashScreen = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    startActivity(new Intent("eu.focusnet.app.activity.LoginActivity"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "Finishing SplashActivity");
                    finish(); //close this activity
                }
            }
        };

        SplashScreen.start();
    }
}

