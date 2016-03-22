/**
 *
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
 *
 */

package eu.focusnet.app.ui.fragment.widget;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import eu.focusnet.app.FocusApplication;
import eu.focusnet.app.service.DataManager;
import eu.focusnet.app.model.internal.widgets.WidgetInstance;
import eu.focusnet.app.ui.util.Constant;
import eu.focusnet.app.ui.util.ViewUtil;

/**
 * Created by yandypiedra on 13.01.16.
 */
public abstract class WidgetFragment extends Fragment
{
	//TODO
	WidgetInstance widgetInstance;

	@Override
	public abstract View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

	@Override
	public void onDestroyView()
	{
		FocusApplication.getInstance().getDataManager().unregisterActiveInstance(this.widgetInstance);

		super.onDestroyView();
	}

	public void setWidgetLayout(View viewRoot)
	{
		Bundle arguments = getArguments();
		int linearLayoutWidth = arguments.getInt(ViewUtil.LAYOUT_WIDTH);
		int linearLayoutHeight = arguments.getInt(ViewUtil.LAYOUT_HEIGHT);
		int linearLayoutWeight = arguments.getInt(ViewUtil.LAYOUT_WEIGHT);
		viewRoot.setLayoutParams(new LinearLayout.LayoutParams(linearLayoutWidth, linearLayoutHeight, linearLayoutWeight));
	}

	public WidgetInstance getWidgetInstance()
	{
		Bundle bundles = getArguments();
		String path = bundles.getString(Constant.UI_EXTRA_PATH);
		DataManager dm =FocusApplication.getInstance().getDataManager();
		this.widgetInstance = dm.getAppContentInstance().getWidgetFromPath(path);
		dm.registerActiveInstance(this.widgetInstance);
		return this.widgetInstance;
	}

}
