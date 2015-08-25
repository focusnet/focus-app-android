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
    private String typeOfBookmark; //no to good
    private int order; //no to good

    public static final int TYPE_STANDARD = 1;

    public StandardListItem(String id, Bitmap icon, String title, String info){
        super(icon, title);
        this.info = info;
        this.id = id;
    }

    public StandardListItem(String id, Bitmap icon, String title, String info, int order, Bitmap rightIcon, boolean isRightIconActive, String typeOfBookmark) {
        this(id, icon, title, info);
        this.order = order;
        this.rightIcon = rightIcon;
        this.isRightIconActive = isRightIconActive;
        this.typeOfBookmark = typeOfBookmark;
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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isRightIconActive() {
        return isRightIconActive;
    }

    public void setIsRightIconActive(boolean isRightIconActive) {
        this.isRightIconActive = isRightIconActive;
    }

    public String getTypeOfBookmark() {
        return typeOfBookmark;
    }

    public void setTypeOfBookmark(String typeOfBookmark) {
        this.typeOfBookmark = typeOfBookmark;
    }

    @Override
    public int getType() {
        return TYPE_STANDARD;
    }
}
