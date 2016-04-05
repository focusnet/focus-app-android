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

package eu.focusnet.app.model.internal.widgets;

import com.google.gson.internal.LinkedTreeMap;

import java.util.LinkedHashMap;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.internal.fields.CheckboxFieldInstance;
import eu.focusnet.app.model.internal.fields.FieldInstance;
import eu.focusnet.app.model.internal.fields.SelectFieldInstance;
import eu.focusnet.app.model.internal.fields.TextareaFieldInstance;
import eu.focusnet.app.model.internal.fields.TextfieldFieldInstance;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by admin on 28.01.2016.
 */
//TODO implements this class and its methods
public class FormWidgetInstance extends DataCollectionWidgetInstance
{

	private LinkedHashMap<String, FieldInstance> fields;

	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public FormWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}


	@Override
	protected void processSpecificConfig()
	{
		this.fields = new LinkedHashMap<>();
		for (Map.Entry e : this.config.entrySet()) {
			String field_name = (String) e.getKey();
			LinkedTreeMap<String, Object> field_config = (LinkedTreeMap<String, Object>) e.getValue();
			FieldInstance new_field = null;
			String type;
			try {
				type = TypesHelper.asString(field_config.get(FieldInstance.FIELD_LABEL_TYPE));
			}
			catch (FocusBadTypeException ex) {
				// invalid type
				throw new FocusInternalErrorException("Invalid type of field #1");
			}
			switch (type) {
				case FieldInstance.FIELD_TYPE_TEXTFIELD:
					new_field = new TextfieldFieldInstance(field_name, field_config, this.dataContext);
					break;
				case FieldInstance.FIELD_TYPE_TEXTAREA:
					new_field = new TextareaFieldInstance(field_name, field_config, this.dataContext);
					break;
				case FieldInstance.FIELD_TYPE_CHECKBOX:
					new_field = new CheckboxFieldInstance(field_name, field_config, this.dataContext);
					break;
				case FieldInstance.FIELD_TYPE_SELECT:
					new_field = new SelectFieldInstance(field_name, field_config, this.dataContext);
					break;
				default:
					throw new FocusInternalErrorException("Invalid type of field #2");
			}

			this.fields.put(field_name, new_field);
		}
	}

	public LinkedHashMap<String, FieldInstance> getFields()
	{
		return this.fields;
	}
}
