package eu.focusnet.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import eu.focusnet.app.activity.BookmarkService;
import eu.focusnet.app.common.AbstractListItem;
import eu.focusnet.app.model.ui.HeaderListItem;
import eu.focusnet.app.model.ui.StandardListItem;
import eu.focusnet.app.activity.R;
import eu.focusnet.app.util.Constant;
import eu.focusnet.app.util.ViewUtil;

/**
 * Created by admin on 24.06.2015.
 */
public class StandardListAdapter extends BaseAdapter {

    private static final String TAG = StandardListAdapter.class.getName();

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<AbstractListItem> abstractListItems;

    public StandardListAdapter(Context context, ArrayList<AbstractListItem> abstractListItems){
        this.context = context;
        this.inflater = android.view.LayoutInflater.from(this.context);
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
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row;
        ViewHolder holder;
        int itemViewType = getItemViewType(position);

        //Test is the imageView is already created
        if(convertView == null){
            holder = new ViewHolder();

            //Find out the item imageView type and set the corresponding layout
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
                holder.rightIcon = (ImageView) row.findViewById(R.id.right_icon);
                holder.rightIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final StandardListItem standardListItem = (StandardListItem) abstractListItems.get(position);

                        if (standardListItem.getRightIcon() != null) {
                            final ImageView imageView = (ImageView) v;

                            final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            View dialogView =  inflater.inflate(R.layout.custom_alert_dialog_layout, null);
                            builder.setView(dialogView);
                            final Dialog dialog = builder.create();

                            TextView dialogTitle = ((TextView) dialogView.findViewById(R.id.dialog_title));
                            TextView selectedContext = (TextView) dialogView.findViewById(R.id.selectedContext);
                            selectedContext.setText(standardListItem.getTitle());

                            Button ok = (Button) dialogView.findViewById(R.id.ok);
                            Button cancel = (Button) dialogView.findViewById(R.id.cancel);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d(TAG, "Cancel clicked");
                                    //Do nothing
                                    dialog.dismiss();
                                }
                            });

                            boolean isRightIconActive = standardListItem.isRightIconActive();
                            Log.d(TAG, "Is right icon active: " + isRightIconActive);

                            final Intent intent = new Intent(context, BookmarkService.class);
                            intent.putExtra(Constant.NAME, standardListItem.getTitle());
                            intent.putExtra(Constant.PATH, standardListItem.getPath());
                            intent.putExtra(Constant.ORDER, standardListItem.getOrder());
                            intent.putExtra(Constant.BOOKMARK_TYPE, standardListItem.getTypeOfBookmark());

                            if (isRightIconActive) {
                                dialogTitle.setText("Are you sure you want to remove this context from the Bookmarks?"); //TODO internationalize all these message
                                ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        standardListItem.setIsRightIconActive(false);
                                        imageView.setImageBitmap(ViewUtil.getBitmap(context, R.drawable.ic_star_o));
                                        intent.putExtra(Constant.IS_TO_SAVE, false);
                                        context.startService(intent);
                                        Log.d(TAG, "OK clicked");
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                dialogTitle.setText("Are you sure you want to add this context to the Bookmarks?");
                                    ok.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        standardListItem.setIsRightIconActive(true);
                                        imageView.setImageBitmap(ViewUtil.getBitmap(context, R.drawable.ic_star));
                                        intent.putExtra(Constant.IS_TO_SAVE, true);
                                        context.startService(intent);
                                        Log.d(TAG, "OK clicked");
                                        dialog.dismiss();
                                    }
                                });
                            }

                            dialog.show();
                        }
                    }
                });
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
            holder.info.setText(((StandardListItem) abstractListItem).getInfo());
            holder.rightIcon.setImageBitmap(((StandardListItem) abstractListItem).getRightIcon());
        }

        //Present in all menu items
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
