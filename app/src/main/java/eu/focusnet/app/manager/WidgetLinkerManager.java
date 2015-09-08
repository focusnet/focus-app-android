package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;
import eu.focusnet.app.db.WidgetLinkerDao;
import eu.focusnet.app.model.data.WidgetLinker;

/**
 * Created by admin on 08.09.2015.
 */
public class WidgetLinkerManager {

    private  WidgetLinkerDao widgetLinkerDao;

    public WidgetLinkerManager(SQLiteDatabase database){
        widgetLinkerDao = new WidgetLinkerDao(database);
    }

    public Long createWidgetLinker(WidgetLinker widgetLinker, String fkPageId){
        return widgetLinkerDao.createWidgetLinker(widgetLinker, fkPageId);
    }
}
