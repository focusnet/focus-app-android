package eu.focusnet.app.model.internal.widgets;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.model.internal.AbstractInstance;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
public abstract class WidgetInstance extends AbstractInstance
{

	/* widget types */
	private static final String WIDGET_TYPE_TEXT = "#/definitions/widget/visualize/text";
	private static final String WIDGET_TYPE_TABLE = "#/definitions/widget/visualize/table";
	private static final String WIDGET_TYPE_PIE_CHART = "#/definitions/widget/visualize/piechart";
	private static final String WIDGET_TYPE_BAR_CHART = "#/definitions/widget/visualize/barchart";
	private static final String WIDGET_TYPE_LINE_CHART = "#/definitions/widget/visualize/linechart";
	private static final String WIDGET_TYPE_CAMERA = "#/definitions/widget/collect/camera";
	private static final String WIDGET_TYPE_GPS = "#/definitions/widget/collect/gps";
	private static final String WIDGET_TYPE_FORM = "#/definitions/widget/collect/form";
	private static final String WIDGET_TYPE_EXTERNAL_APP = "#/definitions/widget/collect/external-app"; // FIXME TODO
	private static final String WIDGET_TYPE_HTML5_WEBAPP = "#/definitions/widget/visualize/html5-widget";
	private static final String WIDGET_TYPE_SUBMIT = "#/definitions/widget/collect/submit";


	private static final String WIDGET_LAYOUT_WIDTH_LABEL = "width";
	private static final String WIDGET_LAYOUT_WIDTH_DEFAULT_VALUE = "4of4";

	private static HashMap<String, String> layoutConfigDefaults = null;

	static {
		layoutConfigDefaults = new HashMap<String, String>();
		layoutConfigDefaults.put(WIDGET_LAYOUT_WIDTH_LABEL, WIDGET_LAYOUT_WIDTH_DEFAULT_VALUE);
	}

	protected LinkedTreeMap<String, Object> config;
	protected DataContext dataContext;
	private String guid;
	private String title;
	private String type = null;
	private WidgetTemplate template;
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

		//	this.title = wTpl.getTitle(); // FIXME
		// 	this.description = wTpl.getDescription();
		Object cfg = wTpl.getConfig();
		if (cfg != null) {
			this.config = (LinkedTreeMap<String, Object>) cfg; // FIXME resolve content right away?
		}
		this.processCommonConfig();
		this.processConfig();
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
				//	return new BarChartWidgetInstance(template, layoutConfig, newCtx);
				// FIXME TODO
				return new LineChartWidgetInstance(template, layoutConfig, newCtx); // FIXME FIXME should be BarChartWidget
			case WIDGET_TYPE_LINE_CHART:
				return new LineChartWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_CAMERA:
				return new CameraWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_GPS:
				return new GPSWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_FORM:
				return new FormWidgetInstance(template, layoutConfig, newCtx);
			case WIDGET_TYPE_EXTERNAL_APP:
				//	return new ExternalAppWidgetInstance(template, layoutConfig, newCtx);
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
	private void processCommonConfig()
	{
		try {
			this.title = TypesHelper.asString(this.template.getTitle());
		}
		catch (FocusBadTypeException e) {
			this.title = "";
		}
	}

	/**
	 * Acquire the configuration of the widget
	 */
	abstract protected void processConfig();

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

	/**
	 * Tells whether this object is valid.
	 *
	 * @return
	 */
	public boolean isValid()
	{
		return true;
	}


	/**
	 * Set the collected data
	 *
	 * @param focusSample
	 */
	public void setCollectedData(FocusSample focusSample)
	{
		//TODO implement this method
	}
}