package com.example.kokoster.cosmoservice;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kokoster on 03.06.16.
 */
public class SessionManager {
    private final String FILE_NAME = "cosmoservice_preferences";
    private final String TOKEN_KEY = "CURRENT_TOKEN";

    SharedPreferences mSharedPreferences;

    public SessionManager(Context context) {
        mSharedPreferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
    }

    public String getCurrentToken() {
        return mSharedPreferences.getString(TOKEN_KEY, "");
    }

    public void saveCurrentToken(String token) {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(TOKEN_KEY, token);
        editor.commit();
    }

    public void removeCurrentToken() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove(TOKEN_KEY);
        editor.apply();
    }
}
