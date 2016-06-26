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

/**
 * In our model, we distinguish between template objects and instance objects. Template objects
 * are raw objects obtained from JSON stored on the backend. Instances are the translation of these
 * templates with real data, resolving iterators and interoplating variables.
 * <p/>
 * This object is the abstract parent object for most instances: {@link AppContentInstance},
 * {@link ProjectInstance}, {@link PageInstance}.
 */
abstract public class AbstractInstance
{
	/**
	 * {@link DataManager} of this instance.
	 */
	protected final DataManager dataManager;

	/**
	 * {@link DataContext} relevant to this instance.
	 */
	protected DataContext dataContext;

	/**
	 * Path to this instance.
	 * <p/>
	 * See {@link eu.focusnet.app.util.Constant.Navigation}
	 */
	protected String path;

	// depth in hierarchy is in fact attached to a datacontext.
	protected int depthInHierarchy;

	/**
	 * Tells whether this instance and the instances it contains are valid
	 */
	private boolean valid;

	/**
	 * If the instance is not valid, this instance variable may contain information on why.
	 */
	protected String nonValidityReason;

	/**
	 * Constructor. Set reasonable defaults for instances.
	 *
	 * @param dm The {@link DataManager} to use
	 */
	public AbstractInstance(@NonNull DataManager dm)
	{
		this.path = null;
		this.valid = true;
		this.dataManager = dm;
		this.depthInHierarchy = 0;
	}

	/**
	 * Get current {@link DataContext}
	 *
	 * @return The {@link DataContext}
	 */
	public DataContext getDataContext()
	{
		return this.dataContext;
	}

	/**
	 * Mark this instance as invalid
	 */
	protected void markAsInvalid()
	{
		this.valid = false;
	}

	/**
	 * Mark this instance as invalid and say why.
	 *
	 * @param why The reason of non-validity.
	 */
	protected void markAsInvalid(String why)
	{
		this.markAsInvalid();
		this.nonValidityReason = why;
	}

	/**
	 * Tells whether instance is valid
	 *
	 * @return {@code true} if it is valid. {@code false} otherwise.
	 */
	public boolean isValid()
	{
		return this.valid;
	}

	/**
	 * Free the current {@link DataContext}. To be called when the whole instance has been
	 * constructed and we don't need it anymore.
	 */
	public void freeDataContext()
	{
		this.dataContext = null;
	}

	/**
	 * Get the {@link DataManager}
	 *
	 * @return The {@link DataManager}
	 */
	public DataManager getDataManager()
	{
		return this.dataManager;
	}

	/**
	 * Look for the instance matching the specified path. Return it if it matches the current
	 * intance or if it is being contained in the current instance.
	 *
	 * @param searchedPath The path to look after.
	 *                     See {@link eu.focusnet.app.util.Constant.Navigation}
	 * @return The instance of interest or {@code null} if no corresponding instance has been
	 * found.
	 */
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

	/**
	 * Iteratively search for an instance. See {@link #lookupByPath(String)}.
	 *
	 * @param searchedPath The path to look after.
	 * @return The intsance of interest or {@code null} if no corresponding instance has been found.
	 */
	protected abstract AbstractInstance propagatePathLookup(String searchedPath);

	/**
	 * Get the path of the current instance.
	 *
	 * See {@link eu.focusnet.app.util.Constant.Navigation}.
	 *
	 * @return The path
	 */
	public String getPath()
	{
		return this.path;
	}

	/**
	 * Build paths for this instanc and other instances it may contain. This cannot be done at
	 * instance creation time because iterators may not be completely built, yet.
	 *
	 * See FIXME (for more details on how it works)
	 *
	 * @param parentPath The parent path on the top of which the new path must be defined.
	 */
	public abstract void buildPaths(String parentPath);

}
