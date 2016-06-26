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

package eu.focusnet.app.ui.activity;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.fragment.ProjectFragment;

/**
 * This {@code Activity} is created when accessing a project from the
 * {@link ProjectsListingActivity}.
 */
public class ProjectActivity extends ToolbarEnabledActivity
{
	/**
	 * Defines the target container of this Activity.
	 *
	 * @return Inherited.
	 */
	@Override
	protected int getTargetLayoutContainer()
	{
		return R.id.project_container;
	}

	/**
	 * Defines the Fragment to include in the container for this Activity.
	 */
	@Override
	protected void prepareNewFragment()
	{
		this.fragment = new ProjectFragment();
	}

	/**
	 * Defines the layout of this activity.
	 *
	 * @return Inherited.
	 */
	@Override
	protected int getTargetView()
	{
		return R.layout.activity_project;
	}
}
