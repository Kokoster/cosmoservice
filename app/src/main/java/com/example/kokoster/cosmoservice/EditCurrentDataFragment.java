package com.example.kokoster.cosmoservice;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by kokoster on 29.05.16.
 */
public class EditCurrentDataFragment extends Fragment {
    private static String COSMO_SERVICE_OBJECT_KEY = "cosmo_service";
    CosmoServiceClient mCosmoServiceClient;

    EditText mColdWaterEditText;
    EditText mHotWaterEditText;
    EditText mDayLightEditText;
    EditText mNightLightEditText;

    BigDecimal mColdWaterValue;
    BigDecimal mHotWaterValue;
    BigDecimal mDayLightValue;
    BigDecimal mNightLightValue;

    public static EditCurrentDataFragment newInstance(CosmoServiceClient cosmoServiceClient) {
        Bundle args = new Bundle();
        args.putSerializable(COSMO_SERVICE_OBJECT_KEY, cosmoServiceClient);
        EditCurrentDataFragment fragment = new EditCurrentDataFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCosmoServiceClient = (CosmoServiceClient) getArguments().getSerializable(
                COSMO_SERVICE_OBJECT_KEY);

        mCosmoServiceClient.retrieveCurrentMetersData(new MetersCurrentDataListener() {
            @Override
            public void onSuccess(HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> currentMetersData) {
                mColdWaterValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.COLD_WATER);
                mHotWaterValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.HOT_WATER);
                mDayLightValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.DAY_LIGHT);
                mNightLightValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT);

                updateViews();
            }

            @Override
            public void onError(int errorCode) {
                System.out.println("can not retrieve current data. Returned with error code " + Integer.toString(errorCode));
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View editView = inflater.inflate(R.layout.fragment_edit_current, container, false);

        mColdWaterEditText = (EditText) editView.findViewById(R.id.cold_water_edit);
        mHotWaterEditText = (EditText) editView.findViewById(R.id.hot_water_edit);
        mDayLightEditText = (EditText) editView.findViewById(R.id.day_light_edit);
        mNightLightEditText = (EditText) editView.findViewById(R.id.night_light_edit);

        updateViews();

        // Inflate the layout for this fragment
        return editView;
    }

    @SuppressLint("SetTextI18n")
    private void updateViews() {
        if (mColdWaterValue == null || mHotWaterValue == null ||
            mDayLightValue == null || mNightLightValue == null)
        {
            return;
        }

        if (mColdWaterEditText == null || mHotWaterEditText == null ||
            mDayLightEditText == null || mNightLightEditText == null)
        {
            return;
        }

        mColdWaterEditText.setText(mColdWaterValue.toString());
        mHotWaterEditText.setText(mHotWaterValue.toString());
        mDayLightEditText.setText(mDayLightValue.toString());
        mNightLightEditText.setText(mNightLightValue.toString());
    }
}
