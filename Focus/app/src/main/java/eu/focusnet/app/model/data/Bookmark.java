package eu.focusnet.app.model.data;

import java.util.ArrayList;

public class Bookmark {

	private Long id;
	private ArrayList<BookmarkLink> pages;
	private ArrayList<BookmarkLink> tools;

	public Bookmark() {}
	
	public Bookmark(ArrayList<BookmarkLink> pages, ArrayList<BookmarkLink> tools) {
		this.pages = pages;
		this.tools = tools;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArrayList<BookmarkLink> getPages() {
		return pages;
	}

	public void setPages(ArrayList<BookmarkLink> pages) {
		this.pages = pages;
	}

	public ArrayList<BookmarkLink> getTools() {
		return tools;
	}

	public void setTools(ArrayList<BookmarkLink> tools) {
		this.tools = tools;
	}
	

	
}
