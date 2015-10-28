package com.example.yolandyan.movielist;

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
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    private final String LOGTAG = MainActivity.class.getSimpleName();

    private ImageAdapter mImageAdapter;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageAdapter = new ImageAdapter(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.poster_gridview);
        mGridView.setAdapter(mImageAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, id);
                startActivity(intent);
            }
        });
        return rootView;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.sort_by_rating) {
            MainActivity activity = (MainActivity)getActivity();
            activity.setSortByOption(Consts.SORT_BY_RATING);
        }

        if (id == R.id.sort_by_popularity) {
            MainActivity activity = (MainActivity)getActivity();
            activity.setSortByOption(Consts.SORT_BY_POPULARITY);
        }
        if (item.getItemId() == R.id.action_update_main) {
            Log.d(LOGTAG, "update main activity");
        }
        updateMovieData();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieData();
    }

    public void updateMovieData() {
        FetchMovieData movieData = new FetchMovieData();
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity.getSortByOption().equals(Consts.SORT_BY_POPULARITY)) {
            movieData.execute(Consts.POPULAR_PATH);
        } else if (mainActivity.getSortByOption().equals(Consts.SORT_BY_RATING)) {
            movieData.execute(Consts.RATING_PATH);
        }
    }

    public class FetchMovieData extends AsyncTask<String, Void, LinkedHashMap<Long, String>> {
        private final String SUB_LOGTAG = FetchMovieData.class.getSimpleName();

        public LinkedHashMap<Long, String> processMovieDataString(String dataString)
                throws JSONException{
            String RST = "results";
            String PSTR = "poster_path";
            String ID = "id";

            JSONObject movieDataJSON = new JSONObject(dataString);
            JSONArray resultArray = movieDataJSON.getJSONArray(RST);

            LinkedHashMap<Long, String> movieHashMap = new LinkedHashMap<>();

            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject oneMovie = resultArray.getJSONObject(i);
                Long movieId = Long.valueOf(oneMovie.getInt(ID));
                String moviePath = oneMovie.getString(PSTR);
                String moviePosterUrl = Uri.parse(Consts.IMAGE_BASE_URL)
                            .buildUpon()
                            .appendPath(Consts.IMAGE_SIZE)
                            .appendEncodedPath(moviePath)
                            .appendQueryParameter(Consts.API_KEY_PARAM, Consts.API_KEY)
                            .build().toString();
                movieHashMap.put(movieId, moviePosterUrl);
            }

            return movieHashMap;

        }

        protected LinkedHashMap<Long, String> doInBackground(String...params) {
            HttpURLConnection conn = null;
            BufferedReader bufferedReader = null;
            String movieDataString = null;
            if (params.length != 1) {
                Log.e(SUB_LOGTAG, String.format("Expect 1 argument for the asynctask to fetch " +
                        "movie data, received %d arguments",  params.length));
                return null;
            }
            String sortPath = params[0];

            try {
                Uri uri = Uri.parse(Consts.BASE_URL)
                        .buildUpon()
                        .appendEncodedPath(Consts.MOVIE_PATH)
                        .appendEncodedPath(sortPath)
                        .appendQueryParameter(Consts.API_KEY_PARAM, Consts.API_KEY)
                        .build();
                URL url = new URL(uri.toString());
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                StringBuffer buffer = new StringBuffer();

                bufferedReader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));

                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    buffer.append(inputLine + "\n");
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

        protected void onPostExecute(LinkedHashMap<Long, String> movieData) {
            Long[] movieIds = movieData.keySet().toArray(new Long[movieData.size()]);
            String[] posterPaths = new String[movieData.size()];
            for (int i = 0; i < posterPaths.length; i++) {
                posterPaths[i] = movieData.get(movieIds[i]);
            }
            mImageAdapter.setMovieData(movieIds, posterPaths);
        }
    }
}
