package eu.focusnet.app.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.model.internal.AppContentInstance;

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
 * FIXME might contain methods for accessing getResource(), getAppContext, ...
 */
public class ConfigurationHelper
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

	/**
	 * Load the proper language based on the value found in the {@link AppContentInstance}
	 * and modify the current {@link Configuration}. This method must be called when appropriate,
	 * but will impact all the rest of the life of the application.
	 */
	public static void loadLanguage()
	{
		// acquire language from instance
		String language = FocusApplication.getInstance()
				.getDataManager().getAppContentInstance()
				.getLanguage();
		Locale targetLocale = Locale.ENGLISH; // default if none found
		if (ConfigurationHelper.getSupportedLanguages().contains(language)) {
			// ok
			targetLocale = new Locale(language);
		}
		else {
			// perhaps we have a cousin locale (e.g. fr if fr_CH was requested)?
			//
			// FIXME FIXME FIXME
			// that is not optimall. We change the language but also the l10n
			// For Switzerland, for example, we may prefer to keep the fr_CH locale
			// (so that Date/number formatting is like we do in CH), but only change the
			// language to French or German
			// -> would be solved if we could define a parent translation set in our translation
			//    sets, e.g. fr_CH is child of fr, but we still set fr_CH as an app locale, and
			//    therefore we will have good formatting without having to translate everything
			//    again in French.
			//    alternate solution: symlinks on the filesystem. UNIX-only.
			Pattern p = Pattern.compile("^([a-z]{2})_.*");
			Matcher m = p.matcher(language);
			if (m.matches()) {
				targetLocale = new Locale(m.group(1));
			}
		}

		// set the language
		Locale.setDefault(targetLocale);
		Configuration config = new Configuration();
		config.locale = targetLocale;
		FocusApplication.getInstance().getResources().updateConfiguration(
				config,
				FocusApplication.getInstance().getResources().getDisplayMetrics()
		);
	}

	/**
	 * Get the list of languages for which we have a translation.
	 *
	 * @return An array consisting of supported languages.
	 * The languages can be also local versions, e.g. fr_CH instead of fr.
	 *
	 * FIXME should we put this elsewhere / e.g. in util.ConfigurationHelper
	 */
	public static List<String> getSupportedLanguages()
	{
		return Arrays.asList(
				"en",
				"fr"
		);
	}
}
