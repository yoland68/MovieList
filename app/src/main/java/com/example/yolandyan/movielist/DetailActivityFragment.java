package com.example.yolandyan.movielist;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by yolandyan on 10/25/15.
 */
public class DetailActivityFragment extends Fragment{
    private Long mMovieId;
    private String TITLE_KEY = "title";
    private String POSTER_URL_KEY = "poster_path";
    private String DESCRIPTION_KEY = "overview";
    private String VOTE_AVG_KEY = "vote_average";
    private String REL_DATE_KEY = "release_date";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            mMovieId = intent.getLongExtra(Intent.EXTRA_TEXT, 77467);
        }
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
        FetchDetailMovieTask task = new FetchDetailMovieTask();
        task.execute();
    }

    public class FetchDetailMovieTask extends AsyncTask<Void, Void, HashMap<String, String>> {
        private final String SUB_LOGTAG = FetchDetailMovieTask.class.getSimpleName();

        public HashMap<String, String> processMovieDataString(String dataString)
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
                            .appendQueryParameter(Consts.API_KEY_PARAM, Consts.API_KEY)
                            .build().toString();
            result.put(POSTER_URL_KEY, posterUrl);
            return result;
        }

        protected HashMap<String, String> doInBackground(Void...params) {
            HttpURLConnection conn = null;
            BufferedReader bufferedReader = null;
            String movieDataString = null;

            try {
                Uri uri = Uri.parse(Consts.BASE_URL)
                        .buildUpon()
                        .appendEncodedPath(Consts.MOVIE_PATH)
                        .appendEncodedPath(mMovieId.toString())
                        .appendQueryParameter(Consts.API_KEY_PARAM, Consts.API_KEY)
                        .build();
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
                return processMovieDataString(movieDataString);
            } catch (JSONException e) {
                Log.e(SUB_LOGTAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            // Find views
            Activity activity = getActivity();
            ImageView imageView = (ImageView) activity.findViewById(R.id.detail_poster);
            TextView titleTextView = (TextView) activity.findViewById(R.id.detail_title);
            TextView relTextView = (TextView) activity.findViewById(R.id.detail_release_date);
            TextView voteTextView = (TextView) activity.findViewById(R.id.detail_vote_average);
            TextView descriptionTextView =
                    (TextView) activity.findViewById(R.id.detail_description);

            // Change views
            String releaseDateString = new StringBuilder("Release Date: ")
                    .append(result.get(REL_DATE_KEY))
                    .toString();
            String voteAverageString = new StringBuilder("Vote Average: ")
                    .append(result.get(VOTE_AVG_KEY))
                    .toString();
            titleTextView.setText(result.get(TITLE_KEY));
            relTextView.setText(releaseDateString);
            voteTextView.setText(voteAverageString);
            descriptionTextView.setText(result.get(DESCRIPTION_KEY));
            Picasso.with(getActivity()).load(result.get(POSTER_URL_KEY)).into(imageView);
        }
    }
}
