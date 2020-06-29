package com.ru.test.issuedriver.helpers;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;

import org.joda.time.DateTime;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.ru.test.issuedriver.helpers.MyFirebaseMessagingService.token_tbl;

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

    public static void setOrderCancelState(order _order, int state, String reason) {
        DocumentReference userRef = db.collection("orders").document(_order.id);
        userRef.update("state", state, "cancel_reason", reason, "completed", true, "end_timestamp", FieldValue.serverTimestamp())
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

    public static void updateUserInfo(MyActivity activity){
        db.collection("users")
                .whereEqualTo("UUID", mysettings.GetUser().UUID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user curr = document.toObject(user.class);
                                mysettings.SetUser(curr);
                                activity.CurrentUser = curr;
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, "Error getting documents.");
                    }
                });
    }
}
