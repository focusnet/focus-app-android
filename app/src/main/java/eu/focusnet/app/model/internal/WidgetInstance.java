package eu.focusnet.app.model.internal;

import com.google.gson.internal.LinkedTreeMap;

import java.util.HashMap;
import java.util.Map;

import eu.focusnet.app.model.focus.WidgetTemplate;
import eu.focusnet.app.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
abstract public class WidgetInstance
{

	/* widget types */
	private static final String WIDGET_TYPE_TEXT = "#/definitions/widget/visualize/text";
	private static final String WIDGET_TYPE_TABLE = "#/definitions/widget/visualize/table";
	private static final String WIDGET_TYPE_PIE_CHART = "#/definitions/widget/visualize/piechart";
	private static final String WIDGET_TYPE_BAR_CHART = "#/definitions/widget/visualize/barchart";
	private static final String WIDGET_TYPE_LINE_CHART = "#/definitions/widget/visualize/linechart";
	private static final String WIDGET_TYPE_CAMERA = "#/definitions/widget/visualize/camera";
	private static final String WIDGET_TYPE_GPS = "#/definitions/widget/visualize/gps";
	private static final String WIDGET_TYPE_FORM = "#/definitions/widget/visualize/form";
	private static final String WIDGET_TYPE_EXTERNAL_APP = "#/definitions/widget/visualize/external-app";
	private static final String WIDGET_TYPE_SUBMIT = "#/definitions/widget/visualize/submit";


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
		this.config = (LinkedTreeMap<String, Object>) wTpl.getConfig(); // FIXME resolve?

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
	/*		case WIDGET_TYPE_BAR_CHART:
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
		*/
		}
		return null;
	}

	/**
	 * Process configuration elements that are common to all widget types.
	 */
	private void processCommonConfig()
	{
		this.title = TypesHelper.asString(this.template.getTitle());
	}

	/**
	 * Acquire the configuration of the widget
	 */
	abstract void processConfig();

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
}