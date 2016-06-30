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

package eu.focusnet.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import eu.focusnet.app.R;
import eu.focusnet.app.controller.FocusAppLogic;
import eu.focusnet.app.controller.NetworkManager;
import eu.focusnet.app.ui.common.UiHelper;
import eu.focusnet.app.util.ApplicationHelper;
import eu.focusnet.app.util.Constant;

/**
 * Demo use case selection activity.
 *
 * @deprecated only used in the prototype. We should use {@link LoginActivity} instead.
 */
public class DemoUseCaseSelectionActivity extends Activity implements AdapterView.OnItemSelectedListener
{

	/**
	 * Index of the selected use case.
	 */
	private int selectedUseCase;

	/**
	 * Instantiate the activity UI
	 *
	 * @param savedInstanceState Inherited.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_demo);

		// default selection
		this.selectedUseCase = 0;

		// populate the spinner with values
		Spinner spinner = (Spinner) findViewById(R.id.login_demo_field_select_use_case);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this,
				R.array.demo_use_cases_labels,
				android.R.layout.simple_spinner_item
		);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(this);
	}

	/**
	 * Item selection listener for the selection list control that contains the different demo
	 * use cases.
	 *
	 * @param parent   Inherited.
	 * @param view     Inhertied.
	 * @param position 0-based position of the selected item in the selection list.
	 * @param id       Inherited.
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
	{
		this.selectedUseCase = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		this.selectedUseCase = -1;
	}

	/**
	 * When the user clicks the button to start the demo, we initiate the login procedure. We then
	 * redirect to the {@link EntryPointActivity}, that will be responsible for retrieving the
	 * application content.
	 * <p/>
	 * This logic requires a network connection.
	 *
	 * @param view Inherited.
	 */
	public void onClickStartDemo(View view)
	{
		if (NetworkManager.isNetworkAvailable()) {

			final Thread login = new Thread()
			{
				public void run()
				{
					String[] useCases = ApplicationHelper.getResources().getStringArray(R.array.demo_use_cases_values);
					String selectedUseCase = useCases[DemoUseCaseSelectionActivity.this.selectedUseCase];

					boolean success = FocusAppLogic.getUserManager().demoLogin(selectedUseCase);
					if (success) {
						Intent i = new Intent(DemoUseCaseSelectionActivity.this, EntryPointActivity.class);
						i.putExtra(Constant.Extra.UI_EXTRA_LOADING_INFO_TEXT, getString(R.string.load_info_load_demo));
						startActivity(i);
						finish();
					}
					else {
						UiHelper.displayToast(DemoUseCaseSelectionActivity.this, R.string.focus_login_error_no_network);
					}
				}
			};
			login.start();

		}
		else {
			UiHelper.displayToast(this, R.string.focus_login_error_no_network);
		}
	}

}
