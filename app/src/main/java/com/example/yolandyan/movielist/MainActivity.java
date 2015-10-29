package com.example.yolandyan.movielist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private final String LOGTAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.main_fragment, new MainActivityFragment())
                    .commit();
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String sortByOption = sharedPref.getString(Consts.SORT_KEY, Consts.SORT_BY_POPULARITY);
        if (sortByOption.equals(Consts.SORT_BY_RATING)) {
            sortByPopItem.setVisible(true);
            sortByRateItem.setVisible(false);
        } else if (sortByOption.equals(Consts.SORT_BY_POPULARITY)) {
            sortByPopItem.setVisible(false);
            sortByRateItem.setVisible(true);
        } else {
            Log.e(LOGTAG, String.format("Sort by preference is invalid: %s", sortByOption));
            return false;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void setSortByOption(String option) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Consts.SORT_KEY, option);
        editor.commit();
        String pref = sharedPref.getString(Consts.SORT_KEY, "");
        if (pref.isEmpty()) {
            Log.e(LOGTAG, "Preference is not applied");
        }
    }
}
