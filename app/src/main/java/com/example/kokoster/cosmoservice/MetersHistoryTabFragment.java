package com.example.kokoster.cosmoservice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by kokoster on 28.05.16.
 */
public class MetersHistoryTabFragment extends Fragment {

    public static MetersHistoryTabFragment newInstance(ArrayList<ArrayList<String>> historyData) {
        MetersHistoryTabFragment fragment = new MetersHistoryTabFragment();
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
        final View historyView = inflater.inflate(R.layout.fragment_history_list, container, false);

        updateView(historyView);

        return historyView;
    }

    private void updateView(View historyView) {
        // adapter -> map -> set to list
        View fragmentView = historyView;

        if (fragmentView == null) {
            return;
        }

        ListView listView = (ListView) fragmentView.findViewById(R.id.metersList);

        int viewResourceID = com.example.kokoster.cosmoservice.R.layout.meter_list_item;

        MainActivity activity = (MainActivity) getActivity();

        final MeterDataArrayAdapter adapter = new MeterDataArrayAdapter(getActivity(), viewResourceID, activity.getHistoryData());

        if (listView != null) {
            listView.setAdapter(adapter);
        }

        listView.addHeaderView(createHeader());
    }

    private View createHeader() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View listHeaderView = inflater.inflate(
                R.layout.meter_list_item, null);

        TextView dateTextView = (TextView) listHeaderView.findViewById(R.id.date_text);
        TextView coldWaterTextView = (TextView) listHeaderView.findViewById(R.id.cold_water_text);
        TextView hotWaterTextView = (TextView) listHeaderView.findViewById(R.id.hot_water_text);
        TextView dayLightTextView = (TextView) listHeaderView.findViewById(R.id.day_light_text);
        TextView nightLightTextView = (TextView) listHeaderView.findViewById(R.id.night_light_text);

        dateTextView.setText("Дата");
        coldWaterTextView.setText("Хол (m3)");
        hotWaterTextView.setText("Гор (м3)");
        dayLightTextView.setText("День (кВт)");
        nightLightTextView.setText("Ночь (кВт)");

        return listHeaderView;
    }
}
