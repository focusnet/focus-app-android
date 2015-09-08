package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.db.LinkerDao;
import eu.focusnet.app.db.PageDao;
import eu.focusnet.app.model.data.Page;

/**
 * Created by admin on 08.09.2015.
 */
public class PageManager {

    private PageDao pageDao;

    public PageManager(SQLiteDatabase database){
        pageDao = new PageDao(database);
    }

    public Long createPage(Page page, String fkProjectId){
        return pageDao.createPage(page, fkProjectId);
    }

    public Page findPage(String pageId){
        return pageDao.findPage(pageId);
    }
}
