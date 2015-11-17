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

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private TextView nameType, nameValue,
            emailType, emailValue,
            numberType, numberValue,
            descriptionType, descriptionValue;

    private static final String RESPONSE = "Response",
            KEY_NAME = "Name",
            KEY_EMAIL = "Email",
            KEY_NUMBER = "Number",
            KEY_DESCRIPTION = "Description";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String focusSampleJson = getIntent().getStringExtra("FocusSample");
        Gson gson = new Gson();
        List<FocusSample> focusSampleList = gson.fromJson(focusSampleJson, new TypeToken<List<FocusSample>>() {
        }.getType());

        if (focusSampleList != null) {
            nameValue = (TextView) findViewById(R.id.nameValue);
            nameType = (TextView) findViewById(R.id.nameType);

            emailValue = (TextView) findViewById(R.id.emailValue);
            emailType = (TextView) findViewById(R.id.emailType);

            numberValue = (TextView) findViewById(R.id.numberValue);
            numberType = (TextView) findViewById(R.id.numberType);

            descriptionValue = (TextView) findViewById(R.id.descriptionValue);
            descriptionType = (TextView) findViewById(R.id.descriptionType);

            for (FocusSample fs : focusSampleList) {
                String property = fs.getProperty();

                TextView valueType, value;

                switch (property) {
                    case KEY_NAME:
                        value = nameValue;
                        valueType = nameType;
                        break;
                    case KEY_EMAIL:
                        value = emailValue;
                        valueType = emailType;
                        break;
                    case KEY_NUMBER:
                        value = numberValue;
                        valueType = numberType;
                        break;
                    default:
                        value = descriptionValue;
                        valueType = descriptionType;
                        break;
                }

                FocusSample.Type type = fs.getType();
                valueType.setText("Type: " + type.toString());
                switch (type) {
                    case numeric:
                        Double valueNumeric = (Double) fs.getValue();
                        value.setText(String.valueOf(valueNumeric));
                        break;
                    case string:
                        String valueString = (String) fs.getValue();
                        value.setText(valueString);
                        break;
                    case array_numeric:
                        Double[] valuesArrayNumeric = (Double[]) fs.getValue();
                        StringBuilder stBuild = new StringBuilder();
                        for (double n : valuesArrayNumeric) {
                            stBuild.append(n + ", ");
                        }
                        String v = stBuild.toString();
                        v.substring(v.length() - 2, v.length());
                        value.setText(v);
                        break;
                    default:
                        String[] valuesArrayString = (String[]) fs.getValue();
                        StringBuilder stBuild2 = new StringBuilder();
                        for (String n : valuesArrayString) {
                            stBuild2.append(n + ", ");
                        }
                        value.setText(stBuild2.toString());
                        break;
                }

            }

        } else {
            LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
            mainLayout.removeView(findViewById(R.id.send_response));
            mainLayout.removeView(findViewById(R.id.response_layout));
            mainLayout.removeView(findViewById(R.id.send_button));
        }


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }

    public void onClick(View view) {
        EditText responseEditText = (EditText) findViewById(R.id.response);
        String response = responseEditText.getText().toString();
        Log.d(TAG, response);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(RESPONSE, response);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
