package com.dattilio.reader;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dattilio.reader.types.Avatar;
import com.dattilio.reader.types.FeedItem;
import com.dattilio.reader.types.User;
import com.squareup.picasso.Picasso;

class FeedAdapter extends CursorAdapter {
    private final int srcWidth;
    private final int srcHeight;

    private final int avatarWidth;
    private final int avatarHeight;

    private LayoutInflater inflater;

    public FeedAdapter(Context context, Cursor cursor) {

        super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
        Resources res = context.getResources();
        srcWidth = (int) res.getDimension(R.dimen.src_width);
        srcHeight = (int) res.getDimension(R.dimen.src_height);
        avatarWidth = (int) res.getDimension(R.dimen.avatar_width);
        avatarHeight = (int) res.getDimension(R.dimen.avatar_height);
        inflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;
        TextView url;
        ImageView userImage;
        TextView userName;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.listview_item, parent, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.image = (ImageView) view.findViewById(R.id.item_image);
        viewHolder.title = (TextView) view.findViewById(R.id.item_title);
        viewHolder.url = (TextView) view.findViewById(R.id.item_url);
        viewHolder.userImage = (ImageView) view.findViewById(R.id.item_user_image);
        viewHolder.userName = (TextView) view.findViewById(R.id.item_user_name);


        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vhold = (ViewHolder) view.getTag();
        Avatar avatar = new Avatar(cursor.getString(FeedQuery.AVATAR_SRC), cursor.getInt(FeedQuery.AVATAR_WIDTH), cursor.getInt(FeedQuery.AVATAR_HEIGHT));
        User user = new User(cursor.getString(FeedQuery.NAME), avatar, cursor.getString(FeedQuery.USERNAME));
        FeedItem item = new FeedItem(cursor.getString(FeedQuery.HREF), cursor.getString(FeedQuery.SRC), cursor.getString(FeedQuery.DESC), cursor.getString(FeedQuery.ATTRIB), user);

        //Load the image using Picasso into the main image, resizing/cropping to fit.
        Picasso.with(context).load(item.src).resize(srcWidth, srcHeight).centerCrop().into(vhold.image);
        vhold.title.setText(item.desc);

        Picasso.with(context).load(avatar.src).resize(avatarWidth, avatarHeight).centerCrop().into(vhold.userImage);
        vhold.userName.setText(user.username);
        vhold.url.setText(item.attrib);

        view.setOnLongClickListener(new ItemOnLongClickListener(item));
        view.setOnClickListener(new ItemOnClickListener(item, context, cursor.getPosition()));
    }

    private interface FeedQuery {
        final int AVATAR_SRC = 0;
        final int USERNAME = 1;
        final int DESC = 2;
        final int ID = 3;
        final int NAME = 4;
        final int AVATAR_WIDTH = 5;
        final int SRC = 6;
        final int HREF = 7;
        final int ATTRIB = 8;
        final int AVATAR_HEIGHT = 9;
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
        private final Context context;

        public ItemOnClickListener(FeedItem item, Context context, int position) {
            this.item = item;
            this.context = context;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            v.setSelected(true);
            ((FeedReaderActivity) context).startSupportActionMode(new ItemActionMode(item, context, position));
        }
    }

    /*
    Action mode for contextual options after clicking an item
     */

    private class ItemActionMode implements ActionMode.Callback {
        private final FeedItem selectedItem;
        private final int position;
        private final Context context;

        public ItemActionMode(FeedItem item, Context context, int position) {
            this.selectedItem = item;
            this.context = context;
            this.position = position;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            ((ListView) ((FeedReaderActivity) context).findViewById(R.id.listview)).setItemChecked(position, true);
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
                context.startActivity(i);
            } else if (item.getItemId() == R.id.share && selectedItem != null) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, selectedItem.desc);
                i.putExtra(Intent.EXTRA_TEXT, selectedItem.href);
                context.startActivity(Intent.createChooser(i, "Share Pin"));
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            ((ListView) ((FeedReaderActivity) context).findViewById(R.id.listview)).setItemChecked(position, false);
        }
    }

}
