package com.ru.test.issuedriver.history;

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


public class HistoryViewModel extends ViewModel {
    FirebaseFirestore db;

    public HistoryViewModel() {
        db = FirebaseFirestore.getInstance();
        listNotifications = new MutableLiveData<>();
    }

    public void initNotificationsHistoryLoad(user _user) {
        observe2notification(_user);
    }

//    public void initNotificationLoad(LifecycleOwner viewLifecycleOwner, MutableLiveData<user> userMutableLiveData) {
//        userMutableLiveData.observe(viewLifecycleOwner, new Observer<user>() {
//            @Override
//            public void onChanged(user user) {
//                observe2notification(user);
//            }
//        });
//    }


    private void observe2notification(user user) {
        String email = user.is_performer? "performer_email" : "customer_email";
        final Query collectionRef = db.collection("orders")
                .whereEqualTo(email, user.email)
                .whereEqualTo("completed", true);; //.document("SF");
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                List<order> questionsList = new ArrayList<>();
                for (DocumentSnapshot snapshot :
                        queryDocumentSnapshots.getDocuments()) {
                    if (snapshot != null && snapshot.exists()) {
                        order curr = snapshot.toObject(order.class);
                        curr.id = snapshot.getId();
                        questionsList.add(curr);
                        Log.d("TAG", "Current data: " + snapshot.getData());
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
                listNotifications.postValue(questionsList);
            }
        });
    }

    private MutableLiveData<List<order>> listNotifications;

    public LiveData<List<order>> getNotifications() {
        return listNotifications;
    }

}