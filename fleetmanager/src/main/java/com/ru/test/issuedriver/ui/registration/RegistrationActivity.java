package com.ru.test.issuedriver.ui.registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import kz.nurzhan.maskededittext.MaskedEditText;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerV2Activity;
import com.ru.test.issuedriver.data.car.car;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.callBacks;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.helpers.mysettings;
import com.ru.test.issuedriver.helpers.storage.fbStorageUploads;
import com.ru.test.issuedriver.helpers.storage.picturelib;
import com.ru.test.issuedriver.performer.PerformerActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends MyActivity implements fbStorageUploads.setPhotoFBpathInterface {


    private RegistrationViewModel registrationViewModel;
    TextInputEditText mFio, mStaff, mEmail, mCorp, mRegistration_auto_marka, mRegistration_auto_model, mRegistration_auto_vin, mRegistration_auto_osago_number,
            mRegistration_auto_osago_date, mRegistration_auto_osago_expire_date, mRegistration_auto_texservice_start_date, mRegistration_auto_texservice_expire_date,
            mRegistration_auto_pts, mRegistration_auto_sts;
    EditText mTel;
    MaskedEditText mRegistration_auto_number;
    Button mRegistrationButton, mRegistration_btn_logout;
    ImageView  mRegistration_photo, mRegistration_auto_search, mRegistration_auto_empty;
    RadioButton mCustomer, mPerformer;
    View mRegistration_performer_groupe, mRegistration_radio_group;

    FirebaseFirestore db;
    private ActionBar actionBar;

    private boolean isFromLogin = false;


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
        mRegistration_auto_marka = findViewById(R.id.registration_auto_marka);
        mRegistration_auto_model = findViewById(R.id.registration_auto_model);
        mRegistration_auto_vin = findViewById(R.id.registration_auto_vin);
        mRegistration_auto_pts = findViewById(R.id.registration_auto_pts);
        mRegistration_auto_sts = findViewById(R.id.registration_auto_sts);
        mRegistration_auto_number = findViewById(R.id.registration_auto_number);
        mRegistrationButton = findViewById(R.id.registration_btn);
        mRegistration_btn_logout = findViewById(R.id.registration_btn_logout);
        mTel = findViewById(R.id.registration_tel);
        mRegistration_radio_group = findViewById(R.id.registration_radio_group);
        mRegistration_performer_groupe = findViewById(R.id.registration_performer_groupe);
        mRegistration_performer_groupe.setVisibility(View.GONE);

        mRegistration_auto_osago_number = findViewById(R.id.registration_auto_osago_number);
        mRegistration_auto_osago_date = findViewById(R.id.registration_auto_osago_start_date);
        mRegistration_auto_osago_expire_date = findViewById(R.id.registration_auto_osago_expire_date);
        mRegistration_auto_texservice_start_date = findViewById(R.id.registration_auto_texservice_start_date);
        mRegistration_auto_texservice_expire_date = findViewById(R.id.registration_auto_texservice_expire_date);

        mRegistration_auto_search  = findViewById(R.id.registration_auto_search);
        mRegistration_auto_empty  = findViewById(R.id.registration_auto_empty);

        mCustomer = findViewById(R.id.radio_customer);
        mPerformer = findViewById(R.id.radio_performer);
        mRegistration_photo  = findViewById(R.id.registration_photo);

        setSpinerOptins();
        //setSpinerOptins2();
        //mCustomer.setEnabled(false);
        //mPerformer.setEnabled(false);

        db = FirebaseFirestore.getInstance();

        mRegistrationButton.setOnClickListener(click);
        mRegistration_btn_logout.setOnClickListener(click);
        mRegistration_auto_search.setOnClickListener(click);
        mRegistration_auto_empty.setOnClickListener(click);
        mRegistration_photo.setOnLongClickListener(longClick);
        init();
        mCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRegistration_performer_groupe.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Личный кабинет");
        }

        picturelib.init(this);

    }


    private void init() {
        String email = googleAuthManager.getEmail();
        mEmail.setText(email);

        if(getIntent().hasExtra("isFromLogin")) {
            isFromLogin = getIntent().getBooleanExtra("isFromLogin", false);
            if (isFromLogin) {
                getUser(email);
                mRegistration_btn_logout.setVisibility(View.GONE);
            }
        } else if(getIntent().hasExtra("user")) {
            String user = getIntent().getStringExtra("user");
            getUser(user);
            mRegistrationButton.setText(getResources().getString(R.string.saveprofile) );
        }
     }

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.registration_btn_logout:
                    googleAuthManager.signOut();
                    break;
                case R.id.registration_btn:
                    addUser();
                    break;
                case R.id.registration_auto_search: 
                    setAuto();
                    break;

                case R.id.registration_auto_empty:
                    setAutoSearchMode();
                    break;
//                case R.id.registration_photo:
                    //if (checkPermissionFromDevice()) {
//                        picturelib.dispatchTakePictureIntent();
                        //photolib.getPhotoFromCamera(true);
                    //}
//                    else {
//                        requestPermission();
//                    }
//                    break;
            }
        }
    };

    private void setAutoSearchMode() {
        mRegistration_auto_number.setEnabled(true);
        setEmptyAutoInfo();
    }

    private void setAuto() {
        String num = mRegistration_auto_number.getText().toString();

        callBacks.callback4autoSearchComplete = new callBacks.autoSearchCompleteInterface() {
            @Override
            public void callback(boolean finded, car obj) {
                if(finded){
                    mRegistration_auto_marka.setText(obj.marka);
                    mRegistration_auto_model.setText(obj.model);
                    mRegistration_auto_vin.setText(obj.vin);
                    mRegistration_auto_pts.setText(obj.pts);
                    mRegistration_auto_sts.setText(obj.sts);
                    mRegistration_auto_osago_number.setText(obj.osago_number);
                    mRegistration_auto_osago_date.setText(obj.osago_start_date.replace('-', '/'));
                    mRegistration_auto_osago_expire_date.setText(obj.osago_expire_date.replace('-', '/'));
                    mRegistration_auto_texservice_start_date.setText(obj.texservice_start_date.replace('-', '/'));
                    mRegistration_auto_texservice_expire_date.setText(obj.texservice_expire_date.replace('-', '/'));
                    mRegistration_auto_search.setVisibility(View.GONE);
                    mRegistration_auto_empty.setVisibility(View.VISIBLE);
                    mRegistration_auto_number.setEnabled(false);
                } else {
                    setEmptyAutoInfo();
                }
            }
        };
        firestoreHelper.getAuto(num);
    }

    private void setEmptyAutoInfo() {
        mRegistration_auto_number.setText("");
        mRegistration_auto_marka.setText("");
        mRegistration_auto_model.setText("");
        mRegistration_auto_vin.setText("");
        mRegistration_auto_pts.setText("");
        mRegistration_auto_sts.setText("");
        mRegistration_auto_osago_number.setText("");
        mRegistration_auto_osago_date.setText("");
        mRegistration_auto_osago_expire_date.setText("");
        mRegistration_auto_texservice_start_date.setText("");
        mRegistration_auto_texservice_expire_date.setText("");
        mRegistration_auto_search.setVisibility(View.VISIBLE);
        mRegistration_auto_empty.setVisibility(View.GONE);
    }

    private View.OnLongClickListener longClick = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            picturelib.dispatchTakePictureIntent();
            return false;
        }
    };

    private void addUser() {
        final user current =
                new user(mFio.getText().toString(),
                        mStaff.getText().toString(),
                        mEmail.getText().toString(),
                        mCorp.getText().toString(),
                        mRegistration_auto_marka.getText().toString(),
                        mRegistration_auto_model.getText().toString(),
                        mRegistration_auto_vin.getText().toString(),
                        mRegistration_auto_pts.getText().toString(),
                        mRegistration_auto_sts.getText().toString(),
                        mRegistration_auto_number.getText().toString(),
                        mTel.getText().toString(),
                        mPerformer.isChecked(),
                        currentUser == null? false : currentUser.accept ,
                        mRegistration_auto_osago_number.getText().toString(),
                        mRegistration_auto_osago_date.getText().toString(),
                        mRegistration_auto_osago_expire_date.getText().toString(),
                        mRegistration_auto_texservice_start_date.getText().toString(),
                        mRegistration_auto_texservice_expire_date.getText().toString()

                );
        if(currentUser != null) {
            current.UUID = currentUser.UUID;
            current.photoPath = currentUser.photoPath;
        }

        current.fcmToken = mysettings.GetFCMToken().getToken();

        firestoreHelper.setUserToken(current.UUID, current.fcmToken, false);


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
        if(current.accept || true) {  // true - в боевой версии убрать
            intent = new Intent(RegistrationActivity.this, current.is_performer ? PerformerActivity.class : CustomerV2Activity.class);
            //intent.putExtra("object", obj);
            startActivity(intent);
            finish();
        } else {
            showToast("Регистрация прошла успешно. Дождитесь активации аккаунта.", Toast.LENGTH_LONG);
            googleAuthManager.signOut();
        }
    }
    user currentUser;
    private void getUser(String email) {
        if(registrationViewModel.currentUser.getValue() == null) {
            db.collection("users")
                    .whereEqualTo("email", email)
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
                                    mRegistration_auto_marka.setText(currentUser.automarka);
                                    mRegistration_auto_model.setText(currentUser.automodel);
                                    mRegistration_auto_vin.setText(currentUser.autovin);
                                    mRegistration_auto_pts.setText(currentUser.autopts);
                                    mRegistration_auto_sts.setText(currentUser.autosts);
                                    mRegistration_auto_number.setText(currentUser.autonumber);
                                    mCustomer.setChecked(!currentUser.is_performer);
                                    mPerformer.setChecked(currentUser.is_performer);

                                    mRegistration_auto_osago_number.setText(currentUser.osago_number);
                                    mRegistration_auto_osago_date.setText(currentUser.osago_start_date);
                                    mRegistration_auto_osago_expire_date.setText(currentUser.osago_expire_date);
                                    mRegistration_auto_texservice_start_date.setText(currentUser.texservice_start_date);
                                    mRegistration_auto_texservice_expire_date.setText(currentUser.texservice_expire_date);

                                    mCustomer.setEnabled(false);
                                    mPerformer.setEnabled(false);
                                    mRegistration_auto_number.setEnabled(false);
                                    mRegistration_auto_search.setVisibility(View.GONE);
                                    mRegistration_auto_empty.setVisibility(View.VISIBLE);

                                    if(currentUser.photoPath != null && currentUser.photoPath.length() > 0) {
//                                        mRegistration_photo.setImageURI(Uri.parse(currentUser.photoPath));
                                        Picasso.get().load(currentUser.photoPath)
                                                .placeholder(R.drawable.avatar)
                                                .error(R.drawable.avatar)
                                                .into(mRegistration_photo);
                                    }
                                    try {
                                        mTel.setText(currentUser.tel);
                                    } catch (Exception ex){
                                        mTel.setText("");
                                    }

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



    /// ActionBar Back button clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                //actionBar.setDisplayHomeAsUpEnabled(false);
                //Toast.makeText(getApplicationContext(),"Back button clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }


    private int hasWriteExtStorePMS;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = picturelib.onActivityResult(requestCode, resultCode, data, resultCode == this.RESULT_OK, mRegistration_photo);
//        permissionsHelper.onActivityResult(requestCode, resultCode, data);
//        picturelib.setPic(mRegistration_photo);
//        Uri uri = photolib.onActivityResult(requestCode, resultCode, data);
        Log.d("TAG", "onActivityResult");
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


    @Override
    public void setPath(String path) {

        currentUser.photoPath = path;
        if(currentUser.photoPath != null && currentUser.photoPath.length() > 0) {
//                                        mRegistration_photo.setImageURI(Uri.parse(currentUser.photoPath));
            Picasso.get().load(currentUser.photoPath)
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(mRegistration_photo);
        }
    }
}
