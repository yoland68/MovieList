package com.example.yolandyan.movielist;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by yoland on 1/15/16.
 */
public class AdjustedImageView extends ImageView{
    public AdjustedImageView(Context context) {
        super(context);
    }

    public AdjustedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdjustedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }
}
