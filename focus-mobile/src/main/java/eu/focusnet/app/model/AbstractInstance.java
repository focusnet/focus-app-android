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

package eu.focusnet.app.model;


import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.focusnet.app.controller.DataManager;
import eu.focusnet.app.util.FocusInternalErrorException;

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
	/**
	 * If the instance is not valid, this instance variable may contain information on why.
	 */
	protected String nonValidityReason;
	/**
	 * Tells whether this instance and the instances it contains are valid
	 */
	private boolean valid;

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
	 * <p/>
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
	 * <p/>
	 * See FIXME (for more details on how it works)
	 *
	 * @param parentPath The parent path on the top of which the new path must be defined.
	 */
	public abstract void buildPaths(String parentPath);

	/**
	 * Get a comparator for {@code AbstractInstance}s that can have iterators.
	 * The comparison is based on the title, but
	 * we do not reorder if the 2 evaluated instances do not have the same current iterator. E.g.
	 *
	 * For example:
	 *
	 * From
	 * - Wonderful forest information (wonderfulforest)
	 * - Stand 2 (stand[http://2])
	 * - Stand 3 (stand[http://3])
	 * - Stand 1 (stand[http://1])
	 *
	 * We obtain
	 * - Wonderful forest information
	 * - Stand 1
	 * - Stand 2
	 * - Stand 3
	 *
	 * and not
	 * - Stand 1
	 * - Stand 2
	 * - Stand 3
	 * - Wonderful forest information
	 *
	 * FIXME check that return 0 keeps the same ordering as the original object.
	 *
	 * @return A comparator.
	 */
	public static Comparator<? super IterableInstance> getComparator()
	{
		return new Comparator<IterableInstance>() {
			@Override
			public int compare(IterableInstance pLeft, IterableInstance pRight)
			{
				Pattern pattern = Pattern.compile("^(.+/)([^/]+)(\\[[^\\]+]\\])?$");
				Matcher matcherLeft = pattern.matcher(pLeft.getPath());
				Matcher matcherRight = pattern.matcher(pRight.getPath());
				if (!matcherLeft.matches() || !matcherRight.matches()) {
					throw new FocusInternalErrorException("Invalid path");
				}
				String currentIteratorLeft = matcherLeft.group(1);
				String currentIteratorRight = matcherLeft.group(1);

				if (currentIteratorLeft.equals(currentIteratorRight)) {
					return 0;
				}
				else {
					return pLeft.getTitle().compareTo(pRight.getTitle());
				}
			}
		};
	}
}
