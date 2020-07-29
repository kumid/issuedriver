package com.ru.test.issuedriver.performer.ui.orderPerforming;

import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.ui.order.OrderViewModel;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.helpers.MyBroadcastReceiver;
import com.ru.test.issuedriver.helpers.mysettings;
import com.ru.test.issuedriver.helpers.utils;
import com.ru.test.issuedriver.performer.PerformerActivity;

import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.util.Calendar;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;

import static com.ru.test.issuedriver.customer.ui.order.OrderViewModel.orderCanceledCalback;
import static com.ru.test.issuedriver.helpers.callBacks.callback4goToNavigate;

public class OrderPerformingActivity extends MyActivity implements View.OnClickListener {  //, OrderCloseBottonDialog.CloseBottomSheetListener

    private static final String TAG = "myLogs";
    OrderViewModel orderViewModel;

    TextInputEditText mOrder_name, mOrder_from, mOrder_to, mOrder_purpose, mOrder_comment; //, mOrder_car, mOrder_carnumber;
    TextView currentDateTime, mOrder_distance, order_log, mOrder_distance_bottom, mOrder_fuel_bottom;
    Button mOrder_btn, mOrder_navigation, mBottom_sheet_btn;
    View mOrder_performing_call;
    ProgressBar mProgress_circular;
    Calendar dateAndTime = Calendar.getInstance();
    Chronometer mOrder_chronometr, mOrder_chronometr_bottom;
    private long fuel_consumption = FirebaseRemoteConfig.getInstance().getLong("fuel_consumption");
    boolean isCountDown = false;

    private BottomSheetBehavior sheetBehavior;
    private LinearLayout bottom_sheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_performing);

        initVM();
        initExtra();
        initViews();
        setInitialDateTime();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Выполнение заказа");
        }

        BroadcastReceiverCreate();
    }

    private void initExtra() {
        order extra = getIntent().getParcelableExtra("order");
        orderViewModel.fromPlace = getIntent().getParcelableExtra("from_place");
        orderViewModel.toPlace = getIntent().getParcelableExtra("to_place");

        orderViewModel.setOrder(extra, true);
        orderCanceledCalback = new OrderViewModel.orderCanceled() {
            @Override
            public void callback(String msg) {
                showToast(String.format("Заказ: %s", msg), Toast.LENGTH_SHORT);
                OrderPerformingActivity.this.finish();
            }
        };
    }


    private void initViews() {
        mOrder_name = findViewById(R.id.order_performing_name);
        currentDateTime = findViewById(R.id.order_performing_data);
        mOrder_from = findViewById(R.id.order_performing_from);
        mOrder_to = findViewById(R.id.order_performing_to);
        mOrder_purpose = findViewById(R.id.order_performing_purpose);
        mOrder_comment = findViewById(R.id.order_performing_comment);
        mOrder_btn = findViewById(R.id.order_performing_btn);
        mOrder_navigation = findViewById(R.id.order_performing_navigation);
        mOrder_performing_call  = findViewById(R.id.order_performing_call);
        mProgress_circular = findViewById(R.id.order_performing_progress_circular);
        mOrder_chronometr = findViewById(R.id.order_performing_chronometr);

        mOrder_distance = findViewById(R.id.order_performing_distance);
        order_log = findViewById(R.id.order_performing_log);

        mOrder_chronometr_bottom = findViewById(R.id.order_chronometr_bottom);
        mOrder_distance_bottom = findViewById(R.id.order_distance_bottom);
        mOrder_fuel_bottom = findViewById(R.id.order_fuel_bottom);
        mBottom_sheet_btn = findViewById(R.id.bottom_sheet_btn);

        mOrder_name.setText(orderViewModel.getCurrentOrder().performer_fio);

        mOrder_from.setText(orderViewModel.getCurrentOrder().from);
        mOrder_to.setText(orderViewModel.getCurrentOrder().to);
        mOrder_purpose.setText(orderViewModel.getCurrentOrder().purpose);
        mOrder_comment.setText(orderViewModel.getCurrentOrder().comment);

        mOrder_btn.setOnClickListener(this);
        mBottom_sheet_btn.setOnClickListener(this);
        mOrder_navigation.setOnClickListener(this);
        mOrder_performing_call.setOnClickListener(this);

        if(orderViewModel.getCurrentOrder().start_distance == 0) {
            orderViewModel.getCurrentOrder().start_distance =
                orderViewModel.getCurrentOrder().end_distance = mysettings.GetDistance();
        }
        distanse =  mysettings.GetDistance() - orderViewModel.getCurrentOrder().start_distance;
        mOrder_distance.setText(convertMetr2km(distanse));
        mOrder_distance_bottom.setText(convertMetr2km(distanse));
        mOrder_fuel_bottom.setText(getFuelStr(distanse));

        mOrder_chronometr.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime()
                        - mOrder_chronometr.getBase();

            }
        });
        startTimer();

        bottom_sheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet);
    }

    private void startTimer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (orderViewModel.getCurrentOrder().start_timestamp != null) {
                DateTime start = new DateTime(orderViewModel.getCurrentOrder().start_timestamp.toDate());
                int delta = DateTime.now().getSecondOfDay() - start.getSecondOfDay();
                long t = SystemClock.elapsedRealtime() - (delta * 1000 + 0 * 1000);
                mOrder_chronometr.setBase(t);
                mOrder_chronometr_bottom.setBase(t);
            }
        }
        else {
            mOrder_chronometr.setBase(SystemClock.elapsedRealtime());
            mOrder_chronometr_bottom.setBase(SystemClock.elapsedRealtime());
        }
        mOrder_chronometr.start();
        mOrder_chronometr_bottom.start();
    }

    private void initVM() {
        orderViewModel =
                ViewModelProviders.of(OrderPerformingActivity.this).get(OrderViewModel.class);
    }


    // установка начальных даты и времени
    private void setInitialDateTime() {

        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }


//    OrderCloseBottonDialog dialog = new OrderCloseBottonDialog(); // .getText().toString());

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.order_performing_btn:
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                dialog.showDialog(getSupportFragmentManager(), mOrder_chronometr.getBase(), distanse);
                break;
            case R.id.bottom_sheet_btn:
                onCloseBottomButtonClicked( mOrder_chronometr_bottom.getText().toString(), mOrder_distance_bottom.getText().toString(), mOrder_fuel_bottom.getText().toString());
                break;
            case R.id.order_performing_navigation:
                if(callback4goToNavigate != null)
                    callback4goToNavigate.callback(orderViewModel.getCurrentOrder());
                break;
            case R.id.order_performing_call:
                    PerformerActivity.getInstance().callPhone(orderViewModel.getCurrentOrder().customer_phone);
                break;
        }
    }

    /// ActionBar Back button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
//                OrderCloseBottonDialog dialog = new OrderCloseBottonDialog(mOrder_chronometr.getText().toString(), distanse);
//                dialog.show(getSupportFragmentManager(), null);
                finish();
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    final String LOG_TAG = "myLogs";

    @Override
    protected void onResume() {
        super.onResume();
        distanse =  mysettings.GetDistance() - orderViewModel.getCurrentOrder().start_distance;

    }

    private Location lastLocation;
    private double distanse = 0;
    /** Called when the activity is first created. */
    public void BroadcastReceiverCreate() {
        MyBroadcastReceiver.callback4gpsposition = new MyBroadcastReceiver.positionChange() {
            @Override
            public void callback(Location position) {
                if (lastLocation == null)
                    lastLocation = position;
                orderViewModel.getCurrentOrder().curr_position = position;
                float newDist = lastLocation.distanceTo(position);
                order_log.setText(String.format("%.1f м", newDist));
                Log.d(TAG, String.format("Form update: %.1f м", newDist));

                if (newDist > 50f) {
//                    distanse += newDist;
                    lastLocation = position;

//                    String rast;
//                    rast = convertMetr2km(distanse);
//                    String fuelStr = getFuel(distanse);
//
//                    mOrder_distance.setText(rast);
//                    mOrder_distance_bottom.setText(rast);
//                    mOrder_fuel_bottom.setText(fuelStr);
                }
                distanse =  mysettings.GetDistance() - orderViewModel.getCurrentOrder().start_distance;
                mOrder_distance.setText(convertMetr2km(distanse));
                mOrder_distance_bottom.setText(convertMetr2km(distanse));
                mOrder_fuel_bottom.setText(getFuelStr(distanse));
            }
        };
    }

    private double getFuel(double distanse) {
        return distanse * fuel_consumption / 100f / 1000f;
    }

    private String getFuelStr(double distanse) {
        return  String.format("%s л.", utils.getDoubleString(getFuel(distanse)));
    }

    @NotNull
    private String convertMetr2km(double dist) {
        String rast;
            rast = String.format("%s км", utils.getDoubleString(dist / 1000f));
            Log.d(TAG, rast);
        return rast;
    }

//    @Override
    public void onCloseBottomButtonClicked(String time, String distStr, String fuelStr) {
        OrderViewModel.orderCompletedCalback = new OrderViewModel.orderCompleted() {
            @Override
            public void callback(boolean pass) {
                if (pass)
                    finish();
                else
                    MyActivity.showToast("Ошибка передачи данных", Toast.LENGTH_SHORT);
            }
        };

        orderViewModel.getCurrentOrder().spent_time = time;
        orderViewModel.getCurrentOrder().distance = distanse;
        orderViewModel.getCurrentOrder().distanceDisplay = String.format("%s км.", utils.getDoubleString(distanse / 1000.0));
        orderViewModel.getCurrentOrder().fuel = getFuel(distanse);
        orderViewModel.getCurrentOrder().fuelDisplay = String.format("%s л.", utils.getDoubleString(getFuel(distanse)));
        orderViewModel.setOrderComleted(orderViewModel.getCurrentOrder());
//        orderViewModel.setOrderComleted(orderViewModel.getCurrentOrder().id, orderViewModel.getCurrentOrder().performer_email, time, distanse, getFuel(distanse));

        Log.d(TAG, "Close order");
    }

    @Override
    public void onBackPressed() {
//        OrderCloseBottonDialog dialog = new OrderCloseBottonDialog(mOrder_chronometr.getText().toString(), distanse);
//        dialog.show(getSupportFragmentManager(), null);
        super.onBackPressed();
    }
}