package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.db.BookmarkLinkDao;
import eu.focusnet.app.db.ProjectDao;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.model.data.Project;

/**
 * Created by admin on 08.09.2015.
 */
public class BookmarkLinkManager {

    private BookmarkLinkDao bookmarkLinkDao;

    public BookmarkLinkManager(SQLiteDatabase database){
        bookmarkLinkDao = new BookmarkLinkDao(database);
    }

    public BookmarkLink findBookmarkLink(String projectId, String bookmarkLinkType){
        return bookmarkLinkDao.findBookmarkLink(projectId, bookmarkLinkType);
    }

    public Long createBookmarkLing(BookmarkLink bookmarkLink, String type, Long bookmark_id){
        return bookmarkLinkDao.createBookmarkLing(bookmarkLink, type, bookmark_id);
    }

    public boolean deleteBookmarkLing(String path, String bl_type, Long fk_bookmarks_id){
        return bookmarkLinkDao.deleteBookmarkLing(path, bl_type, fk_bookmarks_id);
    }

}
