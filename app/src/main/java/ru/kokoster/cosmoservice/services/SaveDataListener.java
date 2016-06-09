package ru.kokoster.cosmoservice.services;

/**
 * Created by kokoster on 30.05.16.
 */
public interface SaveDataListener {
    void onSuccess();
    void onError(int errorCode);
}
