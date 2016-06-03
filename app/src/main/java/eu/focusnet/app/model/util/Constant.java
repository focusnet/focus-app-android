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

	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"; // ISO 8601

	public static final String DATABASE_NAME = "Focus_DB";

	public static final String DATABASE_TABLE_SAMPLES = "samples";

	public static final String ID = "id",
			TYPE = "type",
			URL = "url",
			CONTEXT = "context",
			OWNER = "owner",
			EDITOR = "editor",
			CREATION_EPOCH = "creation_date_time",
			EDITION_EPOCH = "edition_date_time",
			VERSION = "version",
			ACTIVE = "active",
			DATA = "data",
			TO_DELETE = "toDelete",
			TO_PUT = "toPut",
			TO_POST = "toPost";

	public static final String CREATE_TABLE_SAMPLES_QUERY = "CREATE TABLE " + DATABASE_TABLE_SAMPLES + "" +
			"(" + ID + " INTEGER AUTO INCREMENT," +
			URL + " TEXT PRIMARY KEY, " +
			CONTEXT + " TEXT, " +
			VERSION + " INTEGER, " +
			TYPE + " TEXT, " +
			OWNER + " TEXT, " +
			CREATION_EPOCH + " INTEGER , " +
			EDITION_EPOCH + " INTEGER ," +
			EDITOR + " TEXT, " +
			DATA + " TEXT, " +
			TO_DELETE + " BOOL, " +
			TO_PUT + " BOOL, " +
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
