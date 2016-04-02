/**
 *
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package eu.focusnet.app.ui.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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

import de.codecrafters.tableview.TableView;

/**
 * Factory for creating different android views
 */
public class ViewFactory
{
	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static ImageView createImageView(Context context, LinearLayout.LayoutParams layoutParams)
	{
		ImageView imageView = new ImageView(context);
		imageView.setLayoutParams(layoutParams);
		return imageView;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static EditText createEditText(Context context, LinearLayout.LayoutParams layoutParams)
	{
		EditText editText = new EditText(context);
		editText.setLayoutParams(layoutParams);
		return editText;
	}


	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static TableLayout createTableLayout(Context context, LinearLayout.LayoutParams layoutParams)
	{
		TableLayout tableLayout = new TableLayout(context);
		tableLayout.setLayoutParams(layoutParams);
		return tableLayout;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static TableRow createTableRow(Context context, LinearLayout.LayoutParams layoutParams)
	{
		TableRow tableRow = new TableRow(context);
		tableRow.setLayoutParams(layoutParams);
		return tableRow;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static RadioGroup createRadioGroup(Context context, LinearLayout.LayoutParams layoutParams)
	{
		RadioGroup radioGroup = new RadioGroup(context);
		radioGroup.setLayoutParams(layoutParams);
		return radioGroup;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static RadioButton createRadioButton(Context context, LinearLayout.LayoutParams layoutParams)
	{
		RadioButton radioButton = new RadioButton(context);
		radioButton.setLayoutParams(layoutParams);
		return radioButton;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @param numberOfColumn
	 * @return
	 */
	public static TableView<?> createTableView(Context context, LinearLayout.LayoutParams layoutParams, int numberOfColumn)
	{
		TableView<?> tableView = new TableView<>(context);
		tableView.setLayoutParams(layoutParams);
		tableView.setColumnCount(numberOfColumn);
		return tableView;
	}

	/**
	 * @param context
	 * @param style
	 * @param layoutParams
	 * @param text
	 * @return
	 */
	public static TextView createTextView(Context context, int style, LinearLayout.LayoutParams layoutParams, String text)
	{
		TextView textView = new TextView(context);
		textView.setLayoutParams(layoutParams);
		textView.setTextAppearance(context, style);
		textView.setText(text);
		return textView;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @param text
	 * @return
	 */
	public static Button createButton(Context context, LinearLayout.LayoutParams layoutParams, String text)
	{
		Button button = new Button(context);
		button.setText(text);
		button.setLayoutParams(layoutParams);
		return button;
	}


	public static View createEmptyView(Context context, int width, int height, float weight)
	{
		View view = new View(context);
		view.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
		view.setVisibility(View.INVISIBLE);
		return view;
	}

	/**
	 * @param context
	 * @param title
	 * @param message
	 * @return
	 */
	public static ProgressDialog createProgressDialog(Context context, CharSequence title, CharSequence message)
	{
		ProgressDialog progDialog = new ProgressDialog(context);
		progDialog.setTitle(title);
		progDialog.setMessage(message);
		return progDialog;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static CheckBox createCheckBox(Context context, LinearLayout.LayoutParams layoutParams)
	{
		CheckBox checkBox = new CheckBox(context);
		checkBox.setLayoutParams(layoutParams);
		return checkBox;
	}

	/**
	 * @param context
	 * @param layoutParams
	 * @return
	 */
	public static Spinner createSpinner(Context context, LinearLayout.LayoutParams layoutParams)
	{
		Spinner spinner = new Spinner(context);
		spinner.setLayoutParams(layoutParams);
		return spinner;
	}


}
