/*
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
 */

package eu.focusnet.app.ui.fragment.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import eu.focusnet.app.R;
import eu.focusnet.app.model.widgets.FormWidgetInstance;
import eu.focusnet.app.model.widgets.fields.CheckboxFieldInstance;
import eu.focusnet.app.model.widgets.fields.FieldInstance;
import eu.focusnet.app.model.widgets.fields.SelectFieldInstance;
import eu.focusnet.app.model.widgets.fields.TextareaFieldInstance;
import eu.focusnet.app.model.widgets.fields.TextfieldFieldInstance;
import eu.focusnet.app.ui.common.UiHelper;

/**
 * {@code Fragment} rendering a form widget, with different field types.
 */
public class FormWidgetFragment extends WidgetFragment
{
	/**
	 * Maximum number of lines in textareas
	 */
	private static final int TEXTAREA_MAX_NUM_OF_LINES = 10;

	/**
	 * Default padding at the bottom of a control (in dp)
	 */
	private static final int UI_DEFAULT_PADDING_BOTTOM = 16;

	/**
	 * Android context
	 */
	private Context context;

	/**
	 * Default padding in pixels
	 */
	private int defaultPaddingInPixels;


	/**
	 * Add a field to the UI, based on the input parameters
	 *
	 * @param f  The {@link FieldInstance} of interest
	 * @param tl The {@code TableLayout} where to include the new field
	 */
	private void addFieldToUI(FieldInstance f, TableLayout tl)
	{
		if (f instanceof TextfieldFieldInstance) {
			this.addTextfieldToUI(f, tl);
		}
		else if (f instanceof TextareaFieldInstance) {
			this.addTextareaToUI(f, tl);
		}
		else if (f instanceof CheckboxFieldInstance) {
			this.addCheckboxToUI(f, tl);
		}
		else if (f instanceof SelectFieldInstance) {
			this.addSelectToUI(f, tl);
		}
	}

	/**
	 * Include a new Select list to the UI
	 *
	 * @param f  The {@link FieldInstance} of interest
	 * @param tl The {@code TableLayout} where to include the new field
	 */
	private void addSelectToUI(FieldInstance f, TableLayout tl)
	{
		TableRow tr = new TableRow(this.context);
		tr.setPadding(0, 0, 0, this.defaultPaddingInPixels);

		boolean noLabel = false;
		String labelTxt = f.getLabel();
		if (!labelTxt.equals("")) {
			TextView label = new TextView(this.context);
			label.setText(labelTxt);
			label.setGravity(Gravity.TOP);

			TableRow.LayoutParams layoutLabel = new TableRow.LayoutParams();
			layoutLabel.rightMargin = UiHelper.dpToPixels(10, this.context);
			label.setLayoutParams(layoutLabel);

			tr.addView(label);
		}
		else {
			noLabel = true;
		}

		Spinner spinner = new Spinner(this.context);
		List<String> list = ((SelectFieldInstance) f).getTexts();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.context, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		// select the appropriate element
		spinner.setSelection(((SelectFieldInstance) f).getValues().indexOf(f.getDefaultValue()));

		if (noLabel) {
			TableRow.LayoutParams layoutSpinner = new TableRow.LayoutParams();
			layoutSpinner.column = 1;
			spinner.setLayoutParams(layoutSpinner);
		}

		if (f.isReadOnly()) {
			spinner.setEnabled(false);
		}

		tr.addView(spinner);
		tl.addView(tr);
	}

	/**
	 * Insert a new checkbox into the UI
	 *
	 * @param f  The {@link FieldInstance} of interest
	 * @param tl The {@code TableLayout} where to include the new field
	 */
	private void addCheckboxToUI(FieldInstance f, TableLayout tl)
	{
		TableRow tr = new TableRow(this.context);
		tr.setPadding(0, 0, 0, this.defaultPaddingInPixels);

		boolean noLabel = false;
		String labelTxt = f.getLabel();
		if (!labelTxt.equals("")) {
			TextView label = new TextView(this.context);
			label.setText(labelTxt);
			label.setGravity(Gravity.TOP);

			TableRow.LayoutParams layoutLabel = new TableRow.LayoutParams();
			layoutLabel.rightMargin = UiHelper.dpToPixels(10, this.context);
			label.setLayoutParams(layoutLabel);

			tr.addView(label);
		}
		else {
			noLabel = true;
		}

		CheckBox checkBox = new CheckBox(this.context);
		checkBox.setText(((CheckboxFieldInstance) f).getCheckboxLabel());
		checkBox.setGravity(Gravity.TOP);

		if (((CheckboxFieldInstance) f).isDefaultChecked()) {
			checkBox.setChecked(true);
		}

		if (f.isReadOnly()) {
			checkBox.setEnabled(false);
		}

		if (noLabel) {
			TableRow.LayoutParams layoutCheckbox = new TableRow.LayoutParams();
			layoutCheckbox.column = 1;
			checkBox.setLayoutParams(layoutCheckbox);
		}

		tr.addView(checkBox);
		tl.addView(tr);
	}

	/**
	 * Insert a textarea in the UI
	 *
	 * @param f  The {@link FieldInstance} of interest
	 * @param tl The {@code TableLayout} where to include the new field
	 */
	private void addTextareaToUI(FieldInstance f, TableLayout tl)
	{
		TableRow.LayoutParams layout = new TableRow.LayoutParams();
		layout.span = 2;

		TableRow tr = new TableRow(this.context);
		tr.setPadding(0, 0, 0, this.defaultPaddingInPixels / 2);
		TableRow tr2 = new TableRow(this.context);
		tr2.setPadding(0, 0, 0, this.defaultPaddingInPixels);

		TextView label = new TextView(this.context);
		label.setText(f.getLabel());
		label.setGravity(Gravity.TOP);
		label.setLayoutParams(layout);

		EditText textfield = new EditText(this.context);
		textfield.setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE | EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT);
		textfield.setText(f.getDefaultValue());
		textfield.setLines(TEXTAREA_MAX_NUM_OF_LINES);
		textfield.setMaxLines(TEXTAREA_MAX_NUM_OF_LINES);
		textfield.setGravity(Gravity.TOP);
		textfield.setLayoutParams(layout);
		if (f.isReadOnly()) {
			textfield.setEnabled(false);
		}

		tr.addView(label);
		tr2.addView(textfield);

		tl.addView(tr);
		tl.addView(tr2);
	}

	/**
	 * Insert a textfield in the UI
	 *
	 * @param f  The {@link FieldInstance} of interest
	 * @param tl The {@code TableLayout} where to include the new field
	 */
	private void addTextfieldToUI(FieldInstance f, TableLayout tl)
	{
		TableRow tr = new TableRow(this.context);
		tr.setPadding(0, 0, 0, this.defaultPaddingInPixels);

		TextView label = new TextView(this.context);
		label.setText(f.getLabel());
		label.setGravity(Gravity.TOP);
		TableRow.LayoutParams layout = new TableRow.LayoutParams();
		layout.rightMargin = UiHelper.dpToPixels(10, this.context);
		label.setLayoutParams(layout);

		EditText textfield = new EditText(this.context);
		String value = f.getDefaultValue();


		// type of input
		TextfieldFieldInstance.ValidType type = ((TextfieldFieldInstance) f).getInputType();
		int editorType;
		switch (type) {
			case TEXTFIELD_TYPE_DECIMAL:
				editorType = EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL | EditorInfo.TYPE_NUMBER_FLAG_SIGNED;
				// numbers rounding
				if (value == null || value.equals("")) {
					value = "0";
				}
				int decimals = ((TextfieldFieldInstance) f).getDecimalsNumber();
				double factor = Math.pow(10, decimals);
				double newVal = ((int) (Double.parseDouble(value) * factor)) / factor;
				if (decimals == 0) {
					value = Integer.toString((int) newVal);
				}
				else {
					value = Double.toString(newVal);
				}
				break;
			case TEXTFIELD_TYPE_EMAIL:
				editorType = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;
				break;
			case TEXTFIELD_TYPE_NUMBER:
				editorType = EditorInfo.TYPE_CLASS_NUMBER | EditorInfo.TYPE_NUMBER_FLAG_SIGNED;
				break;
			case TEXTFIELD_TYPE_PHONE:
				editorType = EditorInfo.TYPE_CLASS_PHONE;
				break;
			case TEXTFIELD_TYPE_TEXT:
			default:
				editorType = EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_FLAG_AUTO_CORRECT;
				break;
		}
		textfield.setInputType(editorType);
		textfield.setMaxLines(1);
		textfield.setText(value);
		textfield.setGravity(Gravity.TOP);

		if (f.isReadOnly()) {
			textfield.setEnabled(false);
		}

		tr.addView(label);
		tr.addView(textfield);

		tl.addView(tr);
	}


	/**
	 * Create the View, i.e. iterate over the fields of the {@link FormWidgetInstance} and add
	 * each of them to the UI
	 *
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_form, container, false));

		this.context = this.rootView.getContext();
		this.defaultPaddingInPixels = UiHelper.dpToPixels(UI_DEFAULT_PADDING_BOTTOM, this.context);

		TableLayout tl = (TableLayout) this.rootView.findViewById(R.id.formTableLayout);
		for (Map.Entry e : ((FormWidgetInstance) this.widgetInstance).getFields().entrySet()) {
			FieldInstance f = (FieldInstance) e.getValue();
			this.addFieldToUI(f, tl);
		}
		return this.rootView;
	}

}
