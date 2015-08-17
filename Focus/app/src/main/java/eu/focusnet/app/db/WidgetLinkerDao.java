package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.model.data.WidgetLinker;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class WidgetLinkerDao {

    private String[] columnsToRetrieve = {Constant.ID, Constant.ITEM_ORDER, Constant.LAYOUT, Constant.FK_WIDGET_ID};

    private SQLiteDatabase database;

    public WidgetLinkerDao(SQLiteDatabase database){
        this.database = database;
    }

    public Long createWidgetLinker(WidgetLinker widgetLinker, String fkPageId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.ITEM_ORDER, widgetLinker.getOrder());
        contentValues.put(Constant.LAYOUT, widgetLinker.getLayout().toString()); //TODO
        contentValues.put(Constant.FK_WIDGET_ID, widgetLinker.getWidgetid());
        contentValues.put(Constant.FK_PAGE_ID, fkPageId);
        return database.insert(Constant.DATABASE_TABLE_WIDGET_LINKER, null, contentValues);
    }

    public WidgetLinker findWidgetLinker(Long widgetLinkerId){

        String[] params = {String.valueOf(widgetLinkerId)};
        WidgetLinker widgetLinker = new WidgetLinker();

        Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET_LINKER, columnsToRetrieve, Constant.ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            widgetLinker = getLinker(cursor);
            cursor.close();
        }

        return widgetLinker;
    }

    public ArrayList<WidgetLinker> findWidgetLinker(String fkPageId){
        ArrayList<WidgetLinker> widgetLinkers = new ArrayList<>();
        String[] params = {fkPageId};
        WidgetLinker widgetLinker = new WidgetLinker();

        Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET_LINKER, columnsToRetrieve, Constant.FK_PAGE_ID+"=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            widgetLinkers.add(widgetLinker = getLinker(cursor));
            cursor.close();
        }

        return widgetLinkers;
    }


    public boolean deleteWidgetLinker(Long widgetLinkerId){
        return database.delete(Constant.DATABASE_TABLE_WIDGET_LINKER, Constant.ID+"="+widgetLinkerId, null) > 0;
    }

    //TODO update

    private WidgetLinker getLinker(Cursor cursor){
        WidgetLinker widgetLinker = new WidgetLinker();
       // widgetLinker.setLayout(cursor.getString(cursor.getColumnIndex(Constant.LAYOUT))); //TODO
        widgetLinker.setWidgetid(cursor.getString(cursor.getColumnIndex(Constant.FK_WIDGET_ID)));
        widgetLinker.setOrder(cursor.getInt(cursor.getColumnIndex(Constant.ITEM_ORDER)));
        return widgetLinker;
    }
}
