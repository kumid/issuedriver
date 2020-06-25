package com.ru.test.issuedriver.customer.ui.orders_list;

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
import com.ru.test.issuedriver.helpers.callBacks;
import com.ru.test.issuedriver.helpers.fsm.sender;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.helpers.mysettings;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

public class OrdersListViewModel extends ViewModel {
    FirebaseFirestore db;

    public OrdersListViewModel() {
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

//        final CollectionReference collectionRef = db.collection("orders"); //.document("SF");
//        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//                if (e != null) {
//                    Log.w("TAG", "Listen failed.", e);
//                    return;
//                }
//                List<order> questionsList = new ArrayList<>();
//                for (DocumentSnapshot snapshot :
//                        queryDocumentSnapshots.getDocuments()) {
//                    if (snapshot != null && snapshot.exists()) {
//                        order curr = snapshot.toObject(order.class);
//                        curr.id = snapshot.getId();
//                        questionsList.add(curr);
//                        Log.d("TAG", "Current data: " + snapshot.getData());
//                    } else {
//                        Log.d("TAG", "Current data: null");
//                    }
//                }
//                listNotifications.postValue(questionsList);
//            }
//        });
    }

    private user currentUser;
    public void initNotificationLoad(user _user) {
        currentUser = _user;
        observe2notification(_user);
    }


    private void observe2notification(user user) {
        String email = user.is_performer? "performer_email" : "customer_email";
        final Query collectionRef = db.collection("orders")
                .orderBy("accept_timestamp", Query.Direction.DESCENDING)
                .whereEqualTo(email, user.email)
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

                        if(currentUser.is_performer && !curr.is_notify)
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
//                    sender.send(item.customer_email);
                    sender.send(item, sender.orderStateType.accepted_order);
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

    public void setOrderStartPerforming(final order item) {
        if(item.start_timestamp != null){
            if(callBacks.callback4StartOrderPerforming!=null)
                callBacks.callback4StartOrderPerforming.callback(true);
            return;
        }

        int dist = mysettings.GetDistance();

        DocumentReference orderRef = db.collection("orders").document(item.id);
        orderRef.update("start_timestamp", FieldValue.serverTimestamp(),
                                "start_distance", dist)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(callBacks.callback4StartOrderPerforming!=null)
                            callBacks.callback4StartOrderPerforming.callback(true);
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(callBacks.callback4StartOrderPerforming!=null)
                            callBacks.callback4StartOrderPerforming.callback(false);
                        Log.w("TAG", "Error updating document", e);
                    }
                });

    }

    public void setOrderDelete(order item) {
        DocumentReference orderRef = db.collection("orders").document(item.id);
        orderRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully deleted!");
                        firestoreHelper.setUserBusy(item.performer_email, false);
                        if(callBacks.callback4deleteOrder != null)
                            callBacks.callback4deleteOrder.callback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error delating document", e);
                        if(callBacks.callback4deleteOrder != null)
                            callBacks.callback4deleteOrder.callback(false);
                    }
                });
    }




}