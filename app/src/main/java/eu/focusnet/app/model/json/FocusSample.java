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

package eu.focusnet.app.model.json;

/**
 * A FocusSample is a FocusObject containing a specialized HashMap (FocusSampleDataMap)
 * <p/>
 * NOTE: more complex deserializers like URL or BigInteger cannot work because this object Object is too generic.
 */
public class FocusSample extends FocusObject
{
	public static final String FOCUS_SAMPLE_TYPE = "http://reference.focusnet.eu/schemas/focus-data-sample/v0.1";

	/**
	 * The Map containing objects of interest
	 */
	FocusSampleDataMap data = null;

	/**
	 * Dummy c'tor
	 * <p/>
	 * GSON documentation advises to always have an empty ctor, so we keep it.
	 */
	public FocusSample()
	{
	}

	/**
	 * C'tor for a new pre-fed FocusSample
	 *
	 * @param url The URL identifying the created FocusSample
	 */
	public FocusSample(String url)
	{
		super(FOCUS_SAMPLE_TYPE, url, null, null, null, 1, null, null, true);
		this.data = new FocusSampleDataMap();
	}

	/**
	 * Add a key-value pair to the data HashMap
	 *
	 * @param s Key
	 * @param o Value
	 */
	public void add(String s, Object o)
	{
		this.data.put(s, o);
	}

	/**
	 * Generic get() method for the content of the data map
	 *
	 * @param identifier the key being accessed in the map
	 * @return the Object of interest
	 */
	public Object get(String identifier)
	{
		return this.data.get(identifier);
	}

	/**
	 * Get the String representation of the object identified by the key in the map
	 *
	 * @param key the key being accessed in the map
	 * @return the String representation of the Object of interest
	 */
	public String getString(String key)
	{
		return this.data.get(key).toString();
	}

}
