package com.example.yolandyan.movielist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by yolandyan on 10/20/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mPosterPaths = {};
    private Long[] mMovieIds = {};

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mPosterPaths.length;
    }

    public String getItem(int position) {
        return mPosterPaths[position];
    }

    public long getItemId(int position) {
        return mMovieIds[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(getItem(position)).into(imageView);
        return imageView;
    }

    public void setMovieData(Long[] movieIds, String[] posterPaths) {
        mMovieIds = movieIds;
        mPosterPaths = posterPaths;
        notifyDataSetChanged();
    }

    public String[] getPosterPaths() {
        return mPosterPaths;
    }

    public Long[] getMovieIds() {
        return mMovieIds;
    }
}
