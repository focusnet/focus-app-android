package eu.focusnet.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import eu.focusnet.app.activity.R;

/**
 * Created by admin on 16.06.2015.
 */
public class LoginAcitvity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
    }

    public void onClick(View view){
        //TODO get the values of the textViews and find out if the user has an account
        Intent i = new Intent("eu.focusnet.focus.activity.MainActivity");
        startActivity(i);
        finish();
    }
}
