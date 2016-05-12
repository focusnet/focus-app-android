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

package eu.focusnet.app.model.json;

import java.util.ArrayList;
import java.util.Date;


public class Preference extends FocusObject
{

	private Long id;
	private Setting settings;
	private Bookmark bookmarks;

	public Preference(String type, String url, String context, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active, Setting settings, Bookmark bookmarks)
	{
		super(type, url, context, owner, editor, version, creationDateTime, editionDateTime, active);
		this.settings = settings;
		this.bookmarks = bookmarks;
	}

	public Preference(Setting settings, Bookmark bookmarks)
	{
		this.settings = settings;
		this.bookmarks = bookmarks;
	}

	public Preference()
	{
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Setting getSettings()
	{
		return settings;
	}

	public void setSettings(Setting settings)
	{
		this.settings = settings;
	}

	public Bookmark getBookmarks()
	{
		return bookmarks;
	}

	public void setBookmarks(Bookmark bookmarks)
	{
		this.bookmarks = bookmarks;
	}

	public void addBookmarkLink(BookmarkLink bookmarkLink, String bookmarkType)
	{
		if (bookmarkType.equals(BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString())) {
			bookmarks.getPages().add(bookmarkLink);
		}
		else {
			bookmarks.getTools().add(bookmarkLink);
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
		if (bookmarkType.equals(BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString())) {
			ArrayList<BookmarkLink> pages = bookmarks.getPages();
			for (int i = 0; i < pages.size(); i++) {
				BookmarkLink page = pages.get(i);
				if (page.getPath().equals(path) && page.getName().equals(title)) {
					return i;
				}
			}
		}
		else {
			ArrayList<BookmarkLink> tools = bookmarks.getTools();
			for (int i = 0; i < tools.size(); i++) {
				BookmarkLink tool = tools.get(i);
				if (tool.getPath().equals(path) && tool.getName().equals(title)) {
					return i;
				}
			}
		}
		return foundIndex;
	}

	public void removeBookmarkLink(String path, String title, String bookmarkType)
	{
		if (bookmarkType.equals(BookmarkLink.BOOKMARK_LINK_TYPE.PAGE.toString())) {
			int found = this.findBookmarkLinkInSpecificSet(path, title, bookmarkType);
			if (found != -1) {
				bookmarks.getPages().remove(found);
			}
		}
		else {
			ArrayList<BookmarkLink> tools = bookmarks.getTools();
			int found = this.findBookmarkLinkInSpecificSet(path, title, bookmarkType);
			if (found != -1) {
				bookmarks.getTools().remove(found);
			}
		}
	}
}
