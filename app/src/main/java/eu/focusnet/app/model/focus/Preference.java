package eu.focusnet.app.model.focus;

import java.util.Date;

public class Preference extends FocusObject {

	private Long id;
	private Setting settings;
	private Bookmark bookmarks;

	public Preference(String type, String url, String context, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active, Setting settings, Bookmark bookmarks) {
		super(type, url, context, owner, editor, version, creationDateTime, editionDateTime, active);
		this.settings = settings;
		this.bookmarks = bookmarks;
	}

	public Preference(String type, Setting settings, Bookmark bookmarks) {
		this.settings = settings;
		this.bookmarks = bookmarks;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Preference() {}

	public Setting getSettings() {
		return settings;
	}

	public void setSettings(Setting settings) {
		this.settings = settings;
	}

	public Bookmark getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(Bookmark bookmarks) {
		this.bookmarks = bookmarks;
	}
}
