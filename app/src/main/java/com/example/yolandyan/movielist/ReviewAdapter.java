package com.example.yolandyan.movielist;

import android.content.Context;
import android.util.Log;
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
    private ArrayList<Pair<String, String>> mReviewPairArray = new ArrayList<>();
    private Context mContxt;

    public ReviewAdapter(Context c) {
        mContxt = c;
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
        return position+1;
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
        Log.d("YOLAND: ", String.format("Pair is: %s", item.toString()));
        Log.d("YOLAND: ", String.format("Position is %s", Integer.toString(position)));
        Log.d("YOLAND: ", String.format("First is %s", item.first));
        Log.d("YOLAND: ", String.format("Second is %s", item.second));
        userTextView.setText(item.first);
        reviewTextView.setText(item.second);
        return convertView;
    }

    public void setData(ArrayList<Pair<String, String>> list) {
        mReviewPairArray = list;
        notifyDataSetChanged();
    }
}
