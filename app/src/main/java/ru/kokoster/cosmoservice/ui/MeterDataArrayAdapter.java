package ru.kokoster.cosmoservice.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import ru.kokoster.cosmoservice.R;

/**
 * Created by kokoster on 15.05.16.
 */
public class MeterDataArrayAdapter extends ArrayAdapter {
    private Context mContext;
    private final ArrayList<ArrayList<String>> mHistoryData;

    public MeterDataArrayAdapter(Context context, int viewResourceId, ArrayList<ArrayList<String>> data) {
        super(context, viewResourceId, data);
        this.mContext = context;
        this.mHistoryData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.meter_list_item, parent, false);

        TextView dateTextView = (TextView) rowView.findViewById(R.id.date_text);
        TextView coldWaterTextView = (TextView) rowView.findViewById(R.id.cold_water_text);
        TextView hotWaterTextView = (TextView) rowView.findViewById(R.id.hot_water_text);
        TextView dayLightTextView = (TextView) rowView.findViewById(R.id.day_light_text);
        TextView nightLightTextView = (TextView) rowView.findViewById(R.id.night_light_text);


        if (dateTextView == null || coldWaterTextView == null || hotWaterTextView == null ||
                dayLightTextView == null || nightLightTextView == null) {
            return null;
        }

        dateTextView.setText(mHistoryData.get(position).get(0));
        coldWaterTextView.setText(mHistoryData.get(position).get(1));
        hotWaterTextView.setText(mHistoryData.get(position).get(2));
        dayLightTextView.setText(mHistoryData.get(position).get(3));
        nightLightTextView.setText(mHistoryData.get(position).get(4));

        return rowView;
    }
}
