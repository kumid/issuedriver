package com.ru.test.issuedriver;

import android.content.Intent;
import android.os.Bundle;


import com.ru.test.issuedriver.customer.CustomerV2Activity;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.helpers.mysettings;
import com.ru.test.issuedriver.helpers.utils;
import com.ru.test.issuedriver.performer.PerformerActivity;


import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    SplashScreen instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        mysettings.Init(getApplicationContext());

        if(googleAuthManager.callback4signout == null) {
            googleAuthManager.callback4signout = new googleAuthManager.signoutComplete() {
                @Override
                public void callback() {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    intent.putExtra("accept", false);
                    startActivity(intent);
                    SplashScreen.this.finish();
                }
            };
        }

        if (utils.hasConnection(this)) {
            user curr = mysettings.GetUser();
            if(googleAuthManager.isSigned()
                && curr != null) {
                if (curr.accept || true) // true - в боевой версии убрать
                {
                    Intent intent = new Intent(instance, curr.is_performer ? PerformerActivity.class : CustomerV2Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                    googleAuthManager.signOut();
                }
            } else {
                Intent intent = new Intent(instance, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }
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
        else
        {
            setContentView(R.layout.splash_no_internet);
        }

    }
}
