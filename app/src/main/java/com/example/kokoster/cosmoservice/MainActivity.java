package com.example.kokoster.cosmoservice;

import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private CosmoServiceClient mCosmoServiceClient;
    private HashMap<CosmoServiceClient.METER_DATAID, ArrayList<MonthData>> mMeterData;
    private Toolbar toolbar;

    private BottomSheetBehavior mBottomSheetBehavior;
    private Button mButton;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.kokoster.cosmoservice.R.layout.activity_main);

//        View bottomSheetView = findViewById( R.id.bottom_sheet );
//        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView);

//        mButton = (Button) findViewById(R.id.edit_button);
//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//            }
//        });

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager(),
                    MainActivity.this, mCosmoServiceClient));
        }

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tab);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(viewPager);
        }

//        ArrayList<MonthData> allMonthsData;
//
//        mMeterData = new HashMap<>();

//        Listener responseListener = new Listener();
//        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.COLD_WATER, responseListener);
//        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.HOT_WATER, responseListener);
//        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.DAY_LIGHT, responseListener);
//        mCosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, responseListener);
    }
}
