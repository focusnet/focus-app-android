package eu.focusnet.app.ui.util;

/**
 *
 */
public class NavigationUtil
{

	public static PathType checkPathType(String path)
	{
		String[] parts = path.split("\\|");
		PathType pathType = null;
		switch (parts.length) {
			case 1:
				pathType = PathType.PROJECT_ID;
				break;
			case 3:
				pathType = PathType.PROJECT_ID_PAGE_ID;
				break;
		}

		return pathType;

//        if (parts.length >= 1) {
//            return this.projects.get(parts[0]);
//        }
//        return null;// exception instead ?
//
//        if (path.contains("[") && path.contains("]") && path.contains("/")) {
//            return PathType.PROJECT_ID_BRACKETS_PAGE_ID;
//        } else if (path.contains("[") && path.contains("]")) {
//            return PathType.PROJECT_ID_BRACKETS;
//        } else if (path.contains("/")) {
//            return PathType.PROJECT_ID_PAGE_ID;
//        }
//        return PathType.PROJECT_ID;
	}

	public enum PathType
	{
		PROJECT_ID, PROJECT_ID_PAGE_ID
	}
}
