package com.example.yolandyan.movielist;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.GridView;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

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
        assertEquals(adapter.getCount(), adapter.mImageLink.length);
    }
}