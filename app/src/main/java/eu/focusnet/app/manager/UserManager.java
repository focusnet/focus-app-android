package eu.focusnet.app.manager;

import android.database.sqlite.SQLiteDatabase;

import eu.focusnet.app.db.UserDao;
import eu.focusnet.app.model.data.User;

/**
 * Created by admin on 02.09.2015.
 */
public class UserManager {

    private UserDao userDao;

    public UserManager(SQLiteDatabase database){
        userDao  = new UserDao(database);
    }

    public Long saveUser(User user){
        return userDao.createUser(user);
    }
}
