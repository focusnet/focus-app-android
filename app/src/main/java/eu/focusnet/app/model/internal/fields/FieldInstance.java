package eu.focusnet.app.model.internal.fields;

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

import com.google.gson.internal.LinkedTreeMap;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * NOTE: this is not a child of AbstractInstance
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

	public FieldInstance(String field_name, LinkedTreeMap<String, Object> config, DataContext dataContext)
	{
		this.fieldName = field_name;
		this.config = config;
		this.dataContext = dataContext;

		try {
			this.type = TypesHelper.asString(this.config.get(FIELD_LABEL_TYPE));
		}
		catch (FocusBadTypeException ex) {
			throw new FocusInternalErrorException("Invalid type of field.");
		}

		// some common attributes
		if (this.config.get(FIELD_LABEL_MANDATORY) != null) {
			this.isMandatory = (boolean) this.config.get(FIELD_LABEL_MANDATORY);
		}
		else {
			this.isMandatory = false;
		}
		if (this.config.get(FIELD_LABEL_READONLY) != null) {
			this.isReadOnly = (boolean) this.config.get(FIELD_LABEL_READONLY);
		}
		else {
			this.isReadOnly = false;
		}

		try {
			this.label = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(FIELD_LABEL_LABEL))));
		}
		catch (FocusMissingResourceException ex) {
			throw new FocusInternalErrorException("Cannot resolve field label");
		}
		catch (FocusBadTypeException ex) {
			throw new FocusInternalErrorException("Cannot cast field label");
		}

		this.defaultValue = "";
		try {
			this.defaultValue = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(FIELD_LABEL_DEFAULT_VALUE))));
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			// fallback to not value.
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

	public String getFieldName()
	{
		return fieldName;
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
}
