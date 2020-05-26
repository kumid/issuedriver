package com.ru.test.issuedriver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.performer.ui.registration.RegistrationActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        googleAuthManager.init(this);
        Button mLogin_btn = findViewById(R.id.login_btn);
        mLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleAuthManager.signIn();
            }
        });

        googleAuthManager.callback4Auth = new googleAuthManager.AuthCompleate() {
            @Override
            public void callback(boolean isCompleate) {
                if(isCompleate) {
                    Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuthManager.onActivityResult(requestCode, resultCode, data);
    }
}
