package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.model.data.AppContent;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class AppContentDao {

    private String[] columnsToRetrieve = {Constant.ID,
            Constant.TYPE, Constant.URL, Constant.OWNER, Constant.EDITOR, Constant.CREATION_DATE_TIME, Constant.EDITION_DATE_TIME, Constant.VERSION,
            Constant.ACTIVE};

    private SQLiteDatabase database;

    public AppContentDao(SQLiteDatabase database){
        this.database = database;
    }

    public Long createAppContent(AppContent appContent){

        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ID, appContent.getId());
        contentValues.put(Constant.TYPE, appContent.getType());
        contentValues.put(Constant.URL, appContent.getUrl());
        contentValues.put(Constant.OWNER, appContent.getOwner());
        contentValues.put(Constant.EDITOR, appContent.getEditor());
        contentValues.put(Constant.CREATION_DATE_TIME, String.valueOf(appContent.getCreationDateTime()));
        contentValues.put(Constant.EDITION_DATE_TIME, String.valueOf(appContent.getEditionDateTime()));
        contentValues.put(Constant.VERSION, appContent.getVersion());
        contentValues.put(Constant.ACTIVE, appContent.isActive());
        contentValues.put(Constant.VERSION, appContent.getVersion());
        contentValues.put(Constant.ACTIVE, appContent.isActive());

        return database.insert(Constant.DATABASE_TABLE_APP_CONTENT, null, contentValues);
    }

    public AppContent findAppContent(Long appContentId){
        String[] params = {String.valueOf(appContentId)};
        AppContent appContent =null;

        Cursor cursor = database.query(Constant.DATABASE_TABLE_APP_CONTENT, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);

        if(cursor != null){
            cursor.moveToFirst();
            appContent = getAppContent(cursor);
//          ProjectDao projectDao = new ProjectDao(database);
//          appContent.setProjects(projectDao.findProject(appContentId));  //TODO
            cursor.close();
        }

        return appContent;
    }

    public boolean deleteAppContent(Long appContentId){
        return database.delete(Constant.DATABASE_TABLE_PREFERENCE, Constant.ID+"="+appContentId, null) > 0;
    }

    //TODO update


    private AppContent getAppContent(Cursor cursor){
        AppContent appContent = new AppContent();
        appContent.setId(cursor.getLong(cursor.getColumnIndex(Constant.ID)));
        appContent.setType(cursor.getString(cursor.getColumnIndex(Constant.TYPE)));
        appContent.setUrl(cursor.getString(cursor.getColumnIndex(Constant.URL)));
        appContent.setOwner(cursor.getString(cursor.getColumnIndex(Constant.OWNER)));
        appContent.setEditor(cursor.getString(cursor.getColumnIndex(Constant.EDITOR)));
//        preference.setCreationDateTime(new Date(cursor.getString(cursor.getColumnIndex(Constant.CREATION_DATE_TIME))));
//        preference.setEditionDateTime(new Date(cursor.getString(cursor.getColumnIndex(Constant.EDITION_DATE_TIME))));  //TODO
        appContent.setVersion(cursor.getInt(cursor.getColumnIndex(Constant.VERSION)));
        return appContent;
    }
}