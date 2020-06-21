package com.ru.test.issuedriver.taxi;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ru.test.issuedriver.taxi.data.place;
import com.ru.test.issuedriver.taxi.data.user;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {

    private static final String TAG = "myLogs";

    FirebaseFirestore db;
    FirebaseDatabase database;

    private MutableLiveData<user> currentUser;
    public LiveData<user> getCurrentUserLiveData(){
        return currentUser;
    }
    public user CurrentUser;

    // используемое место - адрес пункта назначения
    public place currentPlace;

    public MainViewModel(){}

    public void Init(user current_user) {
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

        currentUser = new MutableLiveData<>();
        places = new MutableLiveData<>();

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
                        database.getReference().child("places").child(current_user.UUID)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        List<place> lst = new ArrayList<>();
                                        for (DataSnapshot item: dataSnapshot.getChildren()) {
                                            lst.add(item.getValue(place.class));
                                        }
                                        places.postValue(lst);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                    }
                });
    }


    private MutableLiveData<List<place>> places;

    public LiveData<List<place>> getPlacesLiveData(){
        return places;
    }


    public void CreateOrder(){

    }
}