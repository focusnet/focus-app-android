/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.ui.fragment.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Map;

import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.fields.CheckboxFieldInstance;
import eu.focusnet.app.model.internal.fields.FieldInstance;
import eu.focusnet.app.model.internal.fields.SelectFieldInstance;
import eu.focusnet.app.model.internal.fields.TextareaFieldInstance;
import eu.focusnet.app.model.internal.fields.TextfieldFieldInstance;
import eu.focusnet.app.model.internal.widgets.FormWidgetInstance;

/**
 * Created by yandypiedra on 14.01.16.
 */
public class FormWidgetFragment extends WidgetFragment
{


	private static TableRow generateFieldUI(FieldInstance f)
	{
		if (f instanceof TextfieldFieldInstance) {
			return FormWidgetFragment.generateTextfieldUI(f);
		}
		else if (f instanceof TextareaFieldInstance) {
			return FormWidgetFragment.generateTextareaUI(f);
		}
		else if (f instanceof CheckboxFieldInstance) {
			return FormWidgetFragment.generateCheckboxUI(f);
		}
		else if (f instanceof SelectFieldInstance) {
			return FormWidgetFragment.generateSelectUI(f);
		}
		return null;
	}

	private static TableRow generateTextfieldUI(FieldInstance f)
	{
		return null;
	}
	private static TableRow generateTextareaUI(FieldInstance f)
	{
		return null;
	}
	private static TableRow generateCheckboxUI(FieldInstance f)
	{
		return null;
	}
	private static TableRow generateSelectUI(FieldInstance f)
	{
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_form, container, false);
		setWidgetLayout(viewRoot);

		this.widgetInstance = (FormWidgetInstance) getWidgetInstance();

		TextView title = (TextView) viewRoot.findViewById(R.id.textTitle);
		title.setText(this.widgetInstance.getTitle());

		TableLayout tl = (TableLayout) viewRoot.findViewById(R.id.formTableLayout);


		TableRow tr = new TableRow(viewRoot.getContext());
		tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
/* Create a Button to be the row-content. */
		Button b = new Button(viewRoot.getContext());
		b.setText("Dynamic Button");
		b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
/* Add Button to row. */
		tr.addView(b);
/* Add row to TableLayout. */
//tr.setBackgroundResource(R.drawable.sf_gradient_03);
		tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));





		for (Map.Entry e : ((FormWidgetInstance) this.widgetInstance).getFields().entrySet()) {
			FieldInstance f = (FieldInstance) e.getValue();
			tr = FormWidgetFragment.generateFieldUI(f);
			tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
		}
		return viewRoot;
	}

}
