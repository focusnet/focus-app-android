/*
 * The MIT License (MIT)
 * Copyright (c) 2015 Berner Fachhochschule (BFH) - www.bfh.ch
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package eu.focusnet.app.model.gson;

import java.util.ArrayList;

/**
 * Bookmarks folders
 * <p/>
 * Refer to JSON Schema for further documentation.
 * See https://github.com/focusnet/focus-data-model
 */
public class BookmarksList
{

	private ArrayList<Bookmark> pages;
	private ArrayList<Bookmark> tools;

	public ArrayList<Bookmark> getPages()
	{
		if (pages == null) {
			pages = new ArrayList<>();
		}
		return pages;
	}

	public ArrayList<Bookmark> getTools()
	{
		if (tools == null) {
			tools = new ArrayList<>();
		}
		return tools;
	}

}
