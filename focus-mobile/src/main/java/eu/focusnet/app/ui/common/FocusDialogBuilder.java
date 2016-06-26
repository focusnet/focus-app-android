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
package eu.focusnet.app.ui.common;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import eu.focusnet.app.R;

/**
 * This class makes it easier to create a custom AlertDialog.
 */
public class FocusDialogBuilder extends AlertDialog.Builder
{
	/**
	 * The root View
	 */
	private View rootView;

	/**
	 * Consttructor
	 *
	 * @param context Android Context
	 */
	public FocusDialogBuilder(Context context)
	{
		super(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		// FIXME Context other than null?
		this.rootView = inflater.inflate(R.layout.dialog_layout, null);
		this.setView(this.rootView);
	}

	/**
	 * Set the title of the dialog
	 *
	 * @param title The dialog
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder setTitle(String title)
	{
		TextView dialogTitle = ((TextView) this.rootView.findViewById(R.id.dialog_title));
		if (dialogTitle != null) {
			dialogTitle.setText(title);
		}
		return this;
	}

	/**
	 * Insert the content of the dialog
	 *
	 * @param content A View containing the content
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder insertContent(View content)
	{
		ViewGroup vg = (ViewGroup) this.rootView.findViewById(R.id.dialog_content);
		if (vg != null) {
			vg.addView(content);
		}
		return this;
	}

	/**
	 * Remove the negative button
	 *
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder removeNegativeButton()
	{
		View b = this.rootView.findViewById(R.id.button_negative);
		if (b != null) {
			((ViewGroup) b.getParent()).removeView(b);
		}
		return this;
	}

	/**
	 * Remove the neutral button
	 *
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder removeNeutralButton()
	{
		View b = this.rootView.findViewById(R.id.button_neutral);
		if (b != null) {
			((ViewGroup) b.getParent()).removeView(b);
		}
		return this;
	}

	/**
	 * Remove the positive button
	 *
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder removePositiveButton()
	{
		View b = this.rootView.findViewById(R.id.button_positive);
		if (b != null) {
			((ViewGroup) b.getParent()).removeView(b);
		}
		return this;
	}

	/**
	 * Set the positive button text
	 *
	 * @param label The text of the button
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder setPositiveButtonText(String label)
	{
		Button b = (Button) this.rootView.findViewById(R.id.button_positive);
		if (b != null) {
			b.setText(label);
		}
		return this;
	}

	/**
	 * Set the neutral button text
	 *
	 * @param label The text of the button
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder setNeutralButtonText(String label)
	{
		Button b = (Button) this.rootView.findViewById(R.id.button_neutral);
		if (b != null) {
			b.setText(label);
		}
		return this;
	}

	/**
	 * Set the negative button text
	 *
	 * @param label The text of the button
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder setNegativeButtonText(String label)
	{
		Button b = (Button) this.rootView.findViewById(R.id.button_negative);
		if (b != null) {
			b.setText(label);
		}
		return this;
	}

	/**
	 * Get the negative butotn's View.
	 *
	 * @return The Button of interest
	 */
	public Button getNegativeButton()
	{
		return (Button) this.rootView.findViewById(R.id.button_negative);
	}

	/**
	 * Get the neutral butotn's View.
	 *
	 * @return The Button of interest
	 */
	public Button getNeutralButton()
	{
		return (Button) this.rootView.findViewById(R.id.button_neutral);
	}

	/**
	 * Get the positive butotn's View.
	 *
	 * @return The Button of interest
	 */
	public Button getPositiveButton()
	{
		return (Button) this.rootView.findViewById(R.id.button_positive);
	}

	/**
	 * Set if the dialog is cancelable or not.
	 *
	 * @param flag {@code true} if the dialog is cancelable, {@code false} otherwise
	 * @return The current object, such that chaining of methods is possible.
	 */
	public FocusDialogBuilder setCancelable(boolean flag)
	{
		super.setCancelable(flag);
		return this;
	}


}
