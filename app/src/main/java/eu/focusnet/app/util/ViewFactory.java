package eu.focusnet.app.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;

import java.util.HashMap;
import java.util.Map;

import de.codecrafters.tableview.TableView;
import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.fragment.BarChartWidgetFragment;
import eu.focusnet.app.fragment.CameraWidgetFragment;
import eu.focusnet.app.fragment.FormWidgetFragment;
import eu.focusnet.app.fragment.GPSWidgetFragment;
import eu.focusnet.app.fragment.LineChartWidgetFragment;
import eu.focusnet.app.fragment.PieChartWidgetFragment;
import eu.focusnet.app.fragment.TableWidgetFragment;
import eu.focusnet.app.fragment.TextWidgetFragment;
import eu.focusnet.app.model.ui.ChartData;

import static eu.focusnet.app.R.drawable.custom_progress_dialog_animation;

/**
 * Factory for creating different android views
 */
public class ViewFactory {

    /**
     *
     * @param context
     * @param orientation
     * @param layoutParams
     * @return
     */
    public static LinearLayout createLinearLayout(Context context, int orientation, LinearLayout.LayoutParams layoutParams){
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(orientation);
        layout.setLayoutParams(layoutParams);
        return layout;
    }

//    public static View createViewWithLinearLayout(Context context, LinearLayout.LayoutParams layoutParams){
//        View view = new View(context);
//        view.setLayoutParams(layoutParams);
//        return view;
//    }

    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static ImageView createImageView(Context context, LinearLayout.LayoutParams layoutParams){
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static EditText createEditText(Context context, LinearLayout.LayoutParams layoutParams){
        EditText editText = new EditText(context);
        editText.setLayoutParams(layoutParams);
        return editText;
    }


    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static TableLayout createTableLayout(Context context, LinearLayout.LayoutParams layoutParams){
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(layoutParams);
        return  tableLayout;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static TableRow createTableRow(Context context, LinearLayout.LayoutParams layoutParams){
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(layoutParams);
        return tableRow;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static RadioGroup createRadioGroup(Context context, LinearLayout.LayoutParams layoutParams){
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setLayoutParams(layoutParams);
        return radioGroup;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static RadioButton createRadioButton(Context context, LinearLayout.LayoutParams layoutParams){
        RadioButton radioButton = new RadioButton(context);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @param numberOfColumn
     * @return
     */
    public static TableView<?> createTableView(Context context, LinearLayout.LayoutParams layoutParams, int numberOfColumn){
        TableView<?> tableView =  new TableView<>(context);
        tableView.setLayoutParams(layoutParams);
        tableView.setColumnCount(numberOfColumn);
        return tableView;
    }

    /**
     *
     * @param context
     * @param style
     * @param layoutParams
     * @param text
     * @return
     */
    public static TextView createTextView(Context context, int style, LinearLayout.LayoutParams layoutParams, String text){
        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setTextAppearance(context, style);
        textView.setText(text);
        return textView;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @param text
     * @return
     */
    public static Button createButton(Context context, LinearLayout.LayoutParams layoutParams, String text){
        Button button = new Button(context);
        button.setText(text);
        button.setLayoutParams(layoutParams);
        return button;
    }


    public static View createEmptyView(Context context, int width, int height, float weight){
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
        view.setVisibility(View.INVISIBLE);
        return view;
    }

    /**
     *
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog createProgressDialog(Context context, CharSequence title, CharSequence message){
        ProgressDialog progDialog = new ProgressDialog(context);
        progDialog.setTitle(title);
        progDialog.setMessage(message);
        return progDialog;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static CheckBox createCheckBox(Context context, LinearLayout.LayoutParams layoutParams) {
        CheckBox checkBox = new CheckBox(context);
        checkBox.setLayoutParams(layoutParams);
        return checkBox;
    }

    /**
     *
     * @param context
     * @param layoutParams
     * @return
     */
    public static Spinner createSpinner(Context context, LinearLayout.LayoutParams layoutParams) {
        Spinner spinner = new Spinner(context);
        spinner.setLayoutParams(layoutParams);
        return spinner;
    }


    public static PieChart createPieChart(Context context, LinearLayout.LayoutParams layoutParams, String centerText, PieData pieData) {
        PieChart pieChart = new PieChart(context);
        pieChart.setLayoutParams(layoutParams);
        pieChart.setUsePercentValues(true);
        pieChart.setDescription("");
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
        pieChart.setCenterText(centerText);
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);

        pieChart.setData(pieData);

        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);

        // mChart.spin(2000, 0, 360);
        //data.setValueTypeface(tf);

        return pieChart;
    }

    public static BarChart createBarChart(Context context, LinearLayout.LayoutParams layoutParams, float yAxisMinValue, BarData data){

        BarChart barChart = new BarChart(context);
        barChart.setLayoutParams(layoutParams);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setDescription("");

        // if more than 40 entries are displayed in the chart, no values will be
        // drawn
        barChart.setMaxVisibleValueCount(40);

        // scaling can now only be done on x- and y-axis separately
        barChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        barChart.setDrawBarShadow(true);

        // barChart.setDrawXLabels(false);

        barChart.setDrawGridBackground(false);
        // barChart.setDrawYLabels(false);

        // mTf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf")

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxis yAxis = barChart.getAxisLeft();
        //leftAxis.setTypeface(mTf);
        yAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        yAxis.setSpaceTop(15f);

        barChart.animateY(2500);

        yAxis.setStartAtZero(false);
        yAxis.setAxisMinValue(yAxisMinValue);

        barChart.getAxisRight().setEnabled(false);
//        YAxis rightAxis = mChart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//       // rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(8, false);
//        //rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);

        barChart.setData(data);

        Legend legend = barChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend.setForm(Legend.LegendForm.SQUARE);
        legend.setFormSize(9f);
        legend.setTextSize(11f);
        legend.setXEntrySpace(4f);

        return barChart;
    }

    public static LineChart createLineChart(Context context,
                                            LinearLayout.LayoutParams layoutParams,
                                            float yAxisMinValue,
                                            float yAxisMaxValue,
                                            float limitLineLowerYAxisValue, String limitLineLowerYAxisText,
                                            float limitLineUpperYAxisValue, String limitLineUpperYAxisText,
                                            LineData data){

        LineChart lineChart = new LineChart(context);
        lineChart.setLayoutParams(layoutParams);
        lineChart.setDrawGridBackground(false);

        // no description text
        lineChart.setDescription("");
        //lineChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable value highlighting
        lineChart.setHighlightEnabled(true);

        // enable touch gestures
        lineChart.setTouchEnabled(true);

        // enable scaling and dragging
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
//        lineChart.setScaleXEnabled(true);
//         lineChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

//        // create a custom MarkerView (extend MarkerView) and specify the layout
//        // to use for it
//        MyMarkerView mv = new MyMarkerView(this, R.layout.custom_marker_view);
//
//        // set the marker to the chart
//        lineChart.setMarkerView(mv);

        // x-axis limit line
//        LimitLine limitLineXAxis = new LimitLine(3f, "Index 10");
//        limitLineXAxis.setLineWidth(4f);
//        limitLineXAxis.enableDashedLine(10f, 10f, 0f);
//        // llXAxis.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
//        limitLineXAxis.setTextSize(10f);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        //xAxis.setXValueFormatter(new MyCustomXValueFormatter());
        xAxis.setSpaceBetweenLabels(1);
//        xAxis.addLimitLine(limitLineXAxis); // add x-axis limit line

        LimitLine limitLineUpperYAxis = new LimitLine(limitLineUpperYAxisValue, limitLineUpperYAxisText);
        limitLineUpperYAxis.setLineWidth(4f);
        limitLineUpperYAxis.enableDashedLine(10f, 10f, 0f);
        //   ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        limitLineUpperYAxis.setTextSize(10f);

        LimitLine limitLineLowerYAxis = new LimitLine(limitLineLowerYAxisValue, limitLineLowerYAxisText);
        limitLineLowerYAxis.setLineWidth(4f);
        limitLineLowerYAxis.enableDashedLine(10f, 10f, 0f);
        //   ll2.setLabelPosition(LimitLabelPosition.RIGHT_BOTTOM);
        limitLineLowerYAxis.setTextSize(10f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(limitLineUpperYAxis);
        leftAxis.addLimitLine(limitLineLowerYAxis);
        leftAxis.setAxisMaxValue(yAxisMaxValue);
        leftAxis.setYOffset(10);
        leftAxis.setStartAtZero(false);
        leftAxis.setAxisMinValue(yAxisMinValue);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        lineChart.getAxisRight().setEnabled(false);

//        lineChart.getViewPortHandler().setMaximumScaleY(2f);
//        lineChart.getViewPortHandler().setMaximumScaleX(2f);

        lineChart.setData(data);

        lineChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend legend1 = lineChart.getLegend();

        // modify the legend ...
        legend1.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        legend1.setForm(Legend.LegendForm.LINE);

        return lineChart;
    }
}
