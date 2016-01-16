package com.example.yolandyan.movielist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.yolandyan.movielist.data.MovieDataContract;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.util.zip.Inflater;


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
//        if (convertView == null) {
//            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
//                    Context.LAYOUT_INFLATER_SERVICE);
//            convertView = inflater.inflate(R.layout.grid_item_movie, null);
//        }
//        ImageView imageView = (ImageView) convertView.findViewById(R.id.main_poster);
//        ImageView star = (ImageView) convertView.findViewById(R.id.main_star);
//        Cursor cursor = mContext.getContentResolver().query(
//                MovieDataContract.MovieEntry.buildUriWithId(getItemId(position)),
//                null,
//                null,
//                null,
//                null
//        );
//        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
//            Picasso.with(mContext).load(getItem(position)).into(imageView);
//            star.setImageDrawable(mContext.getDrawable(R.drawable.star_empty));
//        } else {
//            int colIndex = cursor.getColumnIndex(MovieDataContract.MovieEntry.POSTER_COL);
//            byte[] imageByteArray = cursor.getBlob(colIndex);
//
//            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
//            Bitmap theImage= BitmapFactory.decodeStream(imageStream);
//            imageView.setImageBitmap(theImage);
//            star.setImageDrawable(mContext.getDrawable(R.drawable.star));
//        }
//        return convertView;
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
