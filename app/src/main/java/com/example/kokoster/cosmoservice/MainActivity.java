package com.example.kokoster.cosmoservice;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private CosmoServiceClient mCosmoServiceClient;
    private HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> mMeterData;
    private Toolbar toolbar;

    private CoordinatorLayout mCoordinatorLayout;

    private Fragment mMeterHistoryFragment;
    private Fragment mCurrentDataFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.kokoster.cosmoservice.R.layout.activity_main);

        createBottomBar(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String token;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                token = null;
            } else {
                token = extras.getString("token");
            }
        } else {
            token = (String) savedInstanceState.getSerializable("token");
        }

        System.out.println("In MainActivity token = " + token);

        mCosmoServiceClient = new CosmoServiceClient(this.getCacheDir(), token);

        mMeterData = new HashMap<>();

        Listener responseListener = new Listener();
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.COLD_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.HOT_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.DAY_LIGHT, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, responseListener);

//        mCurrentDataFragment = EditCurrentDataFragment.newInstance(mCosmoServiceClient);
        mCurrentDataFragment = new Fragment();

        showTabs();
    }

    private class Listener implements MeterDataHistoryResponseListener {
        @Override
        public void onSuccess(CosmoServiceClient.METER_DATAID dataId, ArrayList<MonthData> allMonths) {
            mMeterData.put(dataId, allMonths);

            if (allMeterDataRetreived()) {
                System.out.println("in MainActivity success");

                mMeterHistoryFragment = new MetersHistoryTabFragment();

                showTabs();
            }
        }

        @Override
        public void onError(CosmoServiceClient.METER_DATAID dataId, int errorCode) {
            System.out.println("Failed to retreive meter history, dataid: " + dataId + ", error: " + Integer.toString(errorCode));
        }
    }

    private void showTabs() {
        if (mMeterHistoryFragment == null) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mMeterHistoryFragment);
        fragmentTransaction.commit();
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

    private boolean allMeterDataRetreived() {
        return mMeterData.size() == 4;
    }

    private void createBottomBar(Bundle savedInstanceState) {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.bottom_bar_activity);

        BottomBar bottomBar = BottomBar.attach(this, savedInstanceState);
        bottomBar.setItemsFromMenu(R.menu.bottom_bar_menu, new OnMenuTabSelectedListener() {
            @Override
            public void onMenuItemSelected(int itemId) {
                switch (itemId) {
                    case R.id.meters_history:
                        FragmentTransaction historyFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        historyFragmentTransaction.replace(R.id.fragment_container, mMeterHistoryFragment);
                        historyFragmentTransaction.commit();

                        break;

                    case R.id.edit_current:
                        FragmentTransaction editFragmentTransaction = getSupportFragmentManager().beginTransaction();
                        editFragmentTransaction.replace(R.id.fragment_container, mCurrentDataFragment);
                        editFragmentTransaction.commit();

                        break;
                }
            }
        });
    }

    public ArrayList<ArrayList<String>> getHistoryData() {
        return createListFromMap(mMeterData);
    }
}
