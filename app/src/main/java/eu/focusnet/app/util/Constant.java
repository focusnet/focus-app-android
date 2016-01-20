package eu.focusnet.app.util;

/**
 * Constant of the application
 */

//TODO some constant might be changed to other classes on the future
public class Constant {

    public static final int FOCUS_FRAGMENT = 1,
                            BOOKMARK_FRAGMENT = 2,
                            SYNCHRONIZE_FRAGMENT = 3,
                            SETTING_FRAGMENT = 4,
                            USER_MANUAL_FRAGMENT = 5;


    public static final String NOTIFICATION_ID = "NotificationId",
                               PROJECT_ID = "ProjectId",
                               PROJECT_NAME = "ProjectName",
                               USER_DATA = "UserData",
                               PROJECT_DATA = "ProjectData",
                               USER_PREFERENCES = "UserPreferences",
                               FRAGMENT_POSITION = "FragmentPosition",
                               FRAGMENT_TITLE = "FragmentTitle",
                               BOOKMARK_TYPE = "BookmarkType",
                               IS_TO_SAVE = "isToSave";

    public static final String PAGE_PATH = "PagePath",
                               PROJECT_PATH = "ProjectPath";



    //Common for some tables
    public static final String ID = "id",
                            TYPE = "type",
                            URL = "url",
                            OWNER = "owner",
                            EDITOR = "editor",
                            CREATION_DATE_TIME = "creation_date_time",
                            EDITION_DATE_TIME = "edition_date_time",
                            VERSION = "version",
                            ACTIVE = "active",
                            TITLE = "title",
                            DESCRIPTION = "description";

    public static final String  FK_BOOKMARK_ID = "fk_bookmarks_id",
                                FK_SETTINGS_ID = "fk_settings_id",
                                FK_PAGE_ID = "fk_page_id",
                                FK_PROJECT_ID = "fk_project_id",
                                FK_APP_CONTENT_ID = "fk_app_content_id",
                                FK_WIDGET_ID = "fk_widget_id";
    // End of common

    // Table User information
    public static final String DATABASE_TABLE_USER = "users";

    public static final String FIRST_NAME = "first_name",
                            LAST_NAME = "last_name",
                            EMAIL = "email",
                            COMPANY = "company";


    public static final String CREATE_TABLE_USER_QUERY = "CREATE TABLE "+ DATABASE_TABLE_USER +""+
                            "("+ID+" INTEGER PRIMARY KEY, "+
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

    // Table Setting
    public static final String DATABASE_TABLE_SETTING = "settings";

    public static final String LANGUAGE = "language";

    public static final String CREATE_TABLE_SETTING_QUERY = "CREATE TABLE "+ DATABASE_TABLE_SETTING +""+
            "("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT," +
             LANGUAGE+" TEXT)";

    //End Settings


    // Table Bookmarks information
    public static final String DATABASE_TABLE_BOOKMARK = "bookmarks";

    public static final String DUMMY = "dummy"; //TODO

    public static final String CREATE_TABLE_BOOKMARKS_QUERY = "CREATE TABLE "+ DATABASE_TABLE_BOOKMARK +""+
                                "("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                     DUMMY+" TEXT)";

    //End table Bookmarks


    // Table BookmarkLink information
    public static final String DATABASE_TABLE_BOOKMARK_LINK = "bookmark_links";

    public static final String NAME = "name",
                                PATH = "path",
                                ORDER = "item_order",
                                BL_TYPE = "bl_type";


    public static final String CREATE_TABLE_BOOKMARK_LINK_QUERY = "CREATE TABLE "+ DATABASE_TABLE_BOOKMARK_LINK +""+
                                "("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                NAME+" TEXT, " +
            PATH +" TEXT, " +
                                ORDER+" INTEGER, " +
                                BL_TYPE+" TEXT, "+
                                FK_BOOKMARK_ID +" INTEGER,"+
                                "FOREIGN KEY ("+ FK_BOOKMARK_ID +") REFERENCES "+ DATABASE_TABLE_BOOKMARK +"("+ ID +"))";

    //End table BookmarkLink


    // Table User Preferences
    public static final String DATABASE_TABLE_PREFERENCE = "preferences";

    public static final String CREATE_TABLE_PREFERENCE_QUERY = "CREATE TABLE "+ DATABASE_TABLE_PREFERENCE +""+
            "("+ ID +" INTEGER PRIMARY KEY," +
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
            "FOREIGN KEY ("+ FK_BOOKMARK_ID+") REFERENCES "+ DATABASE_TABLE_BOOKMARK +"("+ ID +") " +
            "FOREIGN KEY ("+ FK_SETTINGS_ID+") REFERENCES "+ DATABASE_TABLE_SETTING +"("+ ID +"))";

    //End table Preferences



    // Table AppContentInstance
    public static final String DATABASE_TABLE_APP_CONTENT = "app_contents";

    public static final String CREATE_TABLE_APP_CONTENT_QUERY = "CREATE TABLE "+ DATABASE_TABLE_APP_CONTENT +""+
            "("+ ID +" INTEGER PRIMARY KEY,"+
            TYPE+" TEXT, " +
            URL+" TEXT," +
            OWNER+" TEXT," +
            EDITOR+" TEXT, " +
            CREATION_DATE_TIME+" TEXT, " +
            EDITION_DATE_TIME+" TEXT, " +
            VERSION+" INTEGER, "+
            ACTIVE+" BOOL)";
    //End table AppContentInstance


    // Table Projects
    public static final String DATABASE_TABLE_PROJECT = "projects";
    public static final String ITERATOR = "iterator";


    public static final String CREATE_TABLE_PROJECT_QUERY = "CREATE TABLE "+ DATABASE_TABLE_PROJECT +""+
            "("+ ID +" TEXT PRIMARY KEY," +
            ITERATOR+" TEXT, " +
            TITLE+" TEXT," +
            DESCRIPTION+" TEXT, " +
            ORDER+" INTEGER, "+
            FK_APP_CONTENT_ID +" INTEGER, "+
            "FOREIGN KEY ("+FK_APP_CONTENT_ID+") REFERENCES "+ DATABASE_TABLE_APP_CONTENT +"("+ ID +"))";
    //End table Projects

    // Table Pages
    public static final String DATABASE_TABLE_PAGE = "pages";

    public static final String CREATE_TABLE_PAGE_QUERY = "CREATE TABLE "+ DATABASE_TABLE_PAGE +""+
            "("+ ID +" TEXT PRIMARY KEY,"+
            TITLE+" TEXT,"+
            DESCRIPTION+" TEXT,"+
            FK_PROJECT_ID +" TEXT,"+
            "FOREIGN KEY ("+FK_PROJECT_ID+") REFERENCES "+ DATABASE_TABLE_PROJECT +"("+ ID +"))";
    //End table Pages


    // Table WidgetTemplate
    public static final String DATABASE_TABLE_WIDGET = "widgets";

    public static final String CONFIG = "config";


    public static final String CREATE_TABLE_WIDGETS_QUERY = "CREATE TABLE "+ DATABASE_TABLE_WIDGET +""+
            "("+ ID +" TEXT PRIMARY KEY,"+
            TYPE+" TEXT,"+
            CONFIG +" TEXT, " +
            FK_PROJECT_ID +" TEXT,"+
            "FOREIGN KEY ("+FK_PROJECT_ID+") REFERENCES "+ DATABASE_TABLE_PROJECT +"("+ ID +"))";
    //End table WidgetTemplate


    // Table Linker
    public static final String DATABASE_TABLE_LINKER = "linkers";

    public static final String LK_TYPE = "type",
            ITEM_ORDER = "item_order";


    public static final String CREATE_TABLE_LINKER_QUERY = "CREATE TABLE "+ DATABASE_TABLE_LINKER +""+
            "("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            ITEM_ORDER+" INTEGER,"+
            LK_TYPE+" TEXT,"+
            FK_PAGE_ID+" TEXT,"+
            FK_PROJECT_ID +" TEXT,"+
            "FOREIGN KEY ("+ FK_PAGE_ID+") REFERENCES "+ DATABASE_TABLE_PAGE +"("+ ID +") "+
            "FOREIGN KEY ("+FK_PROJECT_ID+") REFERENCES "+ DATABASE_TABLE_PROJECT +"("+ ID +"))";

    //End table Linker


    // Table WidgetLinker
    public static final String DATABASE_TABLE_WIDGET_LINKER = "widget_linkers";

    public static final String LAYOUT = "layout";

    public static final String CREATE_TABLE_WIDGET_LINKER_QUERY = "CREATE TABLE "+ DATABASE_TABLE_WIDGET_LINKER +""+
            "("+ ID +" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            ITEM_ORDER+" INTEGER,"+
            LAYOUT+" TEXT,"+
            FK_WIDGET_ID +" TEXT,"+
            FK_PAGE_ID+ " TEXT,"+
            "FOREIGN KEY ("+ FK_WIDGET_ID+") REFERENCES "+ DATABASE_TABLE_WIDGET +"("+ ID +") "+
            "FOREIGN KEY ("+ FK_PAGE_ID+") REFERENCES "+ DATABASE_TABLE_PAGE +"("+ ID +"))";


    //End table WidgetLinker

    private Constant(){}


}
