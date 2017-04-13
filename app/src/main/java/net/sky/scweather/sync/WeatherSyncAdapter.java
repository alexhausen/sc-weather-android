package net.sky.scweather.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import net.sky.scweather.R;
import net.sky.scweather.SavedCitiesActivity;
import net.sky.scweather.WeatherForecast;
import net.sky.scweather.data.WeatherContract;
import net.sky.scweather.data.WeatherContract.WeatherEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = WeatherSyncAdapter.class.getSimpleName();
    private static final String BASE_URL = "http://ciram.epagri.sc.gov.br/wsprev/resources/listaJson/prevMuni?";
    private static final String PARAM_CITY_CODE = "cdCidade";
    private static final String PARAM_DATE = "data";
    // Interval at which to sync with the weather, in seconds.
    private static final int SYNC_INTERVAL = 60 /*s*/ * 60 /*m*/ * 3 /*h*/;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    // download weather data, update database and notify observers
    public void onPerformSync(Account account, Bundle bundle, String authority,
                              ContentProviderClient contentProviderClient, SyncResult syncResult) {
        Log.i(TAG, "onPerformSync");
        if (syncResult.hasError()) {
            Log.w(TAG, "onPerformSync error: " + syncResult.toDebugString());
            return;
        }
        List<ContentValues[]> contentValuesArrayList = new LinkedList<>();

        //TODO encapsulate this query
        final List<Integer> cities = new ArrayList<>();
        Cursor cursor = getContext().getContentResolver().query(WeatherContract.SavedCityEntry.CONTENT_URI, null, null, null, null);
        if (cursor!= null) {
            cursor.moveToFirst();
            do {
                cities.add(cursor.getInt(SavedCitiesActivity.COLUMN_SAVED_CITY_FK));
            } while(cursor.moveToNext());
            cursor.close();
        }

        for (Integer cityId : cities) {
            Date date = new Date();
            // maximum of 5 days weather forecast supported
            for (int i = 0; i < WeatherForecast.MAX_FORECAST_DAYS; ++i) {
                final String paramDate = new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(date);
                String jsonStr = downloadWeatherJson(paramDate, cityId);
                try {
                    ContentValues[] cvsArray = parseWeatherDataFromJson(jsonStr, cityId);
                    if (cvsArray != null && cvsArray.length > 0) {
                        contentValuesArrayList.add(cvsArray);
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage() + "\n" + jsonStr);
                }
                // next day
                date.setTime(date.getTime() + DateUtils.DAY_IN_MILLIS);
            }
        }

        // insert download data into the database, delete old data
        if (!contentValuesArrayList.isEmpty()) {
            ContentResolver resolver = getContext().getContentResolver();
            int count = 0;
            for (ContentValues[] cvsArray : contentValuesArrayList) {
                count += resolver.bulkInsert(WeatherEntry.CONTENT_URI, cvsArray);
            }
            Log.i(TAG, "Weather entries inserted: " + count);
            String yesterday = Long.toString(System.currentTimeMillis() - DateUtils.DAY_IN_MILLIS);
            count = resolver.delete(WeatherEntry.CONTENT_URI, WeatherEntry.COLUMN_FORECAST_DATE + "<= ?",
                    new String[]{yesterday});
            Log.i(TAG, "Weather entries deleted: " + count);
        }
    }

    /**
     * Download weather forecast JSON string for a given city and date
     *
     * @param paramDate value of URL parameter 'cdCidade'
     * @param cityId value of URL parameter 'data'
     * @return JSON string or null in case of error
     */
    private String downloadWeatherJson(String paramDate, Integer cityId) {
        String jsonStr = null;
        // example http://ciram.epagri.sc.gov.br/wsprev/resources/listaJson/prevMuni?cdCidade=4205407&data=2017/01/08
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAM_CITY_CODE, cityId.toString())
                .appendQueryParameter(PARAM_DATE, paramDate)
                .build();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (buffer.length() == 0) {
                return null;
            }
            jsonStr = buffer.toString();
        } catch (final IOException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonStr;
    }

    /**
     * Parse weather data from JSON string
     *
     * @param jsonStr JSON string containing the weather forecast data for a given day and city
     * @param cityCode city ID
     * @return parsed values
     * @throws JSONException on error parsing JSON array of objects
     */
    private ContentValues[] parseWeatherDataFromJson(String jsonStr, Integer cityCode) throws JSONException {
        if (jsonStr == null) {
            return null;
        }
        Log.i(TAG, jsonStr);

        final String K_ICON = "iconPrev";
        final String K_DAY_PERIOD = "periodoDia";
        final String K_RAIN = "mmChuva";
        final String K_DESCRIPTION = "nmCondicao";
        final String K_FORECAST_DATE = "dtPrev";
        final String K_LAST_UPDATE = "dtAtualizacao";
        final String K_RELATIVE_HUMIDITY = "umidadeRel";
        final String K_WIND_DIRECTION_START = "dirVentoIni";
        final String K_WIND_DIRECTION_END = "dirVentoFin";
        final String K_WIND_SPEED_MAX = "velVentoMax";
        final String K_WIND_SPEED_AVG = "velVentoMed";
        final String K_TEMPERATURE_MAX = "tempMax";
        final String K_TEMPERATURE_MIN = "tempMin";

        JSONArray weatherArray = new JSONArray(jsonStr);
        ArrayList<ContentValues> cvsList = new ArrayList<>(weatherArray.length());

        for (int i = 0; i < weatherArray.length(); ++i) {
            JSONObject forecast = weatherArray.getJSONObject(i);
            final int icon = forecast.getInt(K_ICON);
            if (icon == 0) continue; // data unavailable, skip record
            final int dayPeriod = forecast.getInt(K_DAY_PERIOD);
            final int rain = forecast.getInt(K_RAIN);
            final String description = forecast.getString(K_DESCRIPTION);
            final long forecastDateMillis = forecast.getLong(K_FORECAST_DATE);
            final long lastUpdate = forecast.getLong(K_LAST_UPDATE);
            final int humidity = forecast.getInt(K_RELATIVE_HUMIDITY);
            final String windDirectionStart = forecast.getString(K_WIND_DIRECTION_START);
            final String windDirectionEnd = forecast.getString(K_WIND_DIRECTION_END);
            final int windSpeedMax = forecast.getInt(K_WIND_SPEED_MAX);
            final int windSpeedAvg = forecast.getInt(K_WIND_SPEED_AVG);
            final int temperatureMax = forecast.getInt(K_TEMPERATURE_MAX);
            final int temperatureMin = forecast.getInt(K_TEMPERATURE_MIN);
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_CITY_FK, cityCode);
            weatherValues.put(WeatherEntry.COLUMN_FORECAST_DATE, forecastDateMillis);
            weatherValues.put(WeatherEntry.COLUMN_DAY_PERIOD, dayPeriod);
            weatherValues.put(WeatherEntry.COLUMN_ICON, icon);
            weatherValues.put(WeatherEntry.COLUMN_RAIN, rain);
            weatherValues.put(WeatherEntry.COLUMN_DESCRIPTION, description);
            weatherValues.put(WeatherEntry.COLUMN_LAST_UPDATE, lastUpdate);
            weatherValues.put(WeatherEntry.COLUMN_RELATIVE_HUMIDITY, humidity);
            weatherValues.put(WeatherEntry.COLUMN_TEMPERATURE_MAX, temperatureMax);
            weatherValues.put(WeatherEntry.COLUMN_TEMPERATURE_MIN, temperatureMin);
            weatherValues.put(WeatherEntry.COLUMN_WIND_DIRECTION_START, windDirectionStart);
            weatherValues.put(WeatherEntry.COLUMN_WIND_DIRECTION_END, windDirectionEnd);
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED_MAX, windSpeedMax);
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED_AVG, windSpeedAvg);
            cvsList.add(weatherValues);
        }
        return cvsList.toArray(new ContentValues[cvsList.size()]);
    }

    public static void initialize(Context context) {
        getSyncAccount(context);
    }

    /**
     * Helper method to get the fake account to be used with WeatherSyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    @Nullable
    private static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if (null == accountManager.getPassword(newAccount)) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                Log.e(TAG, "getSyncAccount error");
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        WeatherSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    private static void configurePeriodicSync(Context context, int syncInterval, int syncFlextime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().syncPeriodic(syncInterval, syncFlextime).setSyncAdapter(account, authority).setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

}
