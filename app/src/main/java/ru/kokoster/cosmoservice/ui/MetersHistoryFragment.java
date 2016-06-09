package ru.kokoster.cosmoservice.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.kokoster.cosmoservice.R;

/**
 * Created by kokoster on 28.05.16.
 */
public class MetersHistoryFragment extends Fragment {
    private View mHistoryView;
    private ArrayList<ArrayList<String>> mMetersHistory;
    private View mListHeaderView;
    private ListView mListView;

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
        fillHeader();

        updateView(mMetersHistory);

        mListView = (ListView) mHistoryView.findViewById(R.id.metersList);
        mListView.addHeaderView(mListHeaderView);

        return mHistoryView;
    }

    public void setMetersHistory(ArrayList<ArrayList<String>> metersHistory) {
        mMetersHistory = metersHistory;
        updateView(metersHistory);
    }

    private void updateView(ArrayList<ArrayList<String>> metersHistory) {
        // adapter -> map -> set to list
        if (mHistoryView == null || metersHistory == null) {
            return;
        }

        if (mListView == null) {
            return;
        }

        int viewResourceID = ru.kokoster.cosmoservice.R.layout.meter_list_item;

        final MeterDataArrayAdapter adapter = new MeterDataArrayAdapter(mHistoryView.getContext(), viewResourceID, metersHistory);

        mListView.setAdapter(adapter);
        mListView.setVisibility(View.VISIBLE);
    }

    private void fillHeader() {
        if (mHistoryView == null || mListHeaderView == null) {
            return;
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
    }
}
