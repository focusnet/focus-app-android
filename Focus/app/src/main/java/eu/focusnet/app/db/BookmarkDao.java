package eu.focusnet.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.util.Constant;

/**
 * Created by Yandy on 08.07.15.
 */
public class BookmarkDao {

    private String[] columnsToRetrieve = {Constant.BOOKMARK_ID, Constant.DUMMY};

    private SQLiteDatabase database;

    public BookmarkDao(SQLiteDatabase database){
        this.database = database;
    }


    public Long createBookmark(Bookmark bookmark){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.DUMMY, "dummy");
        Long bookmarkId = database.insert(Constant.DATABASE_TABLE_BOOKMARK, null, contentValues);
        BookmarkLinkDao bookmarkLinkDao = new BookmarkLinkDao(database);

        for(BookmarkLink page : bookmark.getPages())
            bookmarkLinkDao.createBookmarkLing(page, BookmarkLinkDao.BOOKMARK_LINK_TYPE.PAGE.toString(), bookmarkId);

        for(BookmarkLink tool : bookmark.getTools())
            bookmarkLinkDao.createBookmarkLing(tool, BookmarkLinkDao.BOOKMARK_LINK_TYPE.TOOL.toString(), bookmarkId);
        return bookmarkId;
    }

    public Bookmark findBookmark(Long bookmarkId){

        String[] params = {String.valueOf(bookmarkId)};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_BOOKMARK, columnsToRetrieve, Constant.BOOKMARK_ID +"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }
        cursor.close();

        Bookmark bookmark = new Bookmark();

        BookmarkLinkDao bookmarkLinkDao = new BookmarkLinkDao(database);
        ArrayList<BookmarkLink> pages = bookmarkLinkDao.findBookmarkLingsByType(bookmarkId, BookmarkLinkDao.BOOKMARK_LINK_TYPE.PAGE.toString());
        ArrayList<BookmarkLink> tools = bookmarkLinkDao.findBookmarkLingsByType(bookmarkId, BookmarkLinkDao.BOOKMARK_LINK_TYPE.TOOL.toString());
        bookmark.setPages(pages);
        bookmark.setTools(tools);

        return bookmark;
    }

    public boolean deleteBookmark(Long bookmarkId){
        return database.delete(Constant.DATABASE_TABLE_BOOKMARK, Constant.BOOKMARK_ID +"="+bookmarkId, null) > 0;
    }


}
