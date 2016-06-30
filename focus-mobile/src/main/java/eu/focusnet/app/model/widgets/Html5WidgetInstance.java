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

import android.content.res.AssetManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.TypesHelper;
import eu.focusnet.app.model.gson.WidgetTemplate;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusBadTypeException;
import eu.focusnet.app.util.FocusMissingResourceException;

/**
 * An instance containing all information pertaining to a webapp widget.
 */
public class Html5WidgetInstance extends DataCollectionWidgetInstance
{
	/**
	 * Configuration property for the webapp identifier
	 */
	final private static String CONFIG_WEB_APP_IDENTIFIER_FIELD = "webapp-identifier";

	/**
	 * Configuration property for the context to pass to the webapp
	 */
	final private static String CONFIG_CONTEXT_FIELD = "context";

	/**
	 * Webapp identifier
	 */
	private String webAppIdentifier;

	/**
	 * Context to pass to the webapp. The context is a typically a URL that the webapp will then
	 * process, possibly by calling the facilities of the app via
	 * {@link eu.focusnet.app.ui.fragment.widget.Html5WidgetFragment.WebAppInterface}
	 */
	private String context;

	/**
	 * C'tor
	 *
	 * @param wTpl         Inherited
	 * @param layoutConfig Inherited
	 * @param dataCtx      Inherited
	 */
	public Html5WidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}


	/**
	 * Specific configuration:
	 * - webapp identifier, check that the webapp is actually available in the assets
	 * - context to pass, must be obtained from the DataContext via resolveToString() and hence
	 * may be blocking.
	 */
	@Override
	protected void processSpecificConfig()
	{
		// webapp identifier
		Object rawIdentifier = this.config.get(CONFIG_WEB_APP_IDENTIFIER_FIELD);
		if (rawIdentifier == null) {
			this.markAsInvalid();
			return;
		}
		try {
			this.webAppIdentifier = TypesHelper.asString(rawIdentifier);
		}
		catch (FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		// check that the asset exists
		boolean assetFound = false;
		try {
			AssetManager am = ApplicationHelper.getAssets();
			assetFound = Arrays.asList(
					am.list(
							Constant.AppConfig.ASSETS_HTML5_WEBAPPS_FOLDER + "/" + this.webAppIdentifier
					)
			).contains(Constant.AppConfig.ASSETS_HTML5_WEBAPPS_ENTRY_POINT);
		}
		catch (IOException ignored) {
			// ok, asset_found defaults to false.
		}
		if (!assetFound) {
			this.markAsInvalid();
			return;
		}

		// the context can basically be any string, but is likely to be a unique identifier such as a URI
		Object rawContext = this.config.get(CONFIG_CONTEXT_FIELD);
		if (rawContext == null) {
			this.context = "";
		}
		else {
			try {
				this.context = this.dataContext.resolveToString(rawContext);
			}
			catch (FocusBadTypeException | FocusMissingResourceException ex) {
				this.markAsInvalid();
			}
		}
	}

	/**
	 * Get webapp identifier
	 *
	 * @return The webapp identifier
	 */
	public String getWebAppIdentifier()
	{
		return this.webAppIdentifier;
	}

	/**
	 * Get the context to pass at webapp initialization
	 *
	 * @return The context
	 */
	public String getContext()
	{
		return this.context;
	}
}
