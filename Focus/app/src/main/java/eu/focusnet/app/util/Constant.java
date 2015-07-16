package eu.focusnet.app.util;

/**
 * Created by admin on 24.06.2015.
 */
public class Constant {

    public static final int PROJECT_FRAGMENT = 1,
                            BOOKMARK_FRAGMENT = 2,
                            SYNCHRONIZE_FRAGMENT = 3,
                            SETTING_FRAGMENT = 4,
                            USER_MANUAL_FRAGMENT = 5;


    public static final String NOTIFICATION_ID = "NotificationID",
                               USER_DATA = "UserData",
                               USER_PREFERENCES = "UserPreferences";


    //Common for some tables
    public static final String TYPE = "type",
                            URL = "url",
                            OWNER = "owner",
                            EDITOR = "editor",
                            CREATION_DATE_TIME = "creation_date_time",
                            EDITION_DATE_TIME = "edition_date_time",
                            VERSION = "version",
                            ACTIVE = "active";

    public static final String  FK_BOOKMARK_ID = "fk_bookmarks_id",
                                 FK_SETTINGS_ID = "fk_settings_id";
    // End of common

    // Table User information
    public static final String DATABASE_TABLE_USER = "users";

    public static final String USER_ID = "id",
                            FIRST_NAME = "first_name",
                            LAST_NAME = "last_name",
                            EMAIL = "email",
                            COMPANY = "company";


    public static final String CREATE_TABLE_USER_QUERY = "CREATE TABLE "+ DATABASE_TABLE_USER +""+
                            "("+USER_ID+" INTEGER PRIMARY KEY, "+
                               FIRST_NAME+" TEXT,"+
                               LAST_NAME+" TEXT,"+
                               EMAIL+" TEXT,"+
                               COMPANY+" TEXT,"+
                               TYPE+" TEXT, " +
                               URL+" TEXT," +
                               OWNER+" TEXT," +
                               EDITOR+" TEXT, " +
                              CREATION_DATE_TIME+" TEXT, " +
                              EDITION_DATE_TIME+" TEXT, " +
                              VERSION+" INTEGER, "+
                              ACTIVE+" BOOL)";

    //End table User

    // Table Setting Preferences
    public static final String DATABASE_TABLE_SETTING = "settings";

    public static final String SETTING_ID = "id",
                               LANGUAGE = "language";

    public static final String CREATE_TABLE_SETTING_QUERY = "CREATE TABLE "+ DATABASE_TABLE_SETTING +""+
            "("+ SETTING_ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
             LANGUAGE+" TEXT)";

    //End Settings Preferences


    // Table Bookmarks information
    public static final String DATABASE_TABLE_BOOKMARK = "bookmarks";

    public static final String BOOKMARK_ID = "id",
                                        DUMMY = "dummy"; //TODO

    public static final String CREATE_TABLE_BOOKMARKS_QUERY = "CREATE TABLE "+ DATABASE_TABLE_BOOKMARK +""+
                                "("+ BOOKMARK_ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                     DUMMY+" TEXT)";

    //End table Bookmarks


    // Table BookmarkLink information
    public static final String DATABASE_TABLE_BOOKMARK_LINK = "bookmark_links";

    public static final String BOOKMARK_LINK_ID = "id",
                                                    NAME = "name",
                                                    PATH = "path",
                                                    ORDER = "item_order",
                                                    BL_TYPE = "bl_type";


    public static final String CREATE_TABLE_BOOKMARK_LINK_QUERY = "CREATE TABLE "+ DATABASE_TABLE_BOOKMARK_LINK +""+
                                "("+BOOKMARK_LINK_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                NAME+" TEXT, " +
                                PATH+" TEXT, " +
                                ORDER+" INTEGER, " +
                                BL_TYPE+" TEXT, "+
                                FK_BOOKMARK_ID +" INTEGER,"+
                                "FOREIGN KEY ("+ FK_BOOKMARK_ID +") REFERENCES "+ DATABASE_TABLE_BOOKMARK +"("+ BOOKMARK_ID +") ON DELETE CASCADE)";

    //End table BookmarkLink


    // Table User Preferences
    public static final String DATABASE_TABLE_PREFERENCE = "preferences";
    public static final String PREFERENCE_ID = "id";

    public static final String CREATE_TABLE_PREFERENCE_QUERY = "CREATE TABLE "+ DATABASE_TABLE_PREFERENCE +""+
            "("+ PREFERENCE_ID +" INTEGER PRIMARY KEY," +
            TYPE+" TEXT, " +
            URL+" TEXT," +
            OWNER+" TEXT," +
            EDITOR+" TEXT, " +
            CREATION_DATE_TIME+" TEXT, " +
            EDITION_DATE_TIME+" TEXT, " +
            VERSION+" INTEGER, "+
            ACTIVE+" BOOL, " +
            FK_BOOKMARK_ID +" INTEGER,"+
            FK_SETTINGS_ID +" INTEGER,"+
            "FOREIGN KEY ("+ FK_BOOKMARK_ID+") REFERENCES "+ DATABASE_TABLE_BOOKMARK +"("+ BOOKMARK_ID +") ON DELETE CASCADE," +
            "FOREIGN KEY ("+ FK_SETTINGS_ID+") REFERENCES "+ DATABASE_TABLE_SETTING +"("+ SETTING_ID +") ON DELETE CASCADE)";

    //End table Preferences


    private Constant(){}
}
