package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import eu.focusnet.app.db.LinkerDao;
import eu.focusnet.app.model.data.Linker;

/**
 * Created by admin on 08.09.2015.
 */
public class LinkerManager {

    private LinkerDao linkerDao;

    public LinkerManager(SQLiteDatabase database){
        linkerDao = new LinkerDao(database);
    }

    public Long createLinker(Linker linker, String fkProjectID, LinkerDao.LINKER_TYPE type){
        return linkerDao.createLinker(linker, fkProjectID, type);
    }

    public ArrayList<Linker> findLinkers(String fkProjectID, LinkerDao.LINKER_TYPE type){
        return linkerDao.findLinkers(fkProjectID, type);
    }
}
