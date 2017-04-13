package net.sky.scweather;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

class SavedCitiesAdapter extends CursorAdapter {

    SavedCitiesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item_saved_city, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv = (TextView) view.findViewById(R.id.list_item_saved_city_text_view);
        String cityName = cursor.getString(SavedCitiesActivity.COLUMN_SAVED_CITY_NAME);
        tv.setText(cityName);
    }

}
