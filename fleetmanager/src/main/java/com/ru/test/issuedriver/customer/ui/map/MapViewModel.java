package com.ru.test.issuedriver.customer.ui.map;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    FirebaseFirestore db;
    private MutableLiveData<List<user>> userList;

    public MapViewModel() {
        db = FirebaseFirestore.getInstance();
        userList = new MutableLiveData<>();
        initUsersData();
        orders = new ArrayList<>();
    }

    private void initUsersData() {
        final Query collectionRef = db.collection("users")
                .whereEqualTo("is_performer", true) // водители
                .whereEqualTo("accept", true);       // аккаунт активирован
//                .whereEqualTo("is_busy", false);    // если машина занята
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                List<user> questionsList = new ArrayList<>();
                for (DocumentSnapshot snapshot :
                        queryDocumentSnapshots.getDocuments()) {
                    if (snapshot != null && snapshot.exists()) {
                        user curr = snapshot.toObject(user.class);
                        //curr.id = snapshot.getId();
                        questionsList.add(curr);
                        Log.d("TAG", "Current data: " + snapshot.getData());
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
                userList.postValue(questionsList);
            }
        });
    }

    public LiveData<List<user>> getUsers() {
        return userList;
    }

    private List<order> orders;
    public void setOrders(List<order> _orders) {
        orders.clear();
        for (order item: _orders) {
            if(!item.completed && item.accept)
                orders.add(item);
        }
    }

    public boolean isOrderExist4driver(String email){
        for (order item: orders) {
            if(item.performer_email.equals(email))
                return true;
        }
        return false;
    }
}