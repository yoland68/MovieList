package com.example.yolandyan.movielist;

import android.net.Uri;

/**
 * Created by yoland on 1/8/16.
 */
public class Utilities {
    public static String imageUrl = "https://img.youtube.com/vi";
    public static String youtubeUrl = "https://youtu.be";
    public static String mQuality = "mqdefault.jpg";

    public static Uri constructYoutubeLink(String prefix, String path, String postfix){
        Uri uri = Uri.parse(prefix).buildUpon()
                .appendEncodedPath(path).build();
        if (postfix != null) {
            uri = uri.buildUpon().appendEncodedPath(postfix).build();
        }
        return uri;
    }
}
