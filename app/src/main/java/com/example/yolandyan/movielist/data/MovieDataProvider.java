package com.example.yolandyan.movielist.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.DropBoxManager;
import android.support.annotation.Nullable;

/**
 * Created by yoland on 1/8/16.
 */
public class MovieDataProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDataDbHelper mOpenHelper;

    static final int ONE_MOVIE = 100;
    static final int ALL_MOVIES = 101;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieDataContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieDataContract.MovieEntry.PATH_MOVIE + "/#", ONE_MOVIE);
        matcher.addURI(authority, MovieDataContract.MovieEntry.PATH_MOVIE, ALL_MOVIES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDataDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ONE_MOVIE:
                return MovieDataContract.MovieEntry.CONTENT_ITEM_TYPE;
            case ALL_MOVIES:
                return MovieDataContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ALL_MOVIES:
                return mOpenHelper.getReadableDatabase().query(
                        MovieDataContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

            case ONE_MOVIE:
                String movieKey = MovieDataContract.MovieEntry.getMovieKeyFromUri(uri);
                return mOpenHelper.getReadableDatabase().query(
                        MovieDataContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieDataContract.MovieEntry.TABLE_NAME + "." + MovieDataContract.MovieEntry.KEY_COL + " = ? ",
                        new String[]{movieKey},
                        null,
                        null,
                        sortOrder
                );
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ONE_MOVIE:
                long id = mOpenHelper.getWritableDatabase().insert(MovieDataContract.MovieEntry.TABLE_NAME, null, values);
                return MovieDataContract.MovieEntry.buildUriWithId(id);
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowDeleted;
        switch (match) {
            case ONE_MOVIE:
                String key = MovieDataContract.MovieEntry.getMovieKeyFromUri(uri);
                rowDeleted = db.delete(
                        MovieDataContract.MovieEntry.TABLE_NAME,
                        MovieDataContract.MovieEntry.KEY_COL + "=?",
                        new String[]{key});
                break;
            case ALL_MOVIES:
                rowDeleted = db.delete(MovieDataContract.MovieEntry.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }
        if (rowDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ONE_MOVIE:
                return mOpenHelper.getWritableDatabase().update(MovieDataContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
            case ALL_MOVIES:
                return mOpenHelper.getWritableDatabase().update(MovieDataContract.MovieEntry.TABLE_NAME, values, null, null);
            default:
                throw new UnsupportedOperationException("Invalid uri: " + uri);
        }
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
