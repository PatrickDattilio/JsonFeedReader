package com.dattilio.reader;

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
import com.dattilio.reader.persist.ReaderContentProvider;

import java.io.IOException;
import java.net.MalformedURLException;

public class FeedReaderActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private FeedAdapter adapter;
    private static final int FEED_LOADER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_reader);
        NetworkService.startActionGet(this, getString(R.string.json_feed_url));
        getSupportLoaderManager().initLoader(FEED_LOADER, null, FeedReaderActivity.this);
        adapter = new FeedAdapter(FeedReaderActivity.this, null);
        setupPage(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupPage(Exception e) {
        //If all went well
        findViewById(R.id.progressbar).setVisibility(View.INVISIBLE);
        ListView listView = ((ListView) findViewById(R.id.listview));
        if (e == null) {
            listView.setAdapter(adapter);
        }
        //We had some sort of exception, most likely an IOException due to a bad connection.
        else {

            TextView errorText = (TextView) findViewById(R.id.error_text);
            String error = "Unknown error occurred.";
            if (e instanceof MalformedURLException) {
                error = "The feed URL is malformed.";
            } else if (e instanceof IOException) {
                error = "Unable to connect to the feed, please refresh";
            }
            errorText.setText(error);
            listView.setVisibility(View.INVISIBLE);
            errorText.setVisibility(View.VISIBLE);
        }
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
        if(!data.isAfterLast())
            adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }
}
