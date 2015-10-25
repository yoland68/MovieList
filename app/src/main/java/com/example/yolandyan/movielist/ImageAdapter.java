package com.example.yolandyan.movielist;

import android.content.Context;
import android.util.Log;
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
    private String[] mImageLinks = {
            "http://unrealitymag.bcmediagroup.netdna-cdn.com/wp-content/uploads/2011/09/up_poster_by_adamrabalais-d4120tf-465x688.jpg",
            "http://unrealitymag.bcmediagroup.netdna-cdn.com/wp-content/uploads/2011/09/the_prestige_poster_by_adamrabalais-d3n1yk2-465x688.jpg",
            "http://unrealitymag.bcmediagroup.netdna-cdn.com/wp-content/uploads/2011/09/harry_potter_poa_poster_by_adamrabalais-d41kf65-465x688.jpg",
            "http://unrealitymag.bcmediagroup.netdna-cdn.com/wp-content/uploads/2011/09/2001__a_space_odyssey_poster_2_by_adamrabalais-d45pssr-465x688.jpg",
            "http://unrealitymag.bcmediagroup.netdna-cdn.com/wp-content/uploads/2011/09/2001__a_space_odyssey_poster_2_by_adamrabalais-d45pssr-465x688.jpg",
            "http://unrealitymag.bcmediagroup.netdna-cdn.com/wp-content/uploads/2011/09/american_psycho_poster_by_adamrabalais-d41qyfc-465x688.jpg",
            "http://unrealitymag.bcmediagroup.netdna-cdn.com/wp-content/uploads/2011/09/american_psycho_poster_by_adamrabalais-d41qyfc-465x688.jpg",
    };

    public ImageAdapter(Context c) {
        mContext = c;
        mImageLinks = new String[]{};
    }

    public int getCount() {
        return 0;
//        return mImageLinks.length;
    }

    public String getItem(int position) {
        return mImageLinks[position];
    }

    public long getItemId(int position) {
        return 0;
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

    public void setImageLinks(String[] imageLinks) {
        mImageLinks = imageLinks;
    }

    public String[] getImageLinks() {
        return mImageLinks;
    }


}
