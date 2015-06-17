package eu.focusnet.focus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.focus.activity.R;
import eu.focusnet.focus.common.AbtractDrawListItem;
import eu.focusnet.focus.model.HeaderDrawerListItem;
import eu.focusnet.focus.model.StandardDrawerListItem;

/**
 * Created by admin on 15.06.2015.
 */
public class DrawerListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<AbtractDrawListItem> drawerListItems;

    public DrawerListAdapter(Context context, ArrayList<AbtractDrawListItem> drawerListItems){
        this.inflater = LayoutInflater.from(context);
        this.drawerListItems =  drawerListItems;
    }

    @Override
    public int getViewTypeCount() {
        return HeaderDrawerListItem.TYPE_HEADER + StandardDrawerListItem.TYPE_STANDARD;
    }

    @Override
    public int getItemViewType(int position) {
        return drawerListItems.get(position).getType();
    }

    @Override
    public int getCount() {
        return drawerListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return drawerListItems.get(position);
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
            if(itemViewType == HeaderDrawerListItem.TYPE_HEADER) {
                row = inflater.inflate(R.layout.drawer_list_item_header, parent, false);
                holder.icon = (ImageView) row.findViewById(R.id.logo);
                holder.title = (TextView) row.findViewById(R.id.user); //here is the value of title the user name
                holder.email = (TextView)row.findViewById(R.id.email);
                holder.company = (TextView)row.findViewById(R.id.company);
            }
            else {
                row = inflater.inflate(R.layout.drawer_list_item, parent, false);
                //Present in R.layout.drawer_list_item layout
                holder.icon = (ImageView) row.findViewById(R.id.icon);
                holder.title = (TextView) row.findViewById(R.id.title);
                holder.info = (TextView) row.findViewById(R.id.info);
            }

            row.setTag(holder);
        }
        else{
            row = convertView;
            holder = (ViewHolder)row.getTag();
        }

        AbtractDrawListItem drawerListItem = drawerListItems.get(position);
        if(itemViewType == HeaderDrawerListItem.TYPE_HEADER){
            HeaderDrawerListItem headerDrawerListItem = (HeaderDrawerListItem) drawerListItem;
            holder.email.setText(headerDrawerListItem.getEmail());
            holder.company.setText(headerDrawerListItem.getCompany());
        }
        else{
            holder.info.setText(((StandardDrawerListItem)drawerListItem).getInfo());
        }

        //Present in old menu items
        holder.icon.setImageBitmap(drawerListItem.getIcon());
        holder.title.setText(drawerListItem.getTitle());


        return row;
    }

    private class ViewHolder {

        public ImageView icon;
        public TextView title, info, email, company;
    }
}
