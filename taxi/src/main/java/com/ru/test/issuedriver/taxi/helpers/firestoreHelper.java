package com.ru.test.issuedriver.taxi.helpers;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.taxi.data.Pickup;
import com.ru.test.issuedriver.taxi.data.order;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.ru.test.issuedriver.taxi.rider.Common.token_tbl;

public class firestoreHelper {
    private static final String TAG = "myLogs";
    private static FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

   //освободить машину при отмене ордера

    public static void setUserBusy(String performer, boolean isBusy) {
        setUserState(performer, isBusy ? 1 : 0);
    }

    public static void setUserHalfBusy(String performer_uuid, String performer_email, boolean isBusy) {

        if(isBusy){
            setUserState(performer_email, 1);

            DateTime jtime = new DateTime();
            jtime = jtime.plusMinutes(30);

            database.getReference().child("users").child(performer_uuid).setValue(jtime.toString("yyyyMMddHHmm"))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            Log.e(TAG, "Place save Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Log.e(TAG, "Error");
                        }
                    });

        } else {
            setUserState(performer_email, 0);
            setUserRemoveHalfBusy(performer_uuid);
        }
    }
    public static void setUserRemoveHalfBusy(String performerUUID) {
        database.getReference().child("users").child(performerUUID).removeValue();
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

//                        setUserBusy(_order.performer_email, false);

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

    public static void creteOrder(Pickup pickup) {
        order currentOrder = new order();
        currentOrder.accept = true;
        currentOrder.from_position = new GeoPoint(pickup.getLastLocation().latitude, pickup.getLastLocation().longitude);
        currentOrder.customer_uuid = mysettings.GetUUID();
        currentOrder.customer_email = mysettings.GetEmail();
        currentOrder.performer_uuid = pickup.getUUID();
        currentOrder.performer_email = pickup.getEmail();

        db.collection("orders").document().set(currentOrder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.w("TAG", "OK");
//                         if(orderSendCompleteCalback!=null)
//                            orderSendCompleteCalback.callback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                         Log.w("TAG", "Error writing document", e);
//                        if(orderSendCompleteCalback!=null)
//                            orderSendCompleteCalback.callback(false);
                    }
                });
    }


    public static void setUserToken(String uuid, String token, boolean isUserCollectionSaved) {
        FirebaseDatabase dbToken=FirebaseDatabase.getInstance();
        DatabaseReference tokens = dbToken.getReference(token_tbl);
        tokens.child(uuid).setValue(token);

        if(!isUserCollectionSaved)
            return;

        DocumentReference userRef = db.collection("users").document(uuid);
        userRef.update("fcmToken", token)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
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
