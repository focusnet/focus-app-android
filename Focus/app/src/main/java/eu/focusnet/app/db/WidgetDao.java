package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.model.data.Widget;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class WidgetDao {

    private String[] columnsToRetrieve = {Constant.WIDGET_ID, Constant.TYPE, Constant.PARAMS, Constant.FK_PROJECT_ID};

    private SQLiteDatabase database;
    private Gson gson;

    public WidgetDao(SQLiteDatabase database){
        this.database = database;
        this.gson = new Gson();
    }

    public Long createWidget(Widget widget, String fkProjectId){
        ContentValues contentValues = new ContentValues();
        contentValues.put(Constant.WIDGET_ID, widget.getGuid());
        contentValues.put(Constant.TYPE, widget.getType());
        String params = gson.toJson(widget.getParams());
        contentValues.put(Constant.PARAMS, params);
        contentValues.put(Constant.FK_PROJECT_ID, fkProjectId);
        return database.insert(Constant.DATABASE_TABLE_WIDGET, null, contentValues);
    }

    public Widget findWidget(Long widgetId){

        String[] params = {String.valueOf(widgetId)};
        Widget widget = new Widget();

        Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET, columnsToRetrieve, Constant.WIDGET_ID + "=?", params, null, null, null);
        if(cursor != null){
            cursor.moveToFirst();
            widget = getWidget(cursor);
            cursor.close();
        }

        return widget;
    }

    public ArrayList<Widget> findWidget(String fkProjectId){
        ArrayList<Widget> widgets = new ArrayList<>();
        String[] params = {fkProjectId};
        Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET, columnsToRetrieve, Constant.FK_PROJECT_ID+"=?", params, null, null, null);
        if(cursor != null){
            if(cursor.moveToFirst()){
                do {
                    widgets.add(getWidget(cursor));
                }while (cursor.moveToNext());
            }
            cursor.close();
        }

        return widgets;
    }

    public boolean deleteWidget(Long widgetId){
        return database.delete(Constant.DATABASE_TABLE_WIDGET, Constant.WIDGET_ID+"="+widgetId, null) > 0;
    }

    //TODO update

    private Widget getWidget(Cursor cursor){
        Widget widget = new Widget();
        widget.setGuid(cursor.getString(cursor.getColumnIndex(Constant.WIDGET_ID)));
        widget.setType(cursor.getString(cursor.getColumnIndex(Constant.TYPE)));
        String params = cursor.getString(cursor.getColumnIndex(Constant.PARAMS));
        widget.setParams(gson.fromJson(params, Map.class));
        return widget;
    }
}
