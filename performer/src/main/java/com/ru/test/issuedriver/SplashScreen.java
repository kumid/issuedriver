package com.ru.test.issuedriver;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;


import com.google.firebase.messaging.FirebaseMessaging;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.helpers.mysettings;
import com.ru.test.issuedriver.helpers.utils;


public class SplashScreen extends MyActivity {

    SplashScreen instance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        if (utils.hasConnection(this)) {
            Intent intent;
            if(googleAuthManager.isSigned())
                intent = new Intent(instance, PerformerActivity.class);
            else
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




//        sender.sendNotification("toall", "title", "message", new ArrayList<>());



//        new AsyncTask<String, String, String>(){
//            @Override
//            protected String doInBackground(String... params) {
//                String token = "fzOllob8To6r-Rciich8Yv:APA91bFJ_IPVX7oFUYUScTfhrV8alfpHkk7SM1PEmd5hAlvwlN_QoxChhHcNYHxvu811cj19QiEFqdezjfU8n8_Et5REsUYlLl7e_rcyvOHwx7qxSJwbd7elrCQ9L0u9sdU9NMk5o_XF"; //"toall";
//                FcmSender.sendMsg(token, "sendTest");
//                return null;
//            }
//        }.execute();

    }
}
