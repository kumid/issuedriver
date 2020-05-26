package com.ru.test.issuedriver;

import android.content.Intent;
import android.os.Bundle;


import com.ru.test.issuedriver.customer.CustomerActivity;
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

        if (utils.hasConnection(this)) {
            Intent intent;
            user curr = mysettings.GetUser();
            if(googleAuthManager.isSigned()
                && curr != null) {
                intent = new Intent(instance, curr.is_performer ? PerformerActivity.class : CustomerActivity.class);
            } else
                intent = new Intent(instance, LoginActivity.class);

            startActivity(intent);
            finish();
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
