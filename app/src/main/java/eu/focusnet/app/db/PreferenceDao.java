package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.model.focus.Bookmark;
import eu.focusnet.app.model.focus.Preference;
import eu.focusnet.app.model.focus.Setting;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 07.07.2015.
 */

public class PreferenceDao
{

	private String[] columnsToRetrieve = {Constant.ID,
			Constant.TYPE, Constant.URL, Constant.OWNER, Constant.EDITOR, Constant.CREATION_DATE_TIME, Constant.EDITION_DATE_TIME, Constant.VERSION,
			Constant.ACTIVE, Constant.FK_BOOKMARK_ID, Constant.FK_SETTINGS_ID};

	private SQLiteDatabase database;

	public PreferenceDao(SQLiteDatabase database)
	{
		this.database = database;
	}

	public Long createPreference(Preference preference)
	{

		Setting setting = preference.getSettings();
		SettingDao sDao = new SettingDao(this.database);
		Long settingId = sDao.createSetting(setting);

		Bookmark bookmark = preference.getBookmarks();
		BookmarkDao bmDao = new BookmarkDao(this.database);
		Long bookmarkId = bmDao.createBookmark(bookmark);

		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.ID, preference.getId());
		contentValues.put(Constant.TYPE, preference.getType());
		contentValues.put(Constant.URL, preference.getUrl());
		contentValues.put(Constant.OWNER, preference.getOwner());
		contentValues.put(Constant.EDITOR, preference.getEditor());
		contentValues.put(Constant.CREATION_DATE_TIME, String.valueOf(preference.getCreationDateTime()));
		contentValues.put(Constant.EDITION_DATE_TIME, String.valueOf(preference.getEditionDateTime()));
		contentValues.put(Constant.VERSION, preference.getVersion());
		contentValues.put(Constant.ACTIVE, preference.isActive());
		contentValues.put(Constant.VERSION, preference.getVersion());
		contentValues.put(Constant.ACTIVE, preference.isActive());
		contentValues.put(Constant.FK_BOOKMARK_ID, bookmarkId);
		contentValues.put(Constant.FK_SETTINGS_ID, settingId);
		return database.insert(Constant.DATABASE_TABLE_PREFERENCE, null, contentValues);
	}

	public Preference findPreference(Long preferenceId)
	{
		String[] params = {String.valueOf(preferenceId)};
		Preference preference = null;

		Cursor cursor = database.query(Constant.DATABASE_TABLE_PREFERENCE, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			Long fkBookmarksId = cursor.getLong(cursor.getColumnIndex(Constant.FK_BOOKMARK_ID));
			Long fkSettingsId = cursor.getLong(cursor.getColumnIndex(Constant.FK_SETTINGS_ID));

			preference = getPreference(cursor);

			BookmarkDao bookmarkDao = new BookmarkDao(database);
			Bookmark bookmark = bookmarkDao.findBookmark(fkBookmarksId);

			SettingDao settingDao = new SettingDao(database);
			Setting setting = settingDao.findSetting(fkSettingsId);

			preference.setBookmarks(bookmark);
			preference.setSettings(setting);
			cursor.close();
		}

		return preference;
	}

	public boolean deletePreference(Long preferenceId)
	{
		String[] params = {String.valueOf(preferenceId)};
		return database.delete(Constant.DATABASE_TABLE_PREFERENCE, Constant.ID + "=?", params) > 0;
	}

	//TODO update


	private Preference getPreference(Cursor cursor)
	{
		Preference preference = new Preference();
		preference.setId(cursor.getLong(cursor.getColumnIndex(Constant.ID)));
		preference.setType(cursor.getString(cursor.getColumnIndex(Constant.TYPE)));
		preference.setUrl(cursor.getString(cursor.getColumnIndex(Constant.URL)));
		preference.setOwner(cursor.getString(cursor.getColumnIndex(Constant.OWNER)));
		preference.setEditor(cursor.getString(cursor.getColumnIndex(Constant.EDITOR)));
//        preference.setCreationDateTime(new Date(cursor.getString(cursor.getColumnIndex(Constant.CREATION_DATE_TIME))));
//        preference.setEditionDateTime(new Date(cursor.getString(cursor.getColumnIndex(Constant.EDITION_DATE_TIME))));  //TODO
		preference.setVersion(cursor.getInt(cursor.getColumnIndex(Constant.VERSION)));
		return preference;
	}
}
