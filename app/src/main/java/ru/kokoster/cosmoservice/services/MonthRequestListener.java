package ru.kokoster.cosmoservice.services;

/**
 * Created by kokoster on 30.05.16.
 */
public interface MonthRequestListener {
    void onSuccess(String month);
    void onError(int errorCode);
}
