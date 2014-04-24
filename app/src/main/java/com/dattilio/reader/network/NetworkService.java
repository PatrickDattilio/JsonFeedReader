package com.dattilio.reader.network;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.dattilio.reader.types.FeedItem;
import com.dattilio.reader.persist.DBHelper;
import com.dattilio.reader.persist.ReaderContentProvider;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class NetworkService extends IntentService {

    private static final String ACTION_GET = "com.dattilio.reader.action.GET";
    private static final String EXTRA_URL = "com.dattilio.reader.extra.URL";

    public static void startActionGet(Context context, String url) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_GET);
        intent.putExtra(EXTRA_URL, url);
        context.startService(intent);
    }

    public NetworkService() {
        super("NetworkService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionGet(url);
            }
        }
    }

    /**
     * When our service recieves an ACTION_GET, we attempt to parse an array of FeedItems from a JSON
     * string returned by the provided url.
     *
     * @param urlString - Url that we want to retrieve the JSON string from.
     */
    private void handleActionGet(String urlString) {
        try {

            OkHttpClient client = new OkHttpClient();
            URL url = new URL(urlString);
            Gson gson = new Gson();
            //Using OkHttp and GSON we are able to do the downloading and parsing in essentially one line
            JsonReader reader = new JsonReader(new InputStreamReader(client.open(url).getInputStream()));
            FeedItem[] items = gson.fromJson(reader, FeedItem[].class);
            reader.close();

            //Now we update the ContentProvider with the results
            ContentResolver contentResolver = getContentResolver();
            for (FeedItem item:items) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.ATTRIB,item.attrib);
                values.put(DBHelper.DESC,item.desc);
                values.put(DBHelper.HREF,item.href);
                values.put(DBHelper.SRC,item.src);
                values.put(DBHelper.NAME,item.user.name);
                values.put(DBHelper.AVATAR_SRC,item.user.avatar.src);
                values.put(DBHelper.AVATAR_WIDTH,item.user.avatar.width);
                values.put(DBHelper.AVATAR_HEIGHT,item.user.avatar.height);
                values.put(DBHelper.USERNAME,item.user.username);
                contentResolver.insert(ReaderContentProvider.CONTENT_URI,values);
            }
        } catch (MalformedURLException e) {
            Log.e("NetworkService","Bad Url",e);
        } catch (IOException e) {
            Log.e("NetworkService","IOException",e);
        }
    }
}