package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.model.focus.Setting;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 14.07.2015.
 */
public class SettingDao
{

	private String[] columnsToRetrieve = {Constant.ID, Constant.LANGUAGE};

	private SQLiteDatabase database;

	public SettingDao(SQLiteDatabase database)
	{
		this.database = database;
	}

	public Long createSetting(Setting setting)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.LANGUAGE, setting.getLanguage());
		return database.insert(Constant.DATABASE_TABLE_SETTING, null, contentValues);
	}

	public Setting findSetting(Long settingId)
	{
		String[] params = {String.valueOf(settingId)};
		Setting setting = null;
		Cursor cursor = database.query(Constant.DATABASE_TABLE_SETTING, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			setting = new Setting();
			setting.setLanguage(cursor.getString(cursor.getColumnIndex(Constant.LANGUAGE)));
			cursor.close();
		}
		return setting;
	}
}
