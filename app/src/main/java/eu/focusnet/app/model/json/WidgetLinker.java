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

package eu.focusnet.app.model.json;

import java.util.Map;

/**
 * Created by admin on 03.08.2015.
 */
public class WidgetLinker
{

	private String widgetid;
	private Map<String, String> layout;

	public WidgetLinker(String widgetid, Map<String, String> layout)
	{
		this.widgetid = widgetid;
		this.layout = layout;
	}

	public WidgetLinker()
	{
	}

	public String getWidgetid()
	{
		return widgetid;
	}

	public void setWidgetid(String widgetid)
	{
		this.widgetid = widgetid;
	}

	public Map<String, String> getLayout()
	{
		return layout;
	}

	public void setLayout(Map<String, String> layout)
	{
		this.layout = layout;
	}
}
