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

package eu.focusnet.app.model.widgets;

import com.google.gson.internal.LinkedTreeMap;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.TypesHelper;
import eu.focusnet.app.model.gson.WidgetTemplate;
import eu.focusnet.app.model.widgets.fields.CheckboxFieldInstance;
import eu.focusnet.app.model.widgets.fields.FieldInstance;
import eu.focusnet.app.model.widgets.fields.SelectFieldInstance;
import eu.focusnet.app.model.widgets.fields.TextareaFieldInstance;
import eu.focusnet.app.model.widgets.fields.TextfieldFieldInstance;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusBadTypeException;

/**
 * An instance containing all information pertaining to a form widget.
 */
public class FormWidgetInstance extends DataCollectionWidgetInstance
{

	/**
	 * List of fields
	 */
	private LinkedHashMap<String, FieldInstance> fields;

	/**
	 * Constructor.
	 *
	 * @param wTpl         Inherited
	 * @param layoutConfig Inherited
	 * @param dataCtx      Inherited
	 */
	public FormWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	/**
	 * Specific configuration:
	 * - iterate through fields and create field instances accordingly
	 */
	@Override
	protected void processSpecificConfig()
	{
		this.fields = new LinkedHashMap<>();
		for (Map.Entry e : this.config.entrySet()) {
			String fieldName = (String) e.getKey();
			LinkedTreeMap<String, Object> fieldConfig = (LinkedTreeMap<String, Object>) e.getValue();
			if (fieldConfig == null) {
				this.markAsInvalid();
				return;
			}
			FieldInstance newField;
			String type;
			Object rawType = fieldConfig.get(FieldInstance.FIELD_LABEL_TYPE);
			if (rawType == null) {
				markAsInvalid();
				return;
			}
			try {
				type = TypesHelper.asString(rawType);
			}
			catch (FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
			switch (type) {
				case Constant.DataModelTypes.FIELD_TYPE_TEXTFIELD:
					newField = new TextfieldFieldInstance(fieldName, fieldConfig, this.dataContext);
					break;
				case Constant.DataModelTypes.FIELD_TYPE_TEXTAREA:
					newField = new TextareaFieldInstance(fieldName, fieldConfig, this.dataContext);
					break;
				case Constant.DataModelTypes.FIELD_TYPE_CHECKBOX:
					newField = new CheckboxFieldInstance(fieldName, fieldConfig, this.dataContext);
					break;
				case Constant.DataModelTypes.FIELD_TYPE_SELECT:
					newField = new SelectFieldInstance(fieldName, fieldConfig, this.dataContext);
					break;
				default:
					this.markAsInvalid();
					return;
			}

			this.fields.put(fieldName, newField);
			if (!newField.isValid()) {
				this.markAsInvalid();
			}
		}
	}

	/**
	 * Get fields
	 *
	 * @return The fields instances as a {@code Map}
	 */
	public LinkedHashMap<String, FieldInstance> getFields()
	{
		return this.fields;
	}
}
