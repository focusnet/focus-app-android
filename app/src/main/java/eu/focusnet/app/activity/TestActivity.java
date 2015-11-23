package eu.focusnet.app.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;
import eu.focusnet.app.common.BaseActivity;
import eu.focusnet.app.model.data.FocusSample;
import eu.focusnet.app.model.ui.ChartData;
import eu.focusnet.app.util.DataFactory;
import eu.focusnet.app.util.ViewFactory;
import eu.focusnet.app.util.ViewUtil;

//This activity is only for testing
public class TestActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private static final String TAG = TestActivity.class.getName();

    private static final String[][] DATA_TO_SHOW = {
            {"This", "is", "a", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"},
            {"and", "a", "second", "test"}

    };

    private static final String[] HEADER = {"Text 1", "Text 2", "Text 3", "Text 4"};

    private final int PICTURE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageView;

    private Button deleteButton, viewButton, takePictureButton;

    private GoogleApiClient googleApiClient;
    private TextView longitude, latitude, altitude, accuracy;
    private volatile boolean positionAsked;

    private static final String RESPONSE = "Response",
            KEY_NAME = "Name",
            KEY_EMAIL = "Email",
            KEY_NUMBER = "Number",
            KEY_DESCRIPTION = "Description";


    private static final String EXAMPLE_APP_ACTION = "com.example.exampleapp.SHOW_FOCUS_DATA";
    private static final String FOCUS_SAMPLE = "FocusSample";
    private final int EXAMPLE_APP_REQUEST = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle("This is a test");

        final int verticalSpace = 50;
        final int takeAllRow = 1;

        float quarter = 0.25f;
        float half = 0.50f;
        float treeQuarters = 0.75f;
        float hole = 1.0f;

        LinearLayout linearLayoutPageInfo = (LinearLayout) findViewById(R.id.test_layout);
        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace / 2, takeAllRow));

        LinearLayout linearLayoutVertical = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView title = ViewFactory.createTextView(this, R.style.TitleAppearance,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "This is Title");
        linearLayoutVertical.addView(title);

        title = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "This is info");
        linearLayoutVertical.addView(title);

        linearLayoutPageInfo.addView(linearLayoutVertical);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));


        LinearLayout linearLayoutHorizontalPieChartTitle = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        TextView pieChartTitle = ViewFactory.createTextView(this, R.style.ChartTitleAppearance,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "PieChart Title");
        linearLayoutHorizontalPieChartTitle.addView(pieChartTitle);

        linearLayoutHorizontalPieChartTitle.addView(ViewFactory.createEmptyView(this, verticalSpace, LinearLayout.LayoutParams.MATCH_PARENT, treeQuarters));

        linearLayoutPageInfo.addView(linearLayoutHorizontalPieChartTitle);


        LinearLayout linearLayoutHorizontal = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500));

//        linearLayoutHorizontal.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f));

        ArrayList<ChartData> chartDatas = new ArrayList<>(4);
        ChartData d = new ChartData("Green", 15, Color.GREEN);
        chartDatas.add(d);
        d = new ChartData("Red", 40, Color.RED);
        chartDatas.add(d);
        d = new ChartData("Blue", 15, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("Yellow", 30, Color.YELLOW);
        chartDatas.add(d);

        PieData pieData = DataFactory.createPieData(chartDatas, "Colors");
        PieChart pieChart = ViewFactory.createPieChart(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, half), "This is the center text", pieData);
        linearLayoutHorizontal.addView(pieChart);


        TableView<String[]> tableView = (TableView<String[]>) ViewFactory.createTableView(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, half), 4);
        SimpleTableHeaderAdapter adapter = new SimpleTableHeaderAdapter(this, HEADER);
        adapter.setPaddingTop(25);
        adapter.setPaddingBottom(25);
        adapter.setTextColor(getResources().getColor(R.color.table_header_text));
        adapter.setTextSize(18);
        tableView.setHeaderAdapter(adapter);
        tableView.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, DATA_TO_SHOW));
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowColoriser(TableDataRowColorizers.alternatingRows(colorEvenRows, colorOddRows));
        linearLayoutHorizontal.addView(tableView);

        linearLayoutPageInfo.addView(linearLayoutHorizontal);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));

        LinearLayout linearLayoutHorizontal2 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1100));

        TextView BarChartTitle = ViewFactory.createTextView(this, R.style.ChartTitleAppearance,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "BarChart Title");

        linearLayoutHorizontal2.addView(BarChartTitle);

        chartDatas = new ArrayList<>(8);
        d = new ChartData("January", -10, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("February", -13,Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("March", 10,Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("April", 16, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("May", 20, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("June", 22, Color.RED);
        chartDatas.add(d);
        d = new ChartData("July", 28, Color.RED);
        chartDatas.add(d);
        d = new ChartData("August", 29, Color.RED);
        chartDatas.add(d);
        d = new ChartData("September", 25, Color.RED);
        chartDatas.add(d);
        d = new ChartData("October", 20, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("November", 10, Color.BLUE);
        chartDatas.add(d);
        d = new ChartData("December", 2, Color.BLUE);
        chartDatas.add(d);

        BarChart barChart = ViewFactory.createBarChart(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500), -15f, DataFactory.createBarData(chartDatas, "Heat"));
        linearLayoutHorizontal2.addView(barChart);
//        linearLayoutHorizontal2.addView(ViewFactory.createEmptyView(this, verticalSpace, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f));

        TextView LineChartTitle = ViewFactory.createTextView(this, R.style.ChartTitleAppearance,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "LineChart Title");
        linearLayoutHorizontal2.addView(LineChartTitle);

        LineChart lineChart = ViewFactory.createLineChart(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500),
                -35, 50,
                -27f, "Lower Limit",
                37f, "Upper Limit",
                DataFactory.createLineData("Heat", chartDatas));


        linearLayoutHorizontal2.addView(lineChart);

        linearLayoutPageInfo.addView(linearLayoutHorizontal2);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));

        LinearLayout linearLayoutVertical2 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        final TextView gps = ViewFactory.createTextView(this, R.style.TitleAppearance,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "GPS");
        linearLayoutVertical2.addView(gps);

        longitude = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Longitude: No value yet");
        linearLayoutVertical2.addView(longitude);

        latitude = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Latitude:  No value yet");
        linearLayoutVertical2.addView(latitude);

        altitude = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Altitude:  No value yet");
        linearLayoutVertical2.addView(altitude);

        accuracy = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Accuracy:  No value yet");
        linearLayoutVertical2.addView(accuracy);


        Button gpsPositionButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Get current position");
        gpsPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positionAsked = true;
            }
        });
        linearLayoutVertical2.addView(gpsPositionButton);

        linearLayoutPageInfo.addView(linearLayoutVertical2);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));

        LinearLayout linearLayoutVertical3 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));

        imageView = ViewFactory.createImageView(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        imageView.setImageBitmap(ViewUtil.getBitmap(this, R.drawable.focus_logo));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
////                Bitmap bitmap = bitmapDrawable.getBitmap();
////                ByteArrayOutputStream stream = new ByteArrayOutputStream();
////                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
////                byte[] bytes = stream.toByteArray();
//                if (imageUri != null) {
//                    Intent intent = new Intent("eu.focusnet.app.activity.ImageActivity");
//                    intent.putExtra("imageUri", imageUri);
//                    startActivity(intent);
//                }
//            }
//        });


        linearLayoutVertical3.addView(imageView);

        linearLayoutPageInfo.addView(linearLayoutVertical3);


        LinearLayout linearLayoutHorizontalPictureButtons = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        viewButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "View");
        if(imageUri == null)
            viewButton.setEnabled(false);
        viewButton.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
                                              Intent intent = new Intent(TestActivity.this, ImageActivity.class);
                                              intent.putExtra("imageUri", imageUri);
                                              startActivity(intent);
                                          }
                                      }
        );

        deleteButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Delete");
        if(imageUri == null)
            deleteButton.setEnabled(false);
        deleteButton.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View v) {
                                                imageView.setImageBitmap(ViewUtil.getBitmap(TestActivity.this, R.drawable.focus_logo));
                                                deleteButton.setEnabled(false);
                                                viewButton.setEnabled(false);
                                                takePictureButton.setText("Take a Picture");
                                                imageUri = null;
                                            }
                                        }
        );



        takePictureButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Take a Picture");
        takePictureButton.setOnClickListener(new View.OnClickListener()

                                             {
                                                 @Override
                                                 public void onClick(View v) {
                                                     ContentValues values = new ContentValues();
                                                     values.put(MediaStore.Images.Media.TITLE, "New Picture");
                                                     values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                                                     imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                                     Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                                     intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                                     startActivityForResult(intent, PICTURE_REQUEST);

                                                 }
                                             }

        );


        linearLayoutHorizontalPictureButtons.addView(takePictureButton);
        linearLayoutHorizontalPictureButtons.addView(viewButton);
        linearLayoutHorizontalPictureButtons.addView(deleteButton);
        linearLayoutHorizontalPictureButtons.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));


        linearLayoutPageInfo.addView(linearLayoutHorizontalPictureButtons);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));


        LinearLayout linearLayoutVerticalForm = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        TextView form = ViewFactory.createTextView(this, R.style.TitleAppearance,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Form");

        linearLayoutVerticalForm.addView(form);

        LinearLayout linearLayoutHorizontalName = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelName = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Name");

        final EditText editTextName = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
        linearLayoutHorizontalName.addView(labelName);
        linearLayoutHorizontalName.addView(editTextName);
        linearLayoutHorizontalName.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

        linearLayoutVerticalForm.addView(linearLayoutHorizontalName);


        LinearLayout linearLayoutHorizontalEmail = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelEmail = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Email");

        final EditText editTextEmail = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
        editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayoutHorizontalEmail.addView(labelEmail);
        linearLayoutHorizontalEmail.addView(editTextEmail);
        linearLayoutHorizontalEmail.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

        linearLayoutVerticalForm.addView(linearLayoutHorizontalEmail);


        LinearLayout linearLayoutHorizontalNumber = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelNumber = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Number");

        final EditText editTextNumber = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
        editTextNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayoutHorizontalNumber.addView(labelNumber);
        linearLayoutHorizontalNumber.addView(editTextNumber);
        linearLayoutHorizontalNumber.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

        linearLayoutVerticalForm.addView(linearLayoutHorizontalNumber);


        LinearLayout linearLayoutHorizontalDescription = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelDescription = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Description");

        final EditText editTextDescription = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
        editTextDescription.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editTextDescription.setLines(8);
        editTextDescription.setMaxLines(10);
        editTextDescription.setMinLines(6);
        editTextDescription.setGravity(Gravity.TOP | Gravity.LEFT);

        linearLayoutHorizontalDescription.addView(labelDescription);
        linearLayoutHorizontalDescription.addView(editTextDescription);
        linearLayoutHorizontalDescription.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

        linearLayoutVerticalForm.addView(linearLayoutHorizontalDescription);


//        LinearLayout linearLayoutHorizontalDatePicker = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//        TextView labelDatePicker = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Pick date");
//        linearLayoutHorizontalDatePicker.addView(labelDatePicker);

  //      DatePicker datePicker = ViewFactory.createDatePicker(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 5));

  //      linearLayoutHorizontalDescription.addView(datePicker);
//        linearLayoutHorizontalDescription.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
//
     //   linearLayoutVerticalForm.addView(linearLayoutHorizontalDatePicker);


        LinearLayout linearLayoutHorizontal5 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelC = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Label C");


        RadioGroup radioGroup = ViewFactory.createRadioGroup(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton radioButton1 = ViewFactory.createRadioButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        radioButton1.setText("Radio Button 1");
        RadioButton radioButton2 = ViewFactory.createRadioButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        radioButton2.setText("Radio Button 2");
        radioGroup.addView(radioButton1);
        radioGroup.addView(radioButton2);

        linearLayoutHorizontal5.addView(labelC);
        linearLayoutHorizontal5.addView(radioGroup);
        linearLayoutHorizontal5.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

        linearLayoutVerticalForm.addView(linearLayoutHorizontal5);


        LinearLayout linearLayoutHorizontal6 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelD = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Label D");

        CheckBox checkBox1 = ViewFactory.createCheckBox(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));
        checkBox1.setText("Check box 1");
        CheckBox checkBox2 = ViewFactory.createCheckBox(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));
        checkBox2.setText("Check box 2");

        linearLayoutHorizontal6.addView(labelD);
        linearLayoutHorizontal6.addView(checkBox1);
        linearLayoutHorizontal6.addView(checkBox2);
        linearLayoutHorizontal6.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));


        linearLayoutVerticalForm.addView(linearLayoutHorizontal6);


        LinearLayout linearLayoutHorizontal7 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelE = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Label E");

        Spinner spinner = ViewFactory.createSpinner(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
        List<String> list = new ArrayList<String>();
        list.add("Value 1");
        list.add("Value 2");
        list.add("Value 3");
        list.add("Value 4");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        linearLayoutHorizontal7.addView(labelE);
        linearLayoutHorizontal7.addView(spinner);
        linearLayoutHorizontal7.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));
        linearLayoutVerticalForm.addView(linearLayoutHorizontal7);

        LinearLayout linearLayoutHorizontal8 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button submitButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half), "Submit");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                Long number = new Long(-1);
                try {
                    number = Long.valueOf(editTextNumber.getText().toString());
                }
                catch (NumberFormatException ex){}
                String description = editTextDescription.getText().toString();

                List<FocusSample> fs = new ArrayList<>();
                FocusSample focusSample = new FocusSample(KEY_NAME, FocusSample.Type.string, name);
                fs.add(focusSample);
                focusSample = new FocusSample(KEY_EMAIL, FocusSample.Type.string, name);
                fs.add(focusSample);
                focusSample = new FocusSample(KEY_NUMBER, FocusSample.Type.numeric, number);
                fs.add(focusSample);
                focusSample = new FocusSample(KEY_DESCRIPTION, FocusSample.Type.string, description);
                fs.add(focusSample);

                Gson gson = new Gson();
                String focusSamplesJson = gson.toJson(fs);
                ViewUtil.displayToast(TestActivity.this, focusSamplesJson);

                Intent intent = new Intent(EXAMPLE_APP_ACTION);
                if(isActivityAvailable(intent)) {
                    intent.putExtra(FOCUS_SAMPLE, focusSamplesJson);
                    startActivityForResult(intent, EXAMPLE_APP_REQUEST);
                }
                else {
                    ViewUtil.displayToast(TestActivity.this, "The is no available activity for the following action: "+EXAMPLE_APP_ACTION);
                }
            }
        });

        linearLayoutHorizontal8.addView(submitButton);

        Button resetButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half), "Reset");
        linearLayoutHorizontal8.addView(resetButton);

        linearLayoutVerticalForm.addView(linearLayoutHorizontal8);

        linearLayoutPageInfo.addView(linearLayoutVerticalForm);


    }

    @Override
    protected int getContentView() {
        return R.layout.activity_test;
    }

    public boolean isActivityAvailable(Intent intent) {
        return getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICTURE_REQUEST) {
            if (resultCode == RESULT_OK) {
                //    Bitmap tookPicture = (Bitmap) data.getExtras().get("data");

//                Bitmap thumbnail = null;
//                try {
//                    thumbnail = MediaStore.Images.Media.getBitmap(
//                            getContentResolver(), imageUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                //  ImageView imageView = (ImageView) findViewById(R.id.imageView);
                // imageView.setImageBitmap(tookPicture);


                imageView.setImageURI(imageUri);
                deleteButton.setEnabled(true);
                viewButton.setEnabled(true);
                takePictureButton.setText("Replace Picture");
            }
        }
        else if(requestCode == EXAMPLE_APP_REQUEST){
            if(resultCode == RESULT_OK){
                String response = data.getStringExtra(RESPONSE);
                ViewUtil.displayToast(this, "The response obtained from EXAMPLE_APP was: "+response);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        startLocationUpdates();

    }

    @Override
    protected void onPause() {
        stopLocationUpdates();
        super.onPause();
    }

    private void startLocationUpdates() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        googleApiClient.connect();
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
        googleApiClient.disconnect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
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


    @Override
    public void onConnected(Bundle bundle) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (positionAsked) {
            longitude.setText("Longitude: " + location.getLongitude());
            latitude.setText("Latitude: " + location.getLatitude());
            altitude.setText("Altitude: " + location.getAltitude());
            accuracy.setText("Accuracy: " + location.getAccuracy());
            positionAsked = false;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }
}
