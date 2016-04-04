package eu.focusnet.app.model.internal.fields;

import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.MissingResourceException;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

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

	private final String SELECT_LABEL_OPTIONS_TEXTS = "options-texts";
	private final String SELECT_LABEL_OPTIONS_VALUES = "options-values";

	private ArrayList<String> values;
	private ArrayList<String> texts;


	public SelectFieldInstance(String field_name, LinkedTreeMap<String, Object> config, DataContext dataContext)
	{
		super(field_name, config, dataContext);
	}

	@Override
	protected void processSpecificConfig()
	{

		// first handle values of SELECT
		try {
			this.values = TypesHelper.asArrayOfStrings(this.config.get(SELECT_LABEL_OPTIONS_VALUES));
		}
		catch (FocusBadTypeException ex) {
			throw new FocusInternalErrorException("Invalid values in select");
		}

		// resolve
		this.texts = new ArrayList<>();
		for (int i = 0; i < this.values.size(); ++i) {
			String new_val;
			try {
				new_val = TypesHelper.asString(this.dataContext.resolve(this.values.get(i)));
				this.values.set(i, new_val);
			}
			catch (FocusMissingResourceException ex) {
				throw new FocusInternalErrorException("Invalid value resolution in select");
			}
			catch (FocusBadTypeException ex) {
				throw new FocusInternalErrorException("Invalid value type in select");
			}

			// let's add a default user-friendly text
			this.texts.add(new_val);
		}

		// check if default value is in array of values
		if (!this.getDefaultValue().equals("")) {
			if (!this.values.contains(this.getDefaultValue())) {
				throw new FocusInternalErrorException("Default value is not valid");
			}
		}

		// Then user-friendly texts
		ArrayList<String> tmp_texts;
		try {
			tmp_texts = TypesHelper.asArrayOfStrings(this.config.get(SELECT_LABEL_OPTIONS_TEXTS));
		}
		catch (FocusBadTypeException ex) {
			throw new FocusInternalErrorException("Invalid values in select");
		}

		if (tmp_texts.size() > this.values.size()) {
			throw new FocusInternalErrorException("Too many values in texts array");
		}

		for (int i = 0; i < this.texts.size(); ++i) {
			try {
				String v = TypesHelper.asString(this.dataContext.resolve(tmp_texts.get(i)));
				this.texts.set(i, v);
			}
			catch (FocusMissingResourceException ex) {
				// ignore, keep the current text (=value)
			}
			catch(FocusBadTypeException ex) {
				// ignore, keep the current text (=value)
			}
		}

		if (!this.isMandatory()) {
			this.texts.add(0, "-- Select --");
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
