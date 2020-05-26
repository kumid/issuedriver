package com.ru.test.issuedriver.registration;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.SplashScreen;
import com.ru.test.issuedriver.customer.CustomerActivity;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.helpers.mysettings;
import com.ru.test.issuedriver.performer.PerformerActivity;

import java.util.ArrayList;
import java.util.List;


public class RegistrationActivity extends MyActivity {

    TextInputEditText mFio, mStaff, mEmail, mCorp, mAutomodel, mAutovin, mAutonumber, mTel;
    Button mRegistrationButton, mRegistration_btn_logout;
    RadioButton mCustomer, mPerformer;
    View mRegistration_performer_groupe;
    FirebaseFirestore db;

    RegistrationViewModel registrationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        registrationViewModel =
                ViewModelProviders.of(RegistrationActivity.this).get(RegistrationViewModel.class);

        mFio = findViewById(R.id.registration_name);
        mStaff = findViewById(R.id.registration_staff);
        mEmail = findViewById(R.id.registration_email);
        mCorp = findViewById(R.id.registration_corp_email);
        mAutomodel = findViewById(R.id.registration_auto_model);
        mAutovin = findViewById(R.id.registration_auto_vin);
        mAutonumber = findViewById(R.id.registration_auto_number);
        mRegistrationButton = findViewById(R.id.registration_btn);
        mRegistration_btn_logout = findViewById(R.id.registration_btn_logout);
        mTel = findViewById(R.id.registration_tel);
        mRegistration_performer_groupe = findViewById(R.id.registration_performer_groupe);
        mCustomer = findViewById(R.id.radio_customer);
        mPerformer = findViewById(R.id.radio_performer);

        db = FirebaseFirestore.getInstance();

        mRegistrationButton.setOnClickListener(click);
        mRegistration_btn_logout.setVisibility(View.GONE);
        init();
        getUser();

        mCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRegistration_performer_groupe.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });
    }

    private void init() {
        mEmail.setText(googleAuthManager.getEmail());
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addUser();
        }
    };


    private void addUser() {
        final user current =
                new user(mFio.getText().toString(),
                        mStaff.getText().toString(),
                        mEmail.getText().toString(),
                        mCorp.getText().toString(),
                        mAutomodel.getText().toString(),
                        mAutovin.getText().toString(),
                        mAutonumber.getText().toString(),
                        mTel.getText().toString(),
                        mPerformer.isChecked(),
                        currentUser == null? false : currentUser.accept
                );

        db.collection("users").document(mEmail.getText().toString()).set(current)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //registrationViewModel.currentUser.postValue(current);
                        mysettings.SetUser(current);
                        startMainActivity(current);
                        //Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        MyActivity.showToast("Ошибка сохранения данных", Toast.LENGTH_SHORT);
//                        Log.w("TAG", "Error writing document", e);
                    }
                });
    }

    private void startMainActivity(user current) {
        Gson gson = new Gson();
        String obj = gson.toJson(current);
        Intent intent;
        if(current.accept) {
            intent = new Intent(RegistrationActivity.this, current.is_performer ? PerformerActivity.class : CustomerActivity.class);
            //intent.putExtra("object", obj);
            startActivity(intent);
            finish();
        } else {
            showToast("Регистрация прошла успешно. Дождитесь активации аккаунта.", Toast.LENGTH_LONG);
            googleAuthManager.signOut();
        }
    }
    user currentUser;
    private void getUser() {
        if(registrationViewModel.currentUser.getValue() == null) {
            db.collection("users")
                    .whereEqualTo("email", mEmail.getText().toString())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<user> questionsList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    currentUser = document.toObject(user.class);
                                    questionsList.add(currentUser);
                                    mFio.setText(currentUser.fio);
                                    mStaff.setText(currentUser.staff);
                                    mEmail.setText(currentUser.email);
                                    mCorp.setText(currentUser.corp);
                                    mAutomodel.setText(currentUser.automodel);
                                    mAutovin.setText(currentUser.autovin);
                                    mAutonumber.setText(currentUser.autonumber);
                                    mTel.setText(currentUser.tel);
                                    mCustomer.setChecked(!currentUser.is_performer);
                                    mPerformer.setChecked(currentUser.is_performer);
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                    registrationViewModel.currentUser.postValue(currentUser);
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }
}
