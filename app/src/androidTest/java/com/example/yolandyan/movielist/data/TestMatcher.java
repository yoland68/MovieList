package com.example.yolandyan.movielist.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.regex.Matcher;

/**
 * Created by yoland on 1/15/16.
 */
public class TestMatcher extends AndroidTestCase{
    private Uri mAllMovieUri = MovieDataContract.MovieEntry.CONTENT_URI;
    private Uri mOneMovieUri = MovieDataContract.MovieEntry.buildUriWithId(12345);

    public void testMatching() {
        UriMatcher testMatcher = MovieDataProvider.buildUriMatcher();
        assertEquals("All Movie Matcher doesn't work", testMatcher.match(mOneMovieUri), MovieDataProvider.ONE_MOVIE);
        assertEquals("All Movie Matcher doesn't work", testMatcher.match(mAllMovieUri), MovieDataProvider.ALL_MOVIES);

    }
}
