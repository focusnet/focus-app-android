package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.db.AppContentDao;
import eu.focusnet.app.db.LinkerDao;
import eu.focusnet.app.db.PageDao;
import eu.focusnet.app.db.ProjectDao;
import eu.focusnet.app.db.WidgetDao;
import eu.focusnet.app.db.WidgetLinkerDao;
import eu.focusnet.app.model.data.AppContent;
import eu.focusnet.app.model.data.Linker;
import eu.focusnet.app.model.data.Page;
import eu.focusnet.app.model.data.Project;
import eu.focusnet.app.model.data.Widget;
import eu.focusnet.app.model.data.WidgetLinker;

/**
 * Created by admin on 02.09.2015.
 */
public class AppContentManager {

    private AppContentDao appContentDAO;

    public AppContentManager(SQLiteDatabase database){
        appContentDAO = new AppContentDao(database);
    }

    public Long saveAppContent(AppContent appContent){
        return appContentDAO.createAppContent(appContent);
    }

    public boolean deleteAppContent(Long appContentId){
        return appContentDAO.deleteAppContent(appContentId);
    }

}
