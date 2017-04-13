package net.sky.scweather;

import android.util.LongSparseArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherForecast {

    public static final int MAX_FORECAST_DAYS = 5;

    class PeriodForecast {
        static final int DAILY = 0;
        static final int DAWN = 1;
        static final int MORNING = 2;
        static final int AFTERNOON = 3;
        static final int EVENING = 4;

        private int periodId;
        private int temperatureMin;
        private int temperatureMax;
        private int icon;
        private int rain;
        private String description;
        private int humidity;
        private String windStart;
        private String windEnd;
        private int windAvgSpeed;
        private int windMaxSpeed;

        PeriodForecast(int periodId, int min, int max, int icon, int rain, String description, int humidity,
                       String windStart, String windEnd, int windAvgSpeed, int windMaxSpeed) {
            this.periodId = periodId;
            this.temperatureMax = max;
            this.temperatureMin = min;
            this.rain = rain;
            this.icon = icon;
            this.description = description;
            this.humidity = humidity;
            this.windStart = windStart;
            this.windEnd = windEnd;
            this.windAvgSpeed = windAvgSpeed;
            this.windMaxSpeed = windMaxSpeed;
        }

        int getId() {
            return periodId;
        }

        String getName() {
            //TODO translate
            switch (periodId) {
                case DAILY:
                    return "daily";
                case DAWN:
                    return "dawn";
                case MORNING:
                    return "morning";
                case AFTERNOON:
                    return "afternoon";
                case EVENING:
                    return "evening";
            }
            return "invalid";
        }

        int getTemperatureMin() {
            return temperatureMin;
        }

        int getTemperatureMax() {
            return temperatureMax;
        }

        int getIcon() {
            return icon;
        }

        int getRain() {
            return rain;
        }

        String getDescription() {
            return description;
        }

        int getHumidity() {
            return humidity;
        }

        String getWindStart() {
            return windStart;
        }

        String getWindEnd() {
            return windEnd;
        }

        int getWindAvgSpeed() {
            return windAvgSpeed;
        }

        int getWindMaxSpeed() {
            return windMaxSpeed;
        }

    }

    class DayForecast {

        DayForecast() {
            periods = new ArrayList<>();
        }

        List<PeriodForecast> periods;

        void add(PeriodForecast period) {
            periods.add(period);
        }

        PeriodForecast getPeriod(int i) {
            return periods.get(i);
        }

        int count() {
            return periods.size();
        }
    }

    WeatherForecast() {
        forecastByDay = new LongSparseArray<>(MAX_FORECAST_DAYS);
    }

    private LongSparseArray<DayForecast> forecastByDay;

    int count() {
        return forecastByDay.size();
    }

    long getId(int i) {
        return forecastByDay.keyAt(i);
    }

    DayForecast getDay(int i) {
        return forecastByDay.get(forecastByDay.keyAt(i));
    }

    Date getDate(int i) {
        return new Date(forecastByDay.keyAt(i));
    }

    void add(long date, int periodId, int min, int max, int icon, int rain, String description,
             int humidity, String windStart, String windEnd, int windAvgSpeed, int windMaxSpeed) {
        DayForecast dailyForecast = forecastByDay.get(date);
        if (dailyForecast == null) {
            dailyForecast = new DayForecast();
            forecastByDay.put(date, dailyForecast);
        }
        PeriodForecast periodX = new PeriodForecast(periodId, min, max, icon, rain, description,
                humidity, windStart, windEnd, windAvgSpeed, windMaxSpeed);
        dailyForecast.add(periodX);
    }

}
