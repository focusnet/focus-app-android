package eu.focusnet.app.model.internal.widgets;

import android.content.Intent;
import android.content.pm.PackageManager;

import java.util.Map;
import java.util.Random;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.R;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.util.TypesHelper;

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
public class ExternalAppWidgetInstance extends DataCollectionWidgetInstance
{
	private static String CONFIG_LABEL_APP_IDENTIFIER = "app-identifier";
	private static String CONFIG_LABEL_BUTTON_LABEL = "launcher-button-label";
	private static String CONFIG_LABEL_INPUT_OBJECT = "input-object";

	private String buttonLabel;
	private String appIdentifier;
	private String inputObject;
	private boolean appAvailable;
	private int requestCode;

	public ExternalAppWidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	protected void processSpecificConfig()
	{
		// button label
		try {
			this.buttonLabel = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(CONFIG_LABEL_BUTTON_LABEL))));
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			this.buttonLabel = FocusApplication.getInstance().getResources().getString(R.string.focus_external_app_button_label_default);
		}

		// app identifier
		this.appAvailable = false;
		this.appIdentifier = null;
		try {
			this.appIdentifier = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(CONFIG_LABEL_APP_IDENTIFIER))));
			this.appAvailable = true;
			// check that the app is indeed available
			Intent intent = new Intent(this.appIdentifier);
			this.appAvailable = FocusApplication.getInstance().getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null;
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			// ok, but app is not available and the UI will reflect that.
		}

		// app identifier
		try {
			this.inputObject = TypesHelper.asString(this.dataContext.resolve(TypesHelper.asString(this.config.get(CONFIG_LABEL_INPUT_OBJECT))));
		}
		catch (FocusMissingResourceException | FocusBadTypeException ex) {
			// ok, input object not mandatory.
			this.inputObject = null;
		}

		this.refreshRequestCode();
	}

	public String getButtonLabel()
	{
		return this.buttonLabel;
	}

	public String getAppIdentifier()
	{
		return appIdentifier;
	}

	public String getInputObject()
	{
		return inputObject;
	}

	public boolean isAppAvailable()
	{
		return appAvailable;
	}

	public int getRequestCode()
	{
		return requestCode;
	}

	// set requestCode
	// only can use 16-bit for calls to external apps
	// FIXME this may not be unique. But risks are low, let's accept it for this prototype.
	public int refreshRequestCode()
	{
		this.requestCode = new Random().nextInt(0xffff);
		return this.requestCode;
	}
}
