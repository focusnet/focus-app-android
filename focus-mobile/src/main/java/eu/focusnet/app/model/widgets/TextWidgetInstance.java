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

import java.util.Map;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.gson.WidgetTemplate;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * Instantiation of a widget containing a simple text field
 */
public class TextWidgetInstance extends WidgetInstance
{

	/**
	 * Label of the configuration parameter containing the text to displa.
	 */
	final private static String CONFIG_LABEL_CONTENT = "content";

	/**
	 * Content to render
	 */
	private String content;

	/**
	 * Constructor
	 *
	 * @param template     Inherited
	 * @param layoutConfig Inherited
	 * @param newCtx       Inherited
	 */
	public TextWidgetInstance(WidgetTemplate template, Map<String, String> layoutConfig, DataContext newCtx)
	{
		super(template, layoutConfig, newCtx);
	}

	/**
	 * A TextWidgetInstance defines:
	 * - title (String)
	 * - content (String); may contain HTML markup.
	 */
	@Override
	protected void processSpecificConfig()
	{
		try {
			this.content = this.dataContext.resolveToString(this.config.get(CONFIG_LABEL_CONTENT));
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			this.markAsInvalid();
		}
	}

	/**
	 * Get the content
	 *
	 * @return The content
	 */
	public String getContent()
	{
		return this.content;
	}
}
