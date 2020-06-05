package com.ru.test.issuedriver.helpers;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.performer.ui.orderPerforming.OrderViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class firestoreHelper {
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void setUserBusy(String performer, boolean isBusy) {
        DocumentReference userRef = db.collection("users").document(performer);
        userRef.update("is_busy", isBusy)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                        if(OrderViewModel.orderCompletedCalback != null)
                            OrderViewModel.orderCompletedCalback.callback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                        if(OrderViewModel.orderCompletedCalback != null)
                            OrderViewModel.orderCompletedCalback.callback(false);
                    }
                });

    }

    public static void setUserState(String performer, int state) {
        DocumentReference userRef = db.collection("users").document(performer);
        userRef.update("state", state)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                        if(callBacks.userStateChangedCalback != null)
                            callBacks.userStateChangedCalback.callback(state);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                        if(callBacks.userStateChangedCalback != null)
                            callBacks.userStateChangedCalback.callback(99);
                    }
                });

    }

    public static void setOrderState(order _order, int state, String reason) {
        DocumentReference userRef = db.collection("orders").document(_order.id);
        userRef.update("state", state, "cancel_reason", reason, "completed", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG", "DocumentSnapshot successfully updated!");

                        setUserBusy(_order.performer_email, false);

                        if(callBacks.orderStateChangedCalback != null)
                            callBacks.orderStateChangedCalback.callback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Error updating document", e);
                        if(callBacks.orderStateChangedCalback != null)
                            callBacks.orderStateChangedCalback.callback(false);
                    }
                });

    }


    public static void setDefaultUserState() {

        final Query collectionRef = db.collection("users");
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                 for (DocumentSnapshot snapshot :
                        queryDocumentSnapshots.getDocuments()) {
                    if (snapshot != null && snapshot.exists()) {
                        if(snapshot.get("state") == null)
                            setUserState(snapshot.get("email").toString(), 0);
                        Log.d("TAG", "Current data: " + snapshot.getData());
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
            }
        });
    }

    public static void setDefaultOrderState() {
        final Query collectionRef = db.collection("orders");
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }
                for (DocumentSnapshot snapshot :
                        queryDocumentSnapshots.getDocuments()) {
                    if (snapshot != null && snapshot.exists()) {
                        if(snapshot.get("state") == null)
                            setUserState(snapshot.get("email").toString(), 0);
                        Log.d("TAG", "Current data: " + snapshot.getData());
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
            }
        });

    }
}
