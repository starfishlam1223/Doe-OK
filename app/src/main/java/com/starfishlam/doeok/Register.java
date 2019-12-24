package com.starfishlam.doeok;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Register extends AppCompatActivity {

    EditText username, password, fullname, email;
    Button register, cancel;
    File avatarFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        registerSession();

        username = findViewById(R.id.register_username);
        password = findViewById(R.id.register_password);
        fullname = findViewById(R.id.register_full_name);
        email = findViewById(R.id.register_email);
        register = findViewById(R.id.register_register);
        cancel = findViewById(R.id.register_cancel);

        TextWatcher checker = new TextWatcher() {

            public void afterTextChanged(Editable s) {
                String usernameStr = username.getText().toString();
                String passwordStr = password.getText().toString();
                String fullnameStr = fullname.getText().toString();
                String emailStr = email.getText().toString();


                if (!usernameStr.isEmpty() && !passwordStr.isEmpty() && !fullnameStr.isEmpty() && !emailStr.isEmpty()) {
                    register.setEnabled(true);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        };
        username.addTextChangedListener(checker);
        password.addTextChangedListener(checker);
        fullname.addTextChangedListener(checker);
        email.addTextChangedListener(checker);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String usernameStr = username.getText().toString();
                final String passwordStr = password.getText().toString();
                String fullnameStr = fullname.getText().toString();
                String emailStr = email.getText().toString();

                QBUser qbUser = new QBUser(usernameStr, passwordStr);
                qbUser.setFullName(fullnameStr);
                qbUser.setEmail(emailStr);

                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser registeredQbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(), "Register successfully", Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(), "Register failed:" + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR", e.getMessage());
            }
        });
    }
}
