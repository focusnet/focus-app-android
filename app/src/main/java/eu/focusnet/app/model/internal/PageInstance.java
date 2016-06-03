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

package eu.focusnet.app.model.internal;

import java.util.LinkedHashMap;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.exception.FocusBadTypeException;
import eu.focusnet.app.exception.FocusMissingResourceException;
import eu.focusnet.app.model.internal.widgets.InvalidWidgetInstance;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.model.json.PageTemplate;
import eu.focusnet.app.model.util.Constant;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by julien on 12.01.16.
 */
public class PageInstance extends AbstractInstance
{

	public enum PageType
	{
		DASHBOARD,
		TOOL,
		HIDDEN
	}

	public final static String LABEL_PAGE_ITERATOR = "$page-iterator$";
	LinkedHashMap<String, WidgetInstance> widgets; // layout + widgetdefinition, with dataContext.
	private PageTemplate template;
	private String guid;
	private PageType type;
	private String title;
	private String description;

	/**
	 * C'tor
	 *
	 * @param pageTpl
	 * @param dataCtx
	 */
	public PageInstance(PageTemplate pageTpl, PageType type, DataContext dataCtx) throws FocusMissingResourceException
	{
		super();

		this.template = pageTpl;
		this.guid = pageTpl.getGuid();
		this.type = type;
		this.widgets = new LinkedHashMap<>();
		if (dataCtx.get(LABEL_PAGE_ITERATOR) != null) {
			this.guid = this.guid + Constant.PATH_SELECTOR_OPEN + dataCtx.get(LABEL_PAGE_ITERATOR) + Constant.PATH_SELECTOR_CLOSE;
		}
		this.dataContext = dataCtx;
		if (this.dataContext == null) {
			this.dataContext = new DataContext();
		}

		// register page-specific data to our current data context
		this.dataContext.provideData(this.template.getData());

		try {
			this.title = TypesHelper.asString(this.dataContext.resolve(this.template.getTitle()));
			this.description = TypesHelper.asString(this.dataContext.resolve(this.template.getDescription()));
		}
		catch (FocusMissingResourceException ex) {
			FocusApplication.reportError(ex);
			return;
		}
		catch (FocusBadTypeException ex) {
			FocusApplication.reportError(ex);
			return;
		}

		if (this.description == null) {
			this.description = "";
		}
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
	 *
	 * If any of the widget instances is not valid, then the page is considered as not valid
	 * (but it will still be displayed anyway).
	 *
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
}
