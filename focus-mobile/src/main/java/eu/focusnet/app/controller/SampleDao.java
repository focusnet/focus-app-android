/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.controller;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;
import java.util.HashSet;

import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.FocusInternalErrorException;

/**
 * SQLite Sample Data Access Object
 * <p/>
 * All samples being recorded belong to a specific data set id ({@link #dataSyncSetId}
 * instance variable) that is unique to each {@link DatabaseAdapter} being created. Most operations
 * are constrained on this set, hence allowing us to have different versions of the data set. This
 * is particularily useful for easily cleaning the database after data synchronisation.
 */
public class SampleDao
{
	private long dataSyncSetId;
	private String[] columnsToRetrieve = {
			Constant.Database.URL,
			Constant.Database.VERSION,
			Constant.Database.TYPE,
			Constant.Database.OWNER,
			Constant.Database.CREATION_EPOCH,
			Constant.Database.EDITION_EPOCH,
			Constant.Database.EDITOR,
			Constant.Database.ACTIVE,
			Constant.Database.DATA,
			Constant.Database.TO_DELETE,
			Constant.Database.TO_UPDATE,
			Constant.Database.TO_CREATE
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
	 * Create a new Sample based on the provided cursor. The cursor is assumed to be already at
	 * the position of the object from which we will create the Sample.
	 * <p/>
	 * We assume the query retrieved all columsn (this.columnsToRetrieve)
	 */
	private static Sample buildSampleFromCursor(Cursor cursor)
	{
		Sample sample = new Sample();
		sample.setUrl(cursor.getString(cursor.getColumnIndex(Constant.Database.URL)));
		sample.setVersion(cursor.getInt(cursor.getColumnIndex(Constant.Database.VERSION)));
		sample.setType(cursor.getString(cursor.getColumnIndex(Constant.Database.TYPE)));
		sample.setOwner(cursor.getString(cursor.getColumnIndex(Constant.Database.OWNER)));
		sample.setEditor(cursor.getString(cursor.getColumnIndex(Constant.Database.EDITOR)));
		sample.setData(cursor.getString(cursor.getColumnIndex(Constant.Database.DATA)));

		int creationDateTime = cursor.getInt(cursor.getColumnIndex(Constant.Database.CREATION_EPOCH));
		int editionDateTime = cursor.getInt(cursor.getColumnIndex(Constant.Database.EDITION_EPOCH));

		Date creationDate = new Date(creationDateTime * 1000L);
		Date editionDate = new Date(editionDateTime * 1000L);

		sample.setCreationDateTime(creationDate);
		sample.setEditionDateTime(editionDate);

		sample.setActive(cursor.getInt(cursor.getColumnIndex(Constant.Database.ACTIVE)) > 0);
		sample.setToDelete(cursor.getInt(cursor.getColumnIndex(Constant.Database.TO_DELETE)) > 0);
		sample.setToUpdate(cursor.getInt(cursor.getColumnIndex(Constant.Database.TO_UPDATE)) > 0);

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
		return database.insert(Constant.Database.DATABASE_TABLE_SAMPLES, null, contentValues);
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
				Constant.Database.DATABASE_TABLE_SAMPLES,
				this.columnsToRetrieve,
				Constant.Database.DATA_SET_ID + " = ? AND " + Constant.Database.URL + " = ? AND "
						+ Constant.Database.TO_DELETE + " = 0 AND " + Constant.Database.ACTIVE + " = 1",
				params,
				null,
				null,
				Constant.Database.VERSION + " DESC, " + Constant.Database.EDITION_EPOCH + " DESC",
				"1");
		Sample s = null;
		if (cursor.moveToFirst()) {
			s = SampleDao.buildSampleFromCursor(cursor);
		}
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
		String where = Constant.Database.DATA_SET_ID + " = ? AND " + Constant.Database.URL + "= ?";
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(Constant.Database.TO_DELETE, true);
		updatedValues.put(Constant.Database.EDITION_EPOCH, new Date().getTime() / 1000L);
		this.database.update(Constant.Database.DATABASE_TABLE_SAMPLES, updatedValues, where, params);
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
		return this.database.delete(
				Constant.Database.DATABASE_TABLE_SAMPLES,
				Constant.Database.DATA_SET_ID + " = ? AND " + Constant.Database.URL + "= ? ", params
		) > 0;
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
		String where = Constant.Database.DATA_SET_ID + " = ? AND " + Constant.Database.URL + "=?";
		ContentValues updatedValues = this.createContentValues(sample);
		this.database.update(Constant.Database.DATABASE_TABLE_SAMPLES, updatedValues, where, params);
	}

	/**
	 * Change the current data set id for another
	 */
	public void udpateDataSetId(long newId)
	{
		String[] params = {
				Long.toString(this.dataSyncSetId)
		};
		String where = Constant.Database.DATA_SET_ID + " = ?";
		ContentValues updateValues = new ContentValues();
		updateValues.put(Constant.Database.DATA_SET_ID, Long.toString(newId));
		this.database.update(Constant.Database.DATABASE_TABLE_SAMPLES, updateValues, where, params);
		this.dataSyncSetId = newId;
	}

	/**
	 * Create content values for a row
	 */
	private ContentValues createContentValues(Sample sample)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.Database.URL, sample.getUrl());
		contentValues.put(Constant.Database.VERSION, sample.getVersion());
		contentValues.put(Constant.Database.TYPE, sample.getType());
		contentValues.put(Constant.Database.OWNER, sample.getOwner());
		contentValues.put(Constant.Database.CREATION_EPOCH, (sample.getCreationDateTime().getTime() / 1000L));
		contentValues.put(Constant.Database.EDITION_EPOCH, (sample.getEditionDateTime().getTime() / 1000L));
		contentValues.put(Constant.Database.EDITOR, sample.getEditor());
		contentValues.put(Constant.Database.ACTIVE, sample.isActive());
		contentValues.put(Constant.Database.DATA, sample.getData());
		contentValues.put(Constant.Database.TO_DELETE, sample.isToDelete());
		contentValues.put(Constant.Database.TO_UPDATE, sample.isToPut());
		contentValues.put(Constant.Database.TO_CREATE, sample.isToPost());
		contentValues.put(Constant.Database.DATA_SET_ID, Long.toString(this.dataSyncSetId));
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
		this.database.delete(Constant.Database.DATABASE_TABLE_SAMPLES, Constant.Database.DATA_SET_ID + " != ?", params);
	}

	/**
	 * Retrieve all urls marked with a certain type of operation
	 * <p/>
	 * We only manipulate FocusSamples here, not other types.
	 *
	 * @param flagType
	 * @return
	 */
	public HashSet<Sample> getAllMarkedFocusSamples(String flagType)
	{
		switch (flagType) {
			case Constant.Database.TO_CREATE:
			case Constant.Database.TO_UPDATE:
			case Constant.Database.TO_DELETE:
				break;
			default:
				throw new FocusInternalErrorException("Invalid type for marking operation.");
		}

		HashSet<Sample> result = new HashSet<>();

		String[] params = {
				Long.toString(this.dataSyncSetId),
				//	flagType,
				Constant.DataModelTypes.FOCUS_DATA_MODEL_TYPE_FOCUS_SAMPLE.replaceFirst("[^/]+$", "") + "%"// remove the version specifier
		};
		Cursor cursor = this.database.query(
				Constant.Database.DATABASE_TABLE_SAMPLES,
				this.columnsToRetrieve,
				Constant.Database.DATA_SET_ID + " = ? AND " + flagType + " = 1 AND type LIKE ?", // here does not work if not like this (no placeholder). to check
				params,
				null,
				null,
				null
		);

		while (cursor.moveToNext()) {
			Sample s = SampleDao.buildSampleFromCursor(cursor);
			result.add(s);
		}
		cursor.close();

		return result;
	}

	/**
	 * Delete all entries from the database
	 * <p/>
	 * This really deletes all rows, not only the ones belong to the current data set
	 * {@code this.dataSyncSetId}
	 */
	public void deleteAll()
	{
		String sql = "DELETE FROM " + Constant.Database.DATABASE_TABLE_SAMPLES;
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
				Constant.Database.DATA_SET_ID
		};
		Cursor cursor = this.database.query(
				Constant.Database.DATABASE_TABLE_SAMPLES,
				cols,
				null,
				null,
				null,
				null,
				Constant.Database.DATA_SET_ID + " DESC",
				"1"
		);

		long res = 0;
		if (cursor.getCount() != 0) {
			cursor.moveToFirst();
			res = cursor.getLong(cursor.getColumnIndex(Constant.Database.DATA_SET_ID));
		}
		cursor.close();
		return res;
	}

}

