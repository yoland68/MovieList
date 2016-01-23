package com.example.yolandyan.movielist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    private final String LOGTAG = MainActivity.class.getSimpleName();
    private final String DETAIL_FRAG_TAG = "DFTAG";
    private boolean mTwoPane;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
        if (findViewById(R.id.tablet_detail_frame) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                DetailActivityFragment fragment = new DetailActivityFragment();
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.tablet_detail_frame, new DetailActivityFragment(), DETAIL_FRAG_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sortByPopItem = menu.findItem(R.id.sort_by_popularity);
        MenuItem sortByRateItem = menu.findItem(R.id.sort_by_rating);
        MenuItem sortByFavoriteItem = menu.findItem(R.id.sort_by_favorite);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sortByOption = sharedPref.getString(Consts.SORT_KEY, Consts.SORT_BY_POPULARITY);

        // Alternating the two sort options item's visibility in menu
        switch (sortByOption) {
            case Consts.SORT_BY_POPULARITY:
                sortByPopItem.setVisible(false);
                sortByRateItem.setVisible(true);
                sortByFavoriteItem.setVisible(true);
                break;
            case Consts.SORT_BY_RATING:
                sortByPopItem.setVisible(true);
                sortByRateItem.setVisible(false);
                sortByFavoriteItem.setVisible(true);
                break;
            case Consts.SORT_BY_FAVORITE:
                sortByPopItem.setVisible(true);
                sortByRateItem.setVisible(true);
                sortByFavoriteItem.setVisible(false);
                break;
            default:
                Log.e(LOGTAG, String.format("Sort option doesn't match any valid case: %s", sortByOption));
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void setSortByOption(String option) {
        // Set preferences using SharedPreferences.Editor
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Consts.SORT_KEY, option);
        editor.apply();
        // Make sure the preference is committed
        String pref = sharedPref.getString(Consts.SORT_KEY, "");
        if (pref.isEmpty()) {
            Log.e(LOGTAG, "Preference is not applied");
        }
    }

    public boolean getTwoPane() {
        return mTwoPane;
    }

    @Override
    public void changeDetailFragement(Long id) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putLong(DetailActivityFragment.DETAIL_MOVIE_ID, id);
            DetailActivityFragment frag = new DetailActivityFragment();
            frag.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.tablet_detail_frame, frag, DETAIL_FRAG_TAG).commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(Intent.EXTRA_TEXT, id);
            startActivity(intent);
        }
    }
}
