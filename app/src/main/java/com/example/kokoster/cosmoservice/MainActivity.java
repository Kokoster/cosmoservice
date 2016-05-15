package com.example.kokoster.cosmoservice;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ContentFrameLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CosmoServiceClient mCosmoServiceClient;
    private HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> mMeterData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.kokoster.cosmoservice.R.layout.activity_main);

        String token;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
//                System.out.println("extras = null");
                token = null;
            } else {
//                System.out.println("extras here");
                token = extras.getString("token");
            }
        } else {
            token = (String) savedInstanceState.getSerializable("token");
        }

        System.out.println("In MainActivity token = " + token);

        mCosmoServiceClient = new CosmoServiceClient(this.getCacheDir(), token);
        ArrayList<MonthData> allMonthsData;

        mMeterData = new HashMap<>();

        Listener responseListener = new Listener();
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.COLD_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.HOT_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.DAY_LIGHT, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, responseListener);
    }

    private class Listener implements MeterDataResponseListener {
        @Override
        public void onSuccess(CosmoServiceClient.METER_DATAID dataId, ArrayList<MonthData> allMonths) {
            System.out.println("in MainActivity success");
            mMeterData.put(dataId, allMonths);

            if (allMeterDataRetreived()) {
                updateView();
            }
        }

        @Override
        public void onError(CosmoServiceClient.METER_DATAID dataId, int errorCode) {
            System.out.println("Failed to retreive meter history, dataid: " + dataId + ", error: " + Integer.toString(errorCode));
        }
    }

    private void updateView() {
        // adapter -> map -> set to list
        ListView listview = (ListView) findViewById(R.id.metersList);

        int viewResourceID = com.example.kokoster.cosmoservice.R.layout.meter_list_item;
        final MeterDataArrayAdapter adapter = new MeterDataArrayAdapter(this, viewResourceID, createListFromMap(mMeterData));
        listview.setAdapter(adapter);
    }

    private boolean allMeterDataRetreived() {
        return mMeterData.size() == 4;
    }

    private ArrayList<ArrayList<String>> createListFromMap(HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> historyData) {
        ArrayList<ArrayList<String>> data = new ArrayList<>();

        for (int i = 0; i < historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).size(); ++i) {
            ArrayList<String> row = new ArrayList<>();
            row.add(historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).get(i).date);
            row.add(historyData.get(CosmoServiceClient.METER_DATAID.COLD_WATER).get(i).value);
            row.add(historyData.get(CosmoServiceClient.METER_DATAID.HOT_WATER).get(i).value);

            if (i >= historyData.get(CosmoServiceClient.METER_DATAID.DAY_LIGHT).size()) {
                row.add("");
                row.add("");
            } else {
                row.add(historyData.get(CosmoServiceClient.METER_DATAID.DAY_LIGHT).get(i).value);
                row.add(historyData.get(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT).get(i).value);
            }

            data.add(row);
        }

        return data;
    }
}
