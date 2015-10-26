package com.example.yolandyan.movielist;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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

    private final String BASE_URL = "http://api.themoviedb.org/3/";
    private final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    private final String DISCOVER_PATH = "discover/movie";
    private final String SORT_PARAM = "sort_by";
    private final String SORT = "popularity.desc";
    private final String API_KEY_PARAM = "api_key";
    private final String API_KEY = "964a973c564ea29df85b4cb40c6bec10";

    private final String IMAGE_SIZE = "w500";

    private ImageAdapter mImageAdapter;
    private GridView mGridView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageAdapter = new ImageAdapter(getActivity());
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
                // SOF #STUB
                String info = String.format("postion is %d, id is %d", position, id);
                Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
                // EOF
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieData();
        mGridView.setAdapter(mImageAdapter);
    }

    public void updateMovieData() {
        FetchMovieData movieData = new FetchMovieData();
        movieData.execute(BASE_URL);
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
                Long movieId = new Long(oneMovie.getInt(ID));
                String moviePath = oneMovie.getString(PSTR);
                String moviePosterUrl = Uri.parse(IMAGE_BASE_URL)
                            .buildUpon()
                            .appendPath(IMAGE_SIZE)
                            .appendEncodedPath(moviePath)
                            .build().toString();
                movieHashMap.put(movieId, moviePosterUrl);
            }

            return movieHashMap;

        }

        protected LinkedHashMap<Long, String> doInBackground(String... urls) {
            HttpURLConnection conn = null;
            BufferedReader bufferedReader = null;
            String movieDataString = null;
            String baseUrl = urls[0];

            try {
                Uri uri = Uri.parse(baseUrl)
                        .buildUpon()
                        .appendEncodedPath(DISCOVER_PATH)
                        .appendQueryParameter(SORT_PARAM, SORT)
                        .appendQueryParameter(API_KEY_PARAM, API_KEY)
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
            mImageAdapter.setMovieData(movieData);
        }
    }
}
