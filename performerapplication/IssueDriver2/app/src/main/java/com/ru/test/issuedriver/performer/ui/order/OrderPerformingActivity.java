package com.ru.test.issuedriver.performer.ui.order;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.helpers.ActionBottonSheetDialog;
import com.ru.test.issuedriver.helpers.MyBroadcastReceiver;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.performer.ui.registration.RegistrationViewModel;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;

public class OrderPerformingActivity extends MyActivity implements View.OnClickListener, ActionBottonSheetDialog.BottomSheetListener {

    private static final String TAG = "myLogs";
    OrderViewModel orderViewModel;
    RegistrationViewModel registrationViewModel;

    TextInputEditText mOrder_name, mOrder_from, mOrder_to, mOrder_purpose, mOrder_comment, mOrder_car, mOrder_carnumber;
    TextView currentDateTime, order_distance, order_log;
    Button mOrder_btn;
    ProgressBar mProgress_circular;
    Calendar dateAndTime=Calendar.getInstance();
    Chronometer mOrder_chronometr;

    boolean isCountDown = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_performing);

        initExtra();
        initViews();
        initVM();
        initExtra();
        setInitialDateTime();

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Выполнение заказа");
        }

        BroadcastReceiverCreate();

        //startTest();
    }

    String customer_fio, customer_phone, customer_email, performer_fio,  performer_phone, performer_email, performer_car, performer_car_numbr, order_from, order_to, purpose, comment, orderId;
    private void initExtra() {
        customer_fio = getIntent().getStringExtra("customer_fio");
        customer_phone = getIntent().getStringExtra("customer_phone");
        customer_email = getIntent().getStringExtra("customer_email");
        performer_fio = getIntent().getStringExtra("performer_fio");
        performer_phone = getIntent().getStringExtra("performer_phone");
        performer_email = getIntent().getStringExtra("performer_email");
        performer_car = getIntent().getStringExtra("performer_car");
        performer_car_numbr = getIntent().getStringExtra("performer_car_number");

        order_from = getIntent().getStringExtra("from");
        order_to = getIntent().getStringExtra("to");
        purpose = getIntent().getStringExtra("purpose");
        comment = getIntent().getStringExtra("comment");
        orderId = getIntent().getStringExtra("order_id");
    }


    private void initViews() {
        mOrder_name = findViewById(R.id.order_name);
        currentDateTime = findViewById(R.id.order_data);
        mOrder_from = findViewById(R.id.order_from);
        mOrder_to = findViewById(R.id.order_to);
        mOrder_purpose = findViewById(R.id.order_purpose);
        mOrder_comment = findViewById(R.id.order_comment);
        mOrder_btn = findViewById(R.id.order_btn);
        mProgress_circular = findViewById(R.id.progress_circular);
        mOrder_car = findViewById(R.id.order_car);
        mOrder_carnumber = findViewById(R.id.order_carnumber);

        mOrder_chronometr = findViewById(R.id.order_chronometr);

        order_distance = findViewById(R.id.order_distance);
        order_log = findViewById(R.id.order_log);

        mOrder_name.setText(performer_fio);
        mOrder_car.setText(performer_car);
        mOrder_carnumber.setText(performer_car_numbr);

        mOrder_from.setText(order_from);
        mOrder_to.setText(order_to);
        mOrder_purpose.setText(purpose);
        mOrder_comment.setText(comment);

        mOrder_btn.setOnClickListener(this);
        //currentDateTime.setOnClickListener(this);

        mOrder_chronometr.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime()
                        - mOrder_chronometr.getBase();

//                if ((elapsedMillis > TIMER_RESTART)
//                        || (isCountDown && elapsedMillis > 0)) {
//                    mOrder_chronometr.stop();
//                    mOrder_chronometr.navigateUp();
//                }
            }
        });
        startTimer();
    }

    private void startTimer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            isCountDown = true;
//            mOrder_chronometr.setCountDown(true);
//            mOrder_chronometr.setBase(SystemClock.elapsedRealtime() + TIMER_RESTART);
        }
        else
            mOrder_chronometr.setBase(SystemClock.elapsedRealtime());
        mOrder_chronometr.start();
    }

    private void initVM() {
        orderViewModel =
                ViewModelProviders.of(OrderPerformingActivity.this).get(OrderViewModel.class);
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(OrderPerformingActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(OrderPerformingActivity.this, t,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }
    // установка начальных даты и времени
    private void setInitialDateTime() {

        currentDateTime.setText(DateUtils.formatDateTime(this,
                dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени
    TimePickerDialog.OnTimeSetListener t=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime();
        }
    };

    // установка обработчика выбора даты
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialDateTime();
            setTime(currentDateTime);
        }
    };

    @Override
    public void onClick(View v) {
//        if(v.getId() == R.id.order_data){
//            setDate(v);
//            return;
//        }
        ActionBottonSheetDialog dialog = new ActionBottonSheetDialog(mOrder_chronometr.getText().toString(), distanse);
        dialog.show(getSupportFragmentManager(), null);
    }


    /// ActionBar Back button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ActionBottonSheetDialog dialog = new ActionBottonSheetDialog(mOrder_chronometr.getText().toString(), distanse);
                dialog.show(getSupportFragmentManager(), null);
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    final String LOG_TAG = "myLogs";

    private Location lastLocation;
    private double distanse = 0;
    /** Called when the activity is first created. */
    public void BroadcastReceiverCreate() {
        MyBroadcastReceiver.callback4gpsposition = new MyBroadcastReceiver.positionChange() {
            @Override
            public void callback(Location position) {
                if (lastLocation == null)
                    lastLocation = position;

                float newDist = lastLocation.distanceTo(position);
                order_log.setText(String.format("%.1f м", newDist));
                Log.d(TAG, String.format("Form update: %.1f м", newDist));

                if (newDist > 100f) {
                    distanse += newDist;
                    lastLocation = position;

                    String rast;
                    if (distanse < 1000) {
                        rast = String.format("%d м", Math.round(distanse));
                        Log.d(TAG, rast);
                    } else {
                        rast = String.format("%.1f км", distanse / 1000f);
                        Log.d(TAG, rast);
                    }
                    order_distance.setText(rast);
                }
            }
        };
    }

    @Override
    public void onButtonClicked(int id, String time, double dist, double fuel) {
        OrderViewModel.orderCompletedCalback = new OrderViewModel.orderCompleted() {
            @Override
            public void callback(boolean pass) {
                if (pass)
                    finish();
                else
                    MyActivity.showToast("Ошибка передачи данных", Toast.LENGTH_SHORT);
            }
        };
        orderViewModel.setOrderComleted(orderId, performer_email, time, dist, fuel);

        Log.d(TAG, "Close order");
    }


    public class LL{
        public String X;
        public String Y;

        public LL(String x, String y) {
            X = x;
            Y = y;
        }
    }
    private ArrayList<LL> poss;


    private void startTest() {
        //        https://megapolis-kulebaki.ru/api/lcab/SetGeoPosition/6/60/16.05.2020/53.597017508000135/34.33555598370731/0.87/bus/
        poss = new ArrayList<>();
        poss.add(new LL("55.407806", "42.545458"));
        poss.add(new LL("55.408357", "42.544692"));
        poss.add(new LL("55.408886", "42.543991"));
        poss.add(new LL("55.409458", "42.543206"));
        poss.add(new LL("55.410356", "42.541892"));
        poss.add(new LL("55.412285", "42.537042"));
        poss.add(new LL("55.413270", "42.534844"));
        poss.add(new LL("55.413814", "42.534445"));
        poss.add(new LL("55.414599", "42.531090"));
        poss.add(new LL("55.414777", "42.529329"));


        Runnable runnable = new Runnable() {
            public void run() {
                // Переносим сюда старый код
                long endTime = System.currentTimeMillis()
                        + 5 * 1000;

                int counter = 0;

                //while (System.currentTimeMillis() < endTime) {
                while (true) {
                    synchronized (this) {
                        try {

                            LL pos = poss.get(counter);

                            Location targetLocation = new Location("");//provider name is unnecessary
                            targetLocation.setLatitude(Double.parseDouble(pos.X));//your coords of course
                            targetLocation.setLongitude(Double.parseDouble(pos.Y));

                            Intent intent = new Intent();
                            intent.setAction("com.ru.test.issuedriver.performer.ui.order.MY_NOTIFICATION");
                            intent.putExtra("data", targetLocation);
                            sendBroadcast(intent);

                            counter++;
                            if(counter == poss.size())
                                counter = 0;
                            wait(5000);
//                            wait(endTime -
//                                    System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }
                //Log.i("Thread", "Сегодня коты перебегали дорогу: " + mCounter++ + " раз");
                // Нельзя!
                // TextView infoTextView =
                //         (TextView) findViewById(R.id.textViewInfo);
                // infoTextView.setText("Сегодня коты перебегали дорогу: " + mCounter++ + " раз");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}