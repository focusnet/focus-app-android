package eu.focusnet.app.util;

/**
 * Created by admin on 24.08.2015.
 */
public class NavigationUtil {

    public enum PathType {PROJECTID, PROJECTID_PAGEID, PROJECTID_BRACKETS, PROJECTID_BRACKETS_PAGEID};

    public static PathType checkPathType(String path){
        if(path.contains("[") && path.contains("]") && path.contains("/")){
            return PathType.PROJECTID_BRACKETS_PAGEID;
        }
        else if(path.contains("[") && path.contains("]")){
            return PathType.PROJECTID_BRACKETS;
        }
        else if(path.contains("/")){
            return PathType.PROJECTID_PAGEID;
        }
        return PathType.PROJECTID;
    }


}
