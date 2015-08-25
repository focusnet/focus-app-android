package eu.focusnet.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 06.07.2015.
 */
public class DatabaseAdapter {

    private static final String TAG = DatabaseAdapter.class.getName();

    private static final String DATABASE_NAME = "Focus_DB";
    private static final int DATABASE_VERSION = 1;

    private DataBaseHelper dataBaseHelper;
    private SQLiteDatabase db;

    public DatabaseAdapter(Context context){
        dataBaseHelper = new DataBaseHelper(context);
    }

    public DatabaseAdapter openWritableDatabase(){
        if(db == null || !db.isOpen())
           db = dataBaseHelper.getWritableDatabase();
        return  this;
    }

    public void close(){
        dataBaseHelper.close();
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        private DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Constant.CREATE_TABLE_BOOKMARKS_QUERY);
            db.execSQL(Constant.CREATE_TABLE_BOOKMARK_LINK_QUERY);
            db.execSQL(Constant.CREATE_TABLE_SETTING_QUERY);
            db.execSQL(Constant.CREATE_TABLE_PREFERENCE_QUERY);
            db.execSQL(Constant.CREATE_TABLE_USER_QUERY);

            db.execSQL(Constant.CREATE_TABLE_APP_CONTENT_QUERY);
            db.execSQL(Constant.CREATE_TABLE_PROJECT_QUERY);
            db.execSQL(Constant.CREATE_TABLE_PAGE_QUERY);
            db.execSQL(Constant.CREATE_TABLE_WIDGETS_QUERY);
            db.execSQL(Constant.CREATE_TABLE_LINKER_QUERY);
            db.execSQL(Constant.CREATE_TABLE_WIDGET_LINKER_QUERY);


        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS" + Constant.DATABASE_TABLE_BOOKMARK);
            db.execSQL("DROP TABLE IF EXISTS" + Constant.DATABASE_TABLE_BOOKMARK_LINK);
            db.execSQL("DROP TABLE IF EXISTS" + Constant.DATABASE_TABLE_SETTING);
            db.execSQL("DROP TABLE IF EXISTS" + Constant.DATABASE_TABLE_PREFERENCE);
            db.execSQL("DROP TABLE IF EXISTS" + Constant.DATABASE_TABLE_USER);

            db.execSQL("DROP TABLE IF EXISTS" +Constant.DATABASE_TABLE_APP_CONTENT);
            db.execSQL("DROP TABLE IF EXISTS" +Constant.DATABASE_TABLE_PROJECT);
            db.execSQL("DROP TABLE IF EXISTS" +Constant.DATABASE_TABLE_PAGE);
            db.execSQL("DROP TABLE IF EXISTS" +Constant.DATABASE_TABLE_WIDGET);
            db.execSQL("DROP TABLE IF EXISTS" +Constant.DATABASE_TABLE_LINKER);
            db.execSQL("DROP TABLE IF EXISTS" +Constant.DATABASE_TABLE_WIDGET_LINKER);

            onCreate(db);
        }

        @Override
        public void onOpen(SQLiteDatabase db) {
            super.onOpen(db);
            if (!db.isReadOnly()) {
                // Enable foreign key constraints
                db.execSQL("PRAGMA foreign_keys=ON;");
            }
        }

    }
}
