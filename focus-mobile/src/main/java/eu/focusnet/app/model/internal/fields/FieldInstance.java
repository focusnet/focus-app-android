package eu.focusnet.app.model.internal.fields;

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

import com.google.gson.internal.LinkedTreeMap;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * NOTE: this is not a child of AbstractInstance,
 */
abstract public class FieldInstance
{

	public static final String FIELD_TYPE_TEXTFIELD = "#/definitions/widget/collect/form/fields/textfield";
	public static final String FIELD_TYPE_TEXTAREA = "#/definitions/widget/collect/form/fields/textarea";
	public static final String FIELD_TYPE_CHECKBOX = "#/definitions/widget/collect/form/fields/checkbox";
	public static final String FIELD_TYPE_SELECT = "#/definitions/widget/collect/form/fields/select";
	public static final String FIELD_LABEL_TYPE = "type";
	public static final String FIELD_LABEL_DEFAULT_VALUE = "default";
	private static final String FIELD_LABEL_MANDATORY = "mandatory";
	private static final String FIELD_LABEL_READONLY = "readonly";
	private static final String FIELD_LABEL_LABEL = "label";
	protected DataContext dataContext;
	protected LinkedTreeMap<String, Object> config;
	private boolean isReadOnly;
	private String label;
	private String defaultValue;
	private String fieldName;
	private String type;
	private boolean isMandatory;
	private boolean valid;
	private Object decimalsNumber;

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
			this.label = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(raw_label)));
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
				this.defaultValue = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(raw_default_value)));
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
		}

		// and run specific configuration
		this.processSpecificConfig();
	}


	protected void processSpecificConfig()
	{

	}

	public String getType()
	{
		return type;
	}

	public boolean isMandatory()
	{
		return isMandatory;
	}

	public String getDefaultValue()
	{
		return defaultValue;
	}

	public String getLabel()
	{
		return label;
	}

	public boolean isReadOnly()
	{
		return isReadOnly;
	}

	public boolean isValid()
	{
		return this.valid;
	}

	protected void markAsInvalid()
	{
		this.valid = false;
	}

	protected void markAsInvalid(String why)
	{
		this.markAsInvalid();
	}

}
