package com.ru.test.issuedriver;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private static final String TAG = "myLogs";
    FirebaseFirestore db;
    private MutableLiveData<user> currentUser;

    public LiveData<user> getCurrentUserLiveData(){
        return currentUser;
    }

    public user CurrentUser;

    public MainViewModel(){}

    public void Init(user current_user) {
        db = FirebaseFirestore.getInstance();
        currentUser = new MutableLiveData<>();
        initUserData(current_user);
    }

    private void initUserData(user current_user) {
        if (current_user == null)
            return;

        db.collection("users").document(current_user.email)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        CurrentUser = documentSnapshot.toObject(user.class);
                        currentUser.postValue(CurrentUser);
                    }
                });
    }

}