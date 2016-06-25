/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.service.datastore;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.Random;

import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

/**
 * The DatabaseAdapater is a facility that helps interacting with the SQLlite database.
 */
public class DatabaseAdapter
{


	private DatabaseHelper databaseHelper;
	private SQLiteDatabase db;

	/**
	 * A unique identifier for that instance, based on time.
	 * <p/>
	 * This identifier will be used to identify the different sets of data coming from backends
	 * At each data sync, we create a new DataManager that will replace the old one on success,
	 * and each of these DataManagers contains a single DatabaseAdapter. Therefore, by
	 * identifying data sync sets with this unique ID, we are able to distinguish the differents
	 * syncs. It makes local db cleaning easier, as it then only consists in deleting entries that
	 * do not match this unique ID.
	 * <p/>
	 * When updating/creating/deletion entries (PUT,POST,DELETE), we still use the same ID. This
	 * is not an incremental version number, but only an instance identifier.
	 *
	 *
	 * The databaseAdapter is initially created with a low uniqueInstanceIdentifier and data will
	 * created with identifier. This means that they will NOT be considered as the latest data set.
	 * For the data loaded under this id to become the latest data set, call makeDataPersistent()
	 */
	private long uniqueInstanceIdentifier;
	/**
	 * Use to avoid concurrency issues
	 */
	private int numberOfConsumers;

	/**
	 * Constructor.
	 *
	 */
	public DatabaseAdapter()
	{
		this.numberOfConsumers = 0;
		this.databaseHelper = new DatabaseHelper();

		// generate a unique ID for data generated by this db adapter
		this.uniqueInstanceIdentifier = DatabaseHelper.generateOldDataSetIdentifier();
	}


	/**
	 * Open a database for writing
	 *
	 * @return A DatabaseAdapter to interact with the database.
	 */
	private synchronized SQLiteDatabase openWritableDatabase()
	{
		if (this.db == null || !this.db.isOpen()) {
			this.db = databaseHelper.getWritableDatabase();
		}
		++this.numberOfConsumers;
		return this.db;
	}

	/**
	 * Close the database
	 */
	public synchronized void close()
	{
		--this.numberOfConsumers;
		if (this.numberOfConsumers == 0) {
			databaseHelper.close();
		}
	}

	/**
	 * Helper function to obtain a Sample Data Access Object
	 *
	 * @return
	 */
	public SampleDao getSampleDao()
	{
		return new SampleDao(this.openWritableDatabase(), this.uniqueInstanceIdentifier);
	}

	/**
	 * Get the databse size (whole database, not only entries with the current data set id)
	 */
	public long getDatabaseSize()
	{
		return new File(this.db.getPath()).length();
	}

	/**
	 * use the most recent data set identifier in the database, or continue to use the one of the
	 * current object if none was found.
	 */
	public void useExistingDataSet()
	{
		try {
			long foundIdentifier = this.getSampleDao().getMostRecentDataSetIdentifier();
			if (foundIdentifier != 0) {
				this.uniqueInstanceIdentifier = foundIdentifier;
			}
		}
		finally {
			this.close();
		}
	}

	/**
	 *
	 */
	public void makeDataPersistent()
	{
		try {
			long newId = DatabaseHelper.generateNewDataSetIdentifier();
			SampleDao dao = this.getSampleDao();
			// this method also updates the DAO's data set id
			dao.udpateDataSetId(newId);

			this.uniqueInstanceIdentifier = newId;
		}
		finally {
			this.close();
		}
	}


	/**
	 * Helper class for database management.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		/**
		 * Constructor
		 */
		private DatabaseHelper()
		{
			super(ApplicationHelper.getApplicationContext(), Constant.Database.DATABASE_NAME, null, Constant.Database.DATABASE_VERSION);
		}

		/**
		 * // Generate by a random number for more randomness
		 * // however the time information is useful for knowing which dataset was the last one
		 * // so we add randomness to the end
		 * // 123456789 -> 456789<random>
		 * // remove first 3 numbers
		 *
		 * @return
		 */
		public static long generateNewDataSetIdentifier()
		{
			// get a somehow random number between 1000 and 9999
			int rand = 1_000 + new Random().nextInt(9_000);
			String timeAndRand = Long.toString(System.currentTimeMillis()) + Integer.toString(rand);
			return Long.parseLong(timeAndRand);
		}

		/**
		 * Idem , but generate a small number, which will be considered as an old data set.
		 * This is used for temporarily saving new data before committing them when the data set
		 * is complete by updating to a "new" data set id.
		 * @return
		 */
		public static long generateOldDataSetIdentifier()
		{
			return (new Random().nextLong()) % 1_000_000L;
		}

		/**
		 * When creating the database, register tables.
		 *
		 * @param db the object to work on
		 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			db.execSQL(Constant.Database.CREATE_TABLE_SAMPLES_QUERY);
		}

		/**
		 * Migrate from one database version to another
		 *
		 * @param db         the object to work on
		 * @param oldVersion old version number of the database
		 * @param newVersion new version number of the database
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (newVersion >= oldVersion) {
				return;
			}
			switch (oldVersion) {
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
		 *
		 * @param db the object to work on
		 */
		private void upgradeFromVersion2to3(SQLiteDatabase db)
		{

		}


	}
}
