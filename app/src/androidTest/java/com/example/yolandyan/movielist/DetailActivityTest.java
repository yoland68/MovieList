package com.example.yolandyan.movielist;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yolandyan on 10/25/15.
 */
public class DetailActivityTest extends ActivityInstrumentationTestCase2<DetailActivity> {
    private DetailActivity mActivity;

    public DetailActivityTest() {
        super(DetailActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        mActivity = getActivity();
        super.setUp();
    }

    public void testTitleTextView() throws Exception {
        TextView textView = (TextView) getActivity().findViewById(R.id.detail_title);
        assertNotNull(textView);
    }

    public void testDescriptionTextView() throws Exception {
        TextView textView = (TextView) getActivity().findViewById(R.id.detail_description);
        assertNotNull(textView);
    }

    public void testImageView() throws Exception {
        ImageView imageView = (ImageView) getActivity().findViewById(R.id.detail_poster);
        assertNotNull(imageView);
    }
}
