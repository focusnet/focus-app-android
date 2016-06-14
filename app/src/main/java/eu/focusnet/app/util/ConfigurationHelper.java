package eu.focusnet.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.internal.AppContentInstance;

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

/**
 * A helper class used for retrieving Properties.
 * <p>
 * FIXME might contain methods for accessing getResource(), getAppContext, ...
 */
public class ConfigurationHelper
{
	/**
	 * Name of our properties file in the assets directory.
	 */
	private static final String ASSETS_PROPERTY_FILE = "focus.properties";
	private static final String PROPERTY_DEFAULT_LOCALE = "i18n.default-locale";
	private static final String LOCALE_FALLBACK_LANGUAGE = "en";
	private static final String LOCALE_FALLBACK_COUNTRY = "";
	
	/**
	 * Name of the file for SharedPreferences
	 */
	public static final String SHARED_PREFERENCES_STORE_NAME = "eu.focusnet.app.INTERNAL_CONFIGURATION";
	/**
	 * Key name for Server name as stored in the SharedPreferences.
	 */
	public static final String SHARED_PREFERENCES_LOGIN_SERVER = "login-server";
	/**
	 * Key name for Username for authentication as stored in the SharedPreferences.
	 */
	public static final String SHARED_PREFERENCES_LOGIN_USERNAME = "login-username";
	/**
	 * Key name for Password for authentication as stored in the SharedPreferences.
	 */
	public static final String SHARED_PREFERENCES_LOGIN_PASSWORD = "login-password";
	/**
	 * Key name for URI to user information as stored in the SharedPreferences.
	 */
	public static final String SHARED_PREFERENCES_USER_INFOS = "user-infos";
	/**
	 * Key name for URI to application preferences as stored in the SharedPreferences.
	 */
	public static final String SHARED_PREFERENCES_APPLICATION_SETTINGS = "user-preferences";
	/**
	 * Key name for URI to application content as stored in the SharedPreferences.
	 */
	public static final String SHARED_PREFERENCES_APPLICATION_CONTENT = "application-content";
	/**
	 * Key name for URI to demo use case ID as stored in the SharedPreferences.
	 *
	 * @deprecated Used only in the prototype. Can be safely removed in production version.
	 */
	public static final String SHARED_PREFERENCES_DEMO_USE_CASE = "demo-use-case";
	/**
	 * Key name for Last data synchronization as stored in the SharedPreferences.
	 */
	public static final String SHARED_PREFERENCES_LAST_SYNC = "last-sync";


	/**
	 * Retrieve a property.
	 *
	 * @param key     The key of the property to retrieve
	 * @param context The Context from which we acquire the {@link AssetManager}
	 * @return A String containing the value of the property, or {@code null} on failure.
	 * @throws IOException If the properties file could not be open
	 */
	public static String getProperty(String key, Context context)
	{
		Properties properties = new Properties();
		AssetManager assetManager = context.getAssets();
		try {
			InputStream inputStream = assetManager.open(ASSETS_PROPERTY_FILE);
			properties.load(inputStream);
		}
		catch (IOException ex) {
			throw new FocusInternalErrorException("IO error when accessing property file."); // FIXME throw ex
		}
		return properties.getProperty(key);
	}

	/**
	 * Load the proper language based on the value found in the {@link AppContentInstance}
	 * and modify the current {@link Configuration}. This method must be called when appropriate,
	 * but will impact all the rest of the life of the application. It does not impact the UI
	 * directly but is typically called from an Activity's on Resume() method, or just before
	 * redirecting to another Activity.
	 *
	 * FIXME strange behaviors in emulator. Sometimes does not work. See if any problem on real device.
	 */
	public static void loadLanguage()
	{
		// acquire language from instance
		String language = FocusApplication.getInstance()
				.getDataManager().getAppContentInstance()
				.getLanguage();
		Locale targetLocale = ConfigurationHelper.getDefaultLocale();
		if (ConfigurationHelper.getSupportedLanguages().contains(language)) {
			// ok
			targetLocale = new Locale(language);
		}
		else {
			// perhaps we have a cousin locale (e.g. fr if fr_CH was requested)?
			//
			// FIXME FIXME FIXME
			// that is not optimal. We change the language but also the l10n
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
			else {
				FocusApplication.reportError(
						new FocusNotImplementedException("Non fatal: Attempting to initialize an application content with unsupported language |" + language + "|")
				);
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
	 * <p>
	 * FIXME should we put this elsewhere / e.g. in util.ConfigurationHelper
	 */
	private static List<String> getSupportedLanguages()
	{
		return Arrays.asList(
				"en",
				"fr"
		);
	}

	/**
	 * Reset language to the default language
	 */
	public static void resetLanguage()
	{
		Locale defaultLocale = ConfigurationHelper.getDefaultLocale();
		Locale.setDefault(defaultLocale);
		Configuration config = new Configuration();
		config.locale = defaultLocale;
		FocusApplication.getInstance().getResources().updateConfiguration(
				config,
				FocusApplication.getInstance().getResources().getDisplayMetrics()
		);
	}

	private static Locale getDefaultLocale()
	{
		String defaultLocale = ConfigurationHelper.getProperty(PROPERTY_DEFAULT_LOCALE, FocusApplication.getInstance());
		String[] parts = defaultLocale.split("_");
		String[] ret = new String[]{LOCALE_FALLBACK_LANGUAGE, LOCALE_FALLBACK_COUNTRY};
		for (int i = 0; i < 2; ++i) {
			if (parts.length > i && !parts[i].isEmpty()) {
				ret[i] = parts[i];
			}
		}
		return new Locale(ret[0], ret[1]);
	}

	/**
	 * Save provided preferences into the SharedPreferences store.
	 *
	 * @param pref A key-value pair set to save. All saved values are String.
	 */
	public static void savePreferences(HashMap<String, String> pref)
	{
		SharedPreferences store = FocusApplication.getInstance().getSharedPreferences(SHARED_PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = store.edit();
		for(Map.Entry e : pref.entrySet()) {
			editor.putString((String) e.getKey(), (String) e.getValue());
		}
		editor.apply();
	}

	/**
	 * Get all values for the SharedPreferences store
	 */
	public static HashMap<String, String> getPreferences()
	{
		SharedPreferences store = FocusApplication.getInstance().getSharedPreferences(SHARED_PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);

		HashMap<String, String> ret = new HashMap<>();
		Map<String, ?> prefs = store.getAll();
	 	for(Map.Entry e : prefs.entrySet()) {
			ret.put((String)e.getKey(), (String)e.getValue());
		}
		return ret;
	}

	/**
	 * Delete all values from the SharedPreferences
	 */
	public static void resetPreferences()
	{
		SharedPreferences store = FocusApplication.getInstance().getSharedPreferences(SHARED_PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = store.edit();
		editor.clear();
		editor.apply();
	}

}
