package eu.focusnet.app.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.TableDataRowColorizers;
import eu.focusnet.app.R;
import eu.focusnet.app.common.WidgetFragment;

/**
 * Created by yandypiedra on 13.01.16.
 */
public class TableWidgetFragment extends WidgetFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View viewRoot = inflater.inflate(R.layout.fragment_table, container, false);


        //TODO values hard coded
        String[][] data = {
                {"This", "is", "a", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"},
                {"and", "a", "second", "test"}

        };

        String[] header = {"Text 1", "Text 2", "Text 3", "Text 4"};

        TableView tableView = (TableView) viewRoot.findViewById(R.id.tableView);
        SimpleTableHeaderAdapter adapter = new SimpleTableHeaderAdapter(getActivity(), header);
        adapter.setPaddingTop(25);
        adapter.setPaddingBottom(25);
        adapter.setTextColor(getResources().getColor(R.color.table_header_text));
        tableView.setHeaderAdapter(adapter);
        tableView.setHeaderBackgroundColor(getResources().getColor(R.color.colorPrimary));
        tableView.setDataAdapter(new SimpleTableDataAdapter(getActivity(), data));
        int colorEvenRows = getResources().getColor(R.color.table_data_row_even);
        int colorOddRows = getResources().getColor(R.color.table_data_row_odd);
        tableView.setDataRowColoriser(TableDataRowColorizers.alternatingRows(colorEvenRows, colorOddRows));

        return viewRoot;
    }

}
