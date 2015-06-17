package eu.focusnet.focus.model;

import android.graphics.Bitmap;

import eu.focusnet.focus.common.AbtractDrawListItem;

/**
 * Created by admin on 15.06.2015.
 */
public class StandardDrawerListItem extends AbtractDrawListItem{

    private String info;
    public static final int TYPE_STANDARD = 1;

    public StandardDrawerListItem(Bitmap icon, String title, String info) {
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
