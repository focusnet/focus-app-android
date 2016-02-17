package eu.focusnet.app.ui.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import eu.focusnet.app.R;
import eu.focusnet.app.ui.fragment.widget.BarChartWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.CameraWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.GPSWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.LineChartWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.PieChartWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.TableWidgetFragment;
import eu.focusnet.app.ui.fragment.widget.TextWidgetFragment;
import eu.focusnet.app.ui.util.FragmentManager;
import eu.focusnet.app.model.json.FocusSample;
import eu.focusnet.app.ui.util.ViewFactory;
import eu.focusnet.app.ui.util.ViewUtil;

//This activity is only for testing
public class TestActivity extends BaseActivity
{
	private static final String TAG = TestActivity.class.getName();

	private static final String EXAMPLE_APP_ACTION = "com.example.exampleapp.focus_external_dummy_app";
	/**
	 * Convention for extras names
	 * <p/>
	 * All applications being used as FOCUS Mobile external apps follow this convention
	 */
	private static final String
			FOCUS_INPUT_EXTRA = "FOCUS_INPUT",
			FOCUS_OUTPUT_EXTRA = "FOCUS_OUTPUT";
	private final int EXAMPLE_APP_REQUEST = 2;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setTitle("This is a test");

		final int verticalSpace = 50;
		final int takeAllRow = 1;

		float quarter = 0.25f;
		float half = 0.50f;
		float treeQuarters = 0.75f;
		float whole = 1.0f;

		LinearLayout linearLayoutPageInfo = (LinearLayout) findViewById(R.id.test_layout);
		linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace / 2, takeAllRow));


		LinearLayout linearLayoutVertical = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

//        TextView title = ViewFactory.createTextView(this, R.style.TitleAppearance,
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "This is Title");
//        linearLayoutVertical.addView(title);
//
//        title = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "This is info");
//        linearLayoutVertical.addView(title);

		linearLayoutVertical.setId(65329222);
		FragmentManager.addFragment(linearLayoutVertical.getId(), new TextWidgetFragment(), getFragmentManager());
		linearLayoutPageInfo.addView(linearLayoutVertical);

		linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));


		LinearLayout linearLayoutHorizontalPieChartTitle = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

		linearLayoutHorizontalPieChartTitle.addView(ViewFactory.createEmptyView(this, verticalSpace, LinearLayout.LayoutParams.MATCH_PARENT, treeQuarters));

		linearLayoutPageInfo.addView(linearLayoutHorizontalPieChartTitle);

		LinearLayout linearLayoutHorizontal = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 500));
		linearLayoutHorizontal.setId(9381651);
		FragmentManager.addFragment(linearLayoutHorizontal.getId(), new PieChartWidgetFragment(), getFragmentManager());
		FragmentManager.addFragment(linearLayoutHorizontal.getId(), new TableWidgetFragment(), getFragmentManager());
		linearLayoutPageInfo.addView(linearLayoutHorizontal);

		linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));

		LinearLayout linearLayoutHorizontal2 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1100));
		linearLayoutHorizontal2.setId(111567811);
		FragmentManager.addFragment(linearLayoutHorizontal2.getId(), new BarChartWidgetFragment(), getFragmentManager());
		FragmentManager.addFragment(linearLayoutHorizontal2.getId(), new LineChartWidgetFragment(), getFragmentManager());
		linearLayoutPageInfo.addView(linearLayoutHorizontal2);

		linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));

		LinearLayout linearLayoutVertical2 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayoutVertical2.setId(111529861);
		FragmentManager.addFragment(linearLayoutVertical2.getId(), new GPSWidgetFragment(), getFragmentManager());
		linearLayoutPageInfo.addView(linearLayoutVertical2);

		linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));

		LinearLayout linearLayoutVertical3 = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		linearLayoutVertical3.setId(123423547);
		FragmentManager.addFragment(linearLayoutVertical3.getId(), new CameraWidgetFragment(), getFragmentManager());
		linearLayoutPageInfo.addView(linearLayoutVertical3);

		linearLayoutPageInfo.addView(ViewFactory.createEmptyView(this, LinearLayout.LayoutParams.MATCH_PARENT, verticalSpace, takeAllRow));

		LinearLayout linearLayoutVerticalForm = ViewFactory.createLinearLayout(this, LinearLayout.VERTICAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


		TextView form = ViewFactory.createTextView(this, R.style.TitleAppearance,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Form");

		linearLayoutVerticalForm.addView(form);

		LinearLayout linearLayoutHorizontalName = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView labelName = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
				new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Name");

		final EditText editTextName = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
		linearLayoutHorizontalName.addView(labelName);
		linearLayoutHorizontalName.addView(editTextName);
		linearLayoutHorizontalName.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

		linearLayoutVerticalForm.addView(linearLayoutHorizontalName);


		LinearLayout linearLayoutHorizontalEmail = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView labelEmail = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
				new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Email");

		final EditText editTextEmail = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
		editTextEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		linearLayoutHorizontalEmail.addView(labelEmail);
		linearLayoutHorizontalEmail.addView(editTextEmail);
		linearLayoutHorizontalEmail.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

		linearLayoutVerticalForm.addView(linearLayoutHorizontalEmail);


		LinearLayout linearLayoutHorizontalNumber = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView labelNumber = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
				new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Number");

		final EditText editTextNumber = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
		editTextNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		linearLayoutHorizontalNumber.addView(labelNumber);
		linearLayoutHorizontalNumber.addView(editTextNumber);
		linearLayoutHorizontalNumber.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

		linearLayoutVerticalForm.addView(linearLayoutHorizontalNumber);


		LinearLayout linearLayoutHorizontalDescription = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView labelDescription = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
				new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Description");

		final EditText editTextDescription = ViewFactory.createEditText(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
		editTextDescription.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
		editTextDescription.setLines(8);
		editTextDescription.setMaxLines(10);
		editTextDescription.setMinLines(6);
		editTextDescription.setGravity(Gravity.TOP | Gravity.LEFT);

		linearLayoutHorizontalDescription.addView(labelDescription);
		linearLayoutHorizontalDescription.addView(editTextDescription);
		linearLayoutHorizontalDescription.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

		linearLayoutVerticalForm.addView(linearLayoutHorizontalDescription);


//        LinearLayout linearLayoutHorizontalDatePicker = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
//
//        TextView labelDatePicker = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
//                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT), "Pick date");
//        linearLayoutHorizontalDatePicker.addView(labelDatePicker);

		//      DatePicker datePicker = ViewFactory.createDatePicker(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 5));

		//      linearLayoutHorizontalDescription.addView(datePicker);
//        linearLayoutHorizontalDescription.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, 2));
//
		//   linearLayoutVerticalForm.addView(linearLayoutHorizontalDatePicker);


		LinearLayout linearLayoutHorizontal5 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView labelC = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
				new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Label C");


		RadioGroup radioGroup = ViewFactory.createRadioGroup(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
		radioGroup.setOrientation(LinearLayout.HORIZONTAL);
		RadioButton radioButton1 = ViewFactory.createRadioButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		radioButton1.setText("Radio Button 1");
		RadioButton radioButton2 = ViewFactory.createRadioButton(this, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		radioButton2.setText("Radio Button 2");
		radioGroup.addView(radioButton1);
		radioGroup.addView(radioButton2);

		linearLayoutHorizontal5.addView(labelC);
		linearLayoutHorizontal5.addView(radioGroup);
		linearLayoutHorizontal5.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));

		linearLayoutVerticalForm.addView(linearLayoutHorizontal5);


		LinearLayout linearLayoutHorizontal6 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView labelD = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
				new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Label D");

		CheckBox checkBox1 = ViewFactory.createCheckBox(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));
		checkBox1.setText("Check box 1");
		CheckBox checkBox2 = ViewFactory.createCheckBox(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));
		checkBox2.setText("Check box 2");

		linearLayoutHorizontal6.addView(labelD);
		linearLayoutHorizontal6.addView(checkBox1);
		linearLayoutHorizontal6.addView(checkBox2);
		linearLayoutHorizontal6.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));


		linearLayoutVerticalForm.addView(linearLayoutHorizontal6);


		LinearLayout linearLayoutHorizontal7 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		TextView labelE = ViewFactory.createTextView(this, R.style.Base_TextAppearance_AppCompat,
				new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter), "Label E");

		Spinner spinner = ViewFactory.createSpinner(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half));
		List<String> list = new ArrayList<String>();
		list.add("Value 1");
		list.add("Value 2");
		list.add("Value 3");
		list.add("Value 4");
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		linearLayoutHorizontal7.addView(labelE);
		linearLayoutHorizontal7.addView(spinner);
		linearLayoutHorizontal7.addView(ViewFactory.createEmptyView(this, 0, LinearLayout.LayoutParams.WRAP_CONTENT, quarter));
		linearLayoutVerticalForm.addView(linearLayoutHorizontal7);

		LinearLayout linearLayoutHorizontal8 = ViewFactory.createLinearLayout(this, LinearLayout.HORIZONTAL,
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

		Button submitButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half), "Submit");

		submitButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String name = editTextName.getText().toString();
				String email = editTextEmail.getText().toString();
				Long number = new Long(-1);
				try {
					number = Long.valueOf(editTextNumber.getText().toString());
				}
				catch (NumberFormatException ex) {
				}
				String description = editTextDescription.getText().toString();

				List<FocusSample> fs = new ArrayList<>();
				// FIXME FIXME FIXME FocusSample has chan
			 /*   FocusSample focusSample = new FocusSample(KEY_NAME, FocusSample.Type.string, name);
				fs.add(focusSample);
                focusSample = new FocusSample(KEY_EMAIL, FocusSample.Type.string, name);
                fs.add(focusSample);
                focusSample = new FocusSample(KEY_NUMBER, FocusSample.Type.numeric, number);
                fs.add(focusSample);
                focusSample = new FocusSample(KEY_DESCRIPTION, FocusSample.Type.string, description);
                fs.add(focusSample);
*/
				Gson gson = new Gson();
				String focusSamplesJson = gson.toJson(fs);
				ViewUtil.displayToast(TestActivity.this, focusSamplesJson);

				Intent intent = new Intent(EXAMPLE_APP_ACTION);
				if (isActivityAvailable(intent)) {
					intent.putExtra(FOCUS_INPUT_EXTRA, focusSamplesJson);
					startActivityForResult(intent, EXAMPLE_APP_REQUEST);
				}
				else {
					ViewUtil.displayToast(TestActivity.this, "The is no available activity for the following action: " + EXAMPLE_APP_ACTION);
				}
			}
		});

		linearLayoutHorizontal8.addView(submitButton);

		Button resetButton = ViewFactory.createButton(this, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, half), "Reset");
		linearLayoutHorizontal8.addView(resetButton);

		linearLayoutVerticalForm.addView(linearLayoutHorizontal8);

		linearLayoutPageInfo.addView(linearLayoutVerticalForm);


	}

	@Override
	protected int getContentView()
	{
		return R.layout.activity_test;
	}

	public boolean isActivityAvailable(Intent intent)
	{
		return getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null;
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == EXAMPLE_APP_REQUEST) {
			if (resultCode == RESULT_OK) {
				String response = data.getStringExtra(FOCUS_OUTPUT_EXTRA);
				ViewUtil.displayToast(this, "The response obtained from EXAMPLE_APP was: " + response);
			}
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
