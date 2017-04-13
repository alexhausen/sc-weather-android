package net.sky.scweather.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    static final int EXPECTED_SAVED_CITIES_COUNT = 2;
    static final int EXPECTED_CITIES_COUNT = 295;
    static final int TEST_CITY_ID = 1234;
    static final long TEST_WEATHER_DATE = 1485648000000L;

    private Context context = InstrumentationRegistry.getTargetContext();

    private void deleteTheDatabase() {
        context.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
    }

    @Before
    public void setUp() {
        deleteTheDatabase();
    }

    @After
    public void tearDown() {
        deleteTheDatabase();
    }

    @Test
    public void testCreateDatabase() throws Exception {
        SQLiteDatabase db = new WeatherDbHelper(context).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: Database has not been created correctly", c.moveToFirst());
        final Set<String> tableNames = new HashSet<>();
        tableNames.add(WeatherContract.CityEntry.TABLE_NAME);
        tableNames.add(WeatherContract.SavedCityEntry.TABLE_NAME);
        tableNames.add(WeatherContract.WeatherEntry.TABLE_NAME);
        do {
            tableNames.remove(c.getString(0));
        } while (c.moveToNext());
        assertTrue("Error: " + tableNames.size() + " tables not created", tableNames.isEmpty());
        c.close();

        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.CityEntry.TABLE_NAME + ")", null);
        assertTrue("Error: Unable to query the database for table information.", c.moveToFirst());
        final Set<String> cityTableColumns = new HashSet<>();
        cityTableColumns.add(WeatherContract.CityEntry._ID);
        cityTableColumns.add(WeatherContract.CityEntry.COLUMN_CITY_NAME);
        int columnNameIndex = c.getColumnIndex("name");
        do {
            cityTableColumns.remove(c.getString(columnNameIndex));
        } while (c.moveToNext());
        assertTrue("Error: " + cityTableColumns.size() + " columns not created on table " +
                WeatherContract.CityEntry.TABLE_NAME, cityTableColumns.isEmpty());
        c.close();

        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.SavedCityEntry.TABLE_NAME + ")", null);
        assertTrue("Error: Unable to query the database for table information", c.moveToFirst());
        final Set<String> savedCityTableColumns = new HashSet<>();
        savedCityTableColumns.add(WeatherContract.SavedCityEntry._ID);
        savedCityTableColumns.add(WeatherContract.SavedCityEntry.COLUMN_CITY_FK);
        columnNameIndex = c.getColumnIndex("name");
        do {
            savedCityTableColumns.remove(c.getString(columnNameIndex));
        } while (c.moveToNext());
        assertTrue("Error: " + savedCityTableColumns.size() + " columns not created on table " +
                WeatherContract.SavedCityEntry.TABLE_NAME, cityTableColumns.isEmpty());
        c.close();

        c = db.rawQuery("PRAGMA table_info(" + WeatherContract.WeatherEntry.TABLE_NAME + ")", null);
        assertTrue("Error: Unable to query the database for table information.", c.moveToFirst());
        final Set<String> weatherTableColumns = new HashSet<>();
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_CITY_FK);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_FORECAST_DATE);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_ICON);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_DAY_PERIOD);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_RAIN);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_RELATIVE_HUMIDITY);
        weatherTableColumns.add(WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION_START);
        columnNameIndex = c.getColumnIndex("name");
        do {
            weatherTableColumns.remove(c.getString(columnNameIndex));
        } while (c.moveToNext());
        assertTrue("Error: " + weatherTableColumns.size() + " columns not created on table weather", weatherTableColumns.isEmpty());
        c.close();

        db.close();
    }

    @Test
    public void testCityTable() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(WeatherContract.CityEntry.TABLE_NAME, null, null, null, null, null, null, null);
        assertNotNull(cursor);
        assertTrue("Error: No records found", cursor.moveToFirst());
        assertEquals("Error: Unexpected city count", EXPECTED_CITIES_COUNT, cursor.getCount());
        ContentValues testValues = createCityTestValues();
        long rowId = db.insert(WeatherContract.CityEntry.TABLE_NAME, null, testValues);
        assertTrue("Error: Insert city record failure", rowId != -1);
        cursor = db.query(WeatherContract.CityEntry.TABLE_NAME, null, null, null, null, null, null, null);
        assertTrue("Error: No records found", cursor.moveToFirst());
        assertEquals("Error: Unexpected city count", EXPECTED_CITIES_COUNT + 1, cursor.getCount());
        validateCurrentRecord("Error: City query validation failure", cursor, testValues);
        cursor.close();
        db.close();
    }

    @Test
    public void testSavedCityTable() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(WeatherContract.SavedCityEntry.TABLE_NAME, null, null, null, null, null, null, null);
        assertNotNull(cursor);
        assertTrue("Error: No records found", cursor.moveToFirst());
        assertEquals("Error: Unexpected city count", EXPECTED_SAVED_CITIES_COUNT, cursor.getCount());
        cursor.close();
        db.close();
    }

    @Test
    public void testWeatherTable() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long cityRowId = insertCity();
        ContentValues testValues = createWeatherTestValues(cityRowId);
        long weatherRowId = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, testValues);
        assertTrue("Error: Insert weather record failure", weatherRowId != -1);
        Cursor cursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME, null, null, null, null, null, null, null);
        assertTrue("Error: No records found", cursor.moveToFirst());
        validateCurrentRecord("Error: Weather query validation failure", cursor, testValues);
        assertFalse("Error: More than one record returned from query", cursor.moveToNext());
        cursor.close();
        db.close();
    }

    private long insertCity() {
        WeatherDbHelper dbHelper = new WeatherDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = createCityTestValues();
        long rowId = db.insert(WeatherContract.CityEntry.TABLE_NAME, null, testValues);
        assertTrue("Error: Insert city record failure", rowId != -1);
        db.close();
        return rowId;
    }

    static ContentValues createWeatherTestValues(long cityRowId) {
        ContentValues testValues = new ContentValues();
        testValues.put(WeatherContract.WeatherEntry.COLUMN_CITY_FK, cityRowId);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_FORECAST_DATE, TEST_WEATHER_DATE);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_LAST_UPDATE, 2);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_ICON, 3);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION, "desc");
        testValues.put(WeatherContract.WeatherEntry.COLUMN_DAY_PERIOD, 1);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_RAIN, 5);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_RELATIVE_HUMIDITY, 6);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION_START, "NO");
        testValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_DIRECTION_END, "SE");
        testValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED_MAX, 7);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED_AVG, 8);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_TEMPERATURE_MAX, 9);
        testValues.put(WeatherContract.WeatherEntry.COLUMN_TEMPERATURE_MIN, 10);
        return testValues;
    }

    static ContentValues createCityTestValues() {
        ContentValues testValues = new ContentValues();
        testValues.put(WeatherContract.CityEntry._ID, TEST_CITY_ID);
        testValues.put(WeatherContract.CityEntry.COLUMN_CITY_NAME, "Testville");
        return testValues;
    }

    public static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertNotNull(valueCursor);
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread ht;
        boolean contentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            this.contentChanged = false;
            this.ht = ht;
        }

        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            contentChanged = true;
        }

        void waitForNotificationOrFail() {
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return contentChanged;
                }
            }.run();
            ht.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

}

abstract class PollingCheck {

    private static final long SLEEP_TIME = 10;
    private long timeout;

    PollingCheck(long timeout) {
        this.timeout = timeout;
    }

    protected abstract boolean check();

    void run() {
        if (check()) {
            return;
        }

        long timeout = this.timeout;
        while (timeout > 0) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                fail();
            }
            if (check()) return;
            timeout -= SLEEP_TIME;
        }
        fail("Invalid timeout");
    }
}
