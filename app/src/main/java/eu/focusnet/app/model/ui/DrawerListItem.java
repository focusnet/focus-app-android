package eu.focusnet.app.model.ui;

import android.graphics.Bitmap;

import eu.focusnet.app.common.AbstractListItem;

/**
 * Created by admin on 27.08.2015.
 */
public class DrawerListItem extends AbstractListItem {

    private String info;

    public static final int TYPE_STANDARD = 1;

    public DrawerListItem(Bitmap icon, String title, String info) {
        super(icon, title);
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int getType() {
        return TYPE_STANDARD;
    }
}

