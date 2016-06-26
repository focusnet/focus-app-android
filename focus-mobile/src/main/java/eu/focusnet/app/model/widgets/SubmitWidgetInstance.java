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

package eu.focusnet.app.model.widgets;

import java.util.Map;

import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;
import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.gson.WidgetTemplate;

/**
 * An instance containing all information pertaining to a Submit widget.
 */
public class SubmitWidgetInstance extends DataCollectionWidgetInstance
{

	/**
	 * Configuration property for the button label
	 */
	final private static String CONFIG_LABEL_LABEL = "label";

	/**
	 * Configuration default value for the button label
	 */
	final private static String CONFIG_LABEL_DEFAULT_VALUE = "Submit";

	/**
	 * Label of the button
	 */
	private String submitLabel;

	/**
	 * C'tor
	 *
	 * @param wTpl Inherited
	 * @param layoutConfig Inherited
	 * @param dataCtx Inherited
	 */
	public SubmitWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	/**
	 * Specific configuration:
	 * - a label for the button
	 *
	 * FIXME implement action on submit
	 */
	@Override
	protected void processSpecificConfig()
	{
		Object rawLabel = this.config.get(CONFIG_LABEL_LABEL);
		if (rawLabel == null) {
			this.submitLabel = CONFIG_LABEL_DEFAULT_VALUE;
		}
		else {
			try {
				this.submitLabel = this.dataContext.resolveToString(rawLabel);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
			}
		}
	}

	/**
	 * Get the button label
	 * @return The label
	 */
	public String getSubmitLabel()
	{
		return this.submitLabel;
	}
}
