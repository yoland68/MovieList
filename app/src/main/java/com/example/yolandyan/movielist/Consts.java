package com.example.yolandyan.movielist;

/**
 * Created by yolandyan on 10/29/15.
 */
public class Consts {
    public static final String BASE_URL = "http://api.themoviedb.org/3/";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    public static final String MOVIE_PATH = "movie";
    public static final String VIDEO_PATH = "videos";
    public static final String REVIEW_PATH = "reviews";
    public static final String POPULAR_PATH = "popular";
    public static final String RATING_PATH = "top_rated";
    public static final String API_KEY_PARAM = "api_key";
    public static final String IMAGE_SIZE = "w500";

    public static final String JSON_KEY_RESULT = "results";

    public static final String SORT_BY_POPULARITY = "popularity";
    public static final String SORT_BY_RATING = "rating";
    public static final String SORT_BY_FAVORITE = "favorite";
    public static final String SORT_KEY = "sort";

    public static enum FetchOptions {
        FETCH_GENERAL, FETCH_VIDEOS, FETCH_REVIEWS
    }
}
