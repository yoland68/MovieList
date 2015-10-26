package com.example.yolandyan.movielist;

import android.net.Uri;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.GridView;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private final String IMAGE_BASE_URL = "image.tmdb.org";
    private final String SORT_PARAM = "sort_by";
    private final String SORT = "popularity.desc";

    private MainActivity mMainActivity;
    private GridView mGridView;


    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMainActivity = getActivity();
        mGridView = (GridView) mMainActivity.findViewById(R.id.poster_gridview);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testPreconditions() {
        assertNotNull(mMainActivity);
        assertNotNull(mGridView);
    }

    public void testGridViewAdapter() {
        ImageAdapter adapter = (ImageAdapter) mGridView.getAdapter();
        assertNotNull(adapter);
        assertEquals(adapter.getCount(), adapter.getMovieData().size());
        assertEquals(adapter.getCount(), adapter.getMovieIds().length);
    }

    public void testGridViewItem() {
        ImageAdapter adapter = (ImageAdapter) mGridView.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            String posterUrlString = (String) mGridView.getItemAtPosition(i);
            Uri posterUri = Uri.parse(posterUrlString);
            assertEquals(posterUri.getAuthority(), "image.tmdb.org");
        }
    }

    //How to do unit testing? e.g whether json data is being processed correctly?
    //how to test image views
}