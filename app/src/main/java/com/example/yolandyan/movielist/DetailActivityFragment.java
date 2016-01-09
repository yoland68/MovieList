package com.example.yolandyan.movielist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.LinkedHashMap;


/**
 * Created by yolandyan on 10/25/15.
 */
public class DetailActivityFragment extends Fragment {
    private Long mMovieId;
    private String TITLE_KEY = "title";
    private String POSTER_URL_KEY = "poster_path";
    private String DESCRIPTION_KEY = "overview";
    private String VOTE_AVG_KEY = "vote_average";
    private String REL_DATE_KEY = "release_date";

    private VideoAdapter mVideoAdapter;
    private ReviewAdapter mReviewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVideoAdapter =
                new VideoAdapter(getActivity());
        mReviewAdapter = new ReviewAdapter(getActivity());
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mMovieId = intent.getLongExtra(Intent.EXTRA_TEXT, -1);
        }
        final ListView videoList = (ListView) rootView.findViewById(R.id.trailer_list);
        videoList.setAdapter(mVideoAdapter);
        ListView reviewList = (ListView) rootView.findViewById(R.id.review_list);
        reviewList.setAdapter(mReviewAdapter);

        videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoAdapter videoAdapter = (VideoAdapter)videoList.getAdapter();
                Uri link = Utilities.constructYoutubeLink(Utilities.youtubeUrl,
                        videoAdapter.getKey(position), null);
                Intent intent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(intent);
            }
        });
        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_update_detail) {
            updateMovieData();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieData();
    }

    public void updateMovieData() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            FetchDetailMovieTask generalTask = new FetchDetailMovieTask();
            generalTask.execute(Consts.FetchOptions.FETCH_GENERAL);
            FetchDetailMovieTask trailerTask = new FetchDetailMovieTask();
            trailerTask.execute(Consts.FetchOptions.FETCH_VIDEOS);
            FetchDetailMovieTask reviewTask = new FetchDetailMovieTask();
            reviewTask.execute(Consts.FetchOptions.FETCH_REVIEWS);
        } else {
            Toast.makeText(getActivity(), "No Internet", Toast.LENGTH_SHORT).show();
        }
    }

    public class FetchDetailMovieTask extends AsyncTask<Consts.FetchOptions, Void, Void> {
        private final String SUB_LOGTAG = FetchDetailMovieTask.class.getSimpleName();
        private HashMap<String, String> mMovieGeneralData;
        private LinkedHashMap<String, String> mVideoData;
        private ArrayList<Pair<String, String>> mReviewData;
        private Consts.FetchOptions mOption;

        public void processMovieDataString(String dataString)
                throws JSONException {
            HashMap<String, String> result = new HashMap<>();

            JSONObject movieDataJSON = new JSONObject(dataString);
            result.put(TITLE_KEY, movieDataJSON.getString(TITLE_KEY));
            result.put(DESCRIPTION_KEY, movieDataJSON.getString(DESCRIPTION_KEY));
            result.put(REL_DATE_KEY, movieDataJSON.getString(REL_DATE_KEY));
            result.put(VOTE_AVG_KEY, movieDataJSON.getString(VOTE_AVG_KEY));
            String posterPath = movieDataJSON.getString(POSTER_URL_KEY);
            String posterUrl = Uri.parse(Consts.IMAGE_BASE_URL)
                    .buildUpon()
                    .appendPath(Consts.IMAGE_SIZE)
                    .appendEncodedPath(posterPath)
                    .appendQueryParameter(
                            Consts.API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                    .build().toString();
            result.put(POSTER_URL_KEY, posterUrl);
            mMovieGeneralData = result;
        }

        public void processVideosDataString(String videoDataString)
                throws JSONException {
            String RST = "results";
            String LNK_PTH = "key";
            String ST = "site";
            String NM = "name";
            String YTB = "YouTube";

            LinkedHashMap<String, String> videoResult = new LinkedHashMap<>();
            JSONObject jsonObject = new JSONObject(videoDataString);
            JSONArray resultArray = jsonObject.getJSONArray(RST);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject videoObject = resultArray.getJSONObject(i);
                if (videoObject.getString(ST).equals(YTB)) {
                    videoResult.put(videoObject.getString(LNK_PTH), videoObject.getString(NM));
                }
            }
            mVideoData = videoResult;
        }

        public void processReviewsDataString(String reviewDataString) throws JSONException {
            String RST = "results";
            String ATHR = "author";
            String CNTNT = "content";
            ArrayList<Pair<String, String>> reviewResult = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(reviewDataString);
            JSONArray resultArray = jsonObject.getJSONArray(RST);
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject videoObject = resultArray.getJSONObject(i);
                reviewResult.add(new Pair<String, String>(videoObject.getString(ATHR),
                        videoObject.getString(CNTNT)));
            }
            mReviewData = reviewResult;
            Log.d(SUB_LOGTAG, String.format("Size of mReviewData is %s", Integer.toString(mReviewData.size())));
        }

        protected Void doInBackground(Consts.FetchOptions... options) {
            // Copy pasta
            HttpURLConnection conn = null;
            BufferedReader bufferedReader = null;
            String movieDataString = null;
            if (options.length != 1) {
                Log.e(SUB_LOGTAG, "Invalid amount options giving for FetchDetailMovieTask");
            }
            mOption = options[0];

            try {
                Uri uri = null;
                switch (mOption) {
                    case FETCH_GENERAL:
                        uri = Uri.parse(Consts.BASE_URL)
                                .buildUpon()
                                .appendEncodedPath(Consts.MOVIE_PATH)
                                .appendEncodedPath(mMovieId.toString())
                                .appendQueryParameter(Consts.API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                                .build();
                        break;
                    case FETCH_VIDEOS:
                        uri = Uri.parse(Consts.BASE_URL)
                                .buildUpon()
                                .appendEncodedPath(Consts.MOVIE_PATH)
                                .appendEncodedPath(mMovieId.toString())
                                .appendEncodedPath(Consts.VIDEO_PATH)
                                .appendQueryParameter(Consts.API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                                .build();
                        break;
                    case FETCH_REVIEWS:
                        Log.d(SUB_LOGTAG, "Reviewing task");
                        uri = Uri.parse(Consts.BASE_URL)
                                .buildUpon()
                                .appendEncodedPath(Consts.MOVIE_PATH)
                                .appendEncodedPath(mMovieId.toString())
                                .appendEncodedPath(Consts.REVIEW_PATH)
                                .appendQueryParameter(Consts.API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                                .build();
                        break;
                    default:
                        Log.e(SUB_LOGTAG, "Option is not valid");
                        throw new InvalidPropertiesFormatException("Invalid option");
                }
                URL url = new URL(uri.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                StringBuilder buffer = new StringBuilder();

                bufferedReader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));

                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    buffer.append(inputLine).append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieDataString = buffer.toString();
            } catch (IOException e) {
                Log.e(SUB_LOGTAG, e.toString());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        Log.e(SUB_LOGTAG, "Error closing stream", e);
                    }
                }
            }

            try {
                switch (mOption) {
                    case FETCH_GENERAL:
                        processMovieDataString(movieDataString);
                        break;
                    case FETCH_VIDEOS:
                        processVideosDataString(movieDataString);
                        break;
                    case FETCH_REVIEWS:
                        processReviewsDataString(movieDataString);
                        break;
                    default:
                        throw new InvalidPropertiesFormatException("Invalid option");
                }
            } catch (JSONException | InvalidPropertiesFormatException e) {
                Log.e(SUB_LOGTAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void x) {
            Activity activity = getActivity();
            // Find views
            switch (mOption) {
                case FETCH_GENERAL:
                    ImageView imageView = (ImageView) activity.findViewById(R.id.detail_poster);
                    TextView titleTextView = (TextView) activity.findViewById(R.id.detail_title);
                    TextView relTextView = (TextView) activity.findViewById(R.id.detail_release_date);
                    TextView voteTextView = (TextView) activity.findViewById(R.id.detail_vote_average);
                    TextView descriptionTextView =
                            (TextView) activity.findViewById(R.id.detail_description);

                    // Change views
                    String releaseDateString = new StringBuilder("Release Date: ")
                            .append(mMovieGeneralData.get(REL_DATE_KEY))
                            .toString();
                    String voteAverageString = new StringBuilder("Vote Average: ")
                            .append(mMovieGeneralData.get(VOTE_AVG_KEY))
                            .toString();
                    titleTextView.setText(mMovieGeneralData.get(TITLE_KEY));
                    relTextView.setText(releaseDateString);
                    voteTextView.setText(voteAverageString);
                    descriptionTextView.setText(mMovieGeneralData.get(DESCRIPTION_KEY));
                    Picasso.with(getActivity()).load(mMovieGeneralData.get(POSTER_URL_KEY)).into(imageView);
                    break;
                case FETCH_VIDEOS:
                    String[] links = mVideoData.keySet().toArray(new String[mVideoData.size()]);
                    String[] titles = mVideoData.values().toArray(new String[mVideoData.size()]);
                    mVideoAdapter.setData(links, titles);
                    break;
                case FETCH_REVIEWS:
                    Log.d(SUB_LOGTAG, mReviewData.toString());
                    mReviewAdapter.setData(mReviewData);
                    break;
            }
        }
    }
}
