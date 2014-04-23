package com.dattilio.reader.persist;

/**
 * Created by Patrick Dattilio on 4/22/2014.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// class that creates and manages the provider's database
public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "ReaderDatabase";
    static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "feedItemTable";
    public static final String ID = "_id";
    public static final String ATTRIB = "attrib";
    public static final String DESC = "desc";
    public static final String HREF = "href";
    public static final String SRC = "src";
    public static final String NAME = "name";
    public static final String AVATAR_SRC = "avatar_src";
    public static final String AVATAR_WIDTH = "avatar_width";
    public static final String AVATAR_HEIGHT = "avatar_height";
    public static final String USERNAME = "username";


    static final String CREATE_TABLE =
            " CREATE TABLE " + TABLE_NAME +
                    " (" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    " " + ATTRIB + " TEXT NOT NULL, " +
                    " " + DESC + " TEXT NOT NULL, " +
                    " " + HREF + " TEXT NOT NULL, " +
                    " " + SRC + " TEXT NOT NULL, " +
                    " " + NAME + " TEXT NOT NULL, " +
                    " " + AVATAR_SRC + " TEXT NOT NULL, " +
                    " " + AVATAR_WIDTH + " INTEGER NOT NULL, " +
                    " " + AVATAR_HEIGHT + " INTEGER NOT NULL, " +
                    " " + USERNAME + " TEXT NOT NULL);";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DBHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ". Old data will be destroyed"
        );
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}