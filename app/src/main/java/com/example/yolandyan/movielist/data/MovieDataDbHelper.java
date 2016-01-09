package com.example.yolandyan.movielist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yoland on 1/8/16.
 */
public class MovieDataDbHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "moviedata.db";

    public MovieDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE =
                "CREATE TABLE " + MovieDataContract.MovieEntry.TABLE_NAME
                        + " (" + MovieDataContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + MovieDataContract.MovieEntry.KEY_COL + " TEXT NOT NULL, "
                        + MovieDataContract.MovieEntry.TITLE_COL + " TEXT NOT NULL, "
                        + MovieDataContract.MovieEntry.POSTER_COL + " BLOB NOT NULL, "
                        + MovieDataContract.MovieEntry.DESC_COL + " TEXT NOT NULL, "
                        + MovieDataContract.MovieEntry.RATING_COL + " INTEGER NOT NULL, "
                        + MovieDataContract.MovieEntry.RELEASE_DATE_COL + " TEXT NOT NULL"
                        + ");";
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieDataContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
