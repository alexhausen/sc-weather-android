package net.sky.scweather.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.DateUtils;

public class WeatherContract {
    static final String CONTENT_AUTHORITY = "net.sky.scweather";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String PATH_WEATHER = "weather";
    static final String PATH_CITY = "city";
    static final String PATH_SAVED_CITY = "saved_city";

    public static final class CityEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CITY).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CITY;

        static final String TABLE_NAME = "city";

        // table columns
        public static final String COLUMN_CITY_NAME = "city_name";

        static Uri buildCityUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildCityUriWithCityNameParameter(String cityName) {
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_CITY_NAME, cityName).build();
        }

        static String getCityNameFromUri(Uri uri) {
            String cityName = uri.getQueryParameter(COLUMN_CITY_NAME);
            return cityName != null ? cityName : "";
        }

    }

    public static final class SavedCityEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SAVED_CITY).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SAVED_CITY;

        public static final String TABLE_NAME = "saved_city";

        public static Uri buildSavedCitiesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSavedCitiesUri() {
            return CONTENT_URI.buildUpon().build();
        }

        // table columns
        public static final String COLUMN_CITY_FK = "city_id";
    }

    public static final class WeatherEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";

        // table columns
        public static final String COLUMN_CITY_FK = "city_id";
        public static final String COLUMN_FORECAST_DATE = "forecast_date";
        public static final String COLUMN_LAST_UPDATE = "last_update";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DAY_PERIOD = "period";
        public static final String COLUMN_RAIN = "rain";
        public static final String COLUMN_RELATIVE_HUMIDITY = "humidity";
        public static final String COLUMN_WIND_DIRECTION_START = "wind_dir_start";
        public static final String COLUMN_WIND_DIRECTION_END = "wind_dir_end";
        public static final String COLUMN_WIND_SPEED_MAX = "wind_speed_max";
        public static final String COLUMN_WIND_SPEED_AVG = "wind_speed_avg";
        public static final String COLUMN_TEMPERATURE_MAX = "max";
        public static final String COLUMN_TEMPERATURE_MIN = "min";

        private static long normalizeDate(long date) {
            // always count from 0 hour of that day. Remove hours/min/sec/millis.
            return date / DateUtils.DAY_IN_MILLIS * DateUtils.DAY_IN_MILLIS;
        }

        static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        static Uri buildWeatherUriWithCity(int cityId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(cityId))
                    .build();
        }

        public static Uri buildWeatherUriWithCityAndDateParameter(int cityId, long forecastDate) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Integer.toString(cityId))
                    .appendQueryParameter(COLUMN_FORECAST_DATE, Long.toString(normalizeDate(forecastDate)))
                    .build();
        }

        static long getCityFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        static long getDateParameterFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_FORECAST_DATE);
            if (null == dateString || dateString.length() == 0) return 0;
            return Long.parseLong(dateString);
        }

    }
}
