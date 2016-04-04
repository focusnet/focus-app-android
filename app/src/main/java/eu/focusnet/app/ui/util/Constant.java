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
 * UI-related constants
 */
public class Constant
{
	public static final int LAYOUT_NUM_OF_COLUMNS = 4;
	public static final String WIDGET_LAYOUT_OF = "of";

	// Extra parameters names
	public static final String UI_EXTRA_PROJECT_PATH = "eu.focusnet.extra.PROJECT_PATH",
			UI_EXTRA_PAGE_PATH = "eu.focusnet.extra.PAGE_PATH",
			UI_EXTRA_TITLE = "eu.focusnet.extra.TITLE",
			UI_EXTRA_PATH = "eu.focusnet.extra.PATH",
			UI_EXTRA_IS_TO_SAVE = "eu.focusnet.extra.IS_TO_SAVE",
			UI_EXTRA_NAME = "eu.focusnet.extra.NAME",
			UI_EXTRA_BOOKMARK_TYPE = "eu.focusnet.extra.BOOKMARK_TYPE",
			UI_EXTRA_NOTIFICATION_ID = "eu.focusnet.extra.NOTIFICATION_ID";

	// and bundle
	public static final String UI_BUNDLE_FRAGMENT_TITLE = "eu.focusnet.bundle.FRAGMENT_TITLE",
			UI_BUNDLE_FRAGMENT_POSITION = "eu.focusnet.bundle.FRAGMENT_POSITION",
			UI_BUNDLE_LAYOUT_WIDTH = "eu.focusnet.bundle.LAYOUT_WIDTH",
			UI_BUNDLE_LAYOUT_HEIGHT = "eu.focusnet.bundle.LAYOUT_HEIGHT",
			UI_BUNDLE_LAYOUT_WEIGHT = "eu.focusnet.bundle.LAYOUT_WEIGHT";
	;

	// fragments identifiers
	public static final int UI_FRAGMENT_FOCUS = 1,
			UI_FRAGMENT_BOOKMARK = 2,
			UI_FRAGMENT_SYNCHRONIZE = 3,
			UI_FRAGMENT_SETTINGS = 4,
			UI_FRAGMENT_USER_MANUAL = 5;

}
