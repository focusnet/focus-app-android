package eu.focusnet.app.model.ui;

import android.graphics.Bitmap;

import eu.focusnet.app.common.AbstractListItem;

/**
 * Created by admin on 24.06.2015.
 */
public class HeaderListItem extends AbstractListItem {

    private Bitmap rightIcon;
    public static final int TYPE_HEADER = 0;

    public HeaderListItem(Bitmap icon, String title, Bitmap rightIcon) {
        super(icon, title);
        this.rightIcon = rightIcon;
    }

    public Bitmap getRightIcon() {
        return rightIcon;
    }

    public void setRightIcon(Bitmap rightIcon) {
        this.rightIcon = rightIcon;
    }

    public int getType(){
        return TYPE_HEADER;
    }
}
