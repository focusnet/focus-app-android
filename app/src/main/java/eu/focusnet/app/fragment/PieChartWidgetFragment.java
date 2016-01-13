package eu.focusnet.app.fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import java.util.ArrayList;
import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;
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

        //TODO values hard coded
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

        pieChart.setData(pieData);

        Legend legend = pieChart.getLegend();
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);

        return viewRoot;
    }
}
