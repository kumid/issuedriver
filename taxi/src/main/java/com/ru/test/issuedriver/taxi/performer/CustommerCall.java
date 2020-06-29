package com.ru.test.issuedriver.taxi.performer;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.customer.ui.map.imHere;
import com.ru.test.issuedriver.taxi.helpers.MyBroadcastReceiver;
import com.ru.test.issuedriver.taxi.helpers.PickupHelper;
import com.ru.test.issuedriver.taxi.helpers.fsm.sender;
import com.ru.test.issuedriver.taxi.helpers.mysettings;
import com.ru.test.issuedriver.taxi.performer.ui.Sender;
import com.ru.test.issuedriver.taxi.rider.Common;
import com.ru.test.issuedriver.taxi.rider.Token;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class CustommerCall extends AppCompatActivity {
    private static final String TAG = "myLogs";
    TextView tvTime, tvAddress, tvDistance;
    Button btnAccept, btnDecline;
    MediaPlayer mediaPlayer;

//    googleAPIInterface mService;
//    IFCMService mFCMService;
    String riderID, riderEmail, token;

    private double lat = -1, lng = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custommer_call);
//        mService=Common.getGoogleAPI();
//        mFCMService=Common.getFCMService();
        tvTime=findViewById(R.id.tvTime);
        tvDistance=findViewById(R.id.tvDistance);
        tvAddress=findViewById(R.id.tvAddress);
        btnDecline=findViewById(R.id.btnDecline);
        btnAccept=findViewById(R.id.btnAccept);

        mediaPlayer=MediaPlayer.create(this, R.raw.ringtone);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        if (getIntent() != null) {
            lat=getIntent().getDoubleExtra("lat", -1.0);
            lng=getIntent().getDoubleExtra("lng", -1.0);
            riderID=getIntent().getStringExtra("rider");
            riderEmail=getIntent().getStringExtra("email");
            token=getIntent().getStringExtra("token");

        } else
            finish();

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(riderID)) cancelRequest(riderID);
            }
        });
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PickupHelper.init();
                PickupHelper.sendPickupAccept(token, mysettings.GetUUID(), mysettings.GetEmail(), lat, lng);

                Intent intent = new Intent(CustommerCall.this, PerformerMapActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("riderID", riderID);
                intent.putExtra("riderEmail", riderEmail);
                intent.putExtra("token", token);
                startActivity(intent);
                finish();
            }
        });

        imHere.init(this);
        imHere.myPositionChanged = new imHere.OnMyPositionChanged() {
            @Override
            public void callBack(Location location) {
                getDirection(location);
            }
        };
    }

    private void cancelRequest(String riderID) {
        Token token=new Token(riderID);

//        Notification notification=new Notification("Cancel", "Driver has cancelled your request");
////        Sender sender=new Sender(token.getToken(), notification);
//        mFCMService.sendMessage(sender).enqueue(new Callback<FCMResponse>() {
//            @Override
//            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
//                if(response.body().success==1){
//                    Message.message(getApplicationContext(), Messages.CANCELLED);
//                    finish();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FCMResponse> call, Throwable t) {
//
//            }
//        });
    }

     private void getDirection(Location location) {
         String rast = "", time = "";
         if (!Common.isDirectionEnabled) {
             float[] result = new float[1];

             Location.distanceBetween(location.getLatitude(), location.getLongitude(), lat, lng, result);
             // (double) result[0];

             double distance = (double) result[0];


             if (distance < 1000) {
                 rast = String.format("%d м", Math.round(distance));
                 Log.d(TAG, rast);
             } else {
                 rast = String.format("%.1f км", distance / 1000f);
                 Log.d(TAG, rast);
             }

             double timeCalc = distance * 60f / 30000f;
             if(timeCalc < 1)
                 timeCalc = 1;

             time = String.format("%.0f мин.", timeCalc);
         }


         tvDistance.setText(rast);
         tvTime.setText(time);
         //tvAddress.setText(requestObject.routes.get(0).legs.get(0).end_address);
         return;
     }


    @Override
    protected void onStop() {
        mediaPlayer.release();
        super.onStop();

    }

    @Override
    protected void onPause() {
        mediaPlayer.release();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mediaPlayer.start();
        super.onResume();

    }

}
