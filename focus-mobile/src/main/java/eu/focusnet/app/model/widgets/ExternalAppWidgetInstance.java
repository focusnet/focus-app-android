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

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.google.gson.JsonSyntaxException;

import java.util.Map;
import java.util.Random;

import eu.focusnet.app.R;
import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.gson.FocusObject;
import eu.focusnet.app.model.gson.FocusSample;
import eu.focusnet.app.model.gson.WidgetTemplate;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusInternalErrorException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * An instance containing all information pertaining to a form widget.
 */
public class ExternalAppWidgetInstance extends DataCollectionWidgetInstance
{
	/**
	 * Configuration property for application identifier
	 */
	final private static String CONFIG_LABEL_APP_IDENTIFIER = "app-identifier";

	/**
	 * Configuration property for launcher button label
	 */
	final private static String CONFIG_LABEL_BUTTON_LABEL = "launcher-button-label";

	/**
	 * Configuration property for input parameter
	 */
	final private static String CONFIG_LABEL_INPUT_OBJECT = "input-object";

	/**
	 * Button label
	 */
	private String buttonLabel;

	/**
	 * Application identifier
	 */
	private String appIdentifier;

	/**
	 * Object to pass as the external application input. This is a {@link FocusSample}
	 */
	private FocusSample inputObject;

	/**
	 * External application request code
	 */
	private int requestCode;

	/**
	 * External application response
	 */
	private FocusSample response;

	/**
	 * Installed version of the external application
	 */
	private String installedVersion;

	/**
	 * Constructor
	 *
	 * @param wTpl         Inherited
	 * @param layoutConfig Inherited
	 * @param dataCtx      Inherited
	 */
	public ExternalAppWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	/**
	 * Specific configuration:
	 * - button label
	 * - application identifier and check whether the application is installed.
	 * - External application input object
	 * - Set the application request code to something (almost) random.
	 */
	@Override
	protected void processSpecificConfig()
	{
		// button label
		Object rawLabel = this.config.get(CONFIG_LABEL_BUTTON_LABEL);
		if (rawLabel == null) {
			this.buttonLabel = ApplicationHelper.getResources().getString(R.string.focus_external_app_button_label_default);
		}
		else {
			try {
				this.buttonLabel = this.dataContext.resolveToString(rawLabel);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}

			if (this.buttonLabel.equals("")) {
				this.buttonLabel = ApplicationHelper.getResources().getString(R.string.focus_external_app_button_label_default);
			}
		}

		// app identifier
		this.appIdentifier = null;
		Object rawIdentifer = this.config.get(CONFIG_LABEL_APP_IDENTIFIER);
		if (rawIdentifer == null) {
			this.markAsInvalid();
			return;
		}
		else {
			try {
				this.appIdentifier = this.dataContext.resolveToString(rawIdentifer);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
		}

		// update app availability
		this.updateAppAvailability();

		// input object
		String url;
		/* FIXME active me
		Object rawInput = this.config.get(CONFIG_LABEL_INPUT_OBJECT);
		if (rawInput == null) {
			this.inputObject = null;
			url = "";
		}
		else {
			try {
				url = this.dataContext.resolveToString(rawInput);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
		}


		if (!url.equals("")) {
			try {
				this.inputObject = this.dataManager.getSample(url);
			}
			catch (FocusMissingResourceException ex) {
				this.markAsInvalid();
				return;
			}
		}
		*/

		// set requestCode
		// only can use 16-bit for calls to external apps
		// This may not be unique. But risks are low.
		this.requestCode = new Random().nextInt(0xffff);
	}

	/**
	 * Update the app availability instance variable and return the current availability. This
	 * must be called everytime we may use the application, because users may install or
	 * uninstall the external application between application startup and display of the
	 * page containing the external application launcher.
	 *
	 * @return {@code true} if the application is available, {@code false} otherwise.
	 */
	public boolean updateAppAvailability()
	{
		// app identifier
		boolean appAvailable;
		// check that the app is indeed available
		Intent intent = new Intent(this.appIdentifier);
		PackageManager pm = ApplicationHelper.getPackageManager();
		ResolveInfo activityInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (activityInfo == null) {
			appAvailable = false;
			this.installedVersion = null;
		}
		else {
			appAvailable = true;
			PackageInfo packageInfo;
			try {
				packageInfo = pm.getPackageInfo(activityInfo.activityInfo.packageName, 0);
			}
			catch (PackageManager.NameNotFoundException ex) {
				throw new FocusInternalErrorException("Cannot get package description for application, but we just checked that the application exists");
			}
			this.installedVersion = packageInfo.versionName;
		}
		return appAvailable;
	}

	/**
	 * Get button label
	 *
	 * @return The label
	 */
	public String getButtonLabel()
	{
		return this.buttonLabel;
	}

	/**
	 * Get application identifier
	 *
	 * @return The application identifier
	 */
	public String getAppIdentifier()
	{
		return appIdentifier;
	}

	/**
	 * Get the external application input parameter object
	 *
	 * @return The input parameter object
	 */
	public FocusSample getInputObject()
	{
		return inputObject;
	}

	/**
	 * Get the external application request code.
	 *
	 * @return The request code
	 */
	public int getRequestCode()
	{
		return requestCode;
	}

	/**
	 * Save the response as a valid FocusSample
	 *
	 * @param response The response being transmitted by the external application
	 */
	public void saveResponse(String response)
	{
		// The response is a JSON-encoded FocusSample
		if (response == null) {
			this.response = null;
		}
		else {
			try {
				this.response = (FocusSample) FocusObject.factory(response, FocusSample.class);
			}
			catch (JsonSyntaxException ex) {
				// FIXME TODO log error, but do not crash (we depend on third party software)
				// ACRA is good for that, but we should not disclose sensitive information in production environment
				this.response = null;
			}
		}
	}

	/**
	 * Get the installed version of the external application.
	 *
	 * @return The installed version, or a user-friendly "unknown" string.
	 */
	public String getInstalledVersion()
	{
		return (this.installedVersion == null ? ApplicationHelper.getResources().getString(R.string.version_unknown) : this.installedVersion);
	}
}
