package com.example.yolandyan.movielist;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.LinkedHashMap;

/**
 * Created by yolandyan on 10/20/15.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private LinkedHashMap<Long, String> mMovieData;
    private Long[] mMovieIds = {};

    public ImageAdapter(Context c) {
        mContext = c;
        mMovieData = new LinkedHashMap<>();
    }

    public int getCount() {
        return mMovieData.size();
    }

    public String getItem(int position) {
        // #Question: getting a stack overflow error here?
        return mMovieData.get(getItem(position));
    }

    public long getItemId(int position) {
        return mMovieIds[position];
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(mContext);
            Picasso.with(mContext).load(getItem(position)).into(imageView);
            Log.d("###YOLAND", String.format("Got them: %s", getItem(position)));
        } else {
            imageView = (ImageView) convertView;
        }
        return imageView;
    }

    public void setMovieData(LinkedHashMap<Long, String> movieData) {
        mMovieData = movieData;
        mMovieIds = movieData.keySet().toArray(new Long[movieData.size()]);
        notifyDataSetChanged();
    }

    public LinkedHashMap<Long, String> getMovieData() {
        return mMovieData;
    }

    public Long[] getMovieIds() {
        return mMovieIds;
    }
}
