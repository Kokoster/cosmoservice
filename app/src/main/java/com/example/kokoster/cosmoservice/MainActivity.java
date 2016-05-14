package com.example.kokoster.cosmoservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.kokoster.cosmoservice.R.layout.activity_main);

        String token;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
//                System.out.println("extras = null");
                token = null;
            } else {
//                System.out.println("extras here");
                token = extras.getString("token");
            }
        } else {
            token = (String) savedInstanceState.getSerializable("token");
        }

        System.out.println("In MainActivity token = " + token);

        CosmoServiceClient cosmoServiceClient = new CosmoServiceClient(this.getCacheDir(), token);
        cosmoServiceClient.getMeterHistory(CosmoServiceClient.METER_DATA.COLD_WATER);
    }
}
