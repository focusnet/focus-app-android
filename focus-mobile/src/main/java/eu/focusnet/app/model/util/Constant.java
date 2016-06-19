/**
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.util;

/**
 * Model-related constants
 */
public class Constant
{

	public static final String FOCUS_DATA_MODEL_TYPE_USER = "http://reference.focusnet.eu/schemas/focus-user-information/v1.0",
			FOCUS_DATA_MODEL_TYPE_USER_PREFERENCES = "http://reference.focusnet.eu/schemas/focus-mobile-app-user-preferences/v1.0",
			FOCUS_DATA_MODEL_TYPE_FOCUS_SAMPLE = "http://reference.focusnet.eu/schemas/focus-data-sample/v1.0";

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"; // ISO 8601

	public static final String DATABASE_NAME = "focus";

	public static final String DATABASE_TABLE_SAMPLES = "sample";

	public static final String TYPE = "type",
			URL = "url",
			OWNER = "owner",
			EDITOR = "editor",
			CREATION_EPOCH = "creation_date_time",
			EDITION_EPOCH = "edition_date_time",
			VERSION = "version",
			ACTIVE = "active",
			DATA = "data",
			TO_DELETE = "to_delete",
			TO_UPDATE = "to_update",
			TO_CREATE = "to_create",
			DATA_SET_ID = "data_set_id";

	public static final String CREATE_TABLE_SAMPLES_QUERY = "CREATE TABLE " + DATABASE_TABLE_SAMPLES + "" +
			"(" + URL + " TEXT, " +
			VERSION + " INTEGER, " +
			TYPE + " TEXT, " +
			OWNER + " TEXT, " +
			CREATION_EPOCH + " INTEGER, " +
			EDITION_EPOCH + " INTEGER," +
			EDITOR + " TEXT, " +
			DATA + " TEXT, " +
			TO_DELETE + " BOOL, " +
			TO_UPDATE + " BOOL, " +
			TO_CREATE + " BOOL," +
			DATA_SET_ID + " INTEGER," +
			ACTIVE + " BOOL NOT NULL DEFAULT TRUE," +
			"UNIQUE(" + Constant.URL + ", " + Constant.VERSION + ", " + Constant.DATA_SET_ID + ") ON CONFLICT REPLACE)";

	public static final String PATH_SEPARATOR = "|",
			PATH_SEPARATOR_PATTERN = "\\|",
			PATH_SELECTOR_OPEN = "[",
			PATH_SELECTOR_CLOSE = "]";

	public static final String SELECTOR_OPEN = "<",
			SELECTOR_CLOSE = ">",
			SELECTOR_CONTEXT_LABEL = "ctx",
			SELECTOR_SERVICE_HISTORY = "history",
			SELECTOR_SERVICE_LOOKUP = "lookup",
			SELECTOR_SERVICE_SEPARATOR = "\\|",
			SELECTOR_CONTEXT_SEPARATOR = "/",
			SELECT_CONTEXT_FULL_PATTERN = Constant.SELECTOR_OPEN
					+ Constant.SELECTOR_CONTEXT_LABEL
					+ Constant.SELECTOR_CONTEXT_SEPARATOR
					+ "[^" + Constant.SELECTOR_CONTEXT_SEPARATOR + Constant.SELECTOR_CLOSE + "]+("
					+ Constant.SELECTOR_CONTEXT_SEPARATOR
					+ "[^" + Constant.SELECTOR_CLOSE + "]+)?"
					+ Constant.SELECTOR_CLOSE;

	public static final int MAX_CONCURRENT_DOWNLOADS = 40;
}
