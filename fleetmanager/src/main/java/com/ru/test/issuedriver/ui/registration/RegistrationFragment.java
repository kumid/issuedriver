package com.ru.test.issuedriver.ui.registration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.helpers.MyBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

public class RegistrationFragment extends Fragment {

    private RegistrationViewModel registrationViewModel;
    TextInputEditText mFio, mStaff, mEmail, mCorp, mAutomodel, mAutovin, mAutonumber;
    EditText mTel;
    Button mRegistrationButton, mRegistration_btn_logout;
    ImageView mRegistration_online, mRegistration_offline;
    RadioButton mCustomer, mPerformer;
    View mRegistration_performer_groupe, mRegistration_radio_group;
    FirebaseFirestore db;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        registrationViewModel =
                ViewModelProviders.of(getActivity()).get(RegistrationViewModel.class);

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
        mRegistration_btn_logout = root.findViewById(R.id.registration_btn_logout);
        mTel = root.findViewById(R.id.registration_tel);
        mRegistration_online = root.findViewById(R.id.registration_online);
        mRegistration_offline = root.findViewById(R.id.registration_ofline);

        mRegistration_radio_group = root.findViewById(R.id.registration_radio_group);
        mRegistration_performer_groupe = root.findViewById(R.id.registration_performer_groupe);
        mCustomer = root.findViewById(R.id.radio_customer);
        mPerformer = root.findViewById(R.id.radio_performer);
        mCustomer.setEnabled(false);
        mPerformer.setEnabled(false);
        OnlineStateListen();
        db = FirebaseFirestore.getInstance();

        mRegistration_btn_logout.setVisibility(View.VISIBLE);

        mRegistrationButton.setOnClickListener(click);
        mRegistration_btn_logout.setOnClickListener(click);
        init();

        mCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRegistration_performer_groupe.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });

        setSpinerOptins();

        return root;
    }

private void OnlineStateListen() {
        MyBroadcastReceiver.callback4onlineState = new MyBroadcastReceiver.onlineStateChange() {
            @Override
            public void callback(boolean state) {
                Log.d("TAG", "Online " + state);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        mRegistration_online.setVisibility(state ? View.VISIBLE : View.GONE);
                        mRegistration_offline.setVisibility(!state ? View.VISIBLE : View.GONE);
                    }
                });
            }
        };

        Runnable runnable = new Runnable() {
            public void run() {

                while (true) {
                    synchronized (this) {
                        try {
                            wait(10000);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mRegistration_online.setVisibility(View.GONE);
                                    mRegistration_offline.setVisibility(View.VISIBLE);
                                }
                            });

                        } catch (Exception e) {
                        }
                    }
                }
             }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    private void init() {
        mEmail.setText(googleAuthManager.getEmail());
        mRegistrationButton.setText(getResources().getString(R.string.saveprofile));
        getUser();
    }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.registration_btn_logout) {
                googleAuthManager.signOut();
            }
            if(v.getId() == R.id.registration_btn)
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
                        true
                );

        db.collection("users").document(mEmail.getText().toString()).set(current)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        registrationViewModel.currentUser.postValue(current);
                        ((MyActivity)getActivity()).showToast("Данные успешно синхронизированы", Toast.LENGTH_SHORT);
//                        Log.d("TAG", "DocumentSnapshot successfully written!");
                        googleAuthManager.signOut();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                            ((MyActivity)getActivity()).showToast("Ошибка сохранения данных", Toast.LENGTH_SHORT);
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
        mCustomer.setChecked(!curr.is_performer);
        mPerformer.setChecked(curr.is_performer);
        mTel.setText(curr.tel);
        mRegistration_performer_groupe.setVisibility(curr.is_performer?View.VISIBLE:View.GONE);
        registrationViewModel.currentUser.postValue(curr);
    }


    private void setSpinerOptins() {
        final List<String> affineFormats = new ArrayList<>();
        affineFormats.add("+7 ([000]) [000]-[00]-[00]#[000]");

        final MaskedTextChangedListener listener = MaskedTextChangedListener.Companion.installOn(
                mTel,
                "+7 ([000]) [000]-[00]-[00]",
                affineFormats,
                AffinityCalculationStrategy.WHOLE_STRING,
                new MaskedTextChangedListener.ValueListener() {
                    @Override
                    public void onTextChanged(boolean maskFilled, @NonNull final String extractedValue, @NonNull String formattedText) {

                    }
                }
        );

        mTel.setHint(listener.placeholder());
    }
}
