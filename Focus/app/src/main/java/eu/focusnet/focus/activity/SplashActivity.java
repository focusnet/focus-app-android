package eu.focusnet.focus.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * Created by admin on 09.06.2015.
 */
public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

            Thread SplashScreen = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                    startActivity(new Intent("eu.focusnet.focus.activity.BaseActivity"));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    finish(); //close this activity
                }
            }
        };
        SplashScreen.start();
    }
}

