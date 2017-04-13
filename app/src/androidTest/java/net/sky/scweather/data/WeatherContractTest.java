package net.sky.scweather.data;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class WeatherContractTest {

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testBuildWeatherUri() {
        final long ID = 123456;
        Uri uri = WeatherContract.WeatherEntry.buildWeatherUri(ID);
        assertNotNull("Error: Uri must not be null", uri);
        assertEquals("Error: City ID not appended to the URI", Long.toString(ID), uri.getLastPathSegment());
        final String EXPECTED_URI = "content://net.sky.scweather/weather/123456";
        assertEquals("Error: Unexpected URI " + uri, EXPECTED_URI, uri.toString());
    }

    @Test
    public void testBuildWeatherUriWithCity() {
        final int CITY_ID = 123;
        Uri uri = WeatherContract.WeatherEntry.buildWeatherUriWithCity(CITY_ID);
        assertNotNull("Error: Uri must not be null", uri);
        assertEquals("Error: Wrong city ID", CITY_ID, WeatherContract.WeatherEntry.getCityFromUri(uri));
        assertEquals("Error: Wrong date", 0, WeatherContract.WeatherEntry.getDateParameterFromUri(uri));
        final String EXPECTED_URI = "content://net.sky.scweather/weather/123";
        assertEquals("Error: Unexpected URI " + uri, EXPECTED_URI, uri.toString());
    }

    @Test
    public void testBuildWeatherUriWithCityAndDateParameter() {
        final int CITY_ID = 9999;
        final long FORECAST_DATE = 1485648000000L; // 2017/01/29
        Uri uri = WeatherContract.WeatherEntry.buildWeatherUriWithCityAndDateParameter(CITY_ID, FORECAST_DATE);
        assertNotNull("Error: Uri must not be null", uri);
        assertEquals("Error: Wrong city ID", CITY_ID, WeatherContract.WeatherEntry.getCityFromUri(uri));
        assertEquals("Error: Wrong date", FORECAST_DATE, WeatherContract.WeatherEntry.getDateParameterFromUri(uri));
        final String EXPECTED_URI = "content://net.sky.scweather/weather/9999?forecast_date=1485648000000";
        assertEquals("Error: Unexpected URI " + uri, EXPECTED_URI, uri.toString());
    }
}
