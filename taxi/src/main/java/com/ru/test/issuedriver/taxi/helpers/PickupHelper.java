package com.ru.test.issuedriver.taxi.helpers;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.ru.test.issuedriver.taxi.data.Pickup;
import com.ru.test.issuedriver.taxi.data.user;
import com.ru.test.issuedriver.taxi.helpers.fsm.sender;
import com.ru.test.issuedriver.taxi.rider.Common;
import com.ru.test.issuedriver.taxi.rider.Token;

import java.util.List;


public class PickupHelper {
    private static FirebaseFirestore db;
    private static FirebaseDatabase database;
    private static boolean isInit = false;
    private static DatabaseReference pickups;

    public static void init(){
        if(isInit)
            return;

        db = FirebaseFirestore.getInstance();
        database = FirebaseDatabase.getInstance();
        pickups = database.getReference(Common.pickup_request_tbl);
        isInit = true;
    }

    public static void sendRequest(Pickup currentPikup, List<user> userList){
        if(!isInit)
            return;

        currentPikup.setToken(mysettings.GetFCMToken());
        currentPikup.setUUID(mysettings.GetUUID());
        currentPikup.setEmail(mysettings.GetEmail());

        Gson gson = new Gson();
        String json = gson.toJson(currentPikup);

        for (user item: userList) {
            sender.send(item.fcmToken, "Pickup", json);
        }

//        pickups.child(mysettings.GetUser().UUID).setValue(currentPikup)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        CustomerActivity.showToast("Заказ опубликован", Toast.LENGTH_SHORT);
//                        //sender.send(currentOrder.performer_email);
//                     }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        CustomerActivity.showToast("Ошибка сохранения данных", Toast.LENGTH_SHORT);
////                        Log.w("TAG", "Error writing document", e);
//                     }
//                });
    }

    public static void sendPickupAccept(String token, String driverUUID, String driverEmail, double lat, double lng){
        if(!isInit)
            return;
        Pickup currentPikup = new Pickup();
        currentPikup.setUUID(driverUUID);
        currentPikup.setEmail(driverEmail);
        currentPikup.setLastLocation(new LatLng(lat, lng));
        currentPikup.setToken(new Token(token));
        Gson gson = new Gson();
        String json = gson.toJson(currentPikup);

        sender.send(token, "PickupAccept", json);
    }
}
