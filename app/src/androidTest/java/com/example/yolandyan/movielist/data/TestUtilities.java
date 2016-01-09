package com.example.yolandyan.movielist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.test.AndroidTestCase;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created by yoland on 1/8/16.
 */
public class TestUtilities extends AndroidTestCase{

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long createData(Context context) {
        MovieDataDbHelper openHelper = new MovieDataDbHelper(context);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        long id = db.insert(MovieDataContract.MovieEntry.TABLE_NAME, null, createMovieValues());
        assertTrue("Error: Failed to insert value to db", id != -1);
        db.close();
        return id;
    }

    public static ContentValues createMovieValues(){
        ContentValues cv = new ContentValues();
        byte[] b = {1,2,3};
        cv.put(MovieDataContract.MovieEntry.KEY_COL, "123456");
        cv.put(MovieDataContract.MovieEntry.TITLE_COL, "Yoland's Movie");
        cv.put(MovieDataContract.MovieEntry.POSTER_COL, b);
        cv.put(MovieDataContract.MovieEntry.DESC_COL, "bla bla bla");
        cv.put(MovieDataContract.MovieEntry.RATING_COL, 10);
        cv.put(MovieDataContract.MovieEntry.RELEASE_DATE_COL, "2010/10/20");
        return cv;
    }
}
