package com.ru.test.issuedriver;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//android:paddingTop="?attr/actionBarSize"
public class MyActivity extends AppCompatActivity {
    private static MyActivity instance;

    public static MyActivity getInstance() {
        return instance;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
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
