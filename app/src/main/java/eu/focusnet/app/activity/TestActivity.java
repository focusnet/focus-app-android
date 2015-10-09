package eu.focusnet.app.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;
import eu.focusnet.app.util.ViewFactory;
import eu.focusnet.app.util.ViewUtil;

//This activity is only for testing
public class TestActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

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

    private GoogleApiClient googleApiClient;
    private TextView longitude, latitude, altitude, accuracy;
    private volatile boolean positionAsked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        setTitle("This is a test");

        final int horizontalSpace = 50;
        final int takeAllRow = 1;

        LinearLayout linearLayoutPageInfo = (LinearLayout) findViewById(R.id.test_layout);
        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, horizontalSpace / 2, takeAllRow));

        LinearLayout linearLayoutVertical = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView title = ViewFactory.createTextView(this, R.style.TitleAppearance,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "This is Title");
        linearLayoutVertical.addView(title);

        title = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "This is info");
        linearLayoutVertical.addView(title);

        linearLayoutPageInfo.addView(linearLayoutVertical);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, horizontalSpace, takeAllRow));


        LinearLayout linearLayoutHorizontal = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500));
//
        TableView<String[]> tableView = (TableView<String[]>) ViewFactory.createTableView(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.8f), 4);
        SimpleTableHeaderAdapter adapter = new SimpleTableHeaderAdapter(this, HEADER);
        adapter.setPaddingTop(25);
        adapter.setPaddingBottom(25);
        adapter.setTextColor(getResources().getColor(R.color.table_header_text));
        adapter.setTextSize(18);
        tableView.setHeaderAdapter(adapter);
        tableView.setHeaderBackgroundColor(getResources().getColor(R.color.app_color));
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, DATA_TO_SHOW));
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowColoriser(TableDataRowColorizers.alternatingRows(colorEvenRows, colorOddRows));


        linearLayoutHorizontal.addView(tableView);

        linearLayoutHorizontal.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f));

        PieChart pieChart = new PieChart(this);
        pieChart.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.8f));


        pieChart.setUsePercentValues(true);

        pieChart.setDescription("Description");


        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColorTransparent(true);
        pieChart.setHoleRadius(58f);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);

        pieChart.setDrawCenterText(true);
        pieChart.setCenterText("This is a text");

        //    data.setValueTypeface(tf);

        PieData data = createPieData(4, 100);
        pieChart.setData(data);
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();

        pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
        // mChart.spin(2000, 0, 360);

        Legend l = pieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        linearLayoutHorizontal.addView(pieChart);

        linearLayoutPageInfo.addView(linearLayoutHorizontal);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, horizontalSpace, takeAllRow));


        LinearLayout linearLayoutHorizontal2 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500));


        BarChart barChart = new BarChart(this);
        barChart.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.8f));

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);

        barChart.setDescription("Description");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        barChart.setDrawBarShadow(true);

        // mChart.setDrawXLabels(false);

        barChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);


        //  mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);


        YAxis leftAxis = barChart.getAxisLeft();
        //leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);

        barChart.animateY(2500);

        barChart.getAxisRight().setEnabled(false);
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//       // rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(8, false);
//        //rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);

        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);

        barChart.setData(createBarData(12, 50));

        linearLayoutHorizontal2.addView(barChart);
        linearLayoutHorizontal2.addView(ViewFactory.createEmptyView(this, horizontalSpace, LinearLayout.LayoutParams.MATCH_PARENT, 0.4f));

        LineChart line_chart = new LineChart(this);
        line_chart.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 2.8f));
        line_chart.setDrawGridBackground(false);

        // no description text
        line_chart.setDescription("Description");
        line_chart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        line_chart.setHighlightEnabled(true);

        // enable touch gestures
        line_chart.setTouchEnabled(true);

        // enable scaling and dragging
        line_chart.setDragEnabled(true);
        line_chart.setScaleEnabled(true);
//        mChart.setScaleXEnabled(true);
//         mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        line_chart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

//        // create a custom MarkerView (extend MarkerView) and specify the layout
//        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//        // set the marker to the chart
//        mChart.setMarkerView(mv);

        // x-axis limit line
        LimitLine limitLineXAxis = new LimitLine(3f, "Index 10");
        limitLineXAxis.setLineWidth(4f);
        limitLineXAxis.enableDashedLine(10f, 10f, 0f);
        // llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        limitLineXAxis.setTextSize(10f);


        XAxis xAxis1 = line_chart.getXAxis();
        xAxis1.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setXValueFormatter(new MyCustomXValueFormatter());
        xAxis1.setSpaceBetweenLabels(1);
        xAxis1.addLimitLine(limitLineXAxis); // add x-axis limit line

        LimitLine limitLineUpperYAxis = new LimitLine(60f, "Upper Limit");
        limitLineUpperYAxis.setLineWidth(4f);
        limitLineUpperYAxis.enableDashedLine(10f, 10f, 0f);
        //   ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLineUpperYAxis.setTextSize(10f);

        LimitLine limitLineLowerYAxis = new LimitLine(10f, "Lower Limit");
        limitLineLowerYAxis.setLineWidth(4f);
        limitLineLowerYAxis.enableDashedLine(10f, 10f, 0f);
        //   ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        limitLineLowerYAxis.setTextSize(10f);

        YAxis leftAxis1 = line_chart.getAxisLeft();
        leftAxis1.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis1.addLimitLine(limitLineUpperYAxis);
        leftAxis1.addLimitLine(limitLineLowerYAxis);
        leftAxis1.setAxisMaxValue(75);
        leftAxis1.setYOffset(10);
        leftAxis1.setStartAtZero(true);
        leftAxis1.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis1.setDrawLimitLinesBehindData(true);

        line_chart.getAxisRight().setEnabled(false);

//        mChart.getViewPortHandler().setMaximumScaleY(2f);
//        mChart.getViewPortHandler().setMaximumScaleX(2f);

        line_chart.setData(createLineData(18, 100));

        line_chart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend legend1 = line_chart.getLegend();

        // modify the legend ...
        legend1.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend1.setForm(Legend.LegendForm.LINE);


        linearLayoutHorizontal2.addView(line_chart);

        linearLayoutPageInfo.addView(linearLayoutHorizontal2);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, horizontalSpace, takeAllRow));

        LinearLayout linearLayoutVertical2 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView gps = ViewFactory.createTextView(this, R.style.TitleAppearance,
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

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, horizontalSpace, takeAllRow));

        LinearLayout linearLayoutVertical3 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 300));

        imageView = ViewFactory.createImageView(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        imageView.setImageBitmap(ViewUtil.getBitmap(this, R.drawable.focus_logo));
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                BitmapDrawable bitmapDrawable = ((BitmapDrawable) imageView.getDrawable());
//                Bitmap bitmap = bitmapDrawable.getBitmap();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                byte[] bytes = stream.toByteArray();
                if(imageUri != null) {
                    Intent intent = new Intent("eu.focusnet.app.activity.ImageActivity");
                    intent.putExtra("imageUri", imageUri);
                    startActivity(intent);
                }
            }
        });


        linearLayoutVertical3.addView(imageView);

        linearLayoutPageInfo.addView(linearLayoutVertical3);

        Button pictureButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Replace Picture");
        pictureButton.setOnClickListener(new View.OnClickListener()

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

        linearLayoutPageInfo.addView(pictureButton);

        linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, horizontalSpace, takeAllRow));


        LinearLayout linearLayoutVerticalForm = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        TextView form = ViewFactory.createTextView(this, R.style.TitleAppearance,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Form");

        linearLayoutVerticalForm.addView(form);

        LinearLayout linearLayoutHorizontalName = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelName = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Name");

        EditText editTextName = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
        linearLayoutHorizontalName.addView(labelName);
        linearLayoutHorizontalName.addView(editTextName);
        linearLayoutHorizontalName.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        linearLayoutVerticalForm.addView(linearLayoutHorizontalName);


        LinearLayout linearLayoutHorizontalEmail = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelEmail = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Email");

        EditText editTextEmail = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
        editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        linearLayoutHorizontalEmail.addView(labelEmail);
        linearLayoutHorizontalEmail.addView(editTextEmail);
        linearLayoutHorizontalEmail.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        linearLayoutVerticalForm.addView(linearLayoutHorizontalEmail);


        LinearLayout linearLayoutHorizontalNumber = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelNumber = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Number");

        EditText editTextNumber = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
        editTextNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
        linearLayoutHorizontalNumber.addView(labelNumber);
        linearLayoutHorizontalNumber.addView(editTextNumber);
        linearLayoutHorizontalNumber.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        linearLayoutVerticalForm.addView(linearLayoutHorizontalNumber);


        LinearLayout linearLayoutHorizontalDescription = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelDescription = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Description");

        EditText editTextDescription = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
        editTextDescription.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        editTextDescription.setLines(8);
        editTextDescription.setMaxLines(10);
        editTextDescription.setMinLines(6);
        editTextDescription.setGravity(Gravity.TOP | Gravity.LEFT);

        linearLayoutHorizontalDescription.addView(labelDescription);
        linearLayoutHorizontalDescription.addView(editTextDescription);
        linearLayoutHorizontalDescription.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

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
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Label C");


        RadioGroup radioGroup = ViewFactory.createRadioGroup(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
        RadioButton radioButton1 = ViewFactory.createRadioButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        radioButton1.setText("Radio Button 1");
        RadioButton radioButton2 = ViewFactory.createRadioButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        radioButton2.setText("Radio Button 2");
        radioGroup.addView(radioButton1);
        radioGroup.addView(radioButton2);

        linearLayoutHorizontal5.addView(labelC);
        linearLayoutHorizontal5.addView(radioGroup);
        linearLayoutHorizontal5.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));

        linearLayoutVerticalForm.addView(linearLayoutHorizontal5);


        LinearLayout linearLayoutHorizontal6 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelD = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Label D");

        CheckBox checkBox1 = ViewFactory.createCheckBox(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        checkBox1.setText("Check box 1");
        CheckBox checkBox2 = ViewFactory.createCheckBox(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        checkBox2.setText("Check box 2");

        linearLayoutHorizontal6.addView(labelD);
        linearLayoutHorizontal6.addView(checkBox1);
        linearLayoutHorizontal6.addView(checkBox2);
        linearLayoutHorizontal6.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));

        linearLayoutVerticalForm.addView(linearLayoutHorizontal6);


        LinearLayout linearLayoutHorizontal7 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView labelE = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
                new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Label E");

        Spinner spinner = ViewFactory.createSpinner(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3));
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
        linearLayoutHorizontal7.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
        linearLayoutVerticalForm.addView(linearLayoutHorizontal7);

        LinearLayout linearLayoutHorizontal8 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button submitButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Submit");
        linearLayoutHorizontal8.addView(submitButton);

        Button resetButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1), "Reset");
        linearLayoutHorizontal8.addView(resetButton);

        linearLayoutVerticalForm.addView(linearLayoutHorizontal8);

        linearLayoutPageInfo.addView(linearLayoutVerticalForm);


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

    private LineData createLineData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 6; i <= count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 6; i <= count; i++) {

            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);
            yVals.add(new Entry(val, i)); // i position in the x-axis
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        set1.enableDashedLine(10f, 5f, 0f);
        //  set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleSize(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.BLACK);
//        set1.setDrawFilled(true);
        // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
        // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        return data;
    }


    /**
     * @param count
     * @param range
     */
    private BarData createBarData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add("" + i % 12); //x-axis
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>(); //y-axis values

        for (int i = 0; i < count; i++) {
            float mult = (range + 1);
            float val = (float) (Math.random() * mult);
            yVals1.add(new BarEntry(val, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        // data.setValueFormatter(new MyValueFormatter());
        data.setValueTextSize(10f);
        //data.setValueTypeface(mTf);
        return data;
    }


    //TODO set parameter (List<?> data)
    private PieData createPieData(int count, float range) {

        float mult = range;

        ArrayList<Entry> yVals1 = new ArrayList<>(); // reached votes by participant y-axis

        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        for (int i = 1; i < count + 1; i++) {
            yVals1.add(new Entry((float) (Math.random() * mult) + mult / count + 1, i));
        }

        ArrayList<String> xVals = new ArrayList<String>(); // number of different participants x-axis

        for (int i = 0; i < count + 1; i++)
            xVals.add("" + (i + 1));


        PieDataSet dataSet = new PieDataSet(yVals1, "Election Results");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        return data;
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
