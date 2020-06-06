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
import com.ru.test.issuedriver.customer.CustomerV2Activity;
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

    private order currentOrder;

    public order getCurrentOrder() {
        return currentOrder;
    }
    public String customer_fio, customer_phone, customer_email, performer_fio,  performer_phone, performer_email, performer_car, performer_car_numbr;
    public void setOrder() {
        currentOrder.customer_fio = customer_fio;
        currentOrder.customer_phone = customer_phone;
        currentOrder.customer_email = customer_email;
        currentOrder.performer_fio = performer_fio;
        currentOrder.performer_phone = performer_phone;
        currentOrder.performer_email = performer_email;
        currentOrder.car = performer_car;
        currentOrder.car_number = performer_car_numbr;
    }
    public OrderViewModel() {
        db = FirebaseFirestore.getInstance();
        currentOrder = new order();
    }


    public void sendOrder(){
        db.collection("orders").document().set(currentOrder)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //registrationViewModel.currentUser.postValue(current);
//                        startMainActivity(current);
                        //Log.d("TAG", "DocumentSnapshot successfully written!");
                        sender.send(currentOrder.performer_email);
                        if(orderSendCompleteCalback!=null)
                            orderSendCompleteCalback.callback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CustomerV2Activity.showToast("Ошибка сохранения данных", Toast.LENGTH_SHORT);
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