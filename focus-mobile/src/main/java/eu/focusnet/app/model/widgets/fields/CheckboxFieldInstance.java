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

package eu.focusnet.app.model.widgets.fields;

import com.google.gson.internal.LinkedTreeMap;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * An instance containing all information pertaining to a checkboxx field.
 */
public class CheckboxFieldInstance extends FieldInstance
{

	/**
	 * Configuration property for the checkbox label
	 */
	final private static String CHECKBOX_LABEL_CHECKBOX_LABEL = "checkbox-label";

	/**
	 * Configuration property for the checkbox value when it is checked
	 */
	final private static String CHECKBOX_LABEL_CHECKED_VALUE = "checked-value";

	/**
	 * Configuration property for the checkbox value when it is unchecked
	 */
	final private static String CHECKBOX_LABEL_UNCHECKED_VALUE = "unchecked-value";

	/**
	 * Default value for when the checkbox is checked
	 */
	final private static String CHECKBOX_CHECKED_DEFAULT_VALUE = "1";

	/**
	 * Default value for when the checkbox is unchecked
	 */
	final private static String CHECKBOX_UNCHECKED_DEFAULT_VALUE = "0";

	/**
	 * Default value when the checkbox is checked
	 */
	final private static String CHECKBOX_DEFAULT_IS_CHECKED = "checked";

	/**
	 * Default value when the checkbox is unchecked
	 */
	final private static String CHECKBOX_DEFAULT_IS_UNCHECKED = "unchecked";

	/**
	 * Tells whether the checkbox is checked by default
	 */
	private boolean defaultChecked;

	/**
	 * Value when checked
	 */
	private String checkedValue;

	/**
	 * Value when unchecked
	 */
	private String uncheckedValue;

	/**
	 * Label of the checkbox
	 */
	private String checkboxLabel;

	/**
	 * Constructor
	 *
	 * @param fieldName   Inherited
	 * @param config      Inherited
	 * @param dataContext Inherited
	 */
	public CheckboxFieldInstance(String fieldName, LinkedTreeMap<String, Object> config, DataContext dataContext)
	{
		super(fieldName, config, dataContext);
	}

	/**
	 * Specific configurations:
	 * - label
	 * - checked and unchecked values
	 * - default value
	 */
	@Override
	protected void processSpecificConfig()
	{
		Object rawLabel = this.config.get(CHECKBOX_LABEL_CHECKBOX_LABEL);
		if (rawLabel == null) {
			this.markAsInvalid();
			return;
		}
		try {
			this.checkboxLabel = this.dataContext.resolveToString(rawLabel);
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		this.checkedValue = CHECKBOX_CHECKED_DEFAULT_VALUE;
		this.uncheckedValue = CHECKBOX_UNCHECKED_DEFAULT_VALUE;

		Object rawChecked = this.config.get(CHECKBOX_LABEL_CHECKED_VALUE);
		Object rawUnchecked = this.config.get(CHECKBOX_LABEL_UNCHECKED_VALUE);
		if (rawChecked == null || rawUnchecked == null) {
			this.markAsInvalid();
			return;
		}
		try {
			this.checkedValue = this.dataContext.resolveToString(rawChecked);
			this.uncheckedValue = this.dataContext.resolveToString(rawUnchecked);
		}
		catch (FocusBadTypeException | FocusMissingResourceException ex) {
			this.markAsInvalid();
			return;
		}

		// should we initially check or uncheck the box?
		String isChecked = this.getDefaultValue();
		switch (isChecked) {
			case CHECKBOX_DEFAULT_IS_CHECKED:
				this.defaultChecked = true;
				break;
			case CHECKBOX_DEFAULT_IS_UNCHECKED:
			case "":
				this.defaultChecked = false;
				break;
			default:
				this.markAsInvalid();
				break;
		}

	}

	/**
	 * Tells whether the checkbox is checked by default.
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isDefaultChecked()
	{
		return defaultChecked;
	}

	/**
	 * Get the checkbox label
	 *
	 * @return The label
	 */
	public String getCheckboxLabel()
	{
		return checkboxLabel;
	}
}
