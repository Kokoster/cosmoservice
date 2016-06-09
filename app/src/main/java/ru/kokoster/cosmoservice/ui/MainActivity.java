package ru.kokoster.cosmoservice.ui;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import ru.kokoster.cosmoservice.model.MonthData;
import ru.kokoster.cosmoservice.R;
import ru.kokoster.cosmoservice.services.CosmoServiceClient;
import ru.kokoster.cosmoservice.services.MeterDataHistoryResponseListener;
import ru.kokoster.cosmoservice.services.MonthRequestListener;
import ru.kokoster.cosmoservice.services.SessionManager;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private CosmoServiceClient mCosmoServiceClient;
    private HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> mMeterData;
    private Toolbar mToolbar;

    private CoordinatorLayout mCoordinatorLayout;

    private MetersHistoryFragment mMeterHistoryFragment;
    private EditCurrentDataFragment mCurrentDataFragment;
    private SessionManager mSessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ru.kokoster.cosmoservice.R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.showOverflowMenu();
        setSupportActionBar(mToolbar);

//        TODO: не передавать токен явно
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

        Log.d(TAG, "onCreate. token = " + token);

        mCosmoServiceClient = new CosmoServiceClient(this.getCacheDir(), token);

        mSessionManager = new SessionManager(MainActivity.this.getApplicationContext());

        mMeterHistoryFragment = MetersHistoryFragment.newInstance();
        mCurrentDataFragment = EditCurrentDataFragment.newInstance(mCosmoServiceClient);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mMeterHistoryFragment);
        fragmentTransaction.commit();

        createBottomBar(savedInstanceState);

        mMeterData = new HashMap<>();

        Listener responseListener = new Listener();
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.COLD_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.HOT_WATER, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.DAY_LIGHT, responseListener);
        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, responseListener);

        mCosmoServiceClient.retrieveCurrentMonth(new MonthRequestListener() {
            @Override
            public void onSuccess(String month) {
                mCurrentDataFragment.setCurrentMonth(month);
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "retrieveCurrentMonth failed with " + Integer.toString(errorCode) + " error code");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.tool_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                mSessionManager.removeCurrentToken();
                startLoginActivity();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class Listener implements MeterDataHistoryResponseListener {
        @Override
        public void onSuccess(CosmoServiceClient.METER_DATAID dataId, ArrayList<MonthData> allMonths) {
            mMeterData.put(dataId, allMonths);

            if (allMeterDataRetreived()) {
                Log.d(TAG, "All meter data retrieved");

                mMeterHistoryFragment.setMetersHistory(createListFromMap(mMeterData));
            }
        }

        @Override
        public void onError(CosmoServiceClient.METER_DATAID dataId, int errorCode) {
            Log.d(TAG, "Failed to retrieve meter history, dataid: " + dataId + ", error: " + Integer.toString(errorCode));
        }
    }

    private boolean allMeterDataRetreived() {
        return mMeterData.size() == 4;
    }

    private ArrayList<ArrayList<String>> createListFromMap(HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> historyData) {
//        TODO: подумать над структурами данных: data, historyData
//        TODO: создать класс для данных
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

            row.add(getMeterValue(historyData, CosmoServiceClient.METER_DATAID.COLD_WATER, i));
            row.add(getMeterValue(historyData, CosmoServiceClient.METER_DATAID.HOT_WATER, i));
            row.add(getMeterValue(historyData, CosmoServiceClient.METER_DATAID.DAY_LIGHT, i));
            row.add(getMeterValue(historyData, CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, i));

            data.add(row);
        }

        return data;
    }

    private String getMeterValue(HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> historyData,
                                 CosmoServiceClient.METER_DATAID dataId, int position) {
        if (position >= historyData.get(dataId).size()) {
            return "";
        }

        return historyData.get(dataId).get(position).value.toString();
    }

    private int getMax(Integer ... meters) {
        return Collections.max(new ArrayList<>(Arrays.asList(meters)));
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

    private void startLoginActivity() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(loginActivityIntent);
        overridePendingTransition(0,0);
        finish();
    }
}
