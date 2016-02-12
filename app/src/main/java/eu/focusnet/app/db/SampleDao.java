package eu.focusnet.app.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.focusnet.app.model.json.Sample;
import eu.focusnet.app.util.Constant;

/**
 * Created by admin on 21.01.2016.
 */
public class SampleDao
{

	private String[] columnsToRetrieve = {Constant.ID, Constant.URL, Constant.VERSION, Constant.TYPE, Constant.OWNER, Constant.CREATION_DATE_TIME,
			Constant.EDITION_DATE_TIME, Constant.EDITOR, Constant.ACTIVE, Constant.DATA, Constant.TO_DELETE, Constant.TO_PUSH};

	private SQLiteDatabase database;

	public SampleDao(SQLiteDatabase database)
	{
		this.database = database;
	}

	public Long createSample(Sample sample)
	{
		ContentValues contentValues = createContentValues(sample);
		return database.insert(Constant.DATABASE_TABLE_SAMPLES, null, contentValues);
	}

	public Sample findSample(Long sampleId)
	{
		Sample sample = null;
		String[] params = {String.valueOf(sampleId)};
		Cursor cursor = database.query(Constant.DATABASE_TABLE_SAMPLES, columnsToRetrieve, Constant.ID + "=?", params, null, null, null);

		if (cursor != null) {
			cursor.moveToFirst();
			sample = new Sample();
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
				sample.setCreationDateTime(creationDate);
				sample.setEditionDateTime(editionDate);
			}
			catch (ParseException e) {
				//TODO
			}

			sample.setActive(cursor.getInt(cursor.getColumnIndex(Constant.ACTIVE)) > 0);
			sample.setToDelete(cursor.getInt(cursor.getColumnIndex(Constant.TO_DELETE)) > 0);
			sample.setToPush(cursor.getInt(cursor.getColumnIndex(Constant.TO_PUSH)) > 0);
			cursor.close();
		}

		return sample;
	}

	public boolean deleteSample(Long sampleId)
	{
		String[] params = {String.valueOf(sampleId)};
		return database.delete(Constant.DATABASE_TABLE_SAMPLES, Constant.ID + "=?", params) > 0;
	}

	public void updateSample(Sample sample)
	{
		String where = Constant.ID + "=" + sample.getId();
		ContentValues updatedValues = createContentValues(sample);
		//TODO this call may be needed
		//updatedValues.remove(Constant.ID);
		//
		this.database.update(Constant.DATABASE_TABLE_SAMPLES, updatedValues, where, null);
	}

	private ContentValues createContentValues(Sample sample)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(Constant.ID, sample.getId());
		contentValues.put(Constant.URL, sample.getUrl());
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
		return contentValues;
	}
}

