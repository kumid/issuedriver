package com.ru.test.issuedriver.admin;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;


public class SplashScreen extends AppCompatActivity {

    SplashScreen instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        if (utils.hasConnection(this)) {
            Intent intent;
            if(googleAuthManager.isSigned())
                intent = new Intent(instance, MainActivity.class);
            else
                intent = new Intent(instance, LoginActivity.class);

            startActivity(intent);
            finish();
        }
        else
        {
            setContentView(R.layout.splash_no_internet);
        }




    }
}
