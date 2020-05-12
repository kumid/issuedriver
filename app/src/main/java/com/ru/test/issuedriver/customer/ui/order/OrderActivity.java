package com.ru.test.issuedriver.customer.ui.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
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
import com.ru.test.issuedriver.customer.MainActivity;
import com.ru.test.issuedriver.customer.ui.registration.RegistrationViewModel;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;

import java.util.Calendar;

public class OrderActivity extends MyActivity implements View.OnClickListener {

    OrderViewModel orderViewModel;
    RegistrationViewModel registrationViewModel;

    TextInputEditText mOrder_name, mOrder_from, mOrder_to, mOrder_purpose, mOrder_comment;
    TextView currentDateTime;
    Button mOrder_btn;
    ProgressBar mProgress_circular;
    Calendar dateAndTime=Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initExtra();
        initViews();
        initVM();
        initExtra();
        setInitialDateTime();
    }

    String fio, customer, performer;
    private void initExtra() {
        fio = getIntent().getStringExtra("fio");
        customer = getIntent().getStringExtra("customer");
        performer = getIntent().getStringExtra("performer");
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

        mOrder_name.setText(fio);

        mOrder_btn.setOnClickListener(this);
        currentDateTime.setOnClickListener(this);
    }

    private void initVM() {
        orderViewModel =
                ViewModelProviders.of(MainActivity.getInstance()).get(OrderViewModel.class);
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
                mOrder_name.getText().toString(),
                currentDateTime.getText().toString(),
                mOrder_from.getText().toString(),
                mOrder_to.getText().toString(),
                mOrder_purpose.getText().toString(),
                mOrder_comment.getText().toString(),
                customer,
                performer);

        if(curr.from.length() == 0
            || curr.to.length() == 0
            || curr.performer.length() == 0){
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
}
