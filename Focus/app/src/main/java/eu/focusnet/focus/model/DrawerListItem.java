package eu.focusnet.focus.model;

import android.graphics.Bitmap;

/**
 * Created by admin on 15.06.2015.
 */
public class DrawerListItem {

    private String title, info;
    private Bitmap icon;
    private boolean isInfoVisible;

    public DrawerListItem(Bitmap icon, String title, String info, boolean isInfoVisible) {
        this(icon, title);
        this.info = info;
        this.isInfoVisible = isInfoVisible;
    }

    public DrawerListItem(Bitmap icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }

    public boolean isInfoVisible() {
        return isInfoVisible;
    }

    public void setIsInfoVisible(boolean isInfoVisible) {
        this.isInfoVisible = isInfoVisible;
    }
}
