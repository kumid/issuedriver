package com.ru.test.issuedriver;

import android.Manifest;
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

import com.ru.test.issuedriver.helpers.mysettings;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

//android:paddingTop="?attr/actionBarSize"
public class MyActivity extends AppCompatActivity {
    private static MyActivity instance;
    private static final int REQUEST_CODE_PERMISSION_CALL_PHONE = 1199;

    public static MyActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        mysettings.Init(getApplicationContext());

        checkSelfPermissionCall();
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
                Toast toast = new Toast(getInstance());
                View view = LayoutInflater.from(getInstance()).inflate(R.layout.toast_layout, null);
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
