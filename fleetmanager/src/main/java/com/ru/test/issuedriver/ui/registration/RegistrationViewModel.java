package com.ru.test.issuedriver.ui.registration;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.ru.test.issuedriver.data.user;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistrationViewModel extends ViewModel {
    FirebaseFirestore db;
    FirebaseRemoteConfig mFirebaseRemoteConfig;
    public MutableLiveData<user> currentUser;
    public user notLiveUser;

    public RegistrationViewModel() {
        currentUser = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        long cacheExpiration = 3600;
        // cacheExpirationSeconds is set to cacheExpiration here, indicating that any previously
        // fetched and cached config would be considered expired because it would have been fetched
        // more than cacheExpiration seconds ago. Thus the next fetch would go to the server unless
        // throttling is in progress. The default expiration duration is 43200 (12 hours).
        mFirebaseRemoteConfig.fetch(cacheExpiration).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("FETCH", "OK");
                    // Once the config is successfully fetched it must be activated before newly fetched values are returned.
                    mFirebaseRemoteConfig.activateFetched();
                } else {
                    Log.d("FETCH", "False");
                }

            }
        });
    }

    public LiveData<user> getCurrentUser() {
        return currentUser;
    }
}