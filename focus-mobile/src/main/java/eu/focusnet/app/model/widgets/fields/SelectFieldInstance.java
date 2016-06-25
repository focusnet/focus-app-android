package eu.focusnet.app.model.widgets.fields;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;

import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.TypesHelper;
import eu.focusnet.app.util.ApplicationHelper;

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
public class SelectFieldInstance extends FieldInstance
{

	final private static String SELECT_LABEL_OPTIONS_TEXTS = "options-texts";
	final private static String SELECT_LABEL_OPTIONS_VALUES = "options-values";

	private ArrayList<String> values;
	private ArrayList<String> texts;


	public SelectFieldInstance(String fieldName, LinkedTreeMap<String, Object> config, DataContext dataContext)
	{
		super(fieldName, config, dataContext);
	}

	@Override
	protected void processSpecificConfig()
	{

		// first handle values of SELECT
		Object rawValues = this.config.get(SELECT_LABEL_OPTIONS_VALUES);
		if (rawValues == null) {
			this.markAsInvalid();
			return;
		}
		try {
			this.values = TypesHelper.asArrayOfStrings(rawValues);
		}
		catch (FocusBadTypeException ex) {
			throw new FocusInternalErrorException("Invalid values in select");
		}

		// resolve
		this.texts = new ArrayList<>();
		for (int i = 0; i < this.values.size(); ++i) {
			String newVal;
			String rawVal = this.values.get(i);
			if (rawVal == null) {
				this.markAsInvalid();
				return;
			}
			try {
				newVal = TypesHelper.asString(this.dataContext.resolve(rawVal));
				this.values.set(i, newVal);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}

			// let's add a default user-friendly text
			this.texts.add(newVal);
		}

		// check if default value is in array of values
		if (!this.getDefaultValue().equals("")) {
			if (!this.values.contains(this.getDefaultValue())) {
				this.markAsInvalid();
				return;
			}
		}

		// Then user-friendly texts
		ArrayList<String> tmpTexts;
		Object rawTexts = this.config.get(SELECT_LABEL_OPTIONS_TEXTS);
		if (rawTexts == null) {
			this.markAsInvalid();
			return;
		}
		try {
			tmpTexts = TypesHelper.asArrayOfStrings(rawTexts);
		}
		catch (FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		if (tmpTexts.size() > this.values.size()) {
			this.markAsInvalid("Too many values in texts array");
			return;
		}

		for (int i = 0; i < this.texts.size(); ++i) {
			String rawText = tmpTexts.get(i);
			try {
				String v = TypesHelper.asString(this.dataContext.resolve(rawText));
				this.texts.set(i, v);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				// ignore, keep the current text (=value)
			}
		}

		if (!this.isMandatory()) {
			this.texts.add(0, ApplicationHelper.getResources().getString(R.string.select_no_selection));
			this.values.add(0, "");
		}


	}

	public ArrayList<String> getValues()
	{
		return this.values;
	}

	public ArrayList<String> getTexts()
	{
		return this.texts;
	}

}
