package com.example.kokoster.cosmoservice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText loginEditText;
    private EditText passwordEditText;

    private String username;
    private String password;

    private CosmoServiceClient cosmoServiceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.kokoster.cosmoservice.R.layout.activity_login);

        // TODO: cosmoServiceClient -> onClick ??
        cosmoServiceClient = new CosmoServiceClient(getCacheDir());

        loginEditText = (EditText) findViewById(com.example.kokoster.cosmoservice.R.id.login);
        passwordEditText = (EditText) findViewById(com.example.kokoster.cosmoservice.R.id.password);

        loginEditText.setText("172");
        passwordEditText.setText("tM3kpRUX7G2b");

        username = loginEditText.getText().toString();
        password = passwordEditText.getText().toString();


        loginButton = (Button) findViewById(com.example.kokoster.cosmoservice.R.id.login_button);
        if (loginEditText.getText().equals("") && passwordEditText.getText().equals("")) {
            loginButton.setEnabled(false);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cosmoServiceClient.login(username, password, new ResponseListener() {
                    @Override
                    public void onSuccess() {
                        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainActivityIntent.putExtra("token", cosmoServiceClient.getToken());
                        startActivity(mainActivityIntent);
//                        System.out.println("LoginActivity. Token = " + cosmoServiceClient.getToken());
                    }

                    @Override
                    public void onError(int errorCode) {
                        // TODO: create meassage
                    }
                });
            }
        });

        loginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                username = s.toString();

                if (username.equals("") || password.equals("")) {
                    loginButton.setEnabled(false);
                } else {
                    loginButton.setEnabled(true);
                }
            }
        });

        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();

                if (username.equals("") || password.equals("")) {
                    loginButton.setEnabled(false);
                } else {
                    loginButton.setEnabled(true);

                }
            }
        });
    }
}
