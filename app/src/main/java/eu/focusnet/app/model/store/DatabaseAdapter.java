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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.focusnet.app.model.util.Constant;

/**
 * The DatabaseAdapater is a facility that helps interacting with the SQLlite database.
 */
public class DatabaseAdapter
{

	private static final int DATABASE_VERSION = 1;

	private DatabaseHelper databaseHelper;
	private SQLiteDatabase db;
	private Object sampleDao;

	/**
	 * A unique identifier for that instance, based on time.
	 *
	 * This identifier will be used to identify the different sets of data coming from backends
	 * At each data sync, we create a new DataManager that will replace the old one on success,
	 * and each of these DataManagers contains a single DatabaseAdapter. Therefore, by
	 * identifying data sync sets with this unique ID, we are able to distinguish the differents
	 * syncs. It makes local db cleaning easier, as it then only consists in deleting entries that
	 * do not match this unique ID.
	 *
	 * When updating/creating/deletion entries (PUT,POST,DELETE), we still use the same ID. This
	 * is not an incremental version number, but only an instance identifier.
	 */
	final private long uniqueInstanceIdentifier;


	/**
	 * Constructor.
	 *
	 * @param context the application context
	 */
	public DatabaseAdapter(Context context)
	{
		databaseHelper = new DatabaseHelper(context);

		String time = Long.toString(System.currentTimeMillis());

		// replace 4 first digits by a random number for more randomness
		Pattern p = Pattern.compile("^\\d{3}");
		Matcher matcher = p.matcher(time);
		int rand = new Random().nextInt(1_000);
		this.uniqueInstanceIdentifier = Long.parseLong(matcher.replaceAll(Integer.toString(rand)));
	}


	/**
	 * Open a database for writing
	 *
	 * @return A DatabaseAdapter to interact with the database.
	 */
	private SQLiteDatabase openWritableDatabase()
	{
		if (this.db == null || !this.db.isOpen()) {
			this.db = databaseHelper.getWritableDatabase();
		}
		return this.db;
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
		return this.db;
	}

	public SampleDao getSampleDao()
	{
		return new SampleDao(this.openWritableDatabase(), this.uniqueInstanceIdentifier);
	}

	/**
	 * Get the databse size
	 */
	public long getDatabaseSize()
	{
		return new File(this.db.getPath()).length();
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
		 * When creating the database, register tables.
		 *
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
