package eu.focusnet.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.model.data.User;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 06.07.2015.
 */
public class UserDAO {

    private String[] columnsToRetrieve = {Constant.USER_ID, Constant.EMAIL, Constant.COMPANY, Constant.FIRST_NAME, Constant.LAST_NAME,
            Constant.TYPE, Constant.URL, Constant.OWNER, Constant.EDITOR, Constant.CREATION_DATE_TIME, Constant.EDITION_DATE_TIME, Constant.VERSION, Constant.ACTIVE};

    private SQLiteDatabase database;

    public UserDAO(SQLiteDatabase database){
        this.database = database;
    }

    public Long createUser(User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.USER_ID, user.getId());
        contentValues.put(Constant.EMAIL, user.getEmail());
        contentValues.put(Constant.COMPANY, user.getCompany());
        contentValues.put(Constant.FIRST_NAME, user.getFirstName());
        contentValues.put(Constant.LAST_NAME, user.getLastName());
        contentValues.put(Constant.TYPE, user.getType());
        contentValues.put(Constant.URL, user.getUrl());
        contentValues.put(Constant.OWNER, user.getOwner());
        contentValues.put(Constant.EDITOR, user.getEditor());
        contentValues.put(Constant.CREATION_DATE_TIME, String.valueOf(user.getCreationDateTime()));
        contentValues.put(Constant.EDITION_DATE_TIME, String.valueOf(user.getEditionDateTime()));
        contentValues.put(Constant.VERSION, user.getVersion());
        contentValues.put(Constant.ACTIVE, user.isActive());
        return database.insert(Constant.DATABASE_TABLE_USER, null, contentValues);
    }

    public User findUserById(Long userId){

        String[] params = {String.valueOf(userId)};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_USER, columnsToRetrieve, Constant.USER_ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }

        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(Constant.USER_ID)));
        user.setFirstName(cursor.getString(cursor.getColumnIndex(Constant.FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(Constant.LAST_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(Constant.EMAIL)));
        user.setCompany(cursor.getString(cursor.getColumnIndex(Constant.COMPANY)));
        //TODO add the other properties

        cursor.close();
        return user;
    }

    public boolean deleteUserById(Long userId){
        return database.delete(Constant.DATABASE_TABLE_USER, Constant.USER_ID+"="+userId, null) > 0;
     }

    //TODO update
}
