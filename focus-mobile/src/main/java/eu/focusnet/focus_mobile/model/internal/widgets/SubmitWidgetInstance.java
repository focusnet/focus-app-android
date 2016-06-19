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

package eu.focusnet.focus_mobile.model.internal.widgets;

import java.util.Map;

import eu.focusnet.focus_mobile.exception.FocusBadTypeException;
import eu.focusnet.focus_mobile.exception.FocusMissingResourceException;
import eu.focusnet.focus_mobile.model.internal.DataContext;
import eu.focusnet.focus_mobile.model.json.WidgetTemplate;
import eu.focusnet.focus_mobile.model.util.TypesHelper;

/**
 * Created by admin on 28.01.2016.
 */
public class SubmitWidgetInstance extends DataCollectionWidgetInstance
{

	final private static String CONFIG_LABEL_LABEL = "label";
	final private static String CONFIG_LABEL_DEFAULT_VALUE = "Submit";

	private String submitLabel;

	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public SubmitWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	protected void processSpecificConfig()
	{
		Object label = this.config.get(CONFIG_LABEL_LABEL);
		if (label == null) {
			this.submitLabel = CONFIG_LABEL_DEFAULT_VALUE;
		}
		else {
			try {
				this.submitLabel = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(label)));
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
		}

		// reset button, actions
		// FIXME TODO
	}

	public String getSubmitLabel()
	{
		return this.submitLabel;
	}
}
