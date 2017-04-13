package net.sky.scweather.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import net.sky.scweather.data.WeatherContract.WeatherEntry;
import net.sky.scweather.data.WeatherContract.CityEntry;
import net.sky.scweather.data.WeatherContract.SavedCityEntry;

public class WeatherContentProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();

    static final int WEATHER = 100;
    static final int WEATHER_BY_CITY = 101;
    static final int CITY = 200;
    static final int SAVED_CITY = 300;

    private static final SQLiteQueryBuilder weatherQueryBuilder = new SQLiteQueryBuilder();
    ;
    private static final SQLiteQueryBuilder savedCityQueryBuilder = new SQLiteQueryBuilder();
    ;

    static {
        weatherQueryBuilder.setTables(
                String.format("%s INNER JOIN %s ON %s.%s = %s.%s", WeatherEntry.TABLE_NAME, CityEntry.TABLE_NAME,
                        WeatherEntry.TABLE_NAME, WeatherEntry.COLUMN_CITY_FK, CityEntry.TABLE_NAME, CityEntry._ID));

        savedCityQueryBuilder.setTables(String.format("%s INNER JOIN %s ON %s.%s = %s.%s",
                SavedCityEntry.TABLE_NAME, CityEntry.TABLE_NAME,
                SavedCityEntry.TABLE_NAME, SavedCityEntry.COLUMN_CITY_FK, CityEntry.TABLE_NAME, CityEntry._ID));
    }

    private WeatherDbHelper dbHelper;

    // where city = ?
    private static final String citySelection = CityEntry.TABLE_NAME + "." + CityEntry._ID + " = ?";

    // where city = ? AND date >= ?
    private static final String cityStartDateSelection = CityEntry.TABLE_NAME + "." + CityEntry._ID +
            " = ? AND " + WeatherEntry.COLUMN_FORECAST_DATE + " >= ?";

    private static final String savedCitySelection = "1";

    private static final String searchCitySelection = CityEntry.TABLE_NAME + "." + CityEntry.COLUMN_CITY_NAME +
            " LIKE ?";

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;
        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", WEATHER_BY_CITY);
        matcher.addURI(authority, WeatherContract.PATH_CITY, CITY);
        matcher.addURI(authority, WeatherContract.PATH_SAVED_CITY, SAVED_CITY);
        return matcher;
    }

    public WeatherContentProvider() {
        super();
    }

    private Cursor getWeatherByCity(Uri uri, String[] projection, String sortOrder) {
        String city = Long.toString(WeatherEntry.getCityFromUri(uri));
        long startDate = WeatherEntry.getDateParameterFromUri(uri);
        String selection;
        String[] selectionArgs;
        if (startDate > 0) {
            selection = cityStartDateSelection;
            selectionArgs = new String[]{city, Long.toString(startDate)};
        } else {
            // no start date query present
            selection = citySelection;
            selectionArgs = new String[]{city};
        }
        return weatherQueryBuilder.query(dbHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getSavedCities(String[] projection, String sortOrder) {
        return savedCityQueryBuilder.query(dbHelper.getReadableDatabase(), projection, savedCitySelection, null, null, null,
                sortOrder);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case WEATHER_BY_CITY: // weather/#
                cursor = getWeatherByCity(uri, projection, sortOrder);
                break;
            case WEATHER: // weather
                cursor = dbHelper.getReadableDatabase().query(WeatherEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CITY: // city
                String cityName = CityEntry.getCityNameFromUri(uri);
                if (!cityName.isEmpty()) {
                    selection = searchCitySelection;
                    selectionArgs = new String[]{"%" + cityName + "%"};
                }
                cursor = dbHelper.getReadableDatabase().query(CityEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SAVED_CITY: // saved_city
                cursor = getSavedCities(projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new WeatherDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case WEATHER_BY_CITY:
                return WeatherEntry.CONTENT_TYPE;
            case WEATHER:
                return WeatherEntry.CONTENT_TYPE;
            case CITY:
                return CityEntry.CONTENT_TYPE;
            case SAVED_CITY:
                return SavedCityEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri returnUri;
        switch (uriMatcher.match(uri)) {
            case WEATHER: {
                long id = db.insert(WeatherEntry.TABLE_NAME, null, contentValues);
                if (id != -1) {
                    returnUri = WeatherEntry.buildWeatherUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case CITY: {
                long id = db.insert(CityEntry.TABLE_NAME, null, contentValues);
                if (id != -1) {
                    returnUri = CityEntry.buildCityUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case SAVED_CITY: {
                long id = db.insert(SavedCityEntry.TABLE_NAME, null, contentValues);
                if (id != -1) {
                    returnUri = SavedCityEntry.buildSavedCitiesUri(id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        // this makes delete all rows return the number of rows deleted
        if (selection == null) selection = "1";
        String tableName;
        switch (uriMatcher.match(uri)) {
            case WEATHER:
                tableName = WeatherEntry.TABLE_NAME;
                break;
            case CITY:
                tableName = CityEntry.TABLE_NAME;
                break;
            case SAVED_CITY:
                tableName = SavedCityEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        int rowsDeleted = db.delete(tableName, selection, selectionArgs);
        Context context = getContext();
        if (rowsDeleted > 0 && context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        String tableName;
        switch (uriMatcher.match(uri)) {
            case WEATHER:
                tableName = WeatherEntry.TABLE_NAME;
                break;
            case CITY:
                tableName = CityEntry.TABLE_NAME;
                break;
            case SAVED_CITY:
                tableName = SavedCityEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        int rowsUpdated = db.update(tableName, values, selection, selectionArgs);
        Context context = getContext();
        if (rowsUpdated > 0 && context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    private int bulkInsertHelper(String tableName, Uri uri, ContentValues[] valuesArray) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;
        db.beginTransaction();
        try {
            for (ContentValues values : valuesArray) {
                long id = db.insert(tableName, null, values);
                if (id != -1) ++count;
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        Context context = getContext();
        if (count > 0 && context != null) {
            context.getContentResolver().notifyChange(uri, null);
        }
        return count;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] valuesArray) {
        switch (uriMatcher.match(uri)) {
            case WEATHER:
                return bulkInsertHelper(WeatherEntry.TABLE_NAME, uri, valuesArray);
            case CITY:
                return bulkInsertHelper(CityEntry.TABLE_NAME, uri, valuesArray);
            case SAVED_CITY:
                return bulkInsertHelper(SavedCityEntry.TABLE_NAME, uri, valuesArray);
            default:
                return super.bulkInsert(uri, valuesArray);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        dbHelper.close();
        super.shutdown();
    }

}
