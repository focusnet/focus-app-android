/**
 *
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
 *
 */

package eu.focusnet.app.model.internal.widgets;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.AbstractInstance;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.util.TypesHelper;
import eu.focusnet.app.ui.util.ViewUtil;

/**
 * Created by julien on 12.01.16.
 */
public abstract class WidgetInstance extends AbstractInstance
{

	public static final String WIDGET_TYPE_TEXT = "#/definitions/widget/visualize/text";
	public static final String WIDGET_TYPE_TABLE = "#/definitions/widget/visualize/table";
	public static final String WIDGET_TYPE_PIE_CHART = "#/definitions/widget/visualize/piechart";
	public static final String WIDGET_TYPE_BAR_CHART = "#/definitions/widget/visualize/barchart";
	public static final String WIDGET_TYPE_LINE_CHART = "#/definitions/widget/visualize/linechart";
	public static final String WIDGET_TYPE_CAMERA = "#/definitions/widget/collect/camera";
	public static final String WIDGET_TYPE_GPS = "#/definitions/widget/collect/gps";
	public static final String WIDGET_TYPE_FORM = "#/definitions/widget/collect/form";
	public static final String WIDGET_TYPE_EXTERNAL_APP = "#/definitions/widget/collect/external-app";
	public static final String WIDGET_TYPE_HTML5_WEBAPP = "#/definitions/widget/visualize/html5-widget";
	public static final String WIDGET_TYPE_SUBMIT = "#/definitions/widget/collect/submit";
	
	public static final String WIDGET_LAYOUT_WIDTH_LABEL = "width";
	public static final String WIDGET_LAYOUT_WIDTH_DEFAULT_VALUE = "4of4";
	
	private static HashMap<String, String> layoutConfigDefaults = null;

	static {
		layoutConfigDefaults = new HashMap<>();
		layoutConfigDefaults.put(WIDGET_LAYOUT_WIDTH_LABEL, WIDGET_LAYOUT_WIDTH_DEFAULT_VALUE);
	}

	protected LinkedTreeMap<String, Object> config;
	protected DataContext dataContext;
	private String guid;
	private String title;
	private String type;
	protected WidgetTemplate template;
	private Map<String, String> layoutConfig;

	/**
	 * C'tor
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public WidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, DataContext dataCtx)
	{
		this.guid = wTpl.getGuid();
		this.type = wTpl.getType();
		this.template = wTpl;
		this.layoutConfig = (layoutConfig == null ? this.layoutConfigDefaults : layoutConfig);
		this.dataContext = dataCtx;
		if (this.dataContext == null) {
			this.dataContext = new DataContext();
		}

		//	this.title = wTpl.getTitle(); // FIXME
		// 	this.description = wTpl.getDescription();
		Object cfg = wTpl.getConfig();
		if (cfg != null) {
			this.config = (LinkedTreeMap<String, Object>) cfg;
		}

		this.processSpecificConfig();
		this.processCommonConfig();
	}

	public static WidgetInstance factory(WidgetTemplate template, Map<String, String> layoutConfig, DataContext newCtx)
	{
		switch (template.getType()) {
			case WIDGET_TYPE_TEXT:
				return new TextWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_TABLE:
				return new TableWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_PIE_CHART:
				return new PieChartWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_BAR_CHART:
				return new BarChartWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_LINE_CHART:
				return new LineChartWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_CAMERA:
				return new CameraWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_GPS:
				return new GPSWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_FORM:
				return new FormWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_EXTERNAL_APP:
				return new ExternalAppWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_SUBMIT:
				return new SubmitWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_HTML5_WEBAPP:
				return new Html5WidgetInstance(template, layoutConfig, newCtx);
		}
		return null;
	}

	/**
	 * Process configuration elements that are common to all widget types.
	 */
	protected void processCommonConfig()
	{
		try {
			this.title = TypesHelper.asString(this.dataContext.resolve(this.template.getTitle()));
		}
		catch (FocusMissingResourceException ex) {
			this.title = "";
		}
		catch (FocusBadTypeException e) {
			this.title = "";
		}
	}

	/**
	 * Acquire the configuration of the widget
	 */
	protected void processSpecificConfig()
	{

	}

	/**
	 * Return this WidgetInstance's guid.
	 *
	 * @return
	 */
	public String getGuid()
	{
		return this.guid;
	}

	/**
	 * Get the current widget's type.
	 *
	 * @return
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * Get the title
	 *
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Get a layout attribute.
	 *
	 * @return A string which's value corresponds to the requested attribute.
	 */
	public String getLayoutAttribute(String attribute)
	{
		String tmp = layoutConfig.get(attribute);
		if (tmp == null) {
			return this.layoutConfigDefaults.get(attribute);
		}
		return tmp;
	}
}
