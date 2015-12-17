package eu.focusnet.app.model.ui;

import android.graphics.Bitmap;

import eu.focusnet.app.common.AbstractListItem;

/**
 * Header list item which contains a left icon, a title and a right icon
 * This is the header item from a list
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
