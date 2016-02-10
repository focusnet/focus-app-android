package eu.focusnet.app.util;

import android.graphics.Color;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

import eu.focusnet.app.model.ui.ChartData;

/**
 * Created by yandypiedra on 23.11.15.
 */
public class DataFactory
{

    /* moved to piechartwidgetinstance
	public static PieData createPieData(ArrayList<ChartData> chartDatas, String description) {

        //number of different participants x-axis
        ArrayList<String> xVals = new ArrayList<>();
        // reached votes by participant y-axis
        ArrayList<Entry> yVals = new ArrayList<>();

        // add colors
        ArrayList<Integer> colors = new ArrayList<>();

        for(int i = 0; i < chartDatas.size(); i++) {
            ChartData chartData = chartDatas.get(i);
            xVals.add(chartData.getName());
            yVals.add(new Entry(chartData.getValue(), i));
            colors.add(chartData.getColor());
        }

        PieDataSet dataSet = new PieDataSet(yVals, description);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(colors);

        PieData data = new PieData(xVals, dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.WHITE);
        return data;
    }
*/

	public static BarData createBarData(ArrayList<ChartData> chartDatas, String description)
	{

		////x-axis
		ArrayList<String> xVals = new ArrayList<>();
		// y-axis values
		ArrayList<BarEntry> yVals = new ArrayList<>();

		// add colors
		ArrayList<Integer> colors = new ArrayList<Integer>();

		for (int i = 0; i < chartDatas.size(); i++) {
			ChartData chartData = chartDatas.get(i);
			xVals.add(chartData.getName());
			yVals.add(new BarEntry(chartData.getValue(), i));
			colors.add(chartData.getColor());
		}

		BarDataSet set1 = new BarDataSet(yVals, description);
		set1.setBarSpacePercent(35f);
		set1.setColors(colors);

		ArrayList<BarDataSet> dataSets = new ArrayList<>();
		dataSets.add(set1);


		BarData data = new BarData(xVals, dataSets);
		// data.setValueFormatter(new MyValueFormatter());
		data.setValueTextSize(10f);
		//data.setValueTypeface(mTf);
		return data;
	}


	public static LineData createLineData(String description, ArrayList<ChartData> chartDatas)
	{

		//x-axis
		ArrayList<String> xVals = new ArrayList<>();
		// y-axis values
		ArrayList<Entry> yVals = new ArrayList<>();

		for (int i = 0; i < chartDatas.size(); i++) {
			ChartData chartData = chartDatas.get(i);
			xVals.add(chartData.getName());
			yVals.add(new BarEntry(chartData.getValue(), i));
		}

		// create a dataset and give it a type
		LineDataSet set1 = new LineDataSet(yVals, description);
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

		ArrayList<LineDataSet> dataSets = new ArrayList<>();
		dataSets.add(set1); // add the datasets

		// create a data object with the datasets
		LineData data = new LineData(xVals, dataSets);
		return data;
	}

}
