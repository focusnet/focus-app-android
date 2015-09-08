package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.db.PreferenceDao;
import eu.focusnet.app.model.data.Preference;

/**
 * Created by admin on 02.09.2015.
 */
public class PreferenceManager {

    PreferenceDao preferenceDAO;

    public PreferenceManager(SQLiteDatabase database){
        preferenceDAO = new PreferenceDao(database);
    }

    public Long savePreference(Preference preference){
        return preferenceDAO.createPreference(preference);
    }

    public Preference findPreference(Long preferenceId){
        return preferenceDAO.findPreference(preferenceId);
    }
}
