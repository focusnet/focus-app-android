package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import eu.focusnet.app.model.json.AppContentTemplate;
import eu.focusnet.app.model.json.ProjectTemplate;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 04.08.2015.
 */
public class AppContentDao
{

	private static final String TAG = AppContentTemplate.class.getName();

	private String[] columnsToRetrieve = {Constant.ID,
			Constant.TYPE, Constant.URL, Constant.OWNER, Constant.EDITOR, Constant.CREATION_DATE_TIME, Constant.EDITION_DATE_TIME, Constant.VERSION,
			Constant.ACTIVE};

	private SQLiteDatabase database;

	public AppContentDao(SQLiteDatabase database)
	{
		this.database = database;
	}

	public Long createAppContent(AppContentTemplate appContentTemplate)
	{

		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.ID, appContentTemplate.getId());
		contentValues.put(Constant.TYPE, appContentTemplate.getType());
		contentValues.put(Constant.URL, appContentTemplate.getUrl());
		contentValues.put(Constant.OWNER, appContentTemplate.getOwner());
		contentValues.put(Constant.EDITOR, appContentTemplate.getEditor());
		contentValues.put(Constant.CREATION_DATE_TIME, String.valueOf(appContentTemplate.getCreationDateTime()));
		contentValues.put(Constant.EDITION_DATE_TIME, String.valueOf(appContentTemplate.getEditionDateTime()));
		contentValues.put(Constant.VERSION, appContentTemplate.getVersion());
		contentValues.put(Constant.ACTIVE, appContentTemplate.isActive());
		contentValues.put(Constant.VERSION, appContentTemplate.getVersion());
		contentValues.put(Constant.ACTIVE, appContentTemplate.isActive());

		Long rawId = database.insert(Constant.DATABASE_TABLE_APP_CONTENT, null, contentValues);

		if (rawId != -1) {
			ProjectDao projectDao = new ProjectDao(database);
			ArrayList<ProjectTemplate> projects = appContentTemplate.getProjects();
			if (projects != null) {
				for (ProjectTemplate project : projects) {
					projectDao.createProject(project, appContentTemplate.getId());
				}
			}
		}

		return rawId;
	}

	public AppContentTemplate findAppContent(Long appContentId)
	{
		String[] params = {String.valueOf(appContentId)};
		AppContentTemplate appContentTemplate = null;
		Cursor cursor = database.query(Constant.DATABASE_TABLE_APP_CONTENT, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			appContentTemplate = getAppContent(cursor);
			ProjectDao projectDao = new ProjectDao(database);
			ArrayList<ProjectTemplate> projects = projectDao.findAllProjects(String.valueOf(appContentId));
			appContentTemplate.setProjects(projects);
			cursor.close();
		}

		return appContentTemplate;
	}

	public boolean deleteAppContent(Long appContentId)
	{
		String[] params = {String.valueOf(appContentId)};
		ProjectDao projectDao = new ProjectDao(database);
		ArrayList<ProjectTemplate> projects = projectDao.findAllProjects(String.valueOf(appContentId));
		Log.d(TAG, "Number of projects found: " + projects.size());
		for (ProjectTemplate project : projects) {
			projectDao.deleteProject(project.getGuid());
		}
		return database.delete(Constant.DATABASE_TABLE_APP_CONTENT, Constant.ID + "=?", params) > 0;
	}

	//TODO update


	private AppContentTemplate getAppContent(Cursor cursor)
	{
		AppContentTemplate appContentTemplate = new AppContentTemplate();
		appContentTemplate.setId(cursor.getLong(cursor.getColumnIndex(Constant.ID)));
		appContentTemplate.setType(cursor.getString(cursor.getColumnIndex(Constant.TYPE)));
		appContentTemplate.setUrl(cursor.getString(cursor.getColumnIndex(Constant.URL)));
		appContentTemplate.setOwner(cursor.getString(cursor.getColumnIndex(Constant.OWNER)));
		appContentTemplate.setEditor(cursor.getString(cursor.getColumnIndex(Constant.EDITOR)));
//        preference.setCreationDateTime(new Date(cursor.getString(cursor.getColumnIndex(Constant.CREATION_DATE_TIME))));
//        preference.setEditionDateTime(new Date(cursor.getString(cursor.getColumnIndex(Constant.EDITION_DATE_TIME))));  //TODO
		appContentTemplate.setVersion(cursor.getInt(cursor.getColumnIndex(Constant.VERSION)));
		return appContentTemplate;
	}
}
