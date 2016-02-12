package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import eu.focusnet.app.model.json.WidgetLinker;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class WidgetLinkerDao
{

	private String[] columnsToRetrieve = {Constant.ID, Constant.ITEM_ORDER, Constant.LAYOUT, Constant.FK_WIDGET_ID};

	private SQLiteDatabase database;

	public WidgetLinkerDao(SQLiteDatabase database)
	{
		this.database = database;
	}

	public Long createWidgetLinker(WidgetLinker widgetLinker, String fkPageId)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.ITEM_ORDER, widgetLinker.getOrder());
		Gson gson = new GsonBuilder().create();
		String layout = null;
		if (widgetLinker.getLayout() != null) {
			layout = gson.toJson(widgetLinker.getLayout());
		}
		contentValues.put(Constant.LAYOUT, layout);
		contentValues.put(Constant.FK_WIDGET_ID, widgetLinker.getWidgetid());
		contentValues.put(Constant.FK_PAGE_ID, fkPageId);
		return database.insert(Constant.DATABASE_TABLE_WIDGET_LINKER, null, contentValues);
	}

	public WidgetLinker findWidgetLinker(Long widgetLinkerId)
	{
		String[] params = {String.valueOf(widgetLinkerId)};
		WidgetLinker widgetLinker = null;
		Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET_LINKER, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			widgetLinker = getLinker(cursor);
			cursor.close();
		}
		return widgetLinker;
	}

	public ArrayList<WidgetLinker> findWidgetLinker(String fkPageId)
	{
		ArrayList<WidgetLinker> widgetLinkers = new ArrayList<>();
		String[] params = {fkPageId};

		Cursor cursor = database.query(Constant.DATABASE_TABLE_WIDGET_LINKER, columnsToRetrieve, Constant.FK_PAGE_ID + "=?", params, null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					widgetLinkers.add(getLinker(cursor));
				}
				while (cursor.moveToNext());
			}
			cursor.close();
		}

		return widgetLinkers;
	}


	public boolean deleteWidgetLinker(String fkPageId)
	{
		String[] params = {fkPageId};
		return database.delete(Constant.DATABASE_TABLE_WIDGET_LINKER, Constant.FK_PAGE_ID + "=?", params) > 0;
	}

	//TODO update

	private WidgetLinker getLinker(Cursor cursor)
	{
		WidgetLinker widgetLinker = new WidgetLinker();
		String layoutJson = cursor.getString(cursor.getColumnIndex(Constant.LAYOUT));
		if (layoutJson != null) {
			Gson gson = new GsonBuilder().create();
			Type typeOfMap = new TypeToken<Map<String, String>>()
			{
			}.getType();
			Map<String, String> layout = gson.fromJson(layoutJson, typeOfMap);
			widgetLinker.setLayout(layout);
		}
		widgetLinker.setWidgetid(cursor.getString(cursor.getColumnIndex(Constant.FK_WIDGET_ID)));
		widgetLinker.setOrder(cursor.getInt(cursor.getColumnIndex(Constant.ITEM_ORDER)));
		return widgetLinker;
	}
}
