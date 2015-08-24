package eu.focusnet.app.activity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import eu.focusnet.app.db.BookmarkLinkDao;
import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.model.data.BookmarkLink;
import eu.focusnet.app.model.data.Preference;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.NavigationUtil;

/**
 *
 */
public class BookmarkService extends IntentService {

    private static final String TAG = BookmarkService.class.getName();

    public BookmarkService() {
        super("BookmarkService");
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
        databaseAdapter.openWritableDatabase();
        PreferenceDao preferenceDao = new PreferenceDao(databaseAdapter.getDb());
        //TODO need the preference ID
        Preference foundPref = preferenceDao.findPreference(new Long(1));
        if (foundPref != null) {
            Bookmark bookmarks = foundPref.getBookmarks();
            BookmarkLinkDao bookmarkLinkDao = new BookmarkLinkDao(databaseAdapter.getDb());
            if (bookmarks != null) {
                Long bookmarkId = bookmarks.getId();
                if(isToSave) {
                    BookmarkLink bookmarkLink = new BookmarkLink(title, path, order);
                    bookmarkLinkDao.createBookmarkLing(bookmarkLink, bookmarkType, bookmarkId);
                }
                else{
                    Log.d(TAG, "It is to delete");
                    bookmarkLinkDao.deleteBookmarkLing(title, path, bookmarkType, order, bookmarkId);
                }
            }
        }
        databaseAdapter.close();
        //TODO push the context to the webservice
    }
}
