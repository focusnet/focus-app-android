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

package eu.focusnet.app.model.internal.widgets;

import android.content.res.AssetManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

/**
 */

public class Html5WidgetInstance extends DataCollectionWidgetInstance
{
	private static final String CONFIG_WEB_APP_IDENTIFIER_FIELD = "webapp-identifier";
	private static final String CONFIG_CONTEXT_FIELD = "context";

	private String webAppIdentifier;
	private String context;

	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public Html5WidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		super(wTpl, layoutConfig, dataCtx);
	}

	@Override
	protected void processSpecificConfig()
	{
		// webapp identifier
		Object raw_identifier = this.config.get(CONFIG_WEB_APP_IDENTIFIER_FIELD);
		if (raw_identifier == null) {
			this.markAsInvalid();
			return;
		}
		try {
			this.webAppIdentifier = TypesHelper.asString(raw_identifier);
		}
		catch (FocusBadTypeException ex) {
			this.markAsInvalid();
			return;
		}

		// check that the asset exists
		boolean asset_found = false;
		try {
			AssetManager am = FocusApplication.getInstance().getAssets();
			asset_found = Arrays.asList(am.list("webapps/" + this.webAppIdentifier)).contains("index.html");
		}
		catch (IOException ex) {
			// ok, asset_found defaults to false.
		}
		if (!asset_found) {
			this.markAsInvalid();
			return;
		}

		// context
		// the context can basically be any string, but is likely to be a unique identifier such as a URI
		Object raw_context = this.config.get(CONFIG_CONTEXT_FIELD);
		if (raw_context == null) {
			this.context = "";
		}
		else {
			try {
				this.context = TypesHelper.asString(
						this.dataContext.resolve(
								TypesHelper.asString(raw_context)
						)
				);
			}
			catch (FocusBadTypeException | FocusMissingResourceException ex) {
				this.markAsInvalid();
				return;
			}
		}
	}

	public String getWebAppIdentifier()
	{
		return this.webAppIdentifier;
	}

	public String getContext()
	{
		return this.context;
	}
}
