package ru.kokoster.cosmoservice.services;

import java.util.ArrayList;

import ru.kokoster.cosmoservice.model.MonthData;

/**
 * Created by kokoster on 22.05.16.
 */
public interface MeterDataHistoryResponseListener {
    void onSuccess(CosmoServiceClient.METER_DATAID dataId, ArrayList<MonthData> allMonthsData);
    void onError(CosmoServiceClient.METER_DATAID dataId, int errorCode);
}
