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

package eu.focusnet.app.model.widgets;

import android.support.annotation.NonNull;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.AbstractInstance;
import eu.focusnet.app.model.DataContext;
import eu.focusnet.app.model.gson.WidgetTemplate;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.util.Constant;

/**
 * Created by julien on 12.01.16.
 */
public abstract class WidgetInstance extends AbstractInstance
{


	private static HashMap<String, String> layoutConfigDefaults = null;

	static {
		layoutConfigDefaults = new HashMap<>();
		layoutConfigDefaults.put(Constant.Ui.WIDGET_LAYOUT_WIDTH_LABEL, Constant.Ui.WIDGET_LAYOUT_WIDTH_DEFAULT_VALUE);
	}

	protected LinkedTreeMap<String, Object> config;
	protected WidgetTemplate template;
	private String guid;
	private String title;
	private String type;
	private Map<String, String> layoutConfig;

	/**
	 * C'tor for instantiating real widget instances
	 *
	 * @param wTpl
	 * @param layoutConfig
	 * @param dataCtx
	 */
	public WidgetInstance(WidgetTemplate wTpl, Map<String, String> layoutConfig, @NonNull DataContext dataCtx)
	{
		super(dataCtx.getDataManager());

		this.guid = wTpl.getGuid();
		this.type = wTpl.getType();
		this.template = wTpl;
		this.layoutConfig = (layoutConfig == null ? WidgetInstance.layoutConfigDefaults : layoutConfig);
		this.dataContext = dataCtx;

		Object cfg = wTpl.getConfig();
		if (cfg != null) {
			this.config = (LinkedTreeMap<String, Object>) cfg;
		}

		this.processSpecificConfig();
		this.processCommonConfig();

		this.freeDataContext();
	}

	/**
	 * Dummy c'tor for InvalidWidgetInstance
	 */
	public WidgetInstance(Map<String, String> layoutConfig, DataManager dm)
	{
		super(dm);

		this.layoutConfig = (layoutConfig == null ? WidgetInstance.layoutConfigDefaults : layoutConfig);
	}

	public static WidgetInstance factory(WidgetTemplate template, Map<String, String> layoutConfig, DataContext newCtx)
	{
		WidgetInstance w;
		String a = "abc";
		if (template == null) {
			a = a.replace("asdlkfj", "");
		}
		switch (template.getType()) {
			case Constant.DataModelTypes.WIDGET_TYPE_TEXT:
				w = new TextWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_TABLE:
				w = new TableWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_PIE_CHART:
				w = new PieChartWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_BAR_CHART:
				w = new BarChartWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_LINE_CHART:
				w = new LineChartWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_CAMERA:
				w = new CameraWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_GPS:
				w = new GPSWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_FORM:
				w = new FormWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_EXTERNAL_APP:
				w = new ExternalAppWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_SUBMIT:
				w = new SubmitWidgetInstance(template, layoutConfig, newCtx);
				break;
			case Constant.DataModelTypes.WIDGET_TYPE_HTML5_WEBAPP:
				w = new Html5WidgetInstance(template, layoutConfig, newCtx);
				break;
			default:
				throw new FocusInternalErrorException("Instance type does not exist.");
		}
		if (!w.isValid()) {
			w = new InvalidWidgetInstance(layoutConfig, w);
		}
		return w;
	}

	/**
	 * Process configuration elements that are common to all widget types.
	 */
	protected void processCommonConfig()
	{
		String title = this.template.getTitle();
		if (title != null) {
			try {
				title = this.dataContext.resolveToString(title);
			}
			catch (FocusMissingResourceException | FocusBadTypeException ex) {
				title = "Error in fetching title";
				this.markAsInvalid();
			}
		}
		this.title = title;
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
	 * Set the guid
	 */
	protected void setGuid(String guid)
	{
		this.guid = guid;
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
			return WidgetInstance.layoutConfigDefaults.get(attribute);
		}
		return tmp;
	}

	/**
	 * Return an integer that represents the number of columsn covered by the widget.
	 */
	public int getNumberOfColumnsInUi()
	{
		String width = this.getLayoutAttribute(Constant.Ui.WIDGET_LAYOUT_WIDTH_LABEL);
		int indexOf = width.indexOf(Constant.Ui.WIDGET_LAYOUT_OF);
		return Integer.valueOf(width.substring(0, indexOf).trim());
	}

	@Override
	protected AbstractInstance propagatePathLookup(String searchedPath)
	{
		return null;
	}

	@Override
	public void buildPaths(String parentPath)
	{
		this.path = parentPath + Constant.Navigation.PATH_SEPARATOR + this.guid;
	}


}
