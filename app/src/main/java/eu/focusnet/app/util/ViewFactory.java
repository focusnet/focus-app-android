package eu.focusnet.app.util;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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

    public static TextView createTextView(Context context, LinearLayout.LayoutParams layoutParams, int size, String text){
        TextView textView = new TextView(context);
        textView.setTextSize(size);
        textView.setText(text);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public static Button createButton(Context context, LinearLayout.LayoutParams layoutParams, String text){
        Button button = new Button(context);
        button.setText(text);
        button.setLayoutParams(layoutParams);
        return button;
    }

}
