package net.sky.scweather;

import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

import net.sky.scweather.data.WeatherContract;

public class SavedCitiesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = SavedCitiesActivity.class.getSimpleName();

    private static final String[] SAVED_CITY_COLUMNS = {
            WeatherContract.SavedCityEntry.TABLE_NAME + "." + WeatherContract.SavedCityEntry._ID,
            WeatherContract.SavedCityEntry.COLUMN_CITY_FK,
            WeatherContract.CityEntry.COLUMN_CITY_NAME
    };
    public static final int COLUMN_SAVED_CITY_ID = 0;
    public static final int COLUMN_SAVED_CITY_FK = 1;
    public static final int COLUMN_SAVED_CITY_NAME = 3;

    private static final String[] SEARCH_CITY_COLUMNS = {
            WeatherContract.CityEntry._ID,
            WeatherContract.CityEntry.COLUMN_CITY_NAME
    };
    public static final int COLUMN_CITY_ID = 0;
    public static final int COLUMN_CITY_NAME = 1;

    private SavedCitiesAdapter savedCitiesAdapter;
    private SearchCityAdapter searchCityAdapter;

    private static final int SAVED_CITY_LOADER = 1;
    private static final int SEARCH_CITY_LOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_cities);
        ListView listView = (ListView) findViewById(R.id.list_view_saved_cities);
        savedCitiesAdapter = new SavedCitiesAdapter(this, null, 0);
        listView.setAdapter(savedCitiesAdapter);
        getLoaderManager().initLoader(SAVED_CITY_LOADER, null, this);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {
            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                String query = intent.getStringExtra(SearchManager.QUERY);
            }
        }
    }

//    TODO?
//    void onSavedCitiesChanged() {
//        getLoaderManager().restartLoader(SAVED_CITY_LOADER, null, this);
//    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cities, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i(TAG, "onQueryTextSubmit " + query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange " + newText);
                Uri uri = WeatherContract.CityEntry.buildCityUriWithCityNameParameter(newText);
                Cursor cursor = SavedCitiesActivity.this.getContentResolver().query(uri, null, null, null, null);
                searchCityAdapter.swapCursor(cursor);
                return true;
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchCityAdapter.getItem(position);
                Log.i(TAG, cursor.getString(SavedCitiesActivity.COLUMN_CITY_ID) + ": " +
                        cursor.getString(SavedCitiesActivity.COLUMN_CITY_NAME));
                // TODO insert city in savedCities list
                return true;
            }
        });
        searchCityAdapter = new SearchCityAdapter(this, null, 0);
        searchView.setSuggestionsAdapter(searchCityAdapter);
        getLoaderManager().initLoader(SEARCH_CITY_LOADER, null, this);
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SAVED_CITY_LOADER) {
            String sortOrder = WeatherContract.CityEntry.COLUMN_CITY_NAME + " ASC";
            Uri uri = WeatherContract.SavedCityEntry.buildSavedCitiesUri();
            return new CursorLoader(this, uri, SAVED_CITY_COLUMNS, null, null, sortOrder);
        }
        if (id == SEARCH_CITY_LOADER) {
            return new CursorLoader(this, WeatherContract.CityEntry.CONTENT_URI, SEARCH_CITY_COLUMNS, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == SAVED_CITY_LOADER) {
            savedCitiesAdapter.swapCursor(cursor);
        } else if (loader.getId() == SEARCH_CITY_LOADER) {
            searchCityAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == SAVED_CITY_LOADER) {
            savedCitiesAdapter.swapCursor(null);
        } else if (loader.getId() == SEARCH_CITY_LOADER) {
            searchCityAdapter.swapCursor(null);
        }
    }
}
