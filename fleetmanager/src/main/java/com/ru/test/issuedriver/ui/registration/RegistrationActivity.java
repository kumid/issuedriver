package com.ru.test.issuedriver.ui.registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.redmadrobot.inputmask.MaskedTextChangedListener;
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerV2Activity;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.helpers.mysettings;
import com.ru.test.issuedriver.helpers.storage.fbStorageUploads;
import com.ru.test.issuedriver.helpers.storage.photolib;
import com.ru.test.issuedriver.helpers.storage.picturelib;
import com.ru.test.issuedriver.performer.PerformerActivity;
import com.ru.test.issuedriver.helpers.MyBroadcastReceiver;

import java.util.ArrayList;
import java.util.List;

import static com.ru.test.issuedriver.helpers.MyFirebaseMessagingService.token_tbl;

public class RegistrationActivity extends MyActivity implements fbStorageUploads.setPhotoFBpathInterface {

    final int REQUEST_PERMISSION_CODE = 1000;
    private RegistrationViewModel registrationViewModel;
    TextInputEditText mFio, mStaff, mEmail, mCorp, mAutomodel, mAutovin, mAutonumber;
    EditText mTel;
    Button mRegistrationButton, mRegistration_btn_logout;
    ImageView mRegistration_online, mRegistration_offline, mRegistration_photo;
    RadioButton mCustomer, mPerformer;
    View mRegistration_performer_groupe, mRegistration_radio_group;
    FirebaseFirestore db;
    private String fbPhotoPath = "";
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
        mAutomodel = findViewById(R.id.registration_auto_model);
        mAutovin = findViewById(R.id.registration_auto_vin);
        mAutonumber = findViewById(R.id.registration_auto_number);
        mRegistrationButton = findViewById(R.id.registration_btn);
        mRegistration_btn_logout = findViewById(R.id.registration_btn_logout);
        mTel = findViewById(R.id.registration_tel);
        mRegistration_online = findViewById(R.id.registration_online);
        mRegistration_offline = findViewById(R.id.registration_ofline);
        mRegistration_radio_group = findViewById(R.id.registration_radio_group);
        mRegistration_performer_groupe = findViewById(R.id.registration_performer_groupe);
        mCustomer = findViewById(R.id.radio_customer);
        mPerformer = findViewById(R.id.radio_performer);
        mRegistration_photo  = findViewById(R.id.registration_photo);

        setSpinerOptins();
        //mCustomer.setEnabled(false);
        //mPerformer.setEnabled(false);

        db = FirebaseFirestore.getInstance();

        mRegistrationButton.setOnClickListener(click);
        mRegistration_btn_logout.setOnClickListener(click);
        mRegistration_photo.setOnClickListener(click);
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

        requestPermission();

        photolib.init(RegistrationActivity.getMyInstance());
        fbStorageUploads.init(RegistrationActivity.getMyInstance(), null);
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
            OnlineStateListen();
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
                case R.id.registration_photo:
                    //if (checkPermissionFromDevice()) {
                        picturelib.dispatchTakePictureIntent(RegistrationActivity.this);
                        //photolib.getPhotoFromCamera(true);
                    //}
//                    else {
//                        requestPermission();
//                    }
                    break;
            }
        }
    };

    private void requestPermission() {
        int hasWriteExtStorePMS0 = ActivityCompat.checkSelfPermission(RegistrationActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteExtStorePMS0 != PackageManager.PERMISSION_GRANTED) {

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, RegistrationActivity.RC_STORAGE_PERMS);
                }
            };
            Thread thread = new Thread(run);
            thread.start();

            return;
        }
        int hasWriteExtStorePMS = ActivityCompat.checkSelfPermission(RegistrationActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExtStorePMS != PackageManager.PERMISSION_GRANTED) {

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    ActivityCompat.requestPermissions(RegistrationActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, RegistrationActivity.RC_STORAGE_PERMS);                        }
            };
            Thread thread = new Thread(run);
            thread.start();
            return;
        }
    }

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
        if(currentUser != null)
            current.UUID = currentUser.UUID;

        current.photoPath = fbPhotoPath;

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
                                    mAutomodel.setText(currentUser.automodel);
                                    mAutovin.setText(currentUser.autovin);
                                    mAutonumber.setText(currentUser.autonumber);
                                    mCustomer.setChecked(!currentUser.is_performer);
                                    mPerformer.setChecked(currentUser.is_performer);

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


    public static final int RC_STORAGE_PERMS = 101, RC_STORAGE_PERMS_READ = 102;
    private int hasWriteExtStorePMS;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = picturelib.onActivityResult(requestCode, resultCode, data, resultCode == this.RESULT_OK, mRegistration_photo);
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
        fbPhotoPath = path;
    }
}
