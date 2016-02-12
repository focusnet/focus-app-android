package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import eu.focusnet.app.model.json.FocusSampleDataMap;
import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class WidgetDao
{

	private String[] columnsToRetrieve = {Constant.ID, Constant.TYPE, Constant.CONFIG, Constant.FK_PROJECT_ID};

	private SQLiteDatabase database;

	public WidgetDao(SQLiteDatabase database)
	{
		this.database = database;
	}

	public Long createWidget(WidgetTemplate widget, String fkProjectId)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.ID, widget.getGuid());
		contentValues.put(Constant.TYPE, widget.getType());
		Gson gson = new GsonBuilder().create();
		String params = null;
		if (widget.getConfig() != null) {
			params = gson.toJson(widget.getConfig());
		}
		contentValues.put(Constant.CONFIG, params);
		contentValues.put(Constant.FK_PROJECT_ID, fkProjectId);
		return database.insert(Constant.DATABASE_TABLE_WIDGET, null, contentValues);
	}

	public WidgetTemplate findWidget(String widgetId)
	{
		String[] params = {widgetId};
		WidgetTemplate widget = null;
		Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			widget = getWidget(cursor);
			cursor.close();
		}
		return widget;
	}

	public ArrayList<WidgetTemplate> findWidgetByProjectId(String fkProjectId)
	{
		ArrayList<WidgetTemplate> widgets = new ArrayList<>();
		String[] config = {fkProjectId};
		Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET, columnsToRetrieve, Constant.FK_PROJECT_ID + "=?", config, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					widgets.add(getWidget(cursor));
				}
				while (cursor.moveToNext());
			}
			cursor.close();
		}

		return widgets;
	}

	public boolean deleteWidget(String widgetId)
	{
		String[] config = {widgetId};
		return database.delete(Constant.DATABASE_TABLE_WIDGET, Constant.ID + "=?", config) > 0;
	}

	//TODO update

	private WidgetTemplate getWidget(Cursor cursor)
	{
		WidgetTemplate widget = new WidgetTemplate();
		widget.setGuid(cursor.getString(cursor.getColumnIndex(Constant.ID)));
		widget.setType(cursor.getString(cursor.getColumnIndex(Constant.TYPE)));
		String configJson = cursor.getString(cursor.getColumnIndex(Constant.CONFIG));
		if (configJson != null) {
			Gson gson = new GsonBuilder().create();
			Type typeOfMap = new TypeToken<FocusSampleDataMap>()
			{
			}.getType();
			Object config = gson.fromJson(configJson, typeOfMap);
			widget.setConfig(config);
		}
		return widget;
	}
}
