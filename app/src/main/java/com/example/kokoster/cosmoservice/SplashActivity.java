package com.example.kokoster.cosmoservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by kokoster on 03.06.16.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_splash);

        SessionManager sessionManager = new SessionManager(getApplicationContext());
        final String currentToken = sessionManager.getCurrentToken();

        System.out.println("In Splash Activity. Current token = " + currentToken);

        CosmoServiceClient cosmoServiceClient = new CosmoServiceClient(getCacheDir());
        cosmoServiceClient.checkToken(currentToken, new ResponseListener() {
            @Override
            public void onSuccess() {
                Intent mainActivityIntent = new Intent(SplashActivity.this, MainActivity.class);
                mainActivityIntent.putExtra("token", currentToken);
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainActivityIntent);
            }

            @Override
            public void onError(int errorCode) {
                Intent loginActivityIntent = new Intent(SplashActivity.this, LoginActivity.class);
                loginActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivityIntent);
            }
        });
    }
}
