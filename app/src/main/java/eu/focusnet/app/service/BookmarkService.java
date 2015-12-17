package eu.focusnet.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.gson.Gson;
import java.io.IOException;

import eu.focusnet.app.db.BookmarkLinkDao;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.fragment.BookmarkFragment;
import eu.focusnet.app.manager.DataProviderManager;
import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.EventBus;

/**
 * This service is used to save,remove the bookmarks
 * in the database and send it to the webservice
 */
public class BookmarkService extends IntentService {

    private static final String TAG = BookmarkService.class.getName();
    //TODO this path should be in the user data JSON response after the user is authenticated
    private static final String PATH = "http://focus.yatt.ch/resources-server/data/user/123/app-user-preferences";

    public BookmarkService() {
        super(BookmarkService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        boolean isToSave = intent.getExtras().getBoolean(Constant.IS_TO_SAVE);
        String path = intent.getStringExtra(Constant.PATH);
        String title = intent.getStringExtra(Constant.NAME);
        int order = intent.getExtras().getInt(Constant.ORDER);
        String bookmarkType = intent.getStringExtra(Constant.BOOKMARK_TYPE);

        Log.d(TAG, "The path :" + path);
        Log.d(TAG, "The title :" + title);
        Log.d(TAG, "bookmark type:" + bookmarkType);

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());
        try {
            databaseAdapter.openWritableDatabase();
            PreferenceDao preferenceDao = new PreferenceDao(databaseAdapter.getDb());
            //TODO need the preference ID
            Preference foundPref = preferenceDao.findPreference(new Long(123));
            if (foundPref != null) {
                Bookmark bookmarks = foundPref.getBookmarks();
                BookmarkLinkDao bookmarkLinkDao = new BookmarkLinkDao(databaseAdapter.getDb());
                if (bookmarks != null) {
                    Long bookmarkId = bookmarks.getId();
                    if (isToSave) {
                        BookmarkLink bookmarkLink = new BookmarkLink(title, path, order);
                        bookmarkLinkDao.createBookmarkLing(bookmarkLink, bookmarkType, bookmarkId);
                    } else {
                        bookmarkLinkDao.deleteBookmarkLing(path, bookmarkType, bookmarkId);
                    }
                }
            }
            Gson gson = new Gson();
            //TODO need the preference ID
            foundPref = preferenceDao.findPreference(new Long(123));
            String jsonPref = gson.toJson(foundPref);
            DataProviderManager.updateData(PATH, jsonPref);
            EventBus.fireBookmarksUpdate();
        }
        catch (IOException e) {
            //TODO
            e.printStackTrace();
        }
        finally {
            databaseAdapter.close();
        }
    }
}
