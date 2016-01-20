package eu.focusnet.app.fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;
import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;
import eu.focusnet.app.manager.DataManager;
import eu.focusnet.app.model.internal.PieChartWidgetInstance;
import eu.focusnet.app.model.ui.ChartData;
import eu.focusnet.app.util.DataFactory;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class PieChartWidgetFragment extends WidgetFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_piechart, container, false);

        setWidgetLayout(viewRoot);

        PieChartWidgetInstance w = (PieChartWidgetInstance) getWidgetInstance();

        TextView piechartTitle = (TextView) viewRoot.findViewById(R.id.text_piechart_title);
        piechartTitle.setText(w.getTitle());

        ArrayList<Entry> yVals = new ArrayList<>();

        for(int i = 0; i < w.getValues().size(); i++) {
            float d = w.getValues().get(i).floatValue();
            yVals.add(new Entry(d, i));
            // FIXME?	colors.add(chartData.getColor());
        }


        PieDataSet dataSet = new PieDataSet(yVals, w.getCaption());
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        // FIXME?	dataSet.setColors(colors);

        PieData data = new PieData(w.getLabels(), dataSet); // names, values=new PieDataSet(values, legend)
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);


        PieChart pieChart = (PieChart) viewRoot.findViewById(R.id.pie_chart);

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
        pieChart.setCenterText("This is the center text");
        // undo all highlights
        pieChart.highlightValues(null);
        pieChart.invalidate();
        pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);

        pieChart.setData(data);

        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);

        return viewRoot;
    }
}
