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
import eu.focusnet.focus.model.DrawerListItem;

/**
 * Created by admin on 15.06.2015.
 */
public class DrawerListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<DrawerListItem> drawerListItems;

    public DrawerListAdapter(Context context, ArrayList<DrawerListItem> drawerListItems){
        this.inflater = LayoutInflater.from(context);
        this.drawerListItems =  drawerListItems;
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

        //Test is the view is already created
        if(convertView == null){
            row = inflater.inflate(R.layout.drawer_list_item, parent, false);
            holder = new ViewHolder();
            holder.icon = (ImageView) row.findViewById(R.id.icon);
            holder.title = (TextView) row.findViewById(R.id.title);
            holder.info = (TextView) row.findViewById(R.id.info);
            row.setTag(holder);
        }
        else{
            row = convertView;
            holder = (ViewHolder)row.getTag();
        }

        DrawerListItem drawerListItem = drawerListItems.get(position);
        holder.icon.setImageBitmap(drawerListItem.getIcon());
        holder.title.setText(drawerListItem.getTitle());
        holder.info.setText(drawerListItem.getInfo());

        return row;
    }

    private class ViewHolder {
        public ImageView icon;
        public TextView title, info;
    }
}
