package eu.focusnet.app.util;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import eu.focusnet.app.common.WidgetFragment;

/**
 *
 */
public class NavigationUtil {

    public enum PathType {PROJECT_ID, PROJECT_ID_PAGE_ID, PROJECT_ID_BRACKETS, PROJECT_ID_BRACKETS_PAGE_ID}

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
}
