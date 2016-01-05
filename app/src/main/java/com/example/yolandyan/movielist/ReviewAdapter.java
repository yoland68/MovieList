package com.example.yolandyan.movielist;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by yolandyan on 1/1/16.
 */
public class ReviewAdapter extends BaseAdapter {
    private ArrayList<Pair<String, String>> mReviewPairArray;
    private Context mContxt;

    public ReviewAdapter(Context c) {
        mContxt = c;
        mReviewPairArray = new ArrayList<>();
}

    @Override
    public int getCount() {
        return mReviewPairArray.size();
    }

    @Override
    public Object getItem(int position) {
        return mReviewPairArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContxt.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_review, null);
        }
        TextView userTextView = (TextView) convertView.findViewById(R.id.review_user);
        TextView reviewTextView = (TextView) convertView.findViewById(R.id.review_text);
        Pair<String, String> item = (Pair<String, String>)getItem(position);
        userTextView.setText(item.first);
        reviewTextView.setText(item.second);
        return convertView;
    }

    public void setData(ArrayList<Pair<String, String>> list) {
        mReviewPairArray = list;
        notifyDataSetChanged();
    }
}
