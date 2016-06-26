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

package eu.focusnet.app.ui.fragment.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.focusnet.app.R;
import eu.focusnet.app.model.widgets.WidgetInstance;
import eu.focusnet.app.util.Constant;

/**
 * {@code Fragment} rendering a dummy content for widgets that are not properly configured. If a widget
 * is not correctly configured, the corresponding {@link WidgetInstance} children type will be a
 * {@link eu.focusnet.app.model.widgets.InvalidWidgetInstance}. However, the latter will keep a
 * reference to the original {@link WidgetInstance} that failed to load such that we can display
 * more useful information here.
 */
public class InvalidWidgetFragment extends WidgetFragment
{
	/**
	 * Create the View.
	 * @param inflater           Inherited
	 * @param container          Inherited
	 * @param savedInstanceState Inherited
	 * @return The new View.
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// setup
		super.onCreate(savedInstanceState);
		this.setupWidget(inflater.inflate(R.layout.fragment_widget_invalid, container, false));

		TextView invalidWidgetInfo = (TextView) this.rootView.findViewById(R.id.invalid_widget_info);
		Bundle bundles = getArguments();
		String path = bundles.getString(Constant.Extra.UI_EXTRA_PATH);

		if (path != null) {
			invalidWidgetInfo.setText(path);
		}
		return this.rootView;
	}


}
