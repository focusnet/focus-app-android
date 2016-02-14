package eu.focusnet.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

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
//        int order = intent.getExtras().getInt(Constant.ORDER);
		String bookmarkType = intent.getStringExtra(BOOKMARK_TYPE);

		Log.d(TAG, "The path :" + path);
		Log.d(TAG, "The title :" + title);
		Log.d(TAG, "bookmark type:" + bookmarkType);

		Preference userPreference = DataManager.getInstance().getUserPreferences();

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
		DataManager.getInstance().saveUserPreferences();
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
