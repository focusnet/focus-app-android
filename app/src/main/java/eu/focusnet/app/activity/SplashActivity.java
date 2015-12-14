package eu.focusnet.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import eu.focusnet.app.R;


/**
 * Splash screen activity
 */
public class SplashActivity extends Activity {

    private static final String TAG  = SplashActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

            final Thread SplashScreen = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                       startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    Log.d(TAG, "Finishing SplashActivity");
                    finish(); //close this activity
                }
            }
        };
        SplashScreen.start();
    }
}

