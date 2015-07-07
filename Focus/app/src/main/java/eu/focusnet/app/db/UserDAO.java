package eu.focusnet.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.sql.SQLException;

import eu.focusnet.app.model.data.User;

/**
 * Created by admin on 06.07.2015.
 */
public class UserDAO {

    public static final String DATABASE_TABLE = "users";

    public static final String ID = "id",
                               FIRST_NAME = "first_name",
                               LAST_NAME = "last_name",
                               EMAIL = "email",
                               COMPANY = "company",
                               TYPE = "type",
                               URL = "url",
                               OWNER = "owner",
                               EDITOR = "editor",
                               CREATION_DATE_TIME = "creation_date_time",
                               EDITION_DATE_TIME = "edition_date_time",
                               VERSION = "version",
                               ACTIVE = "active",

                               CREATE_TABLE_QUERY = "CREATE TABLE "+DATABASE_TABLE+""+
                                                    "("+ID+" INTEGER PRIMARY KEY  NOT NULL , "+
                                                    FIRST_NAME+" TEXT," +
                                                    LAST_NAME+" TEXT, " +
                                                    EMAIL+" TEXT, " +
                                                    COMPANY+" TEXT, " +
                                                    TYPE+" TEXT, " +
                                                    URL+" TEXT," +
                                                    OWNER+" TEXT," +
                                                    EDITOR+" TEXT, " +
                                                    CREATION_DATE_TIME+" TEXT, " +
                                                    EDITION_DATE_TIME+" TEXT, " +
                                                    VERSION+" INTEGER, " +
                                                    ACTIVE+" BOOL)";

    private DatabaseAdapter dbAdapter;

    public UserDAO(Context ctx) {
        dbAdapter = new DatabaseAdapter(ctx);
    }

    public void open() throws SQLException {
       dbAdapter.open();
    }

    public void close() {
       dbAdapter.close();
    }

    public Long insertUser(User user){
        ContentValues contentValues = new ContentValues();
        contentValues.put(ID, user.getId());
        contentValues.put(EMAIL, user.getEmail());
        contentValues.put(COMPANY, user.getCompany());
        contentValues.put(FIRST_NAME, user.getFirstName());
        contentValues.put(LAST_NAME, user.getLastName());
        contentValues.put(TYPE, user.getType());
        contentValues.put(URL, user.getUrl());
        contentValues.put(OWNER, user.getOwner());
        contentValues.put(EDITOR, user.getEditor());
        contentValues.put(CREATION_DATE_TIME, String.valueOf(user.getCreationDateTime()));
        contentValues.put(EDITION_DATE_TIME, String.valueOf(user.getEditionDateTime()));
        contentValues.put(VERSION, user.getVersion());
        contentValues.put(ACTIVE, user.isActive());
        return dbAdapter.db.insert(DATABASE_TABLE, null, contentValues);
    }

    public User getUser(int userId){
        String[] columnsToRetrieve = {ID, EMAIL, COMPANY,FIRST_NAME, LAST_NAME, TYPE, URL, OWNER,
                         EDITOR, CREATION_DATE_TIME, EDITION_DATE_TIME, VERSION, ACTIVE};
        String[] params = {String.valueOf(userId)};
        Cursor cursor = dbAdapter.db.query(DATABASE_TABLE, columnsToRetrieve, ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
        }

        User user = new User();
        user.setFirstName(cursor.getString(cursor.getColumnIndex(FIRST_NAME)));
        user.setLastName(cursor.getString(cursor.getColumnIndex(LAST_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(EMAIL)));
        user.setCompany(cursor.getString(cursor.getColumnIndex(COMPANY)));
        //TODO add the other properties

        return user;
    }

    public boolean deleteUser(Long userId){
        return dbAdapter.db.delete(DATABASE_TABLE, ID+"="+userId, null) > 0;
     }

    //TODO update
}
