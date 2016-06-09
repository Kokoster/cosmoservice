package ru.kokoster.cosmoservice.services;

/**
 * Created by kokoster on 13.05.16.
 */
public interface ResponseListener {
    void onSuccess();
    void onError(int errorCode);
}
