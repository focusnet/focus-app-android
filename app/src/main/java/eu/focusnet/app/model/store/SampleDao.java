/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.internal.PageInstance;
import eu.focusnet.app.model.internal.ProjectInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.service.DataManager;

/**
 * SQL Sample Data Access Object
 *
 */
public class SampleDao
{

	private static final int SAMPLES_OUTDATED_AFTER = 7 * 24 * 60 * 60; // seconds

	private String[] columnsToRetrieve = {
			Constant.URL,
			Constant.CONTEXT,
			Constant.VERSION,
			Constant.TYPE,
			Constant.OWNER,
			Constant.CREATION_EPOCH,
			Constant.EDITION_EPOCH,
			Constant.EDITOR,
			Constant.ACTIVE,
			Constant.DATA,
			Constant.TO_DELETE,
			Constant.TO_PUT,
			Constant.TO_POST
	};

	private SQLiteDatabase database;

	/**
	 * Constructor
	 */
	public SampleDao(SQLiteDatabase database)
	{
		this.database = database;
	}

	/**
	 * Create a new Sample based on the provided cursor. We only take the first entry in
	 * consideration, and this method will move the cursor, so the calling method should not.
	 */
	private static Sample buildSampleFromCursor(Cursor cursor)
	{
		if (cursor.getCount() == 0) {
			return null;
		}
		Sample sample = new Sample();
		cursor.moveToFirst();
		sample.setUrl(cursor.getString(cursor.getColumnIndex(Constant.URL)));
		sample.setVersion(cursor.getInt(cursor.getColumnIndex(Constant.VERSION)));
		sample.setType(cursor.getString(cursor.getColumnIndex(Constant.TYPE)));
		sample.setOwner(cursor.getString(cursor.getColumnIndex(Constant.OWNER)));
		sample.setEditor(cursor.getString(cursor.getColumnIndex(Constant.EDITOR)));
		sample.setData(cursor.getString(cursor.getColumnIndex(Constant.DATA)));

		int creationDateTime = cursor.getInt(cursor.getColumnIndex(Constant.CREATION_EPOCH));
		int editionDateTime = cursor.getInt(cursor.getColumnIndex(Constant.EDITION_EPOCH));

		Date creationDate = new Date(creationDateTime * 1000L);
		Date editionDate = new Date(editionDateTime * 1000L);

		sample.setCreationDateTime(creationDate);
		sample.setEditionDateTime(editionDate);

		sample.setActive(cursor.getInt(cursor.getColumnIndex(Constant.ACTIVE)) > 0);
		sample.setToDelete(cursor.getInt(cursor.getColumnIndex(Constant.TO_DELETE)) > 0);
		sample.setToPut(cursor.getInt(cursor.getColumnIndex(Constant.TO_PUT)) > 0);

		return sample;
	}

	/**
	 * Create a new row in the table, based on the provided Sample
	 */
	public Long create(Sample sample)
	{
		ContentValues contentValues = this.createContentValues(sample);
		return database.insert(Constant.DATABASE_TABLE_SAMPLES, null, contentValues);
	}

	/**
	 * Get the latest version of a URL that is stored in the database
	 */
	public Sample get(String url)
	{
		String[] params = {
				url
		};
		Cursor cursor = this.database.query(true,
				Constant.DATABASE_TABLE_SAMPLES,
				this.columnsToRetrieve,
				Constant.URL + " = ? AND " + Constant.TO_DELETE + " = 0 AND " + Constant.ACTIVE + " = 1",
				params,
				null,
				null,
				Constant.VERSION + " DESC, " + Constant.EDITION_EPOCH + " DESC",
				"1");
		Sample s = SampleDao.buildSampleFromCursor(cursor);
		cursor.close();
		return s;
	}

	/**
	 * Mark a resource for deletion
	 * <p/>
	 * we mark ALL versions of the resource for deletion. Is that good? FIXME TODO JULIEN
	 */
	public void markForDeletion(String url)
	{
		String[] params = {
				url
		};
		String where = Constant.URL + "= ?";
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(Constant.TO_DELETE, true);
		updatedValues.put(Constant.EDITION_EPOCH, new Date().getTime() / 1000L);
		this.database.update(Constant.DATABASE_TABLE_SAMPLES, updatedValues, where, params);
	}

	/**
	 * Actually delete a resource
	 * <p/>
	 * We delete all versions of the resource
	 */
	public boolean delete(String url)
	{
		String[] params = {
				url
		};
		return this.database.delete(Constant.DATABASE_TABLE_SAMPLES, Constant.URL + "= ? ", params) > 0;
	}

	/**
	 * Update a sample with new values.
	 *
	 * @param sample
	 */
	public void update(Sample sample)
	{
		String[] params = {
				sample.getUrl()
		};
		String where = Constant.URL + "=?";
		ContentValues updatedValues = this.createContentValues(sample);
		this.database.update(Constant.DATABASE_TABLE_SAMPLES, updatedValues, where, params);
	}

	/**
	 * Create content values for a row
	 */
	private ContentValues createContentValues(Sample sample)
	{
		ContentValues contentValues = new ContentValues();
//		contentValues.register(Constant.ID, sample.getId());
		contentValues.put(Constant.URL, sample.getUrl());
		contentValues.put(Constant.CONTEXT, sample.getContext());
		contentValues.put(Constant.VERSION, sample.getVersion());
		contentValues.put(Constant.TYPE, sample.getType());
		contentValues.put(Constant.OWNER, sample.getOwner());
		contentValues.put(Constant.CREATION_EPOCH, (sample.getCreationDateTime().getTime() / 1000L));
		contentValues.put(Constant.EDITION_EPOCH, (sample.getEditionDateTime().getTime() / 1000L));
		contentValues.put(Constant.EDITOR, sample.getEditor());
		contentValues.put(Constant.ACTIVE, sample.isActive());
		contentValues.put(Constant.DATA, sample.getData());
		contentValues.put(Constant.TO_DELETE, sample.isToDelete());
		contentValues.put(Constant.TO_PUT, sample.isToPut());
		contentValues.put(Constant.TO_POST, sample.isToPost());
		return contentValues;
	}

	/**
	 * Clean the samples table from useless entries
	 */
	public void cleanTable()
	{
		DataManager dm = FocusApplication.getInstance().getDataManager();

		// Build the list of URLs that we use in our data contexts
		Set<String> used_urls = new HashSet<>();

		// register our configuration URLs
		try {
			used_urls.add(dm.getUser().getUrl());
			used_urls.add(dm.getUserPreferences().getUrl());
			used_urls.add(dm.getAppContentTemplate().getUrl());
		}
		catch (FocusMissingResourceException e) {
			// safe to ignore in this case, the clean up will simply not take these exceptions
			// into consideration, but not a problem as they apparently do not exist.
		}

		// populate the URLs set with the ones we have in the different DataContexts
		for (Map.Entry<String, String> e : dm.getAppContentInstance().getDataContext().entrySet()) {
			used_urls.add(e.getValue());
		}

		// foreach project
		for (Map.Entry<String, ProjectInstance> e_project : dm.getAppContentInstance().getProjects().entrySet()) {
			ProjectInstance pi = e_project.getValue();

			// Project data context
			for (Map.Entry<String, String> e_dc : pi.getDataContext().entrySet()) {
				used_urls.add(e_dc.getValue());
			}

			// foreach page
			for (Map.Entry<String, PageInstance> e_page : pi.getDashboards().entrySet()) {
				PageInstance page_instance = e_page.getValue();
				// Dashboards data context
				for (Map.Entry<String, String> e_dc : page_instance.getDataContext().entrySet()) {
					used_urls.add(e_dc.getValue());
				}

				// foreach widget
				for (Map.Entry<String, WidgetInstance> e_widget : page_instance.getWidgets().entrySet()) {
					WidgetInstance wi = e_widget.getValue();
					DataContext dc = wi.getDataContext();
					if (dc == null) {
						continue;
					}
					for (Map.Entry<String, String> e_dc : dc.entrySet()) {
						used_urls.add(e_dc.getValue());
					}
				}
			}
			for (Map.Entry<String, PageInstance> e_page : pi.getTools().entrySet()) {
				PageInstance page_instance = e_page.getValue();
				// Tools data context
				for (Map.Entry<String, String> e_dc : page_instance.getDataContext().entrySet()) {
					used_urls.add(e_dc.getValue());
				}

				// foreach widget
				for (Map.Entry<String, WidgetInstance> e_widget : page_instance.getWidgets().entrySet()) {
					WidgetInstance wi = e_widget.getValue();
					DataContext dc = wi.getDataContext();
					if (dc == null) {
						continue;
					}
					for (Map.Entry<String, String> e_dc : dc.entrySet()) {
						used_urls.add(e_dc.getValue());
					}
				}
			}
		}

		// At this point, we have the list of URLs that we must keep
		//
		// Now let's delete the useless ones from the database:
		// - resources that are not in the above-created 'urls' set
		// - toPut | toPost | toPut is set, and editionDateTime is > 1 week
		// - for remaining resources, delete the not-last versions
		//
		// But we must keep:
		// - Internal configuration
		//
		// This operation is transactional
		/*
			DELETE FROM samples WHERE
				url NOT LIKE '...'
			AND
				(
					url NOT IN (...urls...)
				OR
					(
						(
							toPut = 1
							OR toPost = 1
							OR toDelete = 1
						)
						AND
							editionTime > ...
					)
					OR
					(
						id NOT IN (
							SELECT id FROM samples GROUP BY url ORDER BY MAX(version);
						)
					)
				);
		 */

		// register quotes around urls
		String[] urls_list = (String[]) used_urls.toArray();
		for (int i = 0; i < urls_list.length; ++i) {
			urls_list[i] = "'" + urls_list[i] + "'";
		}

		String sql = "DELETE FROM " + Constant.DATABASE_TABLE_SAMPLES
				+ " WHERE "
				+ Constant.URL + " NOT LIKE '" + DataManager.FOCUS_DATA_MANAGER_INTERNAL_DATA_PREFIX + "%'"
				+ " AND "
				+ " ( "
				+ Constant.URL + " NOT IN (" + TextUtils.join(",", urls_list) + ") "
				+ " OR "
				+ " ( "
				+ "   ( " + Constant.TO_PUT + " = 1 OR " + Constant.TO_POST + " = 1 OR " + Constant.TO_DELETE + " = 1 ) AND "
				+ Constant.EDITION_EPOCH + " > (strftime('%s','now') - " + SAMPLES_OUTDATED_AFTER + ")"
				+ " ) "
				+ " OR "
				+ " ( "
				+ Constant.URL + " NOT IN ("
				+ "  SELECT " + Constant.URL + " FROM " + Constant.DATABASE_TABLE_SAMPLES + " GROUP BY " + Constant.URL + " ORDER BY MAX(" + Constant.VERSION + ")"
				+ " ) ";
		this.database.execSQL(sql);
	}

	/**
	 * Retrieve all urls marked with a certain type of operation
	 *
	 * @param type
	 * @return
	 */
	private String[] getAllMarked(String type)
	{
		Set<String> result = new HashSet<>();

		String[] cols = {
				Constant.URL
		};
		Cursor cursor = this.database.query(
				Constant.DATABASE_TABLE_SAMPLES,
				cols,
				type + " = 1",
				null, null, null, null
		);
		while (cursor.moveToNext()) {
			result.add(cursor.getString(cursor.getColumnIndex(Constant.URL)));
		}
		cursor.close();

		return (String[]) result.toArray();
	}

	/**
	 * Get the list of all urls marked as to be POST as an array of Strings
	 * @return
	 */
	public String[] getAllMarkedForPost()
	{
		return this.getAllMarked(Constant.TO_POST);
	}

	/**
	 * Get the list of all urls marked as to be PUT as an array of Strings
	 * @return
	 */
	public String[] getAllMarkedForPut()
	{
		return this.getAllMarked(Constant.TO_PUT);
	}

	/**
	 * Get the list of all urls marked as to be DELETEd as an array of Strings
	 * @return
	 */
	public String[] getAllMarkedForDeletion()
	{
		return this.getAllMarked(Constant.TO_DELETE);
	}

	/**
	 * Delete all entries from the database
	 */
	public void deleteAll()
	{
		String sql = "DELETE FROM " + Constant.DATABASE_TABLE_SAMPLES;
		this.database.execSQL(sql);
	}

}

