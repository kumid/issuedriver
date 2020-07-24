package com.ru.test.issuedriver.performer.ui.fix;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.performer.PerformerActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import static com.ru.test.issuedriver.MyActivity.CurrentUser;

public class FixActivity extends AppCompatActivity {

    EditText mFix_msg, mFix_callme_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);

        mFix_msg = findViewById(R.id.fix_msg);
        mFix_callme_phone = findViewById(R.id.fix_callme_phone);
        Button mFix_btn = findViewById(R.id.fix_btn);

        mFix_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(mFix_msg.getText().toString(), mFix_callme_phone.getText().toString());
            }
        });
        setupSuffixSample();

        String performer_phone = getIntent().getStringExtra("phone");

        try {
            mFix_callme_phone.setText(performer_phone);
        } catch (Exception ex){
            mFix_callme_phone.setText("");
        }


        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Заявка на ремонт");
        }
    }
    private void sendMessage(String msg, String phone) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("fixs").document().set(new fix(CurrentUser.fio, CurrentUser.email, CurrentUser.photoPath, msg, phone))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        PerformerActivity.showToast("Сообщение успешно отправлено", Toast.LENGTH_SHORT);
                        FixActivity.this.finish();
                        mFix_msg.setText("");
                        mFix_callme_phone.setText("");
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
        final EditText editText = findViewById(R.id.fix_callme_phone);
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
