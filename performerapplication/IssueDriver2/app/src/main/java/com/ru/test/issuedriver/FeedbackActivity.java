package com.ru.test.issuedriver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;

import java.util.ArrayList;
import java.util.List;

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
        setupSuffixSample();
    }

    private void sendFeedback(String toString, String toString1) {



//        String to =  FirebaseRemoteConfig.getInstance().getString("feedback_email");
//        if(to.length() == 0)
//            return;
//
//        String subject = "Feedback";
//        String message = String.format("Телефон: %s. Сообщение: %s", mFeedback_msg.getText().toString(),  mFeedback_callme_phone.getText().toString());



//        Intent email = new Intent(Intent.ACTION_SEND);
//        email.putExtra(Intent.EXTRA_EMAIL, new String[]{ to});
//        email.putExtra(Intent.EXTRA_SUBJECT, subject);
//        email.putExtra(Intent.EXTRA_TEXT, message);
//
//        //need this to prompts email client only
//        email.setType("message/rfc822");
//
//        startActivity(Intent.createChooser(email, "Выберите спосоь Email отправки"));
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

}
