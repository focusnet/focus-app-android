package eu.focusnet.app.model.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.focusnet.app.model.util.Constant;

/**
 * Created by admin on 06.07.2015.
 */
public class DatabaseAdapter
{

	private static final int DATABASE_VERSION = 1;

	private DatabaseHelper dataBaseHelper;
	private SQLiteDatabase db;

	public DatabaseAdapter(Context context)
	{
		dataBaseHelper = new DatabaseHelper(context);
	}

	public DatabaseAdapter openWritableDatabase()
	{
		if (db == null || !db.isOpen()) {
			db = dataBaseHelper.getWritableDatabase();
		}
		return this;
	}

	public void close()
	{
		dataBaseHelper.close();
	}

	public SQLiteDatabase getDb()
	{
		return db;
	}


	/**
	 * Helper class for database management.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{

		private DatabaseHelper(Context context)
		{
			super(context, Constant.DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(Constant.CREATE_TABLE_SAMPLES_QUERY);
		}

		// FIXME TODO do something smarter for migration between versions.
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS " + Constant.DATABASE_TABLE_SAMPLES);
			onCreate(db);

			// FIXME Upgrade path should be a bit smarter.
		}

		/*
		@Override
		public void onOpen(SQLiteDatabase db)
		{
			super.onOpen(db);
			if (!db.isReadOnly()) {
				// Enable foreign key constraints
				db.execSQL("PRAGMA foreign_keys=ON;"); // no need for foreign keys as our database is flat.
			}
		}
		*/

	}
}
