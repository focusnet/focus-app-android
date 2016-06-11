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

package eu.focusnet.app.ui.util;

/**
 * UI-related constants.
 */
public class Constant
{
	/**
	 * The layout in pages is orgianized based on this number of columns.
	 */
	public static final int LAYOUT_NUM_OF_COLUMNS = 4;
	public static final String WIDGET_LAYOUT_OF = "of";

	// Extra parameters names
	public static final String UI_EXTRA_PROJECT_PATH = "eu.focusnet.app.extra.PROJECT_PATH",
			UI_EXTRA_PAGE_PATH = "eu.focusnet.app.extra.PAGE_PATH",
			UI_EXTRA_TITLE = "eu.focusnet.app.extra.TITLE",
			UI_EXTRA_PATH = "eu.focusnet.app.extra.PATH",
			UI_EXTRA_IMAGE_URI = "eu.focusnet.app.extra.IMAGE_URI",
			UI_EXTRA_LOADING_INFO_TEXT = "eu.focusnet.app.extra.LOADING_INFO_TEXT",
			UI_EXTRA_FRAGMENT_TITLE = "eu.focusnet.app.extra.FRAGMENT_TITLE",
			UI_EXTRA_FRAGMENT_POSITION = "eu.focusnet.app.extra.FRAGMENT_POSITION",
			UI_EXTRA_LAYOUT_HEIGHT = "eu.focusnet.app.extra.LAYOUT_HEIGHT",
			UI_EXTRA_LAYOUT_WEIGHT = "eu.focusnet.app.extra.LAYOUT_WEIGHT",
			UI_EXTRA_LAYOUT_POSITION_IN_ROW = "eu.focusnet.app.extra.POSITION_IN_ROW";

	// Extras for communication with third-party apps
	public static final String
			UI_EXTRA_EXTERNAL_APP_INPUT = "eu.focusnet.app.extra.EXTERNAL_APP_INPUT",
			UI_EXTRA_EXTERNAL_APP_OUTPUT = "eu.focusnet.app.extra.EXTERNAL_APP_OUTPUT";

	// fragments identifiers
	public static final int UI_MENU_ENTRY_PROJECTS_LISTING = 1,
			UI_MENU_ENTRY_BOOKMARK = 2,
			UI_MENU_ENTRY_ABOUT = 3,
			UI_MENU_ENTRY_LOGOUT = 4;

}
