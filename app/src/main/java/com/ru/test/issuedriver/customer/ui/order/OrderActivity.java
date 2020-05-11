package com.ru.test.issuedriver.customer.ui.order;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.MainActivity;
import com.ru.test.issuedriver.customer.ui.registration.RegistrationViewModel;
import com.ru.test.issuedriver.data.order;

public class OrderActivity extends AppCompatActivity {

    OrderViewModel orderViewModel;
    RegistrationViewModel registrationViewModel;

    TextInputEditText mOrder_name, mOrder_data, mOrder_from, mOrder_to, mOrder_purpose, mOrder_comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mOrder_name = findViewById(R.id.order_name);
        mOrder_data = findViewById(R.id.order_data);
        mOrder_from = findViewById(R.id.order_from);
        mOrder_to = findViewById(R.id.order_to);
        mOrder_purpose = findViewById(R.id.order_purpose);
        mOrder_comment = findViewById(R.id.order_comment);

        orderViewModel =
                ViewModelProviders.of(MainActivity.getInstance()).get(OrderViewModel.class);
        registrationViewModel =
                ViewModelProviders.of(MainActivity.getInstance()).get(RegistrationViewModel.class);

        if(registrationViewModel.currentUser.getValue() != null)
            mOrder_name.setText(registrationViewModel.currentUser.getValue().fio);

        Button mOrder_btn = findViewById(R.id.order_btn);
        mOrder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order curr =new order(
                        mOrder_name.getText().toString(),
                        mOrder_data.getText().toString(),
                        mOrder_from.getText().toString(),
                        mOrder_to.getText().toString(),
                        mOrder_purpose.getText().toString(),
                        mOrder_comment.getText().toString(),
                        registrationViewModel.currentUser.getValue().email,
                        registrationViewModel.currentUser.getValue().email);
                orderViewModel.sendOrder(curr);
            }
        });
    }
}
