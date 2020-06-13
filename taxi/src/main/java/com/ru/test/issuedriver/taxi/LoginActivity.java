package com.ru.test.issuedriver.taxi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ru.test.issuedriver.taxi.helpers.googleAuthManager;
import com.ru.test.issuedriver.taxi.ui.registration.RegistrationActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        googleAuthManager.init(this);
        View mLogin_btn = findViewById(R.id.login_btn);
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
                    intent.putExtra("isFromLogin", true);
                    startActivity(intent);
                    finish();
                } else {

                }
            }
        };

        if(getIntent().hasExtra("accept")){
            if(getIntent().getBooleanExtra("accept", false))
                Toast.makeText(LoginActivity.this, "Ваш аккаунт не активирован. Свяжитесь с администрацией компании.", Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleAuthManager.onActivityResult(requestCode, resultCode, data);
    }
}
