package com.dattilio.reader;

import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dattilio.reader.network.NetworkService;
import com.dattilio.reader.network.ResponseReceiver;
import com.dattilio.reader.persist.ReaderContentProvider;

import java.io.IOException;
import java.net.MalformedURLException;

public class FeedReaderActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int FEED_LOADER = 0;
    private FeedAdapter adapter;
    private ResponseReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_reader);

        receiver = new ResponseReceiver(this);
        getSupportLoaderManager().initLoader(FEED_LOADER, null, FeedReaderActivity.this);
        adapter = new FeedAdapter(FeedReaderActivity.this, null);
        ((ListView) findViewById(R.id.listview)).setAdapter(adapter);

        findViewById(R.id.refresh_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
                findViewById(R.id.progressbar).setVisibility(View.VISIBLE);
                findViewById(R.id.listview).setVisibility(View.VISIBLE);
                findViewById(R.id.error_layout).setVisibility(View.INVISIBLE);
            }
        });

        refresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ResponseReceiver.ERROR_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    private void refresh() {
        NetworkService.startActionGet(this, getString(R.string.json_feed_url));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case FEED_LOADER:
                return new CursorLoader(this, ReaderContentProvider.CONTENT_URI, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        data.moveToFirst();
        if (!data.isAfterLast()) {
            findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
            adapter.changeCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

}
