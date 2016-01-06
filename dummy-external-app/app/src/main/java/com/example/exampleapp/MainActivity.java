package com.example.exampleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Sample activity enabling external app calling by the FOCUS App
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    /**
     * Convention for extras names
     *
     * All applications being used as FOCUS Mobile external apps follow this convention
     */
    private static final String
            FOCUS_INPUT_EXTRA = "FOCUS_INPUT",
            FOCUS_OUTPUT_EXTRA = "FOCUS_OUTPUT";

    /**
     * Instantiate the activity.
     *
     * We acquire the input extra (input parameter) and update the UI accordingly.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // acquire the input extra
        // this is a JSON string, that can be converted using GSON FIXME TODO Add example
        String inputExtraValue = getIntent().getStringExtra(FOCUS_INPUT_EXTRA);
        if (inputExtraValue == null) {
            inputExtraValue = "";
        }

        // set the acquire value into the UI
        TextView inputValue = (TextView) findViewById(R.id.my_input_field_value);
        inputValue.setText(inputExtraValue);

        return;
    }

    /**
     * Do return the result of the activity.
     *
     * Set the result in the output extra.
     *
     * @param view
     */
    public void onClick(View view) {
        // get the response that has been provided by the user
        EditText responseEditText = (EditText) findViewById(R.id.response);
        String response = responseEditText.getText().toString();
        Log.d(TAG, response);

        // save it as an extra
        Intent returnIntent = new Intent();
        // this is a JSON string, that can be constructed using GSON FIXME TODO Add example
        returnIntent.putExtra(FOCUS_OUTPUT_EXTRA, response);

        // and terminate the activity
        setResult(RESULT_OK, returnIntent); // important, the return code must be RESULT_OK
        finish();
        return;
    }

}
