package com.example.yolandyan.movielist.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by yoland on 1/8/16.
 */
public class TestDb extends AndroidTestCase{
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDataDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        HashSet<String> tableHashSet = new HashSet<>();
        tableHashSet.add(MovieDataContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDataDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDataDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        do{
            tableHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: DB created without movie table", tableHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieDataContract.MovieEntry.TABLE_NAME + ")", null);

        assertTrue("Error: can not query database for info", c.moveToFirst());

        final HashSet<String> columnHashSet = new HashSet<>();
        columnHashSet.add(MovieDataContract.MovieEntry._ID);
        columnHashSet.add(MovieDataContract.MovieEntry.KEY_COL);
        columnHashSet.add(MovieDataContract.MovieEntry.TITLE_COL);
        columnHashSet.add(MovieDataContract.MovieEntry.POSTER_COL);
        columnHashSet.add(MovieDataContract.MovieEntry.DESC_COL);
        columnHashSet.add(MovieDataContract.MovieEntry.RELEASE_DATE_COL);
        columnHashSet.add(MovieDataContract.MovieEntry.RATING_COL);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            columnHashSet.remove(c.getString(columnNameIndex));
        } while (c.moveToNext());

        assertTrue("Error: database doesn't contain all the columns", columnHashSet.isEmpty());
        db.close();
    }
}

