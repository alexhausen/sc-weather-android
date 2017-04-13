package net.sky.scweather.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import net.sky.scweather.data.WeatherContract.CityEntry;
import net.sky.scweather.data.WeatherContract.SavedCityEntry;
import net.sky.scweather.data.WeatherContract.WeatherEntry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class WeatherContentProviderTest {

    private Context context = InstrumentationRegistry.getTargetContext();

    @Before
    public void setUp() {
        deleteRecords();
    }

    @After
    public void tearDown() {
        deleteRecords();
    }

    @Test
    public void testProviderRegistry() {
        PackageManager pm = context.getPackageManager();
        ComponentName componentName = new ComponentName(context.getPackageName(), WeatherContentProvider.class.getName());
        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            assertEquals("Error: WeatherContentProvider registered with authority: " +
                            providerInfo.authority + " instead of authority:" + WeatherContract.CONTENT_AUTHORITY,
                    providerInfo.authority, WeatherContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            fail("Error: WeatherContentProvider not registered at " + context.getPackageName());
        }
    }

    @Test
    public void testUriMatcher() {
        UriMatcher uriMatcher = WeatherContentProvider.buildUriMatcher();

        Uri weatherUri = Uri.parse("content://net.sky.scweather/weather");
        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                WeatherContentProvider.WEATHER, uriMatcher.match(weatherUri));

        weatherUri = WeatherEntry.CONTENT_URI;
        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                WeatherContentProvider.WEATHER, uriMatcher.match(weatherUri));

        Uri weatherUriWithCity = Uri.parse("content://net.sky.scweather/weather/123");
        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                WeatherContentProvider.WEATHER_BY_CITY, uriMatcher.match(weatherUriWithCity));

        weatherUriWithCity = WeatherEntry.buildWeatherUriWithCity(123);
        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                WeatherContentProvider.WEATHER_BY_CITY, uriMatcher.match(weatherUriWithCity));

        Uri weatherUriWithCityAndDateParam = Uri.parse("content://net.sky.scweather/weather/123?date=456");
        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                WeatherContentProvider.WEATHER_BY_CITY, uriMatcher.match(weatherUriWithCityAndDateParam));

        weatherUriWithCityAndDateParam = WeatherEntry.buildWeatherUriWithCityAndDateParameter(123, 456);
        assertEquals("Error: The WEATHER URI was matched incorrectly.",
                WeatherContentProvider.WEATHER_BY_CITY, uriMatcher.match(weatherUriWithCityAndDateParam));

        Uri cityUri = Uri.parse("content://net.sky.scweather/city");
        assertEquals("Error: The CITY URI was matched incorrectly.",
                WeatherContentProvider.CITY, uriMatcher.match(cityUri));

        cityUri = CityEntry.CONTENT_URI;
        assertEquals("Error: The CITY URI was matched incorrectly.",
                WeatherContentProvider.CITY, uriMatcher.match(cityUri));

        Uri cityUriWithName =Uri.parse("content://net.sky.scweather/city?city_name=abc");
        assertEquals("Error: The CITY URI was matched incorrectly.",
                WeatherContentProvider.CITY, uriMatcher.match(cityUriWithName));

        cityUriWithName = CityEntry.buildCityUriWithCityNameParameter("xxx");
        assertEquals("Error: The CITY URI was matched incorrectly.",
                WeatherContentProvider.CITY, uriMatcher.match(cityUriWithName));

        Uri savedCityUri = Uri.parse("content://net.sky.scweather/saved_city");
        assertEquals("Error: The CITY URI was matched incorrectly.",
                WeatherContentProvider.SAVED_CITY, uriMatcher.match(savedCityUri));

        savedCityUri = SavedCityEntry.CONTENT_URI;
        assertEquals("Error: The CITY URI was matched incorrectly.",
                WeatherContentProvider.SAVED_CITY, uriMatcher.match(savedCityUri));
    }

    private void deleteRecords() {
        context.getContentResolver().delete(WeatherEntry.CONTENT_URI, null, null);
        Cursor cursor = context.getContentResolver().query(WeatherEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertEquals("Error: Records not deleted from Weather table", 0, cursor.getCount());
        cursor.close();

        context.getContentResolver().delete(CityEntry.CONTENT_URI, "_id = " + DatabaseTest.TEST_CITY_ID, null);
        context.getContentResolver().delete(CityEntry.CONTENT_URI, "_id < 3 ", null);
        cursor = context.getContentResolver().query(CityEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertEquals("Error: Records not deleted from City table", DatabaseTest.EXPECTED_CITIES_COUNT, cursor.getCount());
        cursor.close();
    }

    @Test
    public void testGetType() {
        String type = context.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        assertEquals("Error: the WeatherEntry CONTENT_URI should return WeatherEntry.CONTENT_TYPE",
                WeatherEntry.CONTENT_TYPE, type);

        type = context.getContentResolver().getType(WeatherEntry.buildWeatherUriWithCity(1));
        assertEquals("Error: the WeatherEntry CONTENT_URI with city should return WeatherEntry.CONTENT_TYPE",
                WeatherEntry.CONTENT_TYPE, type);

        type = context.getContentResolver().getType(WeatherEntry.buildWeatherUriWithCityAndDateParameter(1, 2));
        assertEquals("Error: the WeatherEntry CONTENT_URI with city and date should return WeatherEntry.CONTENT_TYPE",
                WeatherEntry.CONTENT_TYPE, type);

        type = context.getContentResolver().getType(CityEntry.CONTENT_URI);
        assertEquals("Error: the CityEntry CONTENT_URI should return CityEntry.CONTENT_TYPE",
                CityEntry.CONTENT_TYPE, type);

        type = context.getContentResolver().getType(CityEntry.buildCityUriWithCityNameParameter("abc"));
        assertEquals("Error: the CityEntry CONTENT_URI with city name should return CityEntry.CONTENT_TYPE",
                CityEntry.CONTENT_TYPE, type);

        type = context.getContentResolver().getType(SavedCityEntry.CONTENT_URI);
        assertEquals("Error: the SavedCityEntry CONTENT_URI should return SavedCityEntry.CONTENT_TYPE",
                        SavedCityEntry.CONTENT_TYPE, type);
    }

    @Test
    public void testWeatherQuery() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cityValues = DatabaseTest.createCityTestValues();
        long cityRowId = db.insert(CityEntry.TABLE_NAME, null, cityValues);
        assertNotEquals("Error: Failure to insert city values", cityRowId, -1);

        ContentValues weatherValues = DatabaseTest.createWeatherTestValues(cityRowId);
        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
        assertNotEquals("Error: Failure to insert weather values", weatherRowId, -1);

        db.close();

        Cursor weatherCursor = context.getContentResolver().query(WeatherEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(weatherCursor);
        DatabaseTest.validateCursor("testWeatherQuery", weatherCursor, weatherValues);
        weatherCursor.close();
    }

    @Test
    public void testSavedCityQuery() {
        Cursor cursor = context.getContentResolver().query(SavedCityEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);
        assertEquals("Error: SavedCity query didn't properly set NotificationUri", cursor.getNotificationUri(), SavedCityEntry.CONTENT_URI);
        assertEquals(DatabaseTest.EXPECTED_SAVED_CITIES_COUNT, cursor.getCount());
        cursor.close();
    }

    @Test
    public void testCityQuery() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cityValues = DatabaseTest.createCityTestValues();
        long cityRowId = db.insert(CityEntry.TABLE_NAME, null, cityValues);
        db.close();
        assertNotEquals("Error: Failure to insert city values", cityRowId, -1);

        Cursor cityCursor = context.getContentResolver().query(CityEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cityCursor);
        DatabaseTest.validateCursor("testCityQuery, city query", cityCursor, cityValues);
        assertEquals("Error: City query didn't properly set NotificationUri", cityCursor.getNotificationUri(), CityEntry.CONTENT_URI);
        cityCursor.close();

        Uri cityWithName = CityEntry.buildCityUriWithCityNameParameter("gua");
        cityCursor = context.getContentResolver().query(cityWithName, null, null, null, null);
        assertNotNull(cityCursor);
        assertEquals("Error: wrong number of cities with the string 'gua'", 11, cityCursor.getCount());
        cityCursor.close();
    }

    @Test
    public void testUpdateCity() {
        ContentValues initialValues = DatabaseTest.createCityTestValues();
        Uri cityUri = context.getContentResolver().insert(CityEntry.CONTENT_URI, initialValues);
        long cityRowId = ContentUris.parseId(cityUri);
        assertNotEquals(cityRowId, -1);

        Cursor cursor = context.getContentResolver().query(CityEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cursor);

        DatabaseTest.TestContentObserver tco = DatabaseTest.getTestContentObserver();
        cursor.registerContentObserver(tco);

        ContentValues updatedValues = new ContentValues(initialValues);
        updatedValues.put(CityEntry._ID, cityRowId);
        updatedValues.put(CityEntry.COLUMN_CITY_NAME, "Updated Village");

        int count = context.getContentResolver().update(
                CityEntry.CONTENT_URI, updatedValues, CityEntry._ID + "= ?",
                new String[]{Long.toString(cityRowId)});
        assertEquals(count, 1);
        tco.waitForNotificationOrFail();
        cursor.unregisterContentObserver(tco);
        cursor.close();

        cursor = context.getContentResolver().query(CityEntry.CONTENT_URI,
                null,
                CityEntry._ID + " = " + cityRowId,
                null, null);
        assertNotNull(cursor);
        DatabaseTest.validateCursor("testUpdateLocation.  Error validating city entry update.", cursor, updatedValues);
        cursor.close();
    }

    @Test
    public void testInsertReadProvider() {
        ContentValues cityTestValues = DatabaseTest.createCityTestValues();
        DatabaseTest.TestContentObserver tco = DatabaseTest.TestContentObserver.getTestContentObserver();
        context.getContentResolver().registerContentObserver(CityEntry.CONTENT_URI, true, tco);
        Uri cityUri = context.getContentResolver().insert(CityEntry.CONTENT_URI, cityTestValues);
        tco.waitForNotificationOrFail();
        context.getContentResolver().unregisterContentObserver(tco);
        long cityRowId = ContentUris.parseId(cityUri);
        assertNotEquals(cityRowId, -1);
        Cursor cityCursor = context.getContentResolver().query(CityEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(cityCursor);
        DatabaseTest.validateCursor("testInsertReadProvider. Error validating CityEntry.", cityCursor, cityTestValues);
        cityCursor.close();

        ContentValues weatherTestValues = DatabaseTest.createWeatherTestValues(cityRowId);
        tco = DatabaseTest.TestContentObserver.getTestContentObserver();
        context.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, tco);
        Uri weatherUri = context.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherTestValues);
        assertNotNull(weatherUri);
        tco.waitForNotificationOrFail();
        context.getContentResolver().unregisterContentObserver(tco);
        Cursor weatherCursor = context.getContentResolver().query(WeatherEntry.CONTENT_URI, null, null, null, null);
        assertNotNull(weatherCursor);
        DatabaseTest.validateCursor("testInsertReadProvider. Error validating WeatherEntry insert.", weatherCursor, weatherTestValues);
        weatherCursor.close();

        weatherCursor = context.getContentResolver().query(
                WeatherEntry.buildWeatherUriWithCityAndDateParameter(DatabaseTest.TEST_CITY_ID, DatabaseTest.TEST_WEATHER_DATE),
                null, null, null, null);
        assertNotNull(weatherCursor);
        DatabaseTest.validateCursor("testInsertReadProvider.  Error validating joined Weather and Location data for a specific date.",
                weatherCursor, weatherTestValues);
        weatherCursor.close();
    }

    @Test
    public void testDeleteRecords() {
        // insert test data
        testInsertReadProvider();

        // register observers
        DatabaseTest.TestContentObserver cityObserver = DatabaseTest.getTestContentObserver();
        context.getContentResolver().registerContentObserver(CityEntry.CONTENT_URI, true, cityObserver);
        DatabaseTest.TestContentObserver weatherObserver = DatabaseTest.getTestContentObserver();
        context.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, weatherObserver);

        // delete all records
        deleteRecords();

        // WeatherContentProvider.delete shall notifyChange(uri)
        cityObserver.waitForNotificationOrFail();
        weatherObserver.waitForNotificationOrFail();

        // unregister observers
        context.getContentResolver().unregisterContentObserver(cityObserver);
        context.getContentResolver().unregisterContentObserver(weatherObserver);
    }

    static private final int BULK_INSERT_WEATHER_RECORDS_TO_INSERT = 10;

    static private ContentValues[] createBulkInsertWeatherValues(long cityId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_WEATHER_RECORDS_TO_INSERT];
        for (int i = 0; i < BULK_INSERT_WEATHER_RECORDS_TO_INSERT; i++) {
            ContentValues weatherValues = new ContentValues();
            weatherValues.put(WeatherEntry.COLUMN_CITY_FK, cityId);
            weatherValues.put(WeatherEntry.COLUMN_FORECAST_DATE, i);
            weatherValues.put(WeatherEntry.COLUMN_LAST_UPDATE, i);
            weatherValues.put(WeatherEntry.COLUMN_ICON, i);
            weatherValues.put(WeatherEntry.COLUMN_DESCRIPTION, "desc " + i);
            weatherValues.put(WeatherEntry.COLUMN_DAY_PERIOD, i % 4);
            weatherValues.put(WeatherEntry.COLUMN_RAIN, i);
            weatherValues.put(WeatherEntry.COLUMN_RELATIVE_HUMIDITY, i);
            weatherValues.put(WeatherEntry.COLUMN_WIND_DIRECTION_START, "N");
            weatherValues.put(WeatherEntry.COLUMN_WIND_DIRECTION_END, "S");
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED_MAX, i);
            weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED_AVG, i);
            weatherValues.put(WeatherEntry.COLUMN_TEMPERATURE_MAX, i);
            weatherValues.put(WeatherEntry.COLUMN_TEMPERATURE_MIN, i);
            returnContentValues[i] = weatherValues;
        }
        return returnContentValues;
    }

    @Test
    public void testBulkInsertWeather() {
        //add city
        ContentValues testValues = DatabaseTest.createCityTestValues();
        Uri cityUri = context.getContentResolver().insert(CityEntry.CONTENT_URI, testValues);
        long cityId = ContentUris.parseId(cityUri);
        assertNotEquals(cityId, -1);
        Cursor cursor = context.getContentResolver().query(
                CityEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        assertNotNull(cursor);
        assertTrue("Empty cursor returned.", cursor.moveToFirst());
        DatabaseTest.validateCurrentRecord("testBulkInsertWeather. Error validating CityEntry.", cursor, testValues);
        cursor.close();

        //add weather records for city above
        ContentValues[] bulkInsertContentValues = createBulkInsertWeatherValues(cityId);
        DatabaseTest.TestContentObserver observer = DatabaseTest.getTestContentObserver();
        context.getContentResolver().registerContentObserver(WeatherEntry.CONTENT_URI, true, observer);
        int insertCount = context.getContentResolver().bulkInsert(WeatherEntry.CONTENT_URI, bulkInsertContentValues);
        observer.waitForNotificationOrFail();
        context.getContentResolver().unregisterContentObserver(observer);
        assertEquals(insertCount, BULK_INSERT_WEATHER_RECORDS_TO_INSERT);
        cursor = context.getContentResolver().query(
                WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                WeatherEntry.COLUMN_FORECAST_DATE + " ASC");
        assertNotNull(cursor);
        assertEquals(BULK_INSERT_WEATHER_RECORDS_TO_INSERT, cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_WEATHER_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            DatabaseTest.validateCurrentRecord("testBulkInsertWeather.  Error validating WeatherEntry " + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

    static private final int BULK_INSERT_CITY_RECORDS_TO_INSERT = 3;

    private ContentValues[] createBulkInsertCityValues() {
        String[] cities = {"New York", "Chicago", "San Francisco"};
        ContentValues[] cvs = new ContentValues[BULK_INSERT_CITY_RECORDS_TO_INSERT];
        for (int i = 0; i < BULK_INSERT_CITY_RECORDS_TO_INSERT; ++i) {
            ContentValues cv = new ContentValues();
            cv.put(CityEntry._ID, i);
            cv.put(CityEntry.COLUMN_CITY_NAME, cities[i]);
            cvs[i] = cv;
        }
        return cvs;
    }

    @Test
    public void testBulkInsertCities() {
        ContentValues[] bulkInsertContentValues = createBulkInsertCityValues();
        DatabaseTest.TestContentObserver observer = DatabaseTest.getTestContentObserver();
        context.getContentResolver().registerContentObserver(CityEntry.CONTENT_URI, true, observer);
        int insertCount = context.getContentResolver().bulkInsert(CityEntry.CONTENT_URI, bulkInsertContentValues);
        observer.waitForNotificationOrFail();
        context.getContentResolver().unregisterContentObserver(observer);
        assertEquals(BULK_INSERT_CITY_RECORDS_TO_INSERT, insertCount);
        Cursor cursor = context.getContentResolver().query(CityEntry.CONTENT_URI, null, null, null, CityEntry._ID + " ASC");
        assertNotNull(cursor);
        assertEquals(DatabaseTest.EXPECTED_CITIES_COUNT + BULK_INSERT_CITY_RECORDS_TO_INSERT, cursor.getCount());
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_CITY_RECORDS_TO_INSERT; ++i, cursor.moveToNext()) {
            DatabaseTest.validateCurrentRecord("testBulkInsertCities. Error validating CityEntry" + i,
                    cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }

}
