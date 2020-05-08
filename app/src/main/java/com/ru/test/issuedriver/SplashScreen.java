package com.ru.test.issuedriver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    SplashScreen instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

//        if (Owin.hasConnection(this)) {
//            Owin.GetCurrent().GetWaysFromServer();
//
//            Owin.callback4Ways = new ResponseCompleate() {
//                @Override
//                public void callbackCall(final boolean pass) {
//                    if (pass == true) {
//
//                    }
//                    // A class instance
//                    Handler mHandler = new Handler(Looper.getMainLooper());
//                    mHandler.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            Owin.GetCurrent().GetAboutDataFromServer();
//                            Intent intent = new Intent(instance, LoginActivity.class);
//                            if(getIntent().hasExtra("frompush"))
//                                intent.putExtra("frompush", getIntent().getStringExtra("frompush"));
//                            startActivity(intent);
//                            finish();
//
//                        }
//                    });
//                }
//            };
//        }
//        else
//        {
//            setContentView(R.layout.splash_no_internet);
//        }
    }
}
