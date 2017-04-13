package net.sky.scweather;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import net.sky.scweather.data.WeatherContract;
import net.sky.scweather.sync.WeatherSyncAdapter;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private WeatherAdapter weatherAdapter;

    private static final int WEATHER_LOADER = 0;

    private static final String[] WEATHER_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_FORECAST_DATE,
            WeatherContract.WeatherEntry.COLUMN_DAY_PERIOD,
            WeatherContract.WeatherEntry.COLUMN_ICON,
            WeatherContract.WeatherEntry.COLUMN_DESCRIPTION,
            WeatherContract.WeatherEntry.COLUMN_TEMPERATURE_MAX,
            WeatherContract.WeatherEntry.COLUMN_TEMPERATURE_MIN,
            WeatherContract.WeatherEntry.COLUMN_RAIN,
            WeatherContract.WeatherEntry.COLUMN_RELATIVE_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION_START,
            WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION_END,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED_AVG,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED_MAX
    };

    static final int COLUMN_WEATHER_ID = 0;
    static final int COLUMN_FORECAST_DATE = 1;
    static final int COLUMN_DAY_PERIOD = 2;
    static final int COLUMN_ICON = 3;
    static final int COLUMN_DESCRIPTION = 4;
    static final int COLUMN_TEMPERATURE_MAX = 5;
    static final int COLUMN_TEMPERATURE_MIN = 6;
    static final int COLUMN_RAIN = 7;
    static final int COLUMN_RELATIVE_HUMIDITY = 8;
    static final int COLUMN_WIND_DIRECTION_PERIOD_START = 9;
    static final int COLUMN_WIND_DIRECTION_PERIOD_END = 10;
    static final int COLUMN_WIND_SPEED_AVG = 11;
    static final int COLUMN_WIND_SPEED_MAX = 12;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceBundle);
        setHasOptionsMenu(true);
        WeatherSyncAdapter.initialize(getContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        if (id == R.id.action_cities) {
            startActivity(new Intent(getContext(), SavedCitiesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        weatherAdapter = new WeatherAdapter(getContext());
        View rootView = inflater.inflate(R.layout.fragment_main_content, container, false);
        ExpandableListView listView = (ExpandableListView) rootView.findViewById(R.id.list_view_weather);
        listView.setAdapter(weatherAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onActivityCreated");
        getLoaderManager().initLoader(WEATHER_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateWeather() {
        WeatherSyncAdapter.syncImmediately(getActivity());
    }

    // TODO
    public void onCityChanged() {
        getLoaderManager().restartLoader(WEATHER_LOADER, null, MainActivityFragment.this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == WEATHER_LOADER) {
            int cityId = Preferences.getCurrentCity(getContext());
            String sortOrder = WeatherContract.WeatherEntry.COLUMN_FORECAST_DATE + ", " +
                    WeatherContract.WeatherEntry.COLUMN_DAY_PERIOD + " ASC";
            Uri uri = WeatherContract.WeatherEntry.buildWeatherUriWithCityAndDateParameter(cityId, System.currentTimeMillis());
            return new CursorLoader(getActivity(), uri, WEATHER_COLUMNS, null, null, sortOrder);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.i(TAG, "onLoadFinished");
        if (loader.getId() == WEATHER_LOADER) {
            WeatherForecast forecast = new WeatherForecast();
            if (cursor.moveToFirst()) {
                do {
                    long date = cursor.getLong(COLUMN_FORECAST_DATE);
                    int icon = cursor.getInt(COLUMN_ICON);
                    int period = cursor.getInt(COLUMN_DAY_PERIOD);
                    int min = cursor.getInt(COLUMN_TEMPERATURE_MIN);
                    int max = cursor.getInt(COLUMN_TEMPERATURE_MAX);
                    int rain = cursor.getInt(COLUMN_RAIN);
                    String description = cursor.getString(COLUMN_DESCRIPTION);
                    int humidity = cursor.getInt(COLUMN_RELATIVE_HUMIDITY);
                    String windStart = cursor.getString(COLUMN_WIND_DIRECTION_PERIOD_START);
                    String windEnd = cursor.getString(COLUMN_WIND_DIRECTION_PERIOD_END);
                    int windAvgSpeed = cursor.getInt(COLUMN_WIND_SPEED_AVG);
                    int windMaxSpeed = cursor.getInt(COLUMN_WIND_SPEED_MAX);
                    forecast.add(date, period, min, max, icon, rain, description, humidity, windStart,
                            windEnd, windAvgSpeed, windMaxSpeed);
                } while (cursor.moveToNext());
            }
            weatherAdapter.set(forecast);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.i(TAG, "onLoaderReset");
        if (loader.getId() == WEATHER_LOADER) {
            weatherAdapter.clear();
        }
    }

}
