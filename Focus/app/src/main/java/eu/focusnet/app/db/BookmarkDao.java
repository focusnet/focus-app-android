package eu.focusnet.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.util.Constant;

/**
 * Created by Yandy on 08.07.15.
 */
public class BookmarkDao {

    private String[] columnsToRetrieve = {Constant.BOOKMARK_ID};

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public BookmarkDao(Context context) {
        this.dbHelper = DatabaseHelper.getInstance(context);
    }

    public void open(){
        if(database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public Long createBookmark(Bookmark bookmark){
        ContentValues contentValues = new ContentValues();
        return database.insert(Constant.DATABASE_TABLE_BOOKMARK, null, contentValues);
    }

    public Bookmark findBookmarkById(Long bookmarkId){

        String[] params = {String.valueOf(bookmarkId)};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_BOOKMARK, columnsToRetrieve, Constant.BOOKMARK_ID +"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setId(cursor.getLong(cursor.getColumnIndex(Constant.BOOKMARK_ID)));

        cursor.close();

        return bookmark;
    }

    public boolean deleteBookmarkById(Long bookmarkId){
        return database.delete(Constant.DATABASE_TABLE_BOOKMARK, Constant.BOOKMARK_ID +"="+bookmarkId, null) > 0;
    }


}
