package eu.focusnet.app.model.util;

/**
 * Created by julien on 2/12/16.
 */
public class Constant
{

	public static final String DATABASE_NAME = "Focus_DB";

	public static final String DATABASE_TABLE_SAMPLES = "samples";

	public static final String ID = "id",
			TYPE = "type",
			URL = "url",
			CONTEXT = "context",
			OWNER = "owner",
			EDITOR = "editor",
			CREATION_DATE_TIME = "creation_date_time",
			EDITION_DATE_TIME = "edition_date_time",
			VERSION = "version",
			ACTIVE = "active",
			DATA = "data",
			TO_DELETE = "toDelete",
			TO_PUSH = "toPush",
			TO_POST = "toPost";

	public static final String CREATE_TABLE_SAMPLES_QUERY = "CREATE TABLE " + DATABASE_TABLE_SAMPLES + "" +
			"(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
			URL + " TEXT, " +
			CONTEXT + " TEXT, " +
			VERSION + " INTEGER, " +
			TYPE + " TEXT, " +
			OWNER + " TEXT, " +
			CREATION_DATE_TIME + " DATETIME , " +
			EDITION_DATE_TIME + " DATETIME ," +
			EDITOR + " TEXT, " +
			DATA + " TEXT, " +
			TO_DELETE + " BOOL, " +
			TO_PUSH + " BOOL, " +
			TO_POST + " BOOL," +
			ACTIVE + " BOOL NOT NULL DEFAULT TRUE," +
			"UNIQUE(" + Constant.URL + ", " + Constant.VERSION + ") ON CONFLICT REPLACE)";

	public static final String PATH_SEPARATOR = "|",
			PATH_SEPARATOR_PATTERN = "\\|",
			PATH_SELECTOR_OPEN = "[",
			PATH_SELECTOR_CLOSE = "]";

	public static final String SELECTOR_SERVICE_OPEN = "<",
			SELECTOR_SERVICE_CLOSE = ">",
			SELECTOR_SERVICE_CTX = "ctx",
			SELECTOR_SERVICE_HISTORY = "history",
			SELECTOR_SERVICE_LOOKUP = "lookup",
		//	SELECTOR_SERVICE_SEPARATOR_OUTER = "|",
			SELECTOR_SERVICE_SEPARATOR_OUTER_PATTERN = "\\|",
			SELECTOR_SERVICE_SEPARATOR_INNER = "/";
}
