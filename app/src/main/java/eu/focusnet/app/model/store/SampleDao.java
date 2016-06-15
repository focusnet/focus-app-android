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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import eu.focusnet.app.exception.FocusInternalErrorException;
import eu.focusnet.app.model.util.Constant;

/**
 * SQL Sample Data Access Object
 * <p/>
 * All samples being recorded belong to a specific data set id ({@link #dataSyncSetId}
 * instance variable) that is unique to each {@link DatabaseAdapter} being created. Most operations
 * are constrained on this set, hence allowing us to have different versions of the data set. This
 * is particularily useful for easily cleaning the database after data synchronisation.
 */
public class SampleDao
{


	private final long dataSyncSetId;
	private String[] columnsToRetrieve = {
			Constant.URL,
			Constant.VERSION,
			Constant.TYPE,
			Constant.OWNER,
			Constant.CREATION_EPOCH,
			Constant.EDITION_EPOCH,
			Constant.EDITOR,
			Constant.ACTIVE,
			Constant.DATA,
			Constant.TO_DELETE,
			Constant.TO_UPDATE,
			Constant.TO_CREATE
	};
	private SQLiteDatabase database;

	/**
	 * Constructor
	 */
	public SampleDao(SQLiteDatabase database, long dataSyncSetId)
	{
		this.database = database;
		this.dataSyncSetId = dataSyncSetId;
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
		sample.setToPut(cursor.getInt(cursor.getColumnIndex(Constant.TO_UPDATE)) > 0);

		// we don't need the dataSyncSetId in the sample. It is used for maintenance operations
		// only and should be kept hidden

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
				Long.toString(this.dataSyncSetId),
				url
		};
		Cursor cursor = this.database.query(true,
				Constant.DATABASE_TABLE_SAMPLES,
				this.columnsToRetrieve,
				Constant.DATA_SET_ID + " = ? AND " + Constant.URL + " = ? AND " + Constant.TO_DELETE + " = 0 AND " + Constant.ACTIVE + " = 1",
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
				Long.toString(this.dataSyncSetId),
				url
		};
		String where = Constant.DATA_SET_ID + " = ? AND " + Constant.URL + "= ?";
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
				Long.toString(this.dataSyncSetId),
				url
		};
		return this.database.delete(Constant.DATABASE_TABLE_SAMPLES, Constant.DATA_SET_ID + " = ? AND " + Constant.URL + "= ? ", params) > 0;
	}

	/**
	 * Update a sample with new values.
	 *
	 * @param sample
	 */
	public void update(Sample sample)
	{
		String[] params = {
				Long.toString(this.dataSyncSetId),
				sample.getUrl()
		};
		String where = Constant.DATA_SET_ID + " = ? AND " + Constant.URL + "=?";
		ContentValues updatedValues = this.createContentValues(sample);
		this.database.update(Constant.DATABASE_TABLE_SAMPLES, updatedValues, where, params);
	}

	/**
	 * Create content values for a row
	 */
	private ContentValues createContentValues(Sample sample)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.URL, sample.getUrl());
		contentValues.put(Constant.VERSION, sample.getVersion());
		contentValues.put(Constant.TYPE, sample.getType());
		contentValues.put(Constant.OWNER, sample.getOwner());
		contentValues.put(Constant.CREATION_EPOCH, (sample.getCreationDateTime().getTime() / 1000L));
		contentValues.put(Constant.EDITION_EPOCH, (sample.getEditionDateTime().getTime() / 1000L));
		contentValues.put(Constant.EDITOR, sample.getEditor());
		contentValues.put(Constant.ACTIVE, sample.isActive());
		contentValues.put(Constant.DATA, sample.getData());
		contentValues.put(Constant.TO_DELETE, sample.isToDelete());
		contentValues.put(Constant.TO_UPDATE, sample.isToPut());
		contentValues.put(Constant.TO_CREATE, sample.isToPost());
		contentValues.put(Constant.DATA_SET_ID, Long.toString(this.dataSyncSetId));
		return contentValues;
	}

	/**
	 * Clean the samples table from useless entries
	 */
	public void cleanTable()
	{
		String[] params = {
				Long.toString(this.dataSyncSetId)
		};
		this.database.delete(Constant.DATABASE_TABLE_SAMPLES, Constant.DATA_SET_ID + " != ?", params);
	}

	/**
	 * Retrieve all urls marked with a certain type of operation
	 *
	 * @param type
	 * @return
	 */
	private String[] getAllMarked(String type)
	{
		switch (type) {
			case Constant.TO_CREATE:
			case Constant.TO_UPDATE:
			case Constant.TO_DELETE:
				break;
			default:
				throw new FocusInternalErrorException("Invalid type for marking operation.");
		}

		Set<String> result = new HashSet<>();

		String[] cols = {
				Constant.URL
		};
		String[] params = {
				Long.toString(this.dataSyncSetId),
				type
		};
		Cursor cursor = this.database.query(
				Constant.DATABASE_TABLE_SAMPLES,
				cols,
				Constant.DATA_SET_ID + " = ? AND ? = 1",
				params,
				null,
				null,
				null
		);
		while (cursor.moveToNext()) {
			result.add(cursor.getString(cursor.getColumnIndex(Constant.URL)));
		}
		cursor.close();

		return (String[]) result.toArray();
	}

	/**
	 * Get the list of all urls marked as to be POST as an array of Strings
	 *
	 * @return
	 */
	public String[] getAllMarkedForPost()
	{
		return this.getAllMarked(Constant.TO_CREATE);
	}

	/**
	 * Get the list of all urls marked as to be PUT as an array of Strings
	 *
	 * @return
	 */
	public String[] getAllMarkedForPut()
	{
		return this.getAllMarked(Constant.TO_UPDATE);
	}

	/**
	 * Get the list of all urls marked as to be DELETEd as an array of Strings
	 *
	 * @return
	 */
	public String[] getAllMarkedForDeletion()
	{
		return this.getAllMarked(Constant.TO_DELETE);
	}

	/**
	 * Delete all entries from the database
	 * <p/>
	 * This really deletes all rows, not only the ones belong to the current data set
	 * {@code this.dataSyncSetId}
	 */
	public void deleteAll()
	{
		String sql = "DELETE FROM " + Constant.DATABASE_TABLE_SAMPLES;
		this.database.execSQL(sql);
	}

	/**
	 * Return the most recent data set identifier that exists in the database.
	 *
	 * @return the found identifier or 0 if non found
	 */
	public long getMostRecentDataSetIdentifier()
	{

		String[] cols = {
				Constant.DATA_SET_ID
		};
		Cursor cursor = this.database.query(
				Constant.DATABASE_TABLE_SAMPLES,
				cols,
				null,
				null,
				null,
				null,
				Constant.DATA_SET_ID + "DESC",
				"1"
		);

		if (cursor.getCount() == 0) {
			return 0;
		}
		cursor.moveToFirst();
		long res = cursor.getLong(cursor.getColumnIndex(Constant.DATA_SET_ID));

		cursor.close();
		return res;
	}

}

