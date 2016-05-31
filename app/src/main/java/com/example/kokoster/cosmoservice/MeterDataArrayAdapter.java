package com.example.kokoster.cosmoservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kokoster on 15.05.16.
 */
public class MeterDataArrayAdapter extends ArrayAdapter {
    private Context context;
    private final ArrayList<ArrayList<String>> historyData;

    public MeterDataArrayAdapter(Context context, int viewResourceId, ArrayList<ArrayList<String>> data) {
        super(context, viewResourceId, data);
        this.context = context;
        this.historyData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.meter_list_item, parent, false);

        TextView dateTextView = (TextView) rowView.findViewById(R.id.date_text);
        TextView coldWaterTextView = (TextView) rowView.findViewById(R.id.cold_water_text);
        TextView hotWaterTextView = (TextView) rowView.findViewById(R.id.hot_water_text);
        TextView dayLightTextView = (TextView) rowView.findViewById(R.id.day_light_text);
        TextView nightLightTextView = (TextView) rowView.findViewById(R.id.night_light_text);

        dateTextView.setText(historyData.get(position).get(0));
        coldWaterTextView.setText(historyData.get(position).get(1));
        hotWaterTextView.setText(historyData.get(position).get(2));
        dayLightTextView.setText(historyData.get(position).get(3));
        nightLightTextView.setText(historyData.get(position).get(4));

        return rowView;
    }
}
