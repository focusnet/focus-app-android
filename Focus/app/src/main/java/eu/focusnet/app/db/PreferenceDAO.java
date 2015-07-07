package eu.focusnet.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.sql.SQLException;

import eu.focusnet.app.model.data.Preference;

/**
 * Created by admin on 07.07.2015.
 */

public class PreferenceDAO {

    public static final String DATABASE_TABLE = "preferences";

    public static final String ID = "id",
            TYPE = "type",
            URL = "url",
            OWNER = "owner",
            EDITOR = "editor",
            CREATION_DATE_TIME = "creation_date_time",
            EDITION_DATE_TIME = "edition_date_time",
            VERSION = "version",
            ACTIVE = "active",

    CREATE_TABLE_QUERY = "CREATE TABLE "+DATABASE_TABLE+""+
            "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT , "+
            TYPE+" TEXT, " +
            URL+" TEXT," +
            OWNER+" TEXT," +
            EDITOR+" TEXT, " +
            CREATION_DATE_TIME+" TEXT, " +
            EDITION_DATE_TIME+" TEXT, " +
            VERSION+" INTEGER, " +
            ACTIVE+" BOOL)";

    private DatabaseAdapter dbAdapter;

    public PreferenceDAO(Context ctx) {
        dbAdapter = new DatabaseAdapter(ctx);
    }

    public void open() throws SQLException {
        dbAdapter.open();
    }

    public void close() {
        dbAdapter.close();
    }

    public Long insertPreference(Preference preference){
        ContentValues contentValues = new ContentValues();;
        contentValues.put(TYPE, preference.getType());
        contentValues.put(URL, preference.getUrl());
        contentValues.put(OWNER, preference.getOwner());
        contentValues.put(EDITOR, preference.getEditor());
        contentValues.put(CREATION_DATE_TIME, String.valueOf(preference.getCreationDateTime()));
        contentValues.put(EDITION_DATE_TIME, String.valueOf(preference.getEditionDateTime()));
        contentValues.put(VERSION, preference.getVersion());
        contentValues.put(ACTIVE, preference.isActive());
        return dbAdapter.db.insert(DATABASE_TABLE, null, contentValues);
    }

    public Preference getPreference(int preferenceId){
        String[] columnsToRetrieve = {ID, TYPE, URL, OWNER,
                EDITOR, CREATION_DATE_TIME, EDITION_DATE_TIME, VERSION, ACTIVE};
        String[] params = {String.valueOf(preferenceId)};
        Cursor cursor = dbAdapter.db.query(DATABASE_TABLE, columnsToRetrieve, ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }

        Preference preference = new Preference();
//        user.setFirstName(cursor.getString(cursor.getColumnIndex(FIRST_NAME)));

        //TODO add the other properties
        return preference;
    }

    public boolean deletePreference(Long preferenceId){
        return dbAdapter.db.delete(DATABASE_TABLE, ID+"="+preferenceId, null) > 0;
    }

    //TODO update
}
