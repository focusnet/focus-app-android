package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.model.focus.Linker;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 03.08.2015.
 */
public class LinkerDao {

    private String[] columnsToRetrieve = {Constant.ID, Constant.ITEM_ORDER, Constant.LK_TYPE, Constant.FK_PAGE_ID, Constant.FK_PROJECT_ID};
    public static enum LINKER_TYPE {DASHBOARD, TOOL}

    private SQLiteDatabase database;

    public LinkerDao(SQLiteDatabase database){
        this.database = database;
    }

    public Long createLinker(Linker linker, String fkProjectID, LINKER_TYPE type){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ITEM_ORDER, linker.getOrder());
        contentValues.put(Constant.LK_TYPE, type.toString());
        contentValues.put(Constant.FK_PAGE_ID, linker.getPageid());
        contentValues.put(Constant.FK_PROJECT_ID, fkProjectID);
        return database.insert(Constant.DATABASE_TABLE_LINKER, null, contentValues);
    }

    public ArrayList<Linker> findLinkers(String fkProjectID, LINKER_TYPE type){
        ArrayList<Linker> linkers = new ArrayList<>();
        String[] params = {fkProjectID, type.toString()};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_LINKER, columnsToRetrieve, Constant.FK_PROJECT_ID +"=? AND "+Constant.LK_TYPE+"=?", params, null, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    linkers.add(getLinker(cursor));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        return linkers;
    }

    public boolean deleteLinker(String fkProjectID, LINKER_TYPE type){
        String[] params = {fkProjectID, type.toString()};
        return database.delete(Constant.DATABASE_TABLE_LINKER, Constant.FK_PROJECT_ID+"=? AND "+Constant.LK_TYPE+"=?", params) > 0;
    }

    //TODO update

    private Linker getLinker(Cursor cursor){
        Linker linker = new Linker();;
        linker.setPageid(cursor.getString(cursor.getColumnIndex(Constant.FK_PAGE_ID)));
        linker.setOrder(cursor.getInt(cursor.getColumnIndex(Constant.ITEM_ORDER)));
        return linker;
    }
}
