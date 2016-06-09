package ru.kokoster.cosmoservice.ui;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.HashMap;

import ru.kokoster.cosmoservice.R;
import ru.kokoster.cosmoservice.services.CosmoServiceClient;
import ru.kokoster.cosmoservice.services.MetersCurrentDataListener;

/**
 * Created by kokoster on 29.05.16.
 */
public class EditCurrentDataFragment extends Fragment {
    private static final String COSMO_SERVICE_OBJECT_KEY = "cosmo_service";
    private static final String TAG = "EditCurrentDataFragment";

    private View mEditView;

    private ProgressBar mSaveProgressBar;
    private TextView mErrorText;

    private ProgressDialog mProgressDilog;

    private CosmoServiceClient mCosmoServiceClient = null;
    private String mCurrentMonth = "";

    private EditText mColdWaterEditText;
    private EditText mHotWaterEditText;
    private EditText mDayLightEditText;
    private EditText mNightLightEditText;

    private BigDecimal mColdWaterValue;
    private BigDecimal mHotWaterValue;
    private BigDecimal mDayLightValue;
    private BigDecimal mNightLightValue;

    private Button mSaveButton;
    private TextView mCurrentMonthTextView;

    public static EditCurrentDataFragment newInstance(CosmoServiceClient cosmoServiceClient) {
        EditCurrentDataFragment fragment = new EditCurrentDataFragment();

        Bundle args = new Bundle();
        args.putSerializable(COSMO_SERVICE_OBJECT_KEY, cosmoServiceClient);
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
                updateMetersData(currentMetersData);
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "Can not retrieve current data. Returned with error code " + Integer.toString(errorCode));
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

        mCurrentMonthTextView = (TextView) mEditView.findViewById(R.id.current_month);
        mCurrentMonthTextView.setText(mCurrentMonth);

        mErrorText = (TextView) mEditView.findViewById(R.id.error_text_view);

        updateViews();

        mSaveButton = (Button) mEditView.findViewById(R.id.save_button);

//        setEnabledElements(false);

        mSaveButton.setOnClickListener(new OnClickListener());

        // Inflate the layout for this fragment
        return mEditView;
    }

    public void setCurrentMonth(String currentMonth) {
        mCurrentMonth = currentMonth;

        if (mCurrentMonthTextView != null) {
            mCurrentMonthTextView.setText(mCurrentMonth);
        }
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

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mSaveProgressBar.setVisibility(View.VISIBLE);
            setEnabledElements(false);
            mErrorText.setVisibility(View.INVISIBLE);

            mCosmoServiceClient.saveCurrentMetersData(getEditTextData(), new SaveDataListener());
        }

        private HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> getEditTextData() {
            HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> data = new HashMap<>();

            data.put(CosmoServiceClient.METER_DATAID.COLD_WATER,  getMeterValue(mColdWaterEditText));
            data.put(CosmoServiceClient.METER_DATAID.HOT_WATER,   getMeterValue(mHotWaterEditText));
            data.put(CosmoServiceClient.METER_DATAID.DAY_LIGHT,   getMeterValue(mDayLightEditText));
            data.put(CosmoServiceClient.METER_DATAID.NIGHT_LIGHT, getMeterValue(mNightLightEditText));

            return data;
        }

        private BigDecimal getMeterValue(EditText meterEditText) {
            String meterDataString = meterEditText.getText().toString();

            if (meterDataString.equals("")) {
                return new BigDecimal(0);
            }

            return new BigDecimal(meterDataString);
        }

        private class SaveDataListener implements ru.kokoster.cosmoservice.services.SaveDataListener {
            @Override
            public void onSuccess() {
                refreshCurrentMeterData();
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "saveCurrentMetersData failed with error " + Integer.toString(errorCode));

                mProgressDilog.dismiss();
                setEnabledElements(true);
                mErrorText.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refreshCurrentMeterData() {
        mCosmoServiceClient.retrieveCurrentMetersData(new MetersCurrentDataListener() {
            @Override
            public void onSuccess(HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> currentMetersData) {
                updateMetersData(currentMetersData);

                mSaveProgressBar.setVisibility(View.INVISIBLE);
                setEnabledElements(true);
            }

            @Override
            public void onError(int errorCode) {
                Log.d(TAG, "retrieveCurrentMetersData returned with error code " + Integer.toString(errorCode));

                mSaveProgressBar.setVisibility(View.INVISIBLE);
                setEnabledElements(true);
                mErrorText.setVisibility(View.VISIBLE);
            }
        });
    }
}
