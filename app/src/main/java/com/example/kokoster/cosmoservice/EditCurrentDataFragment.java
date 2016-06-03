package com.example.kokoster.cosmoservice;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * Created by kokoster on 29.05.16.
 */
public class EditCurrentDataFragment extends Fragment {
    private static String COSMO_SERVICE_OBJECT_KEY = "cosmo_service";

    private View mEditView = null;

    private ProgressBar mSaveProgressBar;
    private TextView mErrorText;

    private ProgressDialog mProgressDilog;

    private CosmoServiceClient mCosmoServiceClient = null;
    private String mCurrentMonth = "";

    EditText mColdWaterEditText;
    EditText mHotWaterEditText;
    EditText mDayLightEditText;
    EditText mNightLightEditText;

    BigDecimal mColdWaterValue;
    BigDecimal mHotWaterValue;
    BigDecimal mDayLightValue;
    BigDecimal mNightLightValue;

    Button mSaveButton;

    public static EditCurrentDataFragment newInstance(CosmoServiceClient cosmoServiceClient) {
        Bundle args = new Bundle();
        args.putSerializable(COSMO_SERVICE_OBJECT_KEY, cosmoServiceClient);
        EditCurrentDataFragment fragment = new EditCurrentDataFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private void updateMetersData(HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> currentMetersData) {
        mColdWaterValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.COLD_WATER);
        mHotWaterValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.HOT_WATER);
        mDayLightValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.DAY_LIGHT);
        mNightLightValue = currentMetersData.get(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT);

        updateViews();
    }

    private void setEnabledElements(boolean enabled) {
        mColdWaterEditText.setEnabled(enabled);
        mHotWaterEditText.setEnabled(enabled);
        mDayLightEditText.setEnabled(enabled);
        mNightLightEditText.setEnabled(enabled);
        mSaveButton.setEnabled(enabled);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCosmoServiceClient = (CosmoServiceClient) getArguments().getSerializable(
                COSMO_SERVICE_OBJECT_KEY);

        mCosmoServiceClient.retrieveCurrentMetersData(new MetersCurrentDataListener() {
            @Override
            public void onSuccess(HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> currentMetersData) {
                updateMetersData(currentMetersData);
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
        mEditView = inflater.inflate(R.layout.fragment_edit_current, container, false);

        mProgressDilog = ProgressDialog.show(mEditView.getContext(), "", "Загрузка");

        mSaveProgressBar = (ProgressBar) mEditView.findViewById(R.id.save_progress_bar);

        mColdWaterEditText = (EditText) mEditView.findViewById(R.id.cold_water_edit);
        mHotWaterEditText = (EditText) mEditView.findViewById(R.id.hot_water_edit);
        mDayLightEditText = (EditText) mEditView.findViewById(R.id.day_light_edit);
        mNightLightEditText = (EditText) mEditView.findViewById(R.id.night_light_edit);

        TextView currentMonthTextView = (TextView) mEditView.findViewById(R.id.current_month);
        currentMonthTextView.setText(mCurrentMonth);

        mErrorText = (TextView) mEditView.findViewById(R.id.error_text_view);

        updateViews();

        mSaveButton = (Button) mEditView.findViewById(R.id.save_button);

//        setEnabledElements(false);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSaveProgressBar.setVisibility(View.VISIBLE);
                setEnabledElements(false);
                mErrorText.setVisibility(View.INVISIBLE);

                mCosmoServiceClient.saveCurrentMetersData(getEditTextData(), new SaveDataListener() {
                    @Override
                    public void onSuccess() {
                        mCosmoServiceClient.retrieveCurrentMetersData(new MetersCurrentDataListener() {
                            @Override
                            public void onSuccess(HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> currentMetersData) {
                                updateMetersData(currentMetersData);

                                mSaveProgressBar.setVisibility(View.INVISIBLE);
                                setEnabledElements(true);
                            }

                            @Override
                            public void onError(int errorCode) {
                                System.out.println("retrieveCurrentMetersData returned with error code " + Integer.toString(errorCode));

                                mSaveProgressBar.setVisibility(View.INVISIBLE);
                                setEnabledElements(true);
                                mErrorText.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onError(int errorCode) {
                        System.out.println("saveCurrentMetersData failed with error " + Integer.toString(errorCode));

                        mProgressDilog.dismiss();
                        setEnabledElements(true);
                        mErrorText.setVisibility(View.VISIBLE);
                    }
                });
            }

            private HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> getEditTextData() {
                HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> data = new HashMap<>();

                String coldWaterData = mColdWaterEditText.getText().toString();
                String hotWaterData = mHotWaterEditText.getText().toString();
                String dayLightData = mDayLightEditText.getText().toString();
                String nightLightData = mNightLightEditText.getText().toString();

                if (!coldWaterData.equals("")) {
                    data.put(CosmoServiceClient.METER_DATAID.COLD_WATER, new BigDecimal(coldWaterData));
                } else {
                    data.put(CosmoServiceClient.METER_DATAID.COLD_WATER, new BigDecimal(0));
                }

                if (!hotWaterData.equals("")) {
                    data.put(CosmoServiceClient.METER_DATAID.HOT_WATER, new BigDecimal(hotWaterData));
                } else {
                    data.put(CosmoServiceClient.METER_DATAID.HOT_WATER, new BigDecimal(0));
                }

                if (!dayLightData.equals("")) {
                    data.put(CosmoServiceClient.METER_DATAID.DAY_LIGHT, new BigDecimal(dayLightData));
                } else {
                    data.put(CosmoServiceClient.METER_DATAID.DAY_LIGHT, new BigDecimal(0));
                }

                if (!nightLightData.equals("")) {
                    data.put(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, new BigDecimal(nightLightData));
                } else {
                    data.put(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, new BigDecimal(0));
                }

                return data;
            }
        });

        // Inflate the layout for this fragment
        return mEditView;
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

        mProgressDilog.dismiss();
//        mEditCurrentProgressBar.setVisibility(View.INVISIBLE);
    }

    public void setCurrentMonth(String currentMonth) {
        mCurrentMonth = currentMonth;

        if (mEditView == null) {
            return;
        }

        TextView currentMonthTextView = (TextView) mEditView.findViewById(R.id.current_month);
        currentMonthTextView.setText(mCurrentMonth);
    }
}
