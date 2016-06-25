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

package eu.focusnet.app.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

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

import eu.focusnet.app.FocusAppLogic;
import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.internal.AppContentInstance;

/**
 * A helper class to retrieve application configuration and Android facilities.
 *
 * All these facilities should be accessed through this class, and not via the
 * {@link  FocusAppLogic} singleton.
 */
public class ApplicationHelper
{
	/**
	 * Retrieve a property.
	 *
	 * @param key The key of the property to retrieve
	 * @return A String containing the value of the property, or {@code null} on failure.
	 */
	public static String getProperty(String key)
	{
		Properties properties = new Properties();
		AssetManager assetManager = getAssets();
		try {
			InputStream inputStream = assetManager.open(Constant.AppConfig.ASSETS_PROPERTY_FILE);
			properties.load(inputStream);
		}
		catch (IOException ex) {
			throw new FocusInternalErrorException("IO error when accessing property file.");
		}
		return properties.getProperty(key);
	}

	/**
	 * Load the proper language based on the value found in the {@link AppContentInstance}
	 * and modify the current {@link Configuration}. This method must be called when appropriate,
	 * but will impact all the rest of the life of the application. It does not impact the UI
	 * directly but is typically called from an Activity's on Resume() method, or just before
	 * redirecting to another Activity.
	 */
	public static void changeLanguage(String language)
	{
		Locale targetLocale = ApplicationHelper.getDefaultLocale();
		if (ApplicationHelper.getSupportedLanguages().contains(language)) {
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
		getApplicationContext().getResources().updateConfiguration(
				config,
				getApplicationContext().getResources().getDisplayMetrics()
		);
	}

	/**
	 * Get the list of languages for which we have a translation. This is hard-coded.
	 *
	 * @return An array consisting of supported languages. The languages can be also a localized
	 * versions, e.g. fr_CH instead of fr.
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
		Locale defaultLocale = ApplicationHelper.getDefaultLocale();
		Locale.setDefault(defaultLocale);
		Configuration config = new Configuration();
		config.locale = defaultLocale;
		getApplicationContext().getResources().updateConfiguration(
				config,
				getApplicationContext().getResources().getDisplayMetrics()
		);
	}

	/**
	 * Retrieve the default locale to use as a fallback.
	 *
	 * @return The {@code Locale} to use.
	 */
	private static Locale getDefaultLocale()
	{
		String defaultLocale = ApplicationHelper.getProperty(Constant.AppConfig.PROPERTY_DEFAULT_LOCALE);
		String[] parts = defaultLocale.split("_");
		String[] ret = new String[]{
				Constant.AppConfig.LOCALE_FALLBACK_LANGUAGE,
				Constant.AppConfig.LOCALE_FALLBACK_COUNTRY
		};
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
	 * @param preferences A key-value pair set to save. All saved values are String.
	 */
	public static void savePreferences(HashMap<String, String> preferences)
	{
		SharedPreferences store = getApplicationContext().getSharedPreferences(Constant.SharedPreferences.SHARED_PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = store.edit();
		for (Map.Entry e : preferences.entrySet()) {
			editor.putString((String) e.getKey(), (String) e.getValue());
		}
		editor.apply();
	}

	/**
	 * Get all values for the SharedPreferences store
	 *
	 * @return The complete set of preferences.
	 */
	public static HashMap<String, String> getPreferences()
	{
		SharedPreferences store = getApplicationContext().getSharedPreferences(Constant.SharedPreferences.SHARED_PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);

		HashMap<String, String> ret = new HashMap<>();
		Map<String, ?> prefs = store.getAll();
		for (Map.Entry e : prefs.entrySet()) {
			ret.put((String) e.getKey(), (String) e.getValue());
		}
		return ret;
	}

	/**
	 * Delete all values from the SharedPreferences
	 */
	public static void resetPreferences()
	{
		SharedPreferences store = getApplicationContext().getSharedPreferences(Constant.SharedPreferences.SHARED_PREFERENCES_STORE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = store.edit();
		editor.clear();
		editor.apply();
	}

	/**
	 * Get the application-wide Android context.
	 *
	 * @return The Android ApplicationContext.
	 */
	public static Context getApplicationContext()
	{
		return FocusAppLogic.getApplicationContext();
	}

	/**
	 * Get application assets.
	 *
	 * @return The {@code AssetManager} to use to browse assets.
	 */
	public static AssetManager getAssets()
	{
		return getApplicationContext().getAssets();
	}

	/**
	 * Get the Android ID of this device.
	 *
	 * @return The Android ID.
	 */
	public static String getAndroidId()
	{
		return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
	}

	/**
	 * Get the Resources of the application.
	 *
	 * @return The object used to access resources.
	 */
	public static Resources getResources()
	{
		return getApplicationContext().getResources();
	}

	/**
	 * Get the package manager of the system.
	 *
	 * @return The {@code PackageManager} to use to retrieve information.
	 */
	public static PackageManager getPackageManager()
	{
		return getApplicationContext().getPackageManager();
	}

	/**
	 * Check a specific permissions.
	 *
	 * @param what The name of the permission.
	 * @return {@code true} if the permission is granted, {@code false} otherwise.
	 */
	public static boolean checkPermission(String what)
	{
		return ActivityCompat.checkSelfPermission(getApplicationContext(), what) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * Get a system service.
	 *
	 * @param name The name of the service to acquire.
	 * @return The service of interest
	 */
	public static Object getSystemService(String name)
	{
		return getApplicationContext().getSystemService(name);
	}
}
