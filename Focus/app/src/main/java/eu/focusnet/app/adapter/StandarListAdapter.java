package eu.focusnet.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.activity.R;

/**
 * Created by admin on 24.06.2015.
 */
public class StandarListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<AbstractListItem> abstractListItems;

    public StandarListAdapter(Context context, ArrayList<AbstractListItem> abstractListItems){
        this.inflater = android.view.LayoutInflater.from(context);
        this.abstractListItems =  abstractListItems;
    }

    @Override
    public int getViewTypeCount() { return 2; } //two types of views

    @Override
    public int getItemViewType(int position) {
        return abstractListItems.get(position).getType();
    }

    @Override
    public int getCount() {
        return abstractListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return abstractListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        ViewHolder holder;
        int itemViewType = getItemViewType(position);

        //Test is the view is already created
        if(convertView == null){
            holder = new ViewHolder();

            //Find out the item view type and set the corresponding layout
            if(itemViewType == HeaderListItem.TYPE_HEADER) {
                row = inflater.inflate(R.layout.list_item_header, parent, false);
                holder.leftIcon = (ImageView) row.findViewById(R.id.header_left_icon);
                holder.title = (TextView) row.findViewById(R.id.header_title);
                holder.rightIcon = (ImageView)row.findViewById(R.id.header_right_icon);
            }
            else {
                row = inflater.inflate(R.layout.standard_list_item, parent, false);
                holder.leftIcon = (ImageView) row.findViewById(R.id.icon);
                holder.title = (TextView) row.findViewById(R.id.title);
                holder.info = (TextView) row.findViewById(R.id.info);
                holder.rightIcon = (ImageView)row.findViewById(R.id.right_icon);
            }

            row.setTag(holder);
        }
        else{
            row = convertView;
            holder = (ViewHolder)row.getTag();
        }

        AbstractListItem abstractListItem = abstractListItems.get(position);
        if(itemViewType == HeaderListItem.TYPE_HEADER){
            HeaderListItem headerListItem = (HeaderListItem) abstractListItem;
            holder.rightIcon.setImageBitmap(headerListItem.getRightIcon());
        }
        else{
            holder.info.setText(((StandardListItem)abstractListItem).getInfo());
            holder.rightIcon.setImageBitmap(((StandardListItem)abstractListItem).getRightIcon());
        }

        //Present in old menu items
        holder.leftIcon.setImageBitmap(abstractListItem.getIcon());
        holder.title.setText(abstractListItem.getTitle());


        return row;
    }

    private class ViewHolder {
        public ImageView leftIcon;
        public TextView title;
        public TextView info;
        public ImageView rightIcon;
    }
}
