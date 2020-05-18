package com.ru.test.issuedriver.helpers;

import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ru.test.issuedriver.performer.ui.order.OrderViewModel;

import androidx.annotation.NonNull;

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
}
