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
    private static String COSMO_SERVICE_OBJECT_KEY = "cosmo_service";
    CosmoServiceClient mCosmoServiceClient;
    private HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> mMeterData;

    public static MetersHistoryTabFragment newInstance(CosmoServiceClient cosmoServiceClient) {
        Bundle args = new Bundle();
        args.putSerializable(COSMO_SERVICE_OBJECT_KEY, cosmoServiceClient);
        MetersHistoryTabFragment fragment = new MetersHistoryTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCosmoServiceClient = (CosmoServiceClient) getArguments().getSerializable(
                COSMO_SERVICE_OBJECT_KEY);

        ArrayList<MonthData> allMonthsData;

        mMeterData = new HashMap<>();

        Listener responseListener = new Listener();
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.COLD_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.HOT_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.DAY_LIGHT, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, responseListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_list, container, false);
    }

    private class Listener implements MeterDataHistoryResponseListener {
        @Override
        public void onSuccess(CosmoServiceClient.METER_DATAID dataId, ArrayList<MonthData> allMonths) {
            mMeterData.put(dataId, allMonths);

            if (allMeterDataRetreived()) {
                System.out.println("in MainActivity success");
//                mCosmoServiceClient.retrieveCurrentMetersData(new MetersCurrentDataListener() {
//                    @Override
//                    public void onSuccess(HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> metersCurrentData) {
//
//                    }
//
//                    @Override
//                    public void onError(int errorCode) {
//
//                    }
//                });

                updateView();

//                HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> data = new HashMap<>();
//                data.put(CosmoServiceClient.METER_DATAID.COLD_WATER, new BigDecimal(111));
//                data.put(CosmoServiceClient.METER_DATAID.HOT_WATER, new BigDecimal(0));
//                data.put(CosmoServiceClient.METER_DATAID.DAY_LIGHT, new BigDecimal(0));
//                data.put(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, new BigDecimal(0));
//
//                mCosmoServiceClient.saveCurrentMetersData(data);
            }
        }

        @Override
        public void onError(CosmoServiceClient.METER_DATAID dataId, int errorCode) {
            System.out.println("Failed to retreive meter history, dataid: " + dataId + ", error: " + Integer.toString(errorCode));
        }
    }

    private void updateView() {
        // adapter -> map -> set to list
        View fragmentView = getView();

        ListView listView = (ListView) fragmentView.findViewById(R.id.metersList);

        int viewResourceID = com.example.kokoster.cosmoservice.R.layout.meter_list_item;
        final MeterDataArrayAdapter adapter = new MeterDataArrayAdapter(getActivity(), viewResourceID, createListFromMap(mMeterData));
        listView.setAdapter(adapter);

        setHeader(listView);
    }

    private void setHeader(ListView listView) {
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

        listView.addHeaderView(listHeaderView);
    }

    private boolean allMeterDataRetreived() {
        return mMeterData.size() == 4;
    }

    private ArrayList<ArrayList<String>> createListFromMap(HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> historyData) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();

        int size = getMax(historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).size(),
                historyData.get(CosmoServiceClient.METER_DATAID.HOT_WATER).size(),
                historyData.get(CosmoServiceClient.METER_DATAID.DAY_LIGHT).size(),
                historyData.get(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT).size());

        for (int i = 0; i < size; ++i) {
            ArrayList<String> row = new ArrayList<>();

            if (i >= historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).size()) {
                row.add("");
            } else {
                row.add(historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).get(i).date);
            }

            if (i >= historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).size()) {
                row.add("");
            } else {
                row.add(historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).get(i).value.toString());
            }

            if (i >= historyData.get(CosmoServiceClient.METER_DATAID.HOT_WATER).size()) {
                row.add("");
            } else {
                row.add(historyData.get(CosmoServiceClient.METER_DATAID.HOT_WATER).get(i).value.toString());
            }

            if (i >= historyData.get(CosmoServiceClient.METER_DATAID.DAY_LIGHT).size()) {
                row.add("");
            } else {
                row.add(historyData.get(CosmoServiceClient.METER_DATAID.DAY_LIGHT).get(i).value.toString());
            }

            if (i >= historyData.get(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT).size()) {
                row.add("");
            } else {
                row.add(historyData.get(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT).get(i).value.toString());
            }

            data.add(row);
        }

        return data;
    }

    private int getMax(Integer ... meters) {
        return Collections.max(new ArrayList<Integer>(Arrays.asList(meters)));
    }
}
