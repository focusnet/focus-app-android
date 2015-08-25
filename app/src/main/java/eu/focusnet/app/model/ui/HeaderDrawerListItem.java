package eu.focusnet.app.model.ui;

import android.graphics.Bitmap;

import eu.focusnet.app.common.AbstractListItem;

/**
 * Created by admin on 17.06.2015.
 */
public class HeaderDrawerListItem extends AbstractListItem {

    private String email, company;
    public static final int TYPE_HEADER_DRAWER = 0;

    public HeaderDrawerListItem(Bitmap logo, String user, String email, String company) {
        super(logo, user);
        this.email = email;
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public int getType() {
        return TYPE_HEADER_DRAWER;
    }
}
