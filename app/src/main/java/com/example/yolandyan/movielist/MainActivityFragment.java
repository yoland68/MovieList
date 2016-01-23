package com.example.yolandyan.movielist;

import android.annotation.TargetApi;
import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.yolandyan.movielist.data.MovieDataContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOGTAG = MainActivity.class.getSimpleName();

    private ImageAdapter mImageAdapter;
    private GridView mGridView;
    private static final String[] MOVIE_COLUMNS = {
            MovieDataContract.MovieEntry.TABLE_NAME,
            MovieDataContract.MovieEntry.KEY_COL,
            MovieDataContract.MovieEntry.TITLE_COL,
            MovieDataContract.MovieEntry.POSTER_COL,
            MovieDataContract.MovieEntry.DESC_COL,
            MovieDataContract.MovieEntry.RELEASE_DATE_COL,
            MovieDataContract.MovieEntry.RATING_COL,
    };

    public interface Callback {
        public void changeDetailFragement(Long id);
    }

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
                ((Callback) getActivity()).changeDetailFragement(id);
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
        MainActivity activity = (MainActivity) getActivity();

        switch (id) {
            case R.id.sort_by_favorite:
                activity.setSortByOption(Consts.SORT_BY_FAVORITE);
                break;
            case R.id.sort_by_popularity:
                activity.setSortByOption(Consts.SORT_BY_POPULARITY);
                break;
            case R.id.sort_by_rating:
                activity.setSortByOption(Consts.SORT_BY_RATING);
                break;
            case R.id.action_update_main:
                Log.d(LOGTAG, "Update main activity");
                break;
            default:
                Log.d(LOGTAG, "Selected element id invalid");
        }
        updateMovieData();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
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
            FetchMovieData movieData = new FetchMovieData();
            SharedPreferences sharedPref = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());
            String sortByOption = sharedPref.getString(Consts.SORT_KEY, Consts.SORT_BY_POPULARITY);
            if (sortByOption.equals(Consts.SORT_BY_POPULARITY)) {
                movieData.execute(Consts.POPULAR_PATH);
            } else if (sortByOption.equals(Consts.SORT_BY_RATING)) {
                movieData.execute(Consts.RATING_PATH);
            } else if (sortByOption.equals(Consts.SORT_BY_FAVORITE)) {
                getLoaderManager().restartLoader(0, null, this);
            }
        } else {
            getLoaderManager().restartLoader(0, null, this);
            Toast.makeText(getActivity(), "No Internet, Only favorite movies can be shown", Toast.LENGTH_SHORT).show();
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri allMovieUri = MovieDataContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(getActivity(),
                allMovieUri,
                null,
                null,
                null,
                null
                );
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        ArrayList<Long> movieIdsArrayList = new ArrayList<>();
        if (data.moveToFirst()) {
            do {
                int idCol = data.getColumnIndex(MovieDataContract.MovieEntry.KEY_COL);
                movieIdsArrayList.add(data.getLong(idCol));
            } while (data.moveToNext());
        }
        mImageAdapter.setMovieDataWithUrl(movieIdsArrayList, new ArrayList<String>());
    }

    public class FetchMovieData extends AsyncTask<String, Void, LinkedHashMap<Long, String>> {
        private final String SUB_LOGTAG = FetchMovieData.class.getSimpleName();

        public LinkedHashMap<Long, String> processMovieDataString(String dataString)
                throws JSONException {
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
                        .appendQueryParameter(Consts.API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build().toString();
                movieHashMap.put(movieId, moviePosterUrl);
            }

            return movieHashMap;
        }

        // Use LinkedHashMap to maintain the sorting order of the fetched data
        // More readable than using 2d arrays
        protected LinkedHashMap<Long, String> doInBackground(String... params) {
            HttpURLConnection conn = null;
            BufferedReader bufferedReader = null;
            String movieDataString = null;
            if (params.length != 1) {
                Log.e(SUB_LOGTAG, String.format("Expect 1 argument for the asynctask to fetch " +
                        "movie data, received %d arguments", params.length));
                return null;
            }
            String sortPath = params[0];

            try {
                Uri uri = Uri.parse(Consts.BASE_URL)
                        .buildUpon()
                        .appendEncodedPath(Consts.MOVIE_PATH)
                        .appendEncodedPath(sortPath)
                        .appendQueryParameter(Consts.API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY)
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
            // Extract movie ids and its poster path into 2 arrays as sorted
            ArrayList<Long> movieIds = new ArrayList<>();
            movieIds.addAll(movieData.keySet());
            ArrayList<String> posterPaths = new ArrayList<>();
            for (int i = 0; i < movieIds.size(); i++) {
                posterPaths.add(movieData.get(movieIds.get(i)));
            }
            mImageAdapter.setMovieDataWithUrl(movieIds, posterPaths);
        }
    }
}
