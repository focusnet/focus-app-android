package eu.focusnet.focus.model;

import android.graphics.Bitmap;

import eu.focusnet.focus.common.AbtractListItem;

/**
 * Created by admin on 15.06.2015.
 */
public class StandardListItem extends AbtractListItem {

    private String info;
    public static final int TYPE_STANDARD = 1;

    public StandardListItem(Bitmap icon, String title, String info) {
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
