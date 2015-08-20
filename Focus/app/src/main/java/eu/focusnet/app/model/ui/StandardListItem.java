package eu.focusnet.app.model.ui;

import android.graphics.Bitmap;

import eu.focusnet.app.common.AbstractListItem;

/**
 * Created by admin on 15.06.2015.
 */
public class StandardListItem extends AbstractListItem {

    private String id;
    private String info;
    private Bitmap rightIcon;
    private boolean isRightIconActive;

    public static final int TYPE_STANDARD = 1;

    public StandardListItem(String id, Bitmap icon, String title, String info, Bitmap rightIcon, boolean isRightIconActive) {
     this(icon, title, info, rightIcon, isRightIconActive);
        this.id = id;
    }

    public StandardListItem(Bitmap icon, String title, String info, Bitmap rightIcon, boolean isRightIconActive) {
        super(icon, title);
        this.info = info;
        this.rightIcon = rightIcon;
        this.isRightIconActive = isRightIconActive;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRightIconActive() {
        return isRightIconActive;
    }

    public void setIsRightIconActive(boolean isRightIconActive) {
        this.isRightIconActive = isRightIconActive;
    }

    @Override
    public int getType() {
        return TYPE_STANDARD;
    }
}
