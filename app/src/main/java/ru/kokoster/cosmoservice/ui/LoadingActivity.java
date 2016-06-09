package ru.kokoster.cosmoservice.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ru.kokoster.cosmoservice.services.CosmoServiceClient;
import ru.kokoster.cosmoservice.services.ResponseListener;
import ru.kokoster.cosmoservice.services.SessionManager;

/**
 * Created by kokoster on 03.06.16.
 */
public class LoadingActivity extends Activity {
    private static final String TAG = "LoadingActivity";

    private SessionManager mSessionManager;
    private CosmoServiceClient mCosmoServiceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_splash);

        mSessionManager = new SessionManager(getApplicationContext());
        final String currentToken = mSessionManager.getCurrentToken();

        if (currentToken == null) {
            startLoginActivity();
        }

        Log.d(TAG, "Current token = " + currentToken);

        mCosmoServiceClient = new CosmoServiceClient(getCacheDir());
        mCosmoServiceClient.checkToken(currentToken, new ResponseListener() {
            @Override
            public void onSuccess() {
                startMainActivity(currentToken);
            }

            @Override
            public void onError(int errorCode) {
                startLoginActivity();
            }
        });
    }

    private void startLoginActivity() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(loginActivityIntent);

        overridePendingTransition(0,0);
        finish();
    }

    private void startMainActivity(String currentToken) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra("token", currentToken);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mainActivityIntent);

        overridePendingTransition(0,0);
        finish();
    }
}
