package eu.focusnet.app.model.ui;

import android.graphics.Bitmap;

import eu.focusnet.app.common.AbstractListItem;

/**
 * Created by admin on 15.06.2015.
 */
public class StandardListItem extends AbstractListItem {

    private String info;
    private Bitmap rightIcon;
    public static final int TYPE_STANDARD = 1;

    public StandardListItem(Bitmap icon, String title, String info, Bitmap rightIcon) {
        super(icon, title);
        this.info = info;
        this.rightIcon = rightIcon;
    }

    public Bitmap getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(Bitmap rightIcon) {
        this.rightIcon = rightIcon;
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
