package com.ru.test.issuedriver.helpers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private final String TAG = "myLogs";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "BroadcastReceiver: onReceive");
        Object data = intent.getExtras().get("data");
        if(data != null){
            Location pos = (Location) data;
            if(pos != null
                && callback4gpsposition != null){
                Log.d(TAG, "BroadcastReceiver: callback4gpsposition.callback(pos)");
                callback4gpsposition.callback(pos);
            }
        }

//        Toast.makeText(context, "Broadcast Received with data " + data, Toast.LENGTH_LONG).show();
    }

    public static positionChange callback4gpsposition;
    public interface positionChange {
        void callback(Location position);
    }
}