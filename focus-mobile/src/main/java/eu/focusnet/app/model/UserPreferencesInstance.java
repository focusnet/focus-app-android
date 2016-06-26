package eu.focusnet.app.model;

import java.util.ArrayList;

import eu.focusnet.app.model.gson.Bookmark;
import eu.focusnet.app.model.gson.BookmarksList;
import eu.focusnet.app.model.gson.UserPreferences;

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
public class UserPreferencesInstance extends UserPreferences
{
	public UserPreferencesInstance(String targetUrl)
	{
		super(targetUrl);
	}


	public BookmarksList getBookmarks()
	{
		if (this.bookmarks == null) {
			this.bookmarks = new BookmarksList();
		}
		return this.bookmarks;
	}

	public void addBookmarkLink(Bookmark bookmark, String bookmarkType)
	{
		if (this.bookmarks == null) {
			this.bookmarks = new BookmarksList();
		}

		if (bookmarkType.equals(Bookmark.BookmarkLinkType.PAGE.toString())) {
			this.bookmarks.getPages().add(bookmark);
		}
		else {
			this.bookmarks.getTools().add(bookmark);
		}
	}

	/**
	 * Find where the bookmark link is in one of the 2 specific sets (PAGE / TOOL)
	 *
	 * @param path
	 * @param title
	 * @param bookmarkType
	 * @return
	 */
	public int findBookmarkLinkInSpecificSet(String path, String title, String bookmarkType)
	{
		int foundIndex = -1;
		if (this.bookmarks != null) {
			if (bookmarkType.equals(Bookmark.BookmarkLinkType.PAGE.toString())) {
				ArrayList<Bookmark> pages = this.bookmarks.getPages();
				for (int i = 0; i < pages.size(); i++) {
					Bookmark page = pages.get(i);
					if (page.getPath().equals(path)) {
						return i;
					}
				}
			}
			else {
				ArrayList<Bookmark> tools = this.bookmarks.getTools();
				for (int i = 0; i < tools.size(); i++) {
					Bookmark tool = tools.get(i);
					if (tool.getPath().equals(path)) {
						return i;
					}
				}
			}
		}
		return foundIndex;
	}

	public void removeBookmarkLink(String path, String title, String bookmarkType)
	{
		int found = this.findBookmarkLinkInSpecificSet(path, title, bookmarkType);
		if (found != -1) {
			if (bookmarkType.equals(Bookmark.BookmarkLinkType.PAGE.toString())) {
				this.bookmarks.getPages().remove(found);
			}
			else {
				this.bookmarks.getTools().remove(found);
			}
		}
	}
}
