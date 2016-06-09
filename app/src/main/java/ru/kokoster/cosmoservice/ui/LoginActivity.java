package ru.kokoster.cosmoservice.ui;

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

import ru.kokoster.cosmoservice.R;
import ru.kokoster.cosmoservice.services.CosmoServiceClient;
import ru.kokoster.cosmoservice.services.ResponseListener;
import ru.kokoster.cosmoservice.services.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private Button mLoginButton;
    private EditText mLoginEditText;
    private EditText mPasswordEditText;
    private ProgressBar mProgressBar;
    private TextView mErrorTextView;

    private String mUsername;
    private String mPassword;

    private CosmoServiceClient mCosmoServiceClient;
    private SessionManager mSessionManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(ru.kokoster.cosmoservice.R.layout.activity_login);

        // TODO: mCosmoServiceClient -> onClick ??
        mCosmoServiceClient = new CosmoServiceClient(getCacheDir());

        mProgressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        mSessionManager = new SessionManager(LoginActivity.this.getApplicationContext());

        mLoginEditText = (EditText) findViewById(ru.kokoster.cosmoservice.R.id.login);
        mPasswordEditText = (EditText) findViewById(ru.kokoster.cosmoservice.R.id.password);

        mLoginEditText.setText("172");
        mPasswordEditText.setText("tM3kpRUX7G2b");

        mUsername = mLoginEditText.getText().toString();
        mPassword = mPasswordEditText.getText().toString();

        mErrorTextView = (TextView) findViewById(R.id.error_text_view);

        mLoginButton = (Button) findViewById(ru.kokoster.cosmoservice.R.id.login_button);
        updateLoginButton();

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            // TODO: вынести
            mProgressBar.setVisibility(View.VISIBLE);
            mLoginButton.setEnabled(false);
            mLoginEditText.setEnabled(false);
            mPasswordEditText.setEnabled(false);
            mErrorTextView.setVisibility(View.INVISIBLE);

            mCosmoServiceClient.login(mUsername, mPassword, new ResponseListener() {
                @Override
                public void onSuccess() {
                    Log.d(TAG, "Saving token " + mCosmoServiceClient.getToken() + " to preferences");

                    mSessionManager.saveCurrentToken(mCosmoServiceClient.getToken());

                    Log.d(TAG, "Check if token saved. Current saved token: " +
                            (mSessionManager.getCurrentToken() != null ? mSessionManager.getCurrentToken() : ""));

                    startMainActivity(mCosmoServiceClient.getToken());

                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLoginButton.setEnabled(true);
                    mLoginEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);
                }

                @Override
                public void onError(int errorCode) {
                    // TODO: различать виды ошибок (сетевая ошибка, пароль, невалидный токен). Выводить разный текст ошибки
                    mProgressBar.setVisibility(View.INVISIBLE);
                    mLoginButton.setEnabled(true);
                    mLoginEditText.setEnabled(true);
                    mPasswordEditText.setEnabled(true);

                    mErrorTextView.setVisibility(View.VISIBLE);
                }
            });
            }
        });

        mLoginEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mUsername = s.toString();

                updateLoginButton();
            }
        });

        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                mPassword = s.toString();

                updateLoginButton();
            }
        });

        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mLoginButton.performClick();
                return true;
            }

            return false;
            }
        });
    }

    private void updateLoginButton() {
        mLoginButton.setEnabled(!(mUsername.equals("") || mPassword.equals("")));
    }

    private void startMainActivity(String currentToken) {
//        TODO: не передавать токен явно
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra("token", currentToken);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(mainActivityIntent);

        overridePendingTransition(0,0);
        finish();
    }
}
