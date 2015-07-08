package eu.focusnet.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;

import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 07.07.2015.
 */

public class PreferenceDAO {

    private String[] columnsToRetrieve = {Constant.PREFERENCE_ID, Constant.EMAIL, Constant.COMPANY, Constant.FIRST_NAME, Constant.LAST_NAME,
            Constant.TYPE, Constant.URL, Constant.OWNER, Constant.EDITOR, Constant.CREATION_DATE_TIME, Constant.EDITION_DATE_TIME, Constant.VERSION, Constant.ACTIVE};

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;


    public PreferenceDAO(Context ctx) {
        this.dbHelper = DatabaseHelper.getInstance(ctx);
    }


    public void open(){
        if(database == null || !database.isOpen()) {
            database = dbHelper.getWritableDatabase();
        }
    }

    public void close() {
        dbHelper.close();
    }

    public Long createPreference(Preference preference){
        ContentValues contentValues = new ContentValues();;
        contentValues.put(Constant.TYPE, preference.getType());
        contentValues.put(Constant.URL, preference.getUrl());
        contentValues.put(Constant.OWNER, preference.getOwner());
        contentValues.put(Constant.EDITOR, preference.getEditor());
        contentValues.put(Constant.CREATION_DATE_TIME, String.valueOf(preference.getCreationDateTime()));
        contentValues.put(Constant.EDITION_DATE_TIME, String.valueOf(preference.getEditionDateTime()));
        contentValues.put(Constant.VERSION, preference.getVersion());
        contentValues.put(Constant.ACTIVE, preference.isActive());
        return database.insert(Constant.DATABASE_TABLE_PREFERENCE, null, contentValues);
    }

    public Preference findPreference(Long preferenceId){
        String[] params = {String.valueOf(preferenceId)};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_PREFERENCE, columnsToRetrieve, Constant.PREFERENCE_ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }

        Preference preference = new Preference();
//      user.setFirstName(cursor.getString(cursor.getColumnIndex(FIRST_NAME)));

        //TODO add the other properties
        return preference;
    }

    public boolean deletePreference(Long preferenceId){
        return database.delete(Constant.DATABASE_TABLE_PREFERENCE, Constant.PREFERENCE_ID+"="+preferenceId, null) > 0;
    }

    //TODO update
}
