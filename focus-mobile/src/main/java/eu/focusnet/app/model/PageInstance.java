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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.gson.PageTemplate;
import eu.focusnet.app.model.widgets.InvalidWidgetInstance;
import eu.focusnet.app.model.widgets.WidgetInstance;
import eu.focusnet.app.util.Constant;

/**
 */
public class PageInstance extends AbstractInstance
{

	public enum PageType
	{
		DASHBOARD,
		TOOL,
		HIDDEN
	}

	LinkedHashMap<String, WidgetInstance> widgets; // layout + widget definition, with dataContext.
	private PageTemplate template;
	private String guid;
	private PageType type;
	private String title;
	private String description;
	private boolean disabled;

	/**
	 * C'tor
	 *
	 * @param pageTpl
	 * @param dataCtx
	 */
	public PageInstance(PageTemplate pageTpl, PageType type, @NonNull DataContext dataCtx, int depthInHierarchy) throws FocusMissingResourceException
	{
		super(dataCtx.getDataManager());

		this.template = pageTpl;
		this.guid = pageTpl.getGuid();
		this.type = type;
		this.widgets = new LinkedHashMap<>();
		this.dataContext = dataCtx;
		this.depthInHierarchy = depthInHierarchy;
		this.disabled = this.template.isDisabled();

		// register page-specific data to our current data context
		this.dataContext.provideData(this.template.getData(), this.depthInHierarchy);
	}

	// FIXME abstract method?
	public Future fillWithRealData()
	{
		// post-pone setting information after having fetched all resources related to this object
		Callable todo = new Callable()
		{
			@Override
			public Boolean call() throws Exception
			{
				if (template.getIterator() != null) {
					guid = guid + Constant.Navigation.PATH_SELECTOR_OPEN + dataContext.get(Constant.Navigation.LABEL_PAGE_ITERATOR) + Constant.Navigation.PATH_SELECTOR_CLOSE;
				}

				try {
					title = dataContext.resolveToString(template.getTitle());
					description = dataContext.resolveToString(template.getDescription());
				}
				catch (FocusMissingResourceException | FocusBadTypeException ex) {
					FocusApplication.reportError(ex);
					return false;
				}

				if (description == null) {
					description = "";
				}

				freeDataContext();
				return true;
			}
		};

		FutureTask future = new FutureTask<>(todo);
		this.dataContext.toExecuteWhenReady(future);
		return future;
	}


	/**
	 * Get the current page instance guid.
	 *
	 * @return
	 */
	public String getGuid()
	{
		return this.guid;
	}

	/**
	 * Return the type of page (dashboards or tools or null)
	 *
	 * @return
	 */
	public PageType getType()
	{
		return this.type;
	}

	public LinkedHashMap<String, WidgetInstance> getWidgets()
	{
		return widgets;
	}

	/**
	 * Get the page title (resolved within the current data context)
	 *
	 * @return
	 */
	public String getTitle()
	{
		return title;
	}

	/**
	 * Get the description (resolved within the current data context)
	 *
	 * @return
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Add a widget instance to the current page
	 * <p/>
	 * If any of the widget instances is not valid, then the page is considered as not valid
	 * (but it will still be displayed anyway).
	 * <p/>
	 * We never pass invalid widgets to a PageIntance. Instead, we pass an InvalidWidgetInstance
	 * that contains a reference to the invalid widget instance.
	 *
	 * @param guid
	 * @param wi
	 */
	public void addWidget(String guid, WidgetInstance wi)
	{
		if (wi instanceof InvalidWidgetInstance) {
			this.markAsInvalid();
		}
		this.widgets.put(guid, wi);
	}

	@Override
	protected AbstractInstance propagatePathLookup(String searchedPath)
	{
		for (Map.Entry e : this.widgets.entrySet()) {
			WidgetInstance p = (WidgetInstance) e.getValue();
			AbstractInstance ret = p.lookupByPath(searchedPath);
			if (ret != null) {
				return ret;
			}
		}
		return null;
	}

	@Override
	public void buildPaths(String parentPath)
	{
		this.path = parentPath
				+ Constant.Navigation.PATH_SEPARATOR
				+ this.type.toString()
				+ Constant.Navigation.PATH_SEPARATOR
				+ this.guid;
		for (Map.Entry e : this.widgets.entrySet()) {
			WidgetInstance p = (WidgetInstance) e.getValue();
			p.buildPaths(this.path);
		}
	}

	public boolean isDisabled()
	{
		return disabled;
	}
}
