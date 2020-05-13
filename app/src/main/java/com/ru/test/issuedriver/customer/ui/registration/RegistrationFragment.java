package com.ru.test.issuedriver.customer.ui.registration;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerActivity;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.googleAuthManager;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class RegistrationFragment extends Fragment {

    private RegistrationViewModel registrationViewModel;
    TextInputEditText mFio, mStaff, mEmail, mCorp, mAutomodel, mAutovin, mAutonumber, mTel;
    Button mRegistrationButton;
    FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registrationViewModel =
                ViewModelProviders.of(CustomerActivity.getInstance()).get(RegistrationViewModel.class);

        View root = inflater.inflate(R.layout.activity_registration, container, false);
        //final TextView textView = root.findViewById(R.id.text_notifications);
//        registrationViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });



        mFio = root.findViewById(R.id.registration_name);
        mStaff = root.findViewById(R.id.registration_staff);
        mEmail = root.findViewById(R.id.registration_email);
        mCorp = root.findViewById(R.id.registration_corp_email);
        mAutomodel = root.findViewById(R.id.registration_auto_model);
        mAutovin = root.findViewById(R.id.registration_auto_vin);
        mAutonumber = root.findViewById(R.id.registration_auto_number);
        mRegistrationButton = root.findViewById(R.id.registration_btn);
        mTel = root.findViewById(R.id.registration_tel);
        db = FirebaseFirestore.getInstance();

        mRegistrationButton.setOnClickListener(click);

        init();
        return root;
    }

    private void init() {
        mEmail.setText(googleAuthManager.getEmail());
        mRegistrationButton.setText("Синхронизировать");
        getUser();
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
                        mTel.getText().toString()
                );

        db.collection("users").document(mEmail.getText().toString()).set(current)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        registrationViewModel.currentUser.postValue(current);
                        CustomerActivity.showToast("Данные успешно синхронизированы", Toast.LENGTH_SHORT);
//                        Log.d("TAG", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CustomerActivity.showToast("Ошибка сохранения данных", Toast.LENGTH_SHORT);
//                        Log.w("TAG", "Error writing document", e);
                    }
                });


    }

    private void updateUser() {
        DocumentReference userRef = db.collection("users").document(mEmail.getText().toString());
        userRef
                .update(mFio.getText().toString(),
                        mStaff.getText().toString(),
                        mEmail.getText().toString(),
                        mCorp.getText().toString(),
                        mAutomodel.getText().toString(),
                        mAutovin.getText().toString(),
                        mAutonumber.getText().toString(),
                        mTel.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

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
                                    user curr = document.toObject(user.class);
                                    questionsList.add(curr);
                                    fillUserDatas(curr);
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
        else {
            fillUserDatas(registrationViewModel.currentUser.getValue());
        }
    }

    private void fillUserDatas(user curr) {
        mFio.setText(curr.fio);
        mStaff.setText(curr.staff);
        mEmail.setText(curr.email);
        mCorp.setText(curr.corp);
        mAutomodel.setText(curr.automodel);
        mAutovin.setText(curr.autovin);
        mAutonumber.setText(curr.autonumber);
        mTel.setText(curr.tel);
        registrationViewModel.currentUser.postValue(curr);
    }
}
