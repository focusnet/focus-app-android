package eu.focusnet.app.ui.fragment.widget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import eu.focusnet.app.R;
import eu.focusnet.app.model.internal.widgets.TextWidgetInstance;

/**
 * Created by admin on 18.01.2016.
 */
public class TextWidgetFragment extends WidgetFragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View viewRoot = inflater.inflate(R.layout.fragment_text, container, false);
		setWidgetLayout(viewRoot);

		TextWidgetInstance textWidgetInstance = (TextWidgetInstance) getWidgetInstance();

		TextView textTitle = (TextView) viewRoot.findViewById(R.id.text_title);
		textTitle.setText(textWidgetInstance.getTitle());

		TextView textContent = (TextView) viewRoot.findViewById(R.id.text_content);
		textContent.setText(textWidgetInstance.getContent());

		return viewRoot;
	}
}
