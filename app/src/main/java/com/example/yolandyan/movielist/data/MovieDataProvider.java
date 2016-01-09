package com.example.yolandyan.movielist.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
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

        matcher.addURI(authority, MovieDataContract.PATH_ALL_MOVIES, ALL_MOVIES);
        matcher.addURI(authority, MovieDataContract.PATH_ONE_MOVIE + "/#", ONE_MOVIE);

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
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
