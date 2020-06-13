package com.ru.test.issuedriver.taxi.customer.ui.order;

import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ru.test.issuedriver.taxi.customer.CustomerActivity;
import com.ru.test.issuedriver.taxi.data.order;
import com.ru.test.issuedriver.taxi.data.place;
import com.ru.test.issuedriver.taxi.helpers.firestoreHelper;
import com.ru.test.issuedriver.taxi.helpers.fsm.sender;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

public class OrderViewModel extends ViewModel {
    private static final String TAG = "myLogs";
    public place fromPlace, toPlace;

    FirebaseFirestore db;
    FirebaseDatabase database;

    private order currentOrder;

    public order getCurrentOrder() {
        return currentOrder;
    }
    public String customer_uuid, customer_fio, customer_phone, customer_email,
                  performer_uuid, performer_fio,  performer_phone, performer_email, performer_car, performer_car_numbr,
                  order_from, order_to, purpose, comment, orderId;

    public void setOrder() {
        currentOrder.customer_uuid = customer_uuid;
        currentOrder.customer_fio = customer_fio;
        currentOrder.customer_phone = customer_phone;
        currentOrder.customer_email = customer_email;
        currentOrder.performer_uuid = performer_uuid;
        currentOrder.performer_fio = performer_fio;
        currentOrder.performer_phone = performer_phone;
        currentOrder.performer_email = performer_email;
        currentOrder.car = performer_car;
        currentOrder.car_number = performer_car_numbr;

        currentOrder.from = order_from;
        currentOrder.to = order_to;
        currentOrder.purpose = purpose;
        currentOrder.comment = comment;
        currentOrder.id = orderId;

        if(fromPlace != null) {
            currentOrder.from = fromPlace.address;
            currentOrder.from_position = new GeoPoint(fromPlace.latitude, fromPlace.longtitude);
        }

        if(toPlace != null) {
            currentOrder.to = toPlace.address;
            currentOrder.to_position = new GeoPoint(toPlace.latitude, toPlace.longtitude);
        }
    }
    public OrderViewModel() {
        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();

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


    public void setOrderComleted(String orderId, String performer_email, String time, String dist, String fuel) {
        DocumentReference orderRef = db.collection("orders").document(orderId);
        orderRef.update("completed", true,
                "end_timestamp", FieldValue.serverTimestamp(),
                "spent_time", time,
                "distance", dist,
                "fuel", fuel)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firestoreHelper.setUserBusy(performer_email, false);
                        if(orderCompletedCalback != null)
                            orderCompletedCalback.callback(true);
                        addPlace2Collection();
                        Log.d("TAG", "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(orderCompletedCalback != null)
                            orderCompletedCalback.callback(false);
                        Log.w("TAG", "Error updating document", e);
                    }
                });
    }

    private void addPlace2Collection() {
        // Write a pos to the database
        DatabaseReference myRef = database.getReference();

        place newPlace = new place();
        newPlace.address = currentOrder.to;
        if(currentOrder.to_position == null){
            if(currentOrder.curr_position == null)
                return;
            newPlace.latitude = currentOrder.curr_position.getLatitude();
            newPlace.longtitude = currentOrder.curr_position.getLongitude();
        } else {
            newPlace.latitude = currentOrder.to_position.getLatitude();
            newPlace.longtitude = currentOrder.to_position.getLongitude();
        }

        myRef.child("places").child(customer_uuid).child(UUID.randomUUID().toString()).setValue(newPlace)
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
    }

    public static orderCompleted orderCompletedCalback;
    public interface orderCompleted {
        void callback(boolean pass);
    }
}
