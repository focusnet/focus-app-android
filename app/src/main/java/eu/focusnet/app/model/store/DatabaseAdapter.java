package eu.focusnet.app.model.store;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import eu.focusnet.app.model.util.Constant;

/**
 * The DatabaseAdapater is a facility that helps interacting with the SQLlite database.
 */
public class DatabaseAdapter
{

	private static final int DATABASE_VERSION = 1;

	private DatabaseHelper databaseHelper;
	private SQLiteDatabase db;

	/**
	 * Constructor.
	 *
	 * @param context the application context
	 */
	public DatabaseAdapter(Context context)
	{
		databaseHelper = new DatabaseHelper(context);
	}

	/**
	 * Open a database for writing
	 *
	 * @return A DatabaseAdapter to interact with the database.
	 */
	public DatabaseAdapter openWritableDatabase()
	{
		if (db == null || !db.isOpen()) {
			db = databaseHelper.getWritableDatabase();
		}
		return this;
	}

	/**
	 * Close the database
	 */
	public void close()
	{
		databaseHelper.close();
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

		/**
		 * Constructor
		 *
		 * @param context The application context
		 */
		private DatabaseHelper(Context context)
		{
			super(context, Constant.DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * When creating the database, add tables.
		 * @param db the object to work on
		 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(Constant.CREATE_TABLE_SAMPLES_QUERY);
		}

		/**
		 * Migrate from one database version to another
		 *
		 * @param db the object to work on
		 * @param oldVersion old version number of the database
		 * @param newVersion new version number of the database
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (newVersion >= oldVersion) {
				return;
			}
			switch(oldVersion) {
				case 1:
					this.upgradeFromVersion1to2(db);
				case 2:
					this.upgradeFromVersion2to3(db);
			}
		}

		/**
		 * Upgrade from version 1 to version 2
		 *
		 * @param db the object to work on
		 */
		private void upgradeFromVersion1to2(SQLiteDatabase db)
		{

		}

		/**
		 * Upgrade from version 2 to version 3
		 * @param db the object to work on
		 */
		private void upgradeFromVersion2to3(SQLiteDatabase db)
		{

		}


		/*
		// FIXME TODO YANDY: we now have a flat single table (samples), so ne need for constraints
		// on foreign keys, right? if so, let's delete this commented part.
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