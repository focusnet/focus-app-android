package eu.focusnet.app.model.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusNotImplementedException;
import eu.focusnet.app.model.util.Constant;

/**
 * SQL Sample Data Access Object
 */
public class SampleDao
{

	private String[] columnsToRetrieve = {
			Constant.ID,
			Constant.URL,
			Constant.CONTEXT,
			Constant.VERSION,
			Constant.TYPE,
			Constant.OWNER,
			Constant.CREATION_DATE_TIME,
			Constant.EDITION_DATE_TIME,
			Constant.EDITOR,
			Constant.ACTIVE,
			Constant.DATA,
			Constant.TO_DELETE,
			Constant.TO_PUSH,
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
				Constant.VERSION + " DESC, " + Constant.ID + " DESC",
				"1");
		return SampleDao.buildSampleFromCursor(cursor);
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
		sample.setId(cursor.getLong(cursor.getColumnIndex(Constant.ID)));
		sample.setUrl(cursor.getString(cursor.getColumnIndex(Constant.URL)));
		sample.setVersion(cursor.getInt(cursor.getColumnIndex(Constant.VERSION)));
		sample.setType(cursor.getString(cursor.getColumnIndex(Constant.TYPE)));
		sample.setOwner(cursor.getString(cursor.getColumnIndex(Constant.OWNER)));
		sample.setEditor(cursor.getString(cursor.getColumnIndex(Constant.EDITOR)));
		sample.setData(cursor.getString(cursor.getColumnIndex(Constant.DATA)));

		String creationDateTime = cursor.getString(cursor.getColumnIndex(Constant.CREATION_DATE_TIME));
		String editionDateTime = cursor.getString(cursor.getColumnIndex(Constant.EDITION_DATE_TIME));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date creationDate = null;
		Date editionDate = null;
		try {
			creationDate = dateFormat.parse(creationDateTime);
			editionDate = dateFormat.parse(editionDateTime);
		}
		catch (ParseException e) {
			// let's me consilient, and default these dates with the current timestamp
			FocusApplication.reportError(e);
			if (creationDate == null) {
				creationDate = new Date();
			}
			if (editionDate == null) {
				editionDate = new Date();
			}
		}
		sample.setCreationDateTime(creationDate);
		sample.setEditionDateTime(editionDate);

		sample.setActive(cursor.getInt(cursor.getColumnIndex(Constant.ACTIVE)) > 0);
		sample.setToDelete(cursor.getInt(cursor.getColumnIndex(Constant.TO_DELETE)) > 0);
		sample.setToPush(cursor.getInt(cursor.getColumnIndex(Constant.TO_PUSH)) > 0);
		cursor.close();

		return sample;
	}

	/**
	 * Mark a row for deletion
	 */
	public void markForDeletion(String url)
	{
		String[] params = {
				url
		};
		String where = Constant.URL + "= ?";
		ContentValues updatedValues = new ContentValues();
		updatedValues.put(Constant.TO_DELETE, true);
		this.database.update(Constant.DATABASE_TABLE_SAMPLES, updatedValues, where, params);
	}

	/**
	 * Actually delete a row
	 */
	public boolean delete(String url)
	{
		String[] params = {
				url
		};
		return this.database.delete(Constant.DATABASE_TABLE_SAMPLES, Constant.URL + "= ? ", params) > 0;
	}

	public void update(Sample sample)
	{
		String where = Constant.ID + "=" + sample.getId();
		ContentValues updatedValues = this.createContentValues(sample);
		//TODO this call may be needed
		//updatedValues.remove(Constant.ID);
		//
		this.database.update(Constant.DATABASE_TABLE_SAMPLES, updatedValues, where, null);
	}

	/**
	 * Create content values for a row
	 */
	private ContentValues createContentValues(Sample sample)
	{
		ContentValues contentValues = new ContentValues();
//		contentValues.put(Constant.ID, sample.getId());
		contentValues.put(Constant.URL, sample.getUrl());
		contentValues.put(Constant.CONTEXT, sample.getContext());
		contentValues.put(Constant.VERSION, sample.getVersion());
		contentValues.put(Constant.TYPE, sample.getType());
		contentValues.put(Constant.OWNER, sample.getOwner());
		contentValues.put(Constant.CREATION_DATE_TIME, sample.getCreationDateTime().toString());
		contentValues.put(Constant.EDITION_DATE_TIME, sample.getEditionDateTime().toString());
		contentValues.put(Constant.EDITOR, sample.getEditor());
		contentValues.put(Constant.ACTIVE, sample.isActive());
		contentValues.put(Constant.DATA, sample.getData());
		contentValues.put(Constant.TO_DELETE, sample.isToDelete());
		contentValues.put(Constant.TO_PUSH, sample.isToPush());
		contentValues.put(Constant.TO_POST, sample.isToPost());
		return contentValues;
	}

	/**
	 * Clean the samples table from useless entries.
	 *
	 */
	public void cleanTable()
	{
		// will use the DataContext's coming from DataManager
		throw new FocusNotImplementedException("cleanTable");
	}
}

