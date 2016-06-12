package eu.focusnet.app.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

/**
 * A helper class used for retrieving Properties.
 *
 * FIXME should not be in UI package
 */
public class PropertiesHelper
{
	/**
	 * Name of our properties file in the assets directory.
	 */
	private static final String ASSETS_PROPERTY_FILE = "focus.properties";

	/**
	 * Retrieve a property.
	 *
	 * @param key The key of the property to retrieve
	 * @param context The Context from which we acquire the {@link AssetManager}
	 * @return A String containing the value of the property, or {@code null} on failure.
	 * @throws IOException If the properties file could not be open
	 */
	public static String getProperty(String key, Context context) throws IOException
	{
		Properties properties = new Properties();
		AssetManager assetManager = context.getAssets();
		InputStream inputStream = assetManager.open(ASSETS_PROPERTY_FILE);
		properties.load(inputStream);
		return properties.getProperty(key);
	}
}
