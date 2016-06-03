package com.example.kokoster.cosmoservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by kokoster on 03.06.16.
 */
public class LoadingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_splash);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        final String currentToken = sessionManager.getCurrentToken();

        if (currentToken.equals("")) {
            createLoginActivity();
        }

        System.out.println("In Splash Activity. Current token = " + currentToken);

        CosmoServiceClient cosmoServiceClient = new CosmoServiceClient(getCacheDir());
        cosmoServiceClient.checkToken(currentToken, new ResponseListener() {
            @Override
            public void onSuccess() {
                createMainActivity(currentToken);
            }

            @Override
            public void onError(int errorCode) {
                createLoginActivity();
            }
        });
    }

    private void createLoginActivity() {
        Intent loginActivityIntent = new Intent(this, LoginActivity.class);
        loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(loginActivityIntent);
        overridePendingTransition(0,0);
        finish();
    }

    private void createMainActivity(String currentToken) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra("token", currentToken);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mainActivityIntent);
        overridePendingTransition(0,0);
        finish();
    }
}
