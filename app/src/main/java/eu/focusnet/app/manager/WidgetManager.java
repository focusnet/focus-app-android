package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.db.BookmarkLinkDao;
import eu.focusnet.app.db.WidgetDao;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.model.data.Widget;

/**
 * Created by admin on 08.09.2015.
 */
public class WidgetManager {

    private WidgetDao widgetDao;

    public WidgetManager(SQLiteDatabase database){
        widgetDao = new WidgetDao(database);
    }

    public Long createWidget(Widget widget, String fkProjectId){
        return widgetDao.createWidget(widget, fkProjectId);
    }
}
