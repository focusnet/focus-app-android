package eu.focusnet.app.model.internal.widgets;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.google.gson.JsonSyntaxException;

import java.util.Map;
import java.util.Random;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.json.FocusObject;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.util.TypesHelper;

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
public class ExternalAppWidgetInstance extends DataCollectionWidgetInstance
{
	private final static String CONFIG_LABEL_APP_IDENTIFIER = "app-identifier";
	private final static String CONFIG_LABEL_BUTTON_LABEL = "launcher-button-label";
	private final static String CONFIG_LABEL_INPUT_OBJECT = "input-object";

	private String buttonLabel;
	private String appIdentifier;

	/**
	 * Always an URL pointing to a FocusSample
	 */
	private FocusSample inputObject;
	private int requestCode;
	private FocusSample response;
	private String installedVersion;

	public ExternalAppWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	protected void processSpecificConfig()
	{
		// button label
		Object raw_label = this.config.get(CONFIG_LABEL_BUTTON_LABEL);
		if (raw_label == null) {
			this.buttonLabel = FocusApplication.getInstance().getResources().getString(R.string.focus_external_app_button_label_default);
		}
		else {
			try {
				this.buttonLabel = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(raw_label)));
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}

			if (this.buttonLabel.equals("")) {
				this.buttonLabel = FocusApplication.getInstance().getResources().getString(R.string.focus_external_app_button_label_default);
			}
		}

		// app identifier
		this.appIdentifier = null;
		Object raw_identifer = this.config.get(CONFIG_LABEL_APP_IDENTIFIER);
		if (raw_identifer == null) {
			this.markAsInvalid();
			return;
		}
		else {
			try {
				this.appIdentifier = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(raw_identifer)));
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
		Object raw_input = this.config.get(CONFIG_LABEL_INPUT_OBJECT);
		if (raw_input == null) {
			this.inputObject = null;
			url = "";
		}
		else {
			try {
				url = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(raw_input)));
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				this.markAsInvalid();
				return;
			}
		}

		if (!url.equals("")) {
			try {
				this.inputObject = FocusApplication.getInstance().getDataManager().getSample(url);
			}
			catch (FocusMissingResourceException ex) {
				this.markAsInvalid();
				return;
			}
		}

		// set requestCode
		// only can use 16-bit for calls to external apps
		// FIXME this may not be unique. But risks are low, let's accept it for this prototype.
		this.requestCode = new Random().nextInt(0xffff);
	}

	/**
	 * Update the app availability instance variable and return the current availability.
	 */
	public boolean updateAppAvailability()
	{
		// app identifier
		boolean appAvailable;
		// check that the app is indeed available
		Intent intent = new Intent(this.appIdentifier);
		PackageManager pm = FocusApplication.getInstance().getPackageManager();
		ResolveInfo activity_info = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (activity_info == null) {
			appAvailable = false;
			this.installedVersion = null;
		}
		else {
			appAvailable = true;
			PackageInfo pkg_info = null;
			try {
				pkg_info = pm.getPackageInfo(activity_info.activityInfo.packageName, 0);
			}
			catch (PackageManager.NameNotFoundException ex) {
				throw new FocusInternalErrorException("Cannot get package info for application, but we just checked that the application exists");
			}
			this.installedVersion = pkg_info.versionName;
		}
		return appAvailable;
	}

	public String getButtonLabel()
	{
		return this.buttonLabel;
	}

	public String getAppIdentifier()
	{
		return appIdentifier;
	}

	public FocusSample getInputObject()
	{
		return inputObject;
	}

	public int getRequestCode()
	{
		return requestCode;
	}

	/**
	 * Save the response as a valid FocusSample
	 *
	 * @param response
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

	public FocusSample getResponse()
	{
		return this.response;
	}

	public String getInstalledVersion()
	{
		return (this.installedVersion == null ? FocusApplication.getInstance().getResources().getString(R.string.version_unknown) : this.installedVersion);
	}
}
