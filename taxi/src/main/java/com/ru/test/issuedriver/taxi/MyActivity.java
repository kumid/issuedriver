package com.ru.test.issuedriver.taxi;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.ru.test.issuedriver.taxi.data.user;
import com.ru.test.issuedriver.taxi.helpers.googleAuthManager;
import com.ru.test.issuedriver.taxi.helpers.mysettings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//android:paddingTop="?attr/actionBarSize"
public class MyActivity extends AppCompatActivity {
    private static MyActivity myinstance;
    private static final int REQUEST_CODE_PERMISSION_CALL_PHONE = 1199;

    public static MyActivity getMyInstance() {
        return myinstance;
    }
    public static user CurrentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myinstance = this;
        mysettings.Init(getApplicationContext());

        checkSelfPermissionCall();

        mysettings.Init(getApplicationContext());

//        if(getIntent().hasExtra("object")){
//            String obj = getIntent().getStringExtra("object");
//            Gson gson = new Gson();
//            CurrentUser = gson.fromJson(obj, user.class);
//            if(CurrentUser != null) {
//                mysettings.SetUser(CurrentUser);
////                registrationViewModel.currentUser.setValue(CurrentUser);
//            }
//        } else {
            CurrentUser = mysettings.GetUser();
//            registrationViewModel.getUserFromServer(googleAuthManager.getEmail());
//        }

        if(googleAuthManager.callback4signout == null) {
            googleAuthManager.callback4signout = new googleAuthManager.signoutComplete() {
                @Override
                public void callback() {
                    Intent intent = new Intent(MyActivity.this, LoginActivity.class);
                    startActivity(intent);
                    MyActivity.this.finish();
                }
            };
        }

        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public boolean checkSelfPermissionCall() {

        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_PERMISSION_CALL_PHONE);
            return false;
        }
    }

    private String telNumber;

    public void callPhone(String tel) {
        telNumber = tel;

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNumber));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(intent);
    }

    public static void showToast(final String msg, final int toastLength) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = new Toast(getMyInstance());
                View view = LayoutInflater.from(getMyInstance()).inflate(R.layout.toast_layout, null);
                TextView toastTextView = view.findViewById(R.id.textViewToast);
                toastTextView.setText(msg);
                toast.setView(view);
                toast.setDuration(toastLength);

                //toast.setGravity(Gravity.CENTER, 0, 0);
                toast.setGravity(Gravity.END | Gravity.BOTTOM, 32, 32);
                toast.show();
            }
        });
    }
}
