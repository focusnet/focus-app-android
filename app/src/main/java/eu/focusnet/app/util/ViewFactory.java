package eu.focusnet.app.util;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Objects;

import de.codecrafters.tableview.TableView;

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

    public static TextView createTextView(Context context, int style, LinearLayout.LayoutParams layoutParams, String text){
        TextView textView = new TextView(context, null, style);
        textView.setLayoutParams(layoutParams);
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

}
