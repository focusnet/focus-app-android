package eu.focusnet.app.model.internal.fields;

import com.google.gson.internal.LinkedTreeMap;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
public class CheckboxFieldInstance extends FieldInstance
{

	private final String CHECKBOX_LABEL_CHECKBOX_LABEL = "checkbox-label";
	private final String CHECKBOX_LABEL_CHECKED_VALUE = "checked-value";
	private final String CHECKBOX_LABEL_UNCHECKED_VALUE = "unchecked-value";
	private final String CHECKBOX_CHECKED_DEFAULT_VALUE = "1";
	private final String CHECKBOX_UNCHECKED_DEFAULT_VALUE = "0";
	private final String CHECKBOX_DEFAULT_IS_CHECKED = "checked";
	private final String CHECKBOX_DEFAULT_IS_UNCHECKED = "unchecked";


	private boolean defaultChecked;
	private String checkedValue;
	private String uncheckedValue;
	private String checkboxLabel;

	public CheckboxFieldInstance(String field_name, LinkedTreeMap<String, Object> config, DataContext dataContext)
	{
		super(field_name, config, dataContext);
	}

	@Override
	protected void processSpecificConfig()
	{
		try {
			this.checkboxLabel = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(CHECKBOX_LABEL_CHECKBOX_LABEL))));
		}
		catch (FocusMissingResourceException ex) {
			throw new FocusInternalErrorException("Cannot resolve field checkbox-label");
		}
		catch (FocusBadTypeException ex) {
			throw new FocusInternalErrorException("Cannot cast field checkbox-label");
		}

		this.checkedValue = CHECKBOX_CHECKED_DEFAULT_VALUE;
		this.uncheckedValue = CHECKBOX_UNCHECKED_DEFAULT_VALUE;

		try {
			this.checkedValue = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(CHECKBOX_LABEL_CHECKED_VALUE))));
			this.uncheckedValue = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(CHECKBOX_LABEL_UNCHECKED_VALUE))));
		}
		catch (FocusBadTypeException ex) {
			throw new FocusInternalErrorException("Invalid value of (un)checked box");
		}
		catch (FocusMissingResourceException ex) {
			throw new FocusInternalErrorException("Invalid value of (un)checked box");
		}

		// should we initially check or uncheck the box?
		String isChecked = this.getDefaultValue();
		if (isChecked.equals(CHECKBOX_DEFAULT_IS_CHECKED)) {
			this.defaultChecked = true;
		}
		else if (isChecked.equals(CHECKBOX_DEFAULT_IS_UNCHECKED)
				|| isChecked.equals("")) {
			this.defaultChecked = false;
		}
		else {
			throw new FocusInternalErrorException("Invalid default value for checkbox.");
		}

	}

	public boolean isDefaultChecked()
	{
		return defaultChecked;
	}

	public String getCheckedValue()
	{
		return checkedValue;
	}

	public String getUncheckedValue()
	{
		return uncheckedValue;
	}

	public String getCheckboxLabel()
	{
		return checkboxLabel;
	}
}
