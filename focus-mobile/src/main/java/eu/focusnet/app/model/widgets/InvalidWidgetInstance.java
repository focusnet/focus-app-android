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

/**
 * An instance containing all information pertaining to an invalid widget. This type of widget
 * replaces widgets that are found to be invalid, and it keeps a reference of the originally failing
 * widget.
 */
public class InvalidWidgetInstance extends WidgetInstance
{

	/**
	 * Reference to the widget instance that originally failed to laod.
	 */
	private WidgetInstance originalWidgetInstance;

	/**
	 * Constructor.
	 *
	 * @param layoutConfig Inherited
	 * @param w Inherited
	 */
	public InvalidWidgetInstance(Map<String, String> layoutConfig, WidgetInstance w)
	{
		super(layoutConfig, w.getDataManager());
		this.originalWidgetInstance = w;

		// set the guid, for keeping track of paths
		this.setGuid(w.getGuid());
	}
}
