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

	public Preference(String type, Setting settings, Bookmark bookmarks)
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
		if (bookmarkType.equals(BookmarkLink.BOOKMARK_LINK_TYPE.PAGE)) {
			bookmarks.getPages().add(bookmarkLink);
		}
		else {
			bookmarks.getTools().add(bookmarkLink);
		}
	}

	public void removeBookmarkLink(String path, String title, String bookmarkType)
	{
		int indexToRemove = -1;
		if (bookmarkType.equals(BookmarkLink.BOOKMARK_LINK_TYPE.PAGE)) {
			ArrayList<BookmarkLink> pages = bookmarks.getPages();
			for (int i = 0; i < pages.size(); i++) {
				BookmarkLink page = pages.get(i);
				if (page.getPath().equals(path) && page.getName().equals(title)) {
					indexToRemove = i;
					break;
				}
			}
			if (indexToRemove != -1) {
				pages.remove(indexToRemove);
			}
		}
		else {
			ArrayList<BookmarkLink> tools = bookmarks.getTools();
			for (int i = 0; i < tools.size(); i++) {
				BookmarkLink tool = tools.get(i);
				if (tool.getPath().equals(path) && tool.getName().equals(title)) {
					indexToRemove = i;
					break;
				}
			}
			if (indexToRemove != -1) {
				tools.remove(indexToRemove);
			}
		}

	}
}
