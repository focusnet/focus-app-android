package eu.focusnet.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;
import eu.focusnet.app.model.internal.SubmitWidgetInstance;
import eu.focusnet.app.util.ViewUtil;

/**
 * Created by admin on 28.01.2016.
 */
public class SubmitWidgetFragment extends WidgetFragment
{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View viewRoot = inflater.inflate(R.layout.fragment_submit, container, false);
		setWidgetLayout(viewRoot);

		SubmitWidgetInstance submitWidgetInstance = (SubmitWidgetInstance) getWidgetInstance();
		//TODO make something this the submitWidgetInstance

		Button button = (Button) viewRoot.findViewById(R.id.submit_button);
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//TODO
				ViewUtil.displayToast(getActivity(), "Submit button clicked");
			}
		});

		return viewRoot;
	}
}
