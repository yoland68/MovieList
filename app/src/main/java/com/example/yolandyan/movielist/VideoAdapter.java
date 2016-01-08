package com.example.yolandyan.movielist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by yolandyan on 1/7/16.
 */
public class VideoAdapter extends BaseAdapter{
    private String[] mLinks = {};
    private String[] mVideoNames = {};
    private Context mContext;

    public VideoAdapter(Context ctx) {
        mContext = ctx;
    }

    @Override
    public int getCount() {
        return mLinks.length;
    }

    @Override
    public Object getItem(int position) {
        return mVideoNames[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_videos, null);
        }
        ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_image);
        Picasso.with(mContext).load(mLinks[position]).into(thumbnail);
        TextView videoTitle = (TextView) convertView.findViewById(R.id.video_title);
        videoTitle.setText(mVideoNames[position]);
        return convertView;
    }

    public void setData(String[] links, String[] titles) {
        mLinks = links;
        mVideoNames = titles;
        notifyDataSetChanged();
    }
}
