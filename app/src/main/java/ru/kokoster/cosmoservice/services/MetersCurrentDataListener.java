package ru.kokoster.cosmoservice.services;

import java.math.BigDecimal;
import java.util.HashMap;

import ru.kokoster.cosmoservice.services.CosmoServiceClient;

/**
 * Created by kokoster on 24.05.16.
 */
public interface MetersCurrentDataListener {
    void onSuccess(HashMap<CosmoServiceClient.METER_DATAID, BigDecimal> metersCurrentData);
    void onError(int errorCode);
}
