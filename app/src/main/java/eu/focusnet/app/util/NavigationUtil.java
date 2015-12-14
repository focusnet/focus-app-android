package eu.focusnet.app.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 *
 */
public class NavigationUtil {

    public enum PathType {PROJECT_ID, PROJECT_ID_PAGE_ID, PROJECT_ID_BRACKETS, PROJECT_ID_BRACKETS_PAGE_ID}
    public enum WidgetType {TEXT, TABLE, PIE_CHART, BAR_CHART, LINE_CHART, CAMERA, GPS, FORM, EXTERNAL_APP , SUBMIT}

    private static Map<String, WidgetType> widgetTypeMap;

    private static final String TYPE_TEXT = "#/definitions/widget/visualize/text";
    private static final String TYPE_TABLE = "#/definitions/widget/visualize/table";
    private static final String TYPE_PIE_CHART = "#/definitions/widget/visualize/piechart";
    private static final String TYPE_BAR_CHART = "#/definitions/widget/visualize/barchart";
    private static final String TYPE_LINE_CHART = "#/definitions/widget/visualize/linechart";
    private static final String TYPE_CAMERA = "#/definitions/widget/visualize/camera";
    private static final String TYPE_GPS = "#/definitions/widget/visualize/gps";
    private static final String TYPE_FORM = "#/definitions/widget/visualize/form";
    private static final String TYPE_EXTERNAL_APP = "#/definitions/widget/visualize/external-app";
    private static final String TYPE_SUBMIT = "#/definitions/widget/visualize/submit";

    static {
        widgetTypeMap = new HashMap<>();
        widgetTypeMap.put(TYPE_TEXT, WidgetType.TEXT);
        widgetTypeMap.put(TYPE_TABLE, WidgetType.TABLE);
        widgetTypeMap.put(TYPE_PIE_CHART, WidgetType.PIE_CHART);
        widgetTypeMap.put(TYPE_BAR_CHART, WidgetType.BAR_CHART);
        widgetTypeMap.put(TYPE_LINE_CHART, WidgetType.LINE_CHART);
        widgetTypeMap.put(TYPE_CAMERA, WidgetType.CAMERA);
        widgetTypeMap.put(TYPE_GPS, WidgetType.GPS);
        widgetTypeMap.put(TYPE_FORM, WidgetType.FORM);
        widgetTypeMap.put(TYPE_EXTERNAL_APP, WidgetType.EXTERNAL_APP);
        widgetTypeMap.put(TYPE_SUBMIT, WidgetType.SUBMIT);
    }

    public static PathType checkPathType(String path) {
        if (path.contains("[") && path.contains("]") && path.contains("/")) {
            return PathType.PROJECT_ID_BRACKETS_PAGE_ID;
        } else if (path.contains("[") && path.contains("]")) {
            return PathType.PROJECT_ID_BRACKETS;
        } else if (path.contains("/")) {
            return PathType.PROJECT_ID_PAGE_ID;
        }
        return PathType.PROJECT_ID;
    }

    public static WidgetType checkWidgetType(String type) {
       return widgetTypeMap.get(type);
    }


}
