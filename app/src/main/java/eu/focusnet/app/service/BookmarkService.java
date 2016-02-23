/**
 *
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
 *
 */

package eu.focusnet.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.json.BookmarkLink;
import eu.focusnet.app.model.json.Preference;
import eu.focusnet.app.service.util.EventBus;

/**
 * This service is used to save,remove the bookmarks
 * in the database and send it to the webservice
 */
public class BookmarkService extends IntentService
{

	private static final String TAG = BookmarkService.class.getName();

	public static final String
			NAME = "name",
			PATH = "path",
			BOOKMARK_TYPE = "BookmarkType",
			IS_TO_SAVE = "isToSave";


	public BookmarkService()
	{
		super(BookmarkService.class.getName());
	}


	@Override
	protected void onHandleIntent(Intent intent)
	{
		boolean isToSave = intent.getExtras().getBoolean(IS_TO_SAVE);
		String path = intent.getStringExtra(PATH);
		String title = intent.getStringExtra(NAME);
//        int order = cronServiceIntent.getExtras().getInt(Constant.ORDER);
		String bookmarkType = intent.getStringExtra(BOOKMARK_TYPE);

		Log.d(TAG, "The path :" + path);
		Log.d(TAG, "The title :" + title);
		Log.d(TAG, "bookmark type:" + bookmarkType);

		Preference userPreference = null;
		try {
			userPreference = FocusApplication.getInstance().getDataManager().getUserPreferences();
		}
		catch (FocusMissingResourceException ex) {
			// FIXME FIXME TODO
			// kill ???
		}

//        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());
//        try {
//            databaseAdapter.openWritableDatabase();
//            PreferenceDao preferenceDao = new PreferenceDao(databaseAdapter.getDb());
//            //TODO need the preference ID
//            Preference foundPref = preferenceDao.findPreference(new Long(123));
//            if (foundPref != null) {
//                Bookmark bookmarks = foundPref.getBookmarks();
//                BookmarkLinkDao bookmarkLinkDao = new BookmarkLinkDao(databaseAdapter.getDb());
//                if (bookmarks != null) {
//                    Long bookmarkId = bookmarks.getId();
		if (isToSave) {
			BookmarkLink bookmarkLink = new BookmarkLink(title, path, 0);
			userPreference.addBookmarkLink(bookmarkLink, bookmarkType);

//            bookmarkLinkDao.createBookmarkLing(bookmarkLink, bookmarkType, bookmarkId);
		}
		else {
//            bookmarkLinkDao.deleteBookmarkLing(path, bookmarkType, bookmarkId);
			userPreference.removeBookmarkLink(path, title, bookmarkType);
		}
//                }
//            }
//            Gson gson = new Gson();
//            //TODO need the preference ID
//            foundPref = preferenceDao.findPreference(new Long(123));
//            String jsonPref = gson.toJson(foundPref);
//            DataProviderManager.updateData(UI_EXTRA_PATH, jsonPref);
		EventBus.fireBookmarksUpdate();
		FocusApplication.getInstance().getDataManager().saveUserPreferences();
//        }
//        catch(IOException e){
//            //TODO
//            e.printStackTrace();
//        }
//        finally{
//            databaseAdapter.close();
//        }
	}
}
