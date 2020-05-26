package com.ru.test.issuedriver.customer.ui.order;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerActivity;
import com.ru.test.issuedriver.registration.RegistrationViewModel;
import com.ru.test.issuedriver.data.order;

import java.util.Calendar;

public class OrderActivity extends MyActivity implements View.OnClickListener {

    OrderViewModel orderViewModel;
    RegistrationViewModel registrationViewModel;
    ActionBar actionBar;
    TextInputEditText mOrder_name, mOrder_from, mOrder_to, mOrder_purpose, mOrder_comment, mOrder_car, mOrder_carnumber;
    TextView currentDateTime;
    Button mOrder_btn;
    ProgressBar mProgress_circular;
    Calendar dateAndTime=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Новая заявка");
        }
        initExtra();
        initViews();
        initVM();
        initExtra();
        setInitialDateTime();
    }

    String customer_fio, customer_phone, customer_email, performer_fio,  performer_phone, performer_email, performer_car, performer_car_numbr;
    private void initExtra() {
        customer_fio = getIntent().getStringExtra("customer_fio");
        customer_phone = getIntent().getStringExtra("customer_phone");
        customer_email = getIntent().getStringExtra("customer_email");
        performer_fio = getIntent().getStringExtra("performer_fio");
        performer_phone = getIntent().getStringExtra("performer_phone");
        performer_email = getIntent().getStringExtra("performer_email");
        performer_car = getIntent().getStringExtra("performer_car");
        performer_car_numbr = getIntent().getStringExtra("performer_car_number");
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

        mOrder_name.setText(performer_fio);
        mOrder_car.setText(performer_car);
        mOrder_carnumber.setText(performer_car_numbr);

        mOrder_btn.setOnClickListener(this);
        currentDateTime.setOnClickListener(this);
    }

    private void initVM() {
        orderViewModel =
                ViewModelProviders.of(CustomerActivity.getInstance()).get(OrderViewModel.class);
    }

    // отображаем диалоговое окно для выбора даты
    public void setDate(View v) {
        new DatePickerDialog(OrderActivity.this, d,
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }
    // отображаем диалоговое окно для выбора времени
    public void setTime(View v) {
        new TimePickerDialog(OrderActivity.this, t,
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
        if(v.getId() == R.id.order_data){
            setDate(v);
            return;
        }

        order curr = new order(
                currentDateTime.getText().toString(),
                mOrder_from.getText().toString(),
                mOrder_to.getText().toString(),
                mOrder_purpose.getText().toString(),
                mOrder_comment.getText().toString(),
                customer_fio,
                customer_phone,
                customer_email,
                performer_fio,
                performer_phone,
                performer_email,
                mOrder_car.getText().toString(),
                mOrder_carnumber.getText().toString()
                );

        if(curr.from.length() == 0
            || curr.to.length() == 0
            || curr.purpose.length() == 0){
            showToast("Заявка заполнена не полностью", Toast.LENGTH_LONG);
            return;
        }

        mProgress_circular.setVisibility(View.VISIBLE);
        orderViewModel.sendOrder(curr);
        orderViewModel.orderSendCompleteCalback = new OrderViewModel.orderSendComplete() {
            @Override
            public void callback(boolean pass) {
                if(pass){
                     showToast("Заявка успешно зарегистрирована", Toast.LENGTH_LONG);
                     finish();
                     //mProgress_circular.setVisibility(View.GONE);
                }
            }
        };
    }

    /// ActionBar Back button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                actionBar.setDisplayHomeAsUpEnabled(false);
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
