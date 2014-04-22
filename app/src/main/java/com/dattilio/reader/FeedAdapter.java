package com.dattilio.reader;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

class FeedAdapter extends ArrayAdapter<FeedItem> {
    final FeedItem[] items;

    private final int srcWidth;
    private final int srcHeight;

    private final int avatarWidth;
    private final int avatarHeight;


    public FeedAdapter(Context context, FeedItem[] items) {
        super(context, R.layout.listview_item, items);
        this.items = items;
        Resources res = context.getResources();
        srcWidth = (int) res.getDimension(R.dimen.src_width);
        srcHeight = (int) res.getDimension(R.dimen.src_height);
        avatarWidth = (int) res.getDimension(R.dimen.avatar_width);
        avatarHeight = (int) res.getDimension(R.dimen.avatar_height);

    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView url;
        ImageView userImage;
        TextView userName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FeedItem item = items[position];
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.listview_item, null);
            viewHolder.image = (ImageView) convertView.findViewById(R.id.item_image);
            viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
            viewHolder.url = (TextView) convertView.findViewById(R.id.item_url);
            viewHolder.userImage = (ImageView) convertView.findViewById(R.id.item_user_image);
            viewHolder.userName = (TextView) convertView.findViewById(R.id.item_user_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //Load the image using Picasso into the main image, resizing/cropping to fit.
        Picasso.with(getContext()).load(item.src).resize(srcWidth, srcHeight).centerCrop().into(viewHolder.image);
        viewHolder.title.setText(item.desc);

        Picasso.with(getContext()).load(item.user.avatar.src).resize(avatarWidth, avatarHeight).centerCrop().into(viewHolder.userImage);
        viewHolder.userName.setText(item.user.username);
        viewHolder.url.setText(item.attrib);

        convertView.setOnLongClickListener(new ItemOnLongClickListener(item));
        convertView.setOnClickListener(new ItemOnClickListener(item, position));

        return convertView;
    }

    private class ItemOnLongClickListener implements View.OnLongClickListener {
        final FeedItem item;

        public ItemOnLongClickListener(FeedItem item) {
            this.item = item;
        }

        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setData(Uri.parse(item.href));
            v.getContext().getApplicationContext().startActivity(i);
            return true;
        }
    }

    /*
        Click Listeners
     */
    private class ItemOnClickListener implements View.OnClickListener {
        private final FeedItem item;
        private final int position;

        public ItemOnClickListener(FeedItem item, int position) {
            this.item = item;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            v.setSelected(true);
            ((FeedReaderActivity) getContext()).startSupportActionMode(new ItemActionMode(item, position));
        }
    }

    /*
    Action mode for contextual options after clicking an item
     */

    private class ItemActionMode implements ActionMode.Callback {
        private final FeedItem selectedItem;
        private final int position;

        public ItemActionMode(FeedItem item, int position) {

            this.selectedItem = item;
            this.position = position;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ((ListView) ((FeedReaderActivity) getContext()).findViewById(R.id.listview)).setItemChecked(position, true);
            mode.getMenuInflater().inflate(R.menu.action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.open_pin && selectedItem != null) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(selectedItem.href));
                getContext().startActivity(i);
            } else if (item.getItemId() == R.id.share && selectedItem != null) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, selectedItem.desc);
                i.putExtra(Intent.EXTRA_TEXT, selectedItem.href);
                getContext().startActivity(Intent.createChooser(i, "Share Pin"));
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            ((ListView) ((FeedReaderActivity) getContext()).findViewById(R.id.listview)).setItemChecked(position, false);
        }
    }

}
