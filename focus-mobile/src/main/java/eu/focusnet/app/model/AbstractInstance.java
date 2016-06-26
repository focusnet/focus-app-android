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

package eu.focusnet.app.model;


import android.support.annotation.NonNull;

import eu.focusnet.app.controller.DataManager;

abstract public class AbstractInstance
{
	protected final DataManager dataManager;
	protected DataContext dataContext;
	protected String path;
	// depth in hierarchy is in fact attached to a datacontext.
	protected int depthInHierarchy;
	private boolean valid;

	public AbstractInstance(@NonNull DataManager dm)
	{
		this.path = null;
		this.valid = true;
		this.dataManager = dm;
		this.depthInHierarchy = 0;
	}

	public DataContext getDataContext()
	{
		return this.dataContext;
	}

	/**
	 * Mark this widget as invalid
	 */
	protected void markAsInvalid()
	{
		this.valid = false;
	}

	protected void markAsInvalid(String why)
	{
		this.markAsInvalid();
	}

	/**
	 * Tells whether this widget instance is valid
	 */
	public boolean isValid()
	{
		return this.valid;
	}


	public void freeDataContext()
	{
		this.dataContext = null;
	}

	public DataManager getDataManager()
	{
		return this.dataManager;
	}

	public AbstractInstance lookupByPath(String searchedPath)
	{
		if (searchedPath.equals(this.path)) {
			return this;
		}
		else if (searchedPath.startsWith(this.path)) {
			return this.propagatePathLookup(searchedPath);
		}
		return null;
	}

	protected abstract AbstractInstance propagatePathLookup(String searchedPath);

	public String getPath()
	{
		return this.path;
	}

	public abstract void buildPaths(String parentPath);

}
