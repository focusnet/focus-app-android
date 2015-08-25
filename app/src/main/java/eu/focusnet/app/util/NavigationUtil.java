package eu.focusnet.app.util;

/**
 * Created by admin on 24.08.2015.
 */
public class NavigationUtil {


    public static String retrievePath(String path){
        if(path.contains("[]") && path.contains("/")){
            //TODO
            return null;
        }
        else if(path.contains("[]")){
            //TODO
            return null;
        }
        else if(path.contains("/")){
            //TODO
            return null;
        }

        return path;
    }
}
