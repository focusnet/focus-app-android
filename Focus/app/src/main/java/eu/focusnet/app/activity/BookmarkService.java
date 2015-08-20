package eu.focusnet.app.activity;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import eu.focusnet.app.db.DatabaseAdapter;
import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.model.data.Bookmark;
import eu.focusnet.app.model.data.Preference;

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
//        String title = intent.getStringExtra("title");
//        String path = intent.getStringExtra("path");
//        Log.d(TAG, "The title :"+title);
//        Log.d(TAG, "The path :"+path);

        Log.d(TAG, "The path");

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getApplicationContext());
        databaseAdapter.openWritableDatabase();
        PreferenceDao preferenceDao = new PreferenceDao(databaseAdapter.getDb());
        //TODO need the preference ID
        Preference foundPref = preferenceDao.findPreference(new Long(1));
        if(foundPref != null){
            Bookmark bookmarks = foundPref.getBookmarks();
     //       bookmarks.getPages().add(new BookmarkLink(title, path));
        }

    }
}
