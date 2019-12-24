package com.starfishlam.doeok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

    EditText username, password;
    Button register, login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeFramework();

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
                        Intent main = new Intent(Login.this, MainApp.class);
                        main.putExtra("user", qbUser);
                        startActivity(main);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(), APP_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}
