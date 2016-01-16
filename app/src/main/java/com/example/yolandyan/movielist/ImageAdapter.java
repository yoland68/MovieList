package com.example.yolandyan.movielist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.yolandyan.movielist.data.MovieDataContract;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item_movie, null);
        }
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.main_poster);
        ImageView star = (ImageView) convertView.findViewById(R.id.main_star);
        Cursor cursor = mContext.getContentResolver().query(
                MovieDataContract.MovieEntry.buildUriWithId(getItemId(position)),
                null,
                null,
                null,
                null
        );
        if (!(cursor.moveToFirst()) || cursor.getCount() ==0){
            Picasso.with(mContext).load(getItem(position)).into(imageView);
            star.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.star_empty));
        } else {
            int colIndex = cursor.getColumnIndex(MovieDataContract.MovieEntry.POSTER_COL);
            byte[] imageByteArray = cursor.getBlob(colIndex);
            ByteArrayInputStream imageStream = new ByteArrayInputStream(imageByteArray);
            Bitmap theImage= BitmapFactory.decodeStream(imageStream);
            imageView.setImageBitmap(theImage);
            star.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.star));
        }
        cursor.close();
        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor clickCursor = mContext.getContentResolver().query(
                        MovieDataContract.MovieEntry.buildUriWithId(getItemId(position)),
                        null,
                        null,
                        null,
                        null
                );
                if (!(clickCursor.moveToFirst()) || clickCursor.getCount() == 0) {
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();
                    ContentValues cv = new ContentValues();
                    cv.put(MovieDataContract.MovieEntry.KEY_COL, getItemId(position));
                    cv.put(MovieDataContract.MovieEntry.TITLE_COL, "Title stub");
                    cv.put(MovieDataContract.MovieEntry.POSTER_COL, bitmapdata);
                    cv.put(MovieDataContract.MovieEntry.DESC_COL, "Desc stub");
                    cv.put(MovieDataContract.MovieEntry.RATING_COL, 0);
                    cv.put(MovieDataContract.MovieEntry.RELEASE_DATE_COL, "Release date stub");
                    mContext.getContentResolver().insert(
                            MovieDataContract.MovieEntry.buildUriWithId(getItemId(position)),
                            cv
                    );
                    ImageView clickImageView = (ImageView) v.findViewById(R.id.main_star);
                    clickImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.star));
                } else {
                    Long x = getItemId(position);
                    Log.d("#YOLAND", x.toString());
                    String[] xString = new String[]{x.toString()};
                    int rows = mContext.getContentResolver().delete(
                            MovieDataContract.MovieEntry.buildUriWithId(getItemId(position)),
                            MovieDataContract.MovieEntry.KEY_COL, xString
                        );
                    ImageView clickImageView = (ImageView) v.findViewById(R.id.main_star);
                    clickImageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.star_empty));
                }
                clickCursor.close();
            }
        });
        return convertView;
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
