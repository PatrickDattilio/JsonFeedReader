package com.dattilio.reader.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dattilio.reader.FeedReaderActivity;
import com.dattilio.reader.R;

/**
 * Created by Patrick Dattilio on 4/23/2014.
 */
public class ResponseReceiver extends BroadcastReceiver {
    public static final String ERROR_RESPONSE =
            "com.dattilio.intent.action.ERROR";

    private FeedReaderActivity feedReaderActivity;

    public ResponseReceiver(FeedReaderActivity feedReaderActivity) {
        this.feedReaderActivity = feedReaderActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        TextView errorText = (TextView) feedReaderActivity.findViewById(R.id.error_text);

        String error = intent.getStringExtra(NetworkService.ERROR_TEXT);
        errorText.setText(error);

        feedReaderActivity.findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
        feedReaderActivity.findViewById(R.id.listview).setVisibility(View.INVISIBLE);
        feedReaderActivity.findViewById(R.id.error_layout).setVisibility(View.VISIBLE);
    }
}
