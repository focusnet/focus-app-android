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

package eu.focusnet.app.model.gson;

import eu.focusnet.app.util.Constant;

/**
 * A {@code FocusSample} is a {@link FocusObject} containing a specialized {@code HashMap}
 * {@link FocusSampleDataMap}. All external data that we use in our dahsboards and widgets come
 * as instances of {@code FocusSample}s.
 * <p/>
 * Refer to JSON Schema for further documentation.
 * See https://github.com/focusnet/focus-data-mode
 */
public class FocusSample extends FocusObject
{

	/**
	 * The Map containing objects of interest
	 */
	FocusSampleDataMap data = null;

	/**
	 * C'tor for a new empty FocusSample
	 *
	 * @param url The URL identifying the created FocusSample
	 */
	public FocusSample(String url)
	{
		super(Constant.DataModelTypes.FOCUS_DATA_MODEL_TYPE_FOCUS_SAMPLE, url);
		this.data = new FocusSampleDataMap();
	}

	/**
	 * Add a key-value pair to the data {@code Map}.
	 *
	 * @param s Key
	 * @param o Value
	 */
	public void add(String s, Object o)
	{
		// if an added object is null, then do not add it
		if (o == null) {
			return;
		}
		this.data.put(s, o);
	}

	/**
	 * Generic get() method for the content of the data map
	 *
	 * @param key the key being accessed in the map
	 * @return the Object of interest
	 */
	public Object get(String key)
	{
		if (key.equals(Constant.Navigation.LABEL_SELF_REFERENCE_URL)) {
			return this.getUrl();
		}
		else {
			return this.data.get(key);
		}
	}

}
