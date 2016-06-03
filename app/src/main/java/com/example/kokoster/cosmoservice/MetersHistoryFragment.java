package com.example.kokoster.cosmoservice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kokoster on 28.05.16.
 */
public class MetersHistoryFragment extends Fragment {
    private View mHistoryView = null;
    private ArrayList<ArrayList<String>> mMetersHistory = null;
    private View mListHeaderView;

    public static MetersHistoryFragment newInstance() {
        MetersHistoryFragment fragment = new MetersHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mHistoryView = inflater.inflate(R.layout.fragment_history_list, container, false);

        mListHeaderView = inflater.inflate(
                R.layout.meter_list_item, null);

        updateView(mMetersHistory);

        return mHistoryView;
    }

    private void updateView(ArrayList<ArrayList<String>> metersHistory) {
        // adapter -> map -> set to list
        if (mHistoryView == null) {
            return;
        }

        if (metersHistory == null) {
            return;
        }

        ListView listView = (ListView) mHistoryView.findViewById(R.id.metersList);

        if (listView == null) {
            return;
        }

        int viewResourceID = com.example.kokoster.cosmoservice.R.layout.meter_list_item;

        final MeterDataArrayAdapter adapter = new MeterDataArrayAdapter(mHistoryView.getContext(), viewResourceID, metersHistory);

        listView.setAdapter(adapter);
        listView.addHeaderView(createHeader());
        listView.setVisibility(View.VISIBLE);
    }

    public void setMetersHistory(ArrayList<ArrayList<String>> metersHistory) {
        mMetersHistory = metersHistory;
        updateView(metersHistory);
    }


    // TODO: createHeader(inflater) or member?
    private View createHeader() {
        if (mHistoryView == null || mListHeaderView == null) {
            return null;
        }

        TextView dateTextView = (TextView) mListHeaderView.findViewById(R.id.date_text);
        TextView coldWaterTextView = (TextView) mListHeaderView.findViewById(R.id.cold_water_text);
        TextView hotWaterTextView = (TextView) mListHeaderView.findViewById(R.id.hot_water_text);
        TextView dayLightTextView = (TextView) mListHeaderView.findViewById(R.id.day_light_text);
        TextView nightLightTextView = (TextView) mListHeaderView.findViewById(R.id.night_light_text);

        dateTextView.setText("Дата");
        coldWaterTextView.setText("Хол (м3)");
        hotWaterTextView.setText("Гор (м3)");
        dayLightTextView.setText("День (кВт)");
        nightLightTextView.setText("Ночь (кВт)");

        return mListHeaderView;
    }
}
