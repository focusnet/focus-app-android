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

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by admin on 03.08.2015.
 */
@SuppressWarnings("unused")
public class PageTemplate
{

	private String guid,
			title,
			description;

	private String iterator;

	private LinkedHashMap<String, String> data = new LinkedHashMap<>();

	private ArrayList<WidgetReference> widgets = new ArrayList<>();
	private boolean disabled;

	public String getGuid()
	{
		return guid;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDescription()
	{
		return description;
	}

	public ArrayList<WidgetReference> getWidgets()
	{
		return widgets;
	}

	public String getIterator()
	{
		return iterator;
	}

	public LinkedHashMap<String, String> getData()
	{
		return data;
	}

	public boolean isDisabled()
	{
		return disabled;
	}
}
