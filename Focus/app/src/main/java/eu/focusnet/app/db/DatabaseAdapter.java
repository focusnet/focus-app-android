package eu.focusnet.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by admin on 06.07.2015.
 */
public class DatabaseAdapter {

    private static final String TAG  = DatabaseAdapter.class.getName();

    private static final String DATABASE_NAME = "Focus_DB";
    private static final int DATABASE_VERSION = 1;

    //TODO create queries for table creation

    DatabaseHelper dbHelper;
    SQLiteDatabase db;


    public DatabaseAdapter(Context ctx) {
        dbHelper = new DatabaseHelper(ctx);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            //TODO create the databases
            db.execSQL(UserDAO.CREATE_TABLE_QUERY);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS"+ UserDAO.DATABASE_TABLE); //TODO the name of the tables
            onCreate(db);
        }
    }

}
