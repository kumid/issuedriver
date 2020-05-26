package com.ru.test.issuedriver.customer.ui.order;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.customer.CustomerActivity;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.helpers.fsm.sender;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {
    FirebaseFirestore db;

    public MutableLiveData<order> currentOrder;

    public OrderViewModel() {
        currentOrder = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
//        mText.setValue("This is home fragment");
    }

    public LiveData<order> getCurrentOrder() {
        return currentOrder;
    }

    public void getUserFromServer(String email) {
        if (currentOrder.getValue() == null) {
            db.collection("users")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<order> questionsList = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    order curr = document.toObject(order.class);
                                    curr.id = document.getId();
                                    questionsList.add(curr);
                                    currentOrder.postValue(curr);
                                    Log.d("TAG", document.getId() + " => " + document.getData());
                                }
                            } else {
                                Log.w("TAG", "Error getting documents.", task.getException());
                            }
                        }
                    });
        }
    }

    public void sendOrder(order curr){
        db.collection("orders").document().set(curr)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //registrationViewModel.currentUser.postValue(current);
//                        startMainActivity(current);
                        //Log.d("TAG", "DocumentSnapshot successfully written!");
                        sender.send(curr.performer_email);
                        if(orderSendCompleteCalback!=null)
                            orderSendCompleteCalback.callback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CustomerActivity.showToast("Ошибка сохранения данных", Toast.LENGTH_SHORT);
//                        Log.w("TAG", "Error writing document", e);
                        if(orderSendCompleteCalback!=null)
                            orderSendCompleteCalback.callback(false);
                    }
                });
    }

    public static orderSendComplete orderSendCompleteCalback;
    public interface orderSendComplete{
        void callback(boolean pass);
    }
}