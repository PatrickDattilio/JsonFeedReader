package com.dattilio.reader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class FeedReaderActivity extends ActionBarActivity {

    private FeedAdapter adapter;
    private ParseTask parseTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_reader);
        boolean parse = true;
        if (savedInstanceState != null) {
            FeedItem[] items = (FeedItem[]) savedInstanceState.getParcelableArray("items");
            if (items != null) {
                parse = false;
                adapter = new FeedAdapter(FeedReaderActivity.this, items);
                setupPage(null);
            }
        }

        if (parse) {
            parseTask = new ParseTask();
            parseTask.execute();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (adapter != null && !adapter.isEmpty())
            outState.putParcelableArray("items", adapter.items);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (parseTask != null)
            parseTask.cancel(true);
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

    private class ParseTask extends AsyncTask<Void, Void, Exception> {

        @Override
        protected Exception doInBackground(Void... params) {
            Exception exception = null;
            try {

                OkHttpClient client = new OkHttpClient();
                URL url = new URL(getString(R.string.json_feed_url));
                Gson gson = new Gson();
                JsonReader reader = new JsonReader(new InputStreamReader(client.open(url).getInputStream()));
                FeedItem[] items = gson.fromJson(reader, FeedItem[].class);
                reader.close();
                adapter = new FeedAdapter(FeedReaderActivity.this, items);
            } catch (MalformedURLException e) {
                exception = e;
            } catch (IOException e) {
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception e) {
            setupPage(e);
        }
    }
}
