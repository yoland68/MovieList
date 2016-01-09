package com.example.yolandyan.movielist.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Created by yoland on 1/8/16.
 */
public class TestProvider extends AndroidTestCase {
    public void testBasicQuery() {
        TestUtilities.createData(getContext());
        ContentResolver cr = getContext().getContentResolver();
        Uri uri = MovieDataContract.MovieEntry.CONTENT_URI;
        Cursor cursor = cr.query(uri, null, null, null, null, null);
        Log.d("YOLAND", DatabaseUtils.dumpCursorToString(cursor));
        assertTrue(cursor != null);
        assertTrue(cursor.moveToFirst());
        TestUtilities.validateCursor("Error: data inserted not equal to data retrieved", cursor,
                TestUtilities.createMovieValues());
    }
}
