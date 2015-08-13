package eu.focusnet.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.util.Constant;

/**
 * Created by Yandy on 08.07.15.
 */
public class BookmarkLinkDao {

    private String[] columnsToRetrieve = {Constant.BOOKMARK_LINK_ID, Constant.NAME, Constant.ORDER, Constant.PATH, Constant.BL_TYPE, Constant.FK_BOOKMARK_ID};
    public static enum BOOKMARK_LINK_TYPE {PAGE, TOOL}

    private SQLiteDatabase database;

    public BookmarkLinkDao(SQLiteDatabase database){
        this.database = database;
    }

    public Long createBookmarkLing(BookmarkLink bookmarkLink, String type, Long bookmark_id){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.NAME, bookmarkLink.getName());
        contentValues.put(Constant.ORDER, bookmarkLink.getOrder());
        contentValues.put(Constant.PATH, bookmarkLink.getPath());
        contentValues.put(Constant.BL_TYPE, type);
        contentValues.put(Constant.FK_BOOKMARK_ID, bookmark_id);
        return database.insert(Constant.DATABASE_TABLE_BOOKMARK_LINK, null, contentValues);
    }

    public BookmarkLink findBookmarkLink(Long bookmarkLinkId){

        String[] params = {String.valueOf(bookmarkLinkId)};
        BookmarkLink bookmarkLink = new BookmarkLink();

        Cursor cursor = database.query(Constant.DATABASE_TABLE_BOOKMARK_LINK, columnsToRetrieve, Constant.BOOKMARK_LINK_ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            bookmarkLink = getBookmarkLink(cursor);
            cursor.close();
        }

        return bookmarkLink;
    }

    public ArrayList<BookmarkLink> findBookmarkLings(Long bookmarkId, String type){
        ArrayList<BookmarkLink> bookmarkLinks = new ArrayList<>();
        String[] params = {String.valueOf(bookmarkId), type};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_BOOKMARK_LINK, columnsToRetrieve, Constant.FK_BOOKMARK_ID +"=? AND "+Constant.BL_TYPE+"=?", params, null, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    bookmarkLinks.add(getBookmarkLink(cursor));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        return bookmarkLinks;
    }

    public boolean deleteBookmarkLing(Long bookmarLinkkId){
        return database.delete(Constant.DATABASE_TABLE_BOOKMARK_LINK, Constant.BOOKMARK_LINK_ID+"="+bookmarLinkkId, null) > 0;
    }

    //TODO update


    private BookmarkLink getBookmarkLink(Cursor cursor){
        BookmarkLink bookmarkLink = new BookmarkLink();
        bookmarkLink.setId(cursor.getLong(cursor.getColumnIndex(Constant.BOOKMARK_LINK_ID)));
        bookmarkLink.setName(cursor.getString(cursor.getColumnIndex(Constant.NAME)));
        bookmarkLink.setOrder(cursor.getInt(cursor.getColumnIndex(Constant.ORDER)));
        bookmarkLink.setPath(cursor.getString(cursor.getColumnIndex(Constant.PATH)));
        return bookmarkLink;
    }
}