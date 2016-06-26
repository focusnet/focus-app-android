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
import eu.focusnet.app.model.TypesHelper;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * Abstract class. An instance containing all information pertaining to a form field.
 * <p/>
 * NOTE: this is NOT a child of AbstractInstance,
 */
abstract public class FieldInstance
{

	/**
	 * Configuration property for the type of field
	 */
	final public static String FIELD_LABEL_TYPE = "type";

	/**
	 * Configuration property for the default value.
	 */
	final protected static String FIELD_LABEL_DEFAULT_VALUE = "default";

	/**
	 * Configuration property for the mandatory flag
	 */
	final private static String FIELD_LABEL_MANDATORY = "mandatory";

	/**
	 * Configuration property for the readonly flag
	 */
	final private static String FIELD_LABEL_READONLY = "readonly";

	/**
	 * Configuration property for the label
	 */
	final private static String FIELD_LABEL_LABEL = "label";

	/**
	 * Instance data context
	 */
	protected DataContext dataContext;

	/**
	 * Instance configuration {@code Map}
	 */
	protected LinkedTreeMap<String, Object> config;

	/**
	 * Tells whether the field is read-only.
	 */
	private boolean isReadOnly;

	/**
	 * The field label
	 */
	private String label;

	/**
	 * The default value
	 */
	private String defaultValue;

	/**
	 * The field name
	 */
	private String fieldName;

	/**
	 * The field type
	 */
	private String type;

	/**
	 * Tells whether the field is mandatory.
	 */
	private boolean isMandatory;

	/**
	 * Tells whether the field is valid
	 */
	private boolean valid;


	/**
	 * Constructor. Initializes settings that are common to all field types and then
	 * request customized configuration.
	 *
	 * @param fieldName   The name of the field
	 * @param config      The configuration {@code Map}
	 * @param dataContext The data context against which we must resolve
	 */
	public FieldInstance(String fieldName, LinkedTreeMap<String, Object> config, DataContext dataContext)
	{
		this.valid = true;
		this.fieldName = fieldName;
		this.config = config;
		this.dataContext = dataContext;

		Object raw_type = this.config.get(FIELD_LABEL_TYPE);
		if (raw_type == null) {
			this.markAsInvalid();
			return;
		}
		try {
			this.type = TypesHelper.asString(raw_type);
		}
		catch (FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		// some common attributes
		this.isMandatory = this.config.get(FIELD_LABEL_MANDATORY) != null && (boolean) this.config.get(FIELD_LABEL_MANDATORY);
		this.isReadOnly = this.config.get(FIELD_LABEL_READONLY) != null && (boolean) this.config.get(FIELD_LABEL_READONLY);

		Object raw_label = this.config.get(FIELD_LABEL_LABEL);
		try {
			this.label = this.dataContext.resolveToString(raw_label);
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		this.defaultValue = "";
		Object raw_default_value = this.config.get(FIELD_LABEL_DEFAULT_VALUE);
		if (raw_default_value == null) {
			this.defaultValue = "";
		}
		else {
			try {
				this.defaultValue = this.dataContext.resolveToString(raw_default_value);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
		}

		// and run specific configuration
		this.processSpecificConfig();
	}


	/**
	 * Type-specific configuration
	 */
	protected void processSpecificConfig()
	{

	}

	/**
	 * Get the type
	 *
	 * @return The type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Tells if the field is mandatory
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isMandatory()
	{
		return isMandatory;
	}

	/**
	 * Get default value
	 *
	 * @return The default value
	 */
	public String getDefaultValue()
	{
		return defaultValue;
	}

	/**
	 * Get the label
	 *
	 * @return The label
	 */
	public String getLabel()
	{
		return label;
	}

	/**
	 * Tells if the field is read-only
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isReadOnly()
	{
		return isReadOnly;
	}

	/**
	 * Tells if the field is valid
	 *
	 * @return {@code true} if this is the case, {@code false} otherwise.
	 */
	public boolean isValid()
	{
		return this.valid;
	}

	/**
	 * Mark this field as invalid
	 */
	protected void markAsInvalid()
	{
		this.valid = false;
	}

	/**
	 * Mark this field as invalid and say why
	 *
	 * @param why The reason for non-validity
	 */
	protected void markAsInvalid(String why)
	{
		this.markAsInvalid();
	}

}
