package eu.focusnet.app.common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import eu.focusnet.app.activity.R;

/**
 * Created by yandypiedra on 17.11.15.
 */
public abstract class BaseActivity extends AppCompatActivity {


    //Set the toolbar
    protected  Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled());
        getSupportActionBar().setHomeButtonEnabled(isHomeButtonEnabled());
    }

    protected abstract int getContentView();

    protected boolean isDisplayHomeAsUpEnabled(){
        return true;
    }

    protected boolean isHomeButtonEnabled(){
        return true;
    }

}
