package com.example.kokoster.cosmoservice;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabSelectedListener;

import java.util.ArrayList;
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

        mMeterHistoryFragment = MetersHistoryTabFragment.newInstance(mCosmoServiceClient);
        mCurrentDataFragment = EditCurrentDataFragment.newInstance(mCosmoServiceClient);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, mMeterHistoryFragment);
        fragmentTransaction.commit();
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
}
