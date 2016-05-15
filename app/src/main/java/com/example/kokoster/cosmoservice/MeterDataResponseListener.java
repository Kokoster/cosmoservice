package com.example.kokoster.cosmoservice;

import java.util.ArrayList;

/**
 * Created by kokoster on 15.05.16.
 */
public interface MeterDataResponseListener {
    void onSuccess(CosmoServiceClient.METER_DATAID dataId, ArrayList<MonthData> allMonthsData);
    void onError(CosmoServiceClient.METER_DATAID dataId, int errorCode);
}
