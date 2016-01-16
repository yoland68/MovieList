package com.example.yolandyan.movielist.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by yoland on 1/8/16.
 */
public class MovieDataContract {
    public static String CONTENT_AUTHORITY = "com.example.yolandyan.movielist";

    public static class MovieEntry implements BaseColumns {
        public static String PATH_MOVIE = "movie";
        public static Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)
                .buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static String TABLE_NAME = "movie";
        public static String KEY_COL = "movie_id";
        public static String TITLE_COL = "title";
        public static String POSTER_COL = "poster";
        public static String DESC_COL = "description";
        public static String RELEASE_DATE_COL = "release_date";
        public static String RATING_COL = "rating";

        public static String getMovieKeyFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static Uri buildUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }
}
