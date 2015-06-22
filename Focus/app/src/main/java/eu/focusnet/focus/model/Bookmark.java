package eu.focusnet.focus.model;

import java.util.ArrayList;

public class Bookmark {
	
	private ArrayList<Page> pages;
	private ArrayList<Tool> tools;

	public Bookmark() {}
	
	public Bookmark(ArrayList<Page> pages, ArrayList<Tool> tools) {
		this.pages = pages;
		this.tools = tools;
	}

	public ArrayList<Page> getPages() {
		return pages;
	}

	public void setPages(ArrayList<Page> pages) {
		this.pages = pages;
	}

	public ArrayList<Tool> getTools() {
		return tools;
	}

	public void setTools(ArrayList<Tool> tools) {
		this.tools = tools;
	}
	

	
}
