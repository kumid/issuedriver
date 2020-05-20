package com.ru.test.issuedriver.feedback;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;
import com.ru.test.issuedriver.PerformerActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.helpers.ActionBottonSheetDialog;
import com.ru.test.issuedriver.helpers.fcm.sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class FeedbackActivity extends AppCompatActivity {

    EditText mFeedback_msg, mFeedback_callme_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mFeedback_msg = findViewById(R.id.feedback_msg);
        mFeedback_callme_phone = findViewById(R.id.feedback_callme_phone);
        Button mFeedback_btn = findViewById(R.id.feedback_btn);

        mFeedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedback(mFeedback_msg.getText().toString(), mFeedback_callme_phone.getText().toString());
            }
        });
        //setupSuffixSample();

        String performer_phone = getIntent().getStringExtra("phone");
        mFeedback_callme_phone.setText(performer_phone);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Обратная связь");
        }
    }
    private void sendFeedback(String msg, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("feedbacks").document().set(new feedback(msg, phone))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        PerformerActivity.showToast("Сообщение успешно отправлено", Toast.LENGTH_SHORT);
                        mFeedback_msg.setText("");
                        mFeedback_callme_phone.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        PerformerActivity.showToast("Ошибка передачи сообщения", Toast.LENGTH_SHORT);
                     }
                });
    }

    private void setupSuffixSample() {
        final EditText editText = findViewById(R.id.feedback_callme_phone);
        final List<String> affineFormats = new ArrayList<>();
        affineFormats.add("+7 ([000]) [000]-[00]-[00]#[000]");

        final MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                editText,
                "+7 ([000]) [000]-[00]-[00]",
                affineFormats,
                AffinityCalculationStrategy.WHOLE_STRING,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue, @NonNull String formattedText) {


                    }
                }
        );

        editText.setHint(listener.placeholder());
    }

    /// ActionBar Back button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                 finish();
                break;
        }
        return true;
    }
}
