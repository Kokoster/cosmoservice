package com.example.kokoster.cosmoservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private EditText loginEditText;
    private EditText passwordEditText;
    private ProgressBar mProgressBar;
    private TextView errorTextView;

    private String username;
    private String password;

    private CosmoServiceClient cosmoServiceClient;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.kokoster.cosmoservice.R.layout.activity_login);

        // TODO: cosmoServiceClient -> onClick ??
        cosmoServiceClient = new CosmoServiceClient(getCacheDir());

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress_bar);

        loginEditText = (EditText) findViewById(com.example.kokoster.cosmoservice.R.id.login);
        passwordEditText = (EditText) findViewById(com.example.kokoster.cosmoservice.R.id.password);

        loginEditText.setText("172");
        passwordEditText.setText("tM3kpRUX7G2b");

        username = loginEditText.getText().toString();
        password = passwordEditText.getText().toString();


        errorTextView = (TextView) findViewById(R.id.error_text_view);

        loginButton = (Button) findViewById(com.example.kokoster.cosmoservice.R.id.login_button);
        if (loginEditText.getText().toString().equals("") && passwordEditText.getText().toString().equals("")) {
            loginButton.setEnabled(false);
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                loginButton.setEnabled(false);
                loginEditText.setEnabled(false);
                passwordEditText.setEnabled(false);
                errorTextView.setVisibility(View.INVISIBLE);

                cosmoServiceClient.login(username, password, new ResponseListener() {
                    @Override
                    public void onSuccess() {
                        Intent mainActivityIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainActivityIntent.putExtra("token", cosmoServiceClient.getToken());
                        startActivity(mainActivityIntent);

                        System.out.println("LoginActivity. Token = " + cosmoServiceClient.getToken());

                        mProgressBar.setVisibility(View.INVISIBLE);
                        loginButton.setEnabled(true);
                        loginEditText.setEnabled(true);
                        passwordEditText.setEnabled(true);
                    }


                    @Override
                    public void onError(int errorCode) {
                        mProgressBar.setVisibility(View.INVISIBLE);
                        loginButton.setEnabled(true);
                        loginEditText.setEnabled(true);
                        passwordEditText.setEnabled(true);

                        errorTextView.setVisibility(View.VISIBLE);
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

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginButton.performClick();
                    return true;
                }

                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
