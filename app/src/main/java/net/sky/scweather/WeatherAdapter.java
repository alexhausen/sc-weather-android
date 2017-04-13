package net.sky.scweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class WeatherAdapter extends BaseExpandableListAdapter {

    private static final int VIEW_TYPE_SUMMARY = 0;
    private static final int VIEW_TYPE_SINGLE = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private static DateFormat dateFormat;

    static {
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    private Context context;
    private WeatherForecast forecast = null;

    WeatherAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getGroupTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getChildTypeCount() {
        return 1;
    }

    @Override
    public int getGroupType(int i) {
        if (forecast == null)
            return VIEW_TYPE_SINGLE;
        return forecast.getDay(i).count() > 1 ? VIEW_TYPE_SUMMARY : VIEW_TYPE_SINGLE;
    }

    @Override
    public int getChildType(int i, int j) {
        return 0;
    }

    @Override
    public int getGroupCount() {
        return forecast != null ? forecast.count() : 0;
    }

    @Override
    public int getChildrenCount(int i) {
        return forecast != null ? forecast.getDay(i).count() : 0;
    }

    @Override
    public Object getGroup(int i) {
        return forecast != null ? forecast.getDay(i) : null;
    }

    @Override
    public Object getChild(int i, int j) {
        return forecast != null ? forecast.getDay(i).getPeriod(j) : null;
    }

    @Override
    public long getGroupId(int i) {
        return forecast != null ? forecast.getId(i) : 0;
    }

    @Override
    public long getChildId(int i, int j) {
        return forecast != null ? forecast.getDay(i).getPeriod(j).getId() : 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView != null ? convertView : newGroupView(context, parent, i);
        bindGroupView(view, i, isExpanded);
        return view;
    }

    private View newGroupView(Context context, ViewGroup parent, int i) {
        int viewType = getGroupType(i);
        if (viewType == VIEW_TYPE_SUMMARY) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_weather_parent_summary, parent, false);
        } else if (viewType == VIEW_TYPE_SINGLE) {
            return LayoutInflater.from(context).inflate(R.layout.list_item_weather_parent_single, parent, false);
        }
        return null;
    }

    private void bindGroupView(View view, int i, boolean isExpanded) {
        if (forecast == null) return;
        WeatherForecast.DayForecast dayForecast = forecast.getDay(i);
        if (dayForecast.count() == 0) return;
        Date date = forecast.getDate(i);
        int viewType = getGroupType(i);
        String expandedChar = isExpanded ? "-" : "+";
        if (viewType == VIEW_TYPE_SUMMARY) {
            TextView tv = (TextView) view.findViewById(R.id.list_item_weather_parent_summary_text_view);
            String text = String.format(Locale.getDefault(), "%s %s %d periods", expandedChar,
                    dateFormat.format(date), dayForecast.count());
            tv.setText(text);
        } else if (viewType == VIEW_TYPE_SINGLE) {
            TextView tv = (TextView) view.findViewById(R.id.list_item_weather_single_text_view);
            WeatherForecast.PeriodForecast period = dayForecast.getPeriod(0);
            String text = String.format(Locale.getDefault(), "%s %s min:%d max:%d, icon:%d", expandedChar,
                    dateFormat.format(date), period.getTemperatureMin(), period.getTemperatureMax(),
                    period.getIcon());
            tv.setText(text);
        }
    }

    @Override
    public View getChildView(int i, int j, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_weather_child, parent, false);
        }
        bindChildView(convertView, i, j, isExpanded);
        return convertView;
    }

    private void bindChildView(View view, int i, int j, boolean isExpanded) {
        if (forecast == null) return;
        WeatherForecast.PeriodForecast period = forecast.getDay(i).getPeriod(j);
        TextView tv = (TextView) view.findViewById(R.id.list_item_weather_child_text_view);
        String text = String.format(Locale.getDefault(), "%s min:%d max:%d, icon:%d", period.getName(),
                period.getTemperatureMin(), period.getTemperatureMax(), period.getIcon());
        tv.setText(text);
    }

    @Override
    public boolean isChildSelectable(int i, int j) {
        return false;
    }

    void clear() {
        forecast = null;
        notifyDataSetInvalidated();
    }

    void set(WeatherForecast forecast) {
        this.forecast = forecast;
        notifyDataSetChanged();
    }

}
