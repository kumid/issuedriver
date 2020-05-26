package com.ru.test.issuedriver.performer.ui.notifications;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.fsm.sender;
import com.ru.test.issuedriver.performer.helpers.firestoreHelper;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class NotificationsViewModel extends ViewModel {
    FirebaseFirestore db;


    public NotificationsViewModel() {
        db = FirebaseFirestore.getInstance();
        listNotifications = new MutableLiveData<>();

    }

    public void initNotificationLoad(LifecycleOwner viewLifecycleOwner, MutableLiveData<user> userMutableLiveData) {
        userMutableLiveData.observe(viewLifecycleOwner, new Observer<user>() {
            @Override
            public void onChanged(user user) {
                observe2notification(user);
            }
        });
//        db.collection("orders")
//                //.whereEqualTo("email", email)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            List<order> questionsList = new ArrayList<>();
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                order curr = document.toObject(order.class);
//                                questionsList.add(curr);
//                                Log.d("TAG", document.getId() + " => " + document.getData());
//                            }
//                            listNotifications.postValue(questionsList);
//                        } else {
//                            Log.w("TAG", "Error getting documents.", task.getException());
//                        }
//                    }
//                });
    }

    private void observe2notification(user user) {
        final Query collectionRef = db.collection("orders")
                .whereEqualTo("performer_email", user.email)
                .whereEqualTo("completed", false); //.document("SF");
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
                        if(!curr.is_notify)
                        {
                            notifycateIt(curr, snapshot);
                        }
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

    private void notifycateIt(order curr, DocumentSnapshot snapshot) {
         snapshot.getReference().update("is_notify", true);
    }

    private MutableLiveData<List<order>> listNotifications;

    public LiveData<List<order>> getNotifications() {
        return listNotifications;
    }

    public void setOrderAccept(final order item) {
        if(item.accept)
            return;

        DocumentReference orderRef = db.collection("orders").document(item.id);
        orderRef.update("accept", true,
                        "accept_timestamp", FieldValue.serverTimestamp())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    firestoreHelper.setUserBusy(item.performer_email, true);
                    sender.send(item.customer_email);
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


}