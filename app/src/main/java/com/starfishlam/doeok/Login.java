package com.starfishlam.doeok;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class Login extends AppCompatActivity {

    static final String APP_ID = "79665";
    static final String AUTH_KEY = "WRaWNeYzrBYJLyc";
    static final String AUTH_SECRET = "LUcyO5OecsqHK9-";
    static final String ACCOUNT_KEY = "ouy58v2MnYmQ4avombhh";

    QBUser loginedUser;

    Context mCtx;
    Activity mAct;

    EditText username, password;
    Button register, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeFramework();

        mCtx = this;
        mAct = this;

        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        register = findViewById(R.id.login_register);
        login = findViewById(R.id.login_login);

        TextWatcher checker = new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();

                if (!usernameStr.isEmpty() && !passwordStr.isEmpty()) {
                    login.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        username.addTextChangedListener(checker);
        password.addTextChangedListener(checker);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();

                QBUser qbUser = new QBUser(usernameStr, passwordStr);
                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(), "Login successfully", Toast.LENGTH_LONG).show();

                        loginedUser = qbUser;

                        if (ContextCompat.checkSelfPermission(mCtx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ContextCompat.checkSelfPermission(mCtx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mAct,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    Common.MY_PERMISSION_REQUEST_READ_FINE_LOCATION);
                        } else {
                            Intent main = new Intent(Login.this, MainApp.class);
                            main.putExtra("user", loginedUser);
                            startActivity(main);
                        }
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Common.MY_PERMISSION_REQUEST_READ_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the contacts-related task you need to do.
                    Intent main = new Intent(Login.this, MainApp.class);
                    main.putExtra("user", loginedUser);
                    startActivity(main);
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}
