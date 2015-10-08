package eu.focusnet.app.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import de.codecrafters.tableview.TableView;
import eu.focusnet.app.activity.R;

import static eu.focusnet.app.activity.R.drawable.custom_progress_dialog_animation;

/**
 * Created by admin on 09.09.2015.
 */
public class ViewFactory {

    public static LinearLayout createLinearLayout(Context context, int orientation, LinearLayout.LayoutParams layoutParams){
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(orientation);
        layout.setLayoutParams(layoutParams);
        return layout;
    }

//    public static View createViewWithLinearLayout(Context context, LinearLayout.LayoutParams layoutParams){
//        View view = new View(context);
//        view.setLayoutParams(layoutParams);
//        return view;
//    }

    public static ImageView createImageView(Context context, LinearLayout.LayoutParams layoutParams){
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(layoutParams);
        return imageView;
    }

    public static EditText createEditText(Context context, LinearLayout.LayoutParams layoutParams){
        EditText editText = new EditText(context);
        editText.setLayoutParams(layoutParams);
        return editText;
    }


    public static TableLayout createTableLayout(Context context, LinearLayout.LayoutParams layoutParams){
        TableLayout tableLayout = new TableLayout(context);
        tableLayout.setLayoutParams(layoutParams);
        return  tableLayout;
    }

    public static TableRow createTableRow(Context context, LinearLayout.LayoutParams layoutParams){
        TableRow tableRow = new TableRow(context);
        tableRow.setLayoutParams(layoutParams);
        return tableRow;
    }

    public static RadioGroup createRadioGroup(Context context, LinearLayout.LayoutParams layoutParams){
        RadioGroup radioGroup = new RadioGroup(context);
        radioGroup.setLayoutParams(layoutParams);
        return radioGroup;
    }

    public static RadioButton createRadioButton(Context context, LinearLayout.LayoutParams layoutParams){
        RadioButton radioButton = new RadioButton(context);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

    public static TableView<?> createTableView(Context context, LinearLayout.LayoutParams layoutParams, int numberOfColumn){
        TableView<?> tableView =  new TableView<>(context);
        tableView.setLayoutParams(layoutParams);
        tableView.setColumnCount(numberOfColumn);
        return tableView;
    }

    public static TextView createTextView(Context context, int style, LinearLayout.LayoutParams layoutParams, String text){
        TextView textView = new TextView(context);
        textView.setLayoutParams(layoutParams);
        textView.setTextAppearance(context, style);
        textView.setText(text);
        return textView;
    }

    public static Button createButton(Context context, LinearLayout.LayoutParams layoutParams, String text){
        Button button = new Button(context);
        button.setText(text);
        button.setLayoutParams(layoutParams);
        return button;
    }

    public static View createEmptyView(Context context, int width, int height, float weight){
        View view = new View(context);
        view.setLayoutParams(new LinearLayout.LayoutParams(width, height, weight));
        view.setVisibility(View.INVISIBLE);
        return view;
    }

    public static ProgressDialog createProgressDialog(Context context, CharSequence title, CharSequence message){
        ProgressDialog progDialog = new ProgressDialog(context);
        progDialog.setTitle(title);
        progDialog.setMessage(message);
        return progDialog;
    }

    public static CheckBox createCheckBox(Context context, LinearLayout.LayoutParams layoutParams) {
        CheckBox checkBox = new CheckBox(context);
        checkBox.setLayoutParams(layoutParams);
        return checkBox;
    }

    public static Spinner createSpinner(Context context, LinearLayout.LayoutParams layoutParams) {
        Spinner spinner = new Spinner(context);
        spinner.setLayoutParams(layoutParams);
        return spinner;
    }
}
