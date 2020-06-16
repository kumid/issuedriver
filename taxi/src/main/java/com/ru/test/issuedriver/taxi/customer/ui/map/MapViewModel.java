package com.ru.test.issuedriver.taxi.customer.ui.map;

import android.location.Location;
import android.util.Log;

import com.firebase.geofire.GeoLocation;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.taxi.data.order;
import com.ru.test.issuedriver.taxi.data.user;
import com.ru.test.issuedriver.taxi.helpers.callBacks;
import com.ru.test.issuedriver.taxi.helpers.geofireHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import static com.ru.test.issuedriver.taxi.helpers.callBacks.callback4geofireItemRecieve;

public class MapViewModel extends ViewModel {

    private static final String TAG = "myLogs";
    FirebaseFirestore db;
    private MutableLiveData<List<user>> userList;
    public boolean isCameraOnPerformer = false;
    private Map<String, GeoLocation> mapGeohash;
    public MapViewModel() {
        db = FirebaseFirestore.getInstance();
        userList = new MutableLiveData<>();
        initUsersData();
        orders = new ArrayList<>();
    }

    private void initUsersData() {
        db.collection("users")
                .whereEqualTo("is_performer", true) // водители
                //.whereEqualTo("accept", true)       // аккаунт активирован
                .whereLessThanOrEqualTo("state", 1)       // состояние
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e)
            {
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e);
                    return;
                }

                List<user> questionsList = new ArrayList<>();
                for (DocumentSnapshot snapshot :
                        queryDocumentSnapshots.getDocuments()) {
                    if (snapshot != null && snapshot.exists()) {
                        user curr = snapshot.toObject(user.class);
                        //curr.id = snapshot.getId();
                        questionsList.add(curr);
                        Log.d("TAG", "Current data: " + snapshot.getData());
                    } else {
                        Log.d("TAG", "Current data: null");
                    }
                }
                userList.postValue(questionsList);
            }
        });

        mapGeohash = new HashMap<>();

//        callback4geofireItemRecieve = new callBacks.geofireItemRecieveInterface() {
//            @Override
//            public void callback(String key, GeoLocation location) {
//                mapGeohash.put(key, location);
//            }
//        };
//
//        callBacks.callback4geofireFinishRecieve= new callBacks.geofireFinishRecieveInterface() {
//            @Override
//            public void callback() {
//                GeoLocation tmp;
//                List<user> newList = new ArrayList<>();
//                for(user item: userList.getValue()) {
//                    tmp = mapGeohash.get(item.UUID);
//                    if (mapGeohash.containsKey(item.UUID)) {
//                        item.position = new GeoPoint(tmp.latitude, tmp.longitude);
//                        newList.add(item);
//                    }
//                }
//                userList.postValue(newList);
//            }
//        };
    }

    public LiveData<List<user>> getUsers() {
        return userList;
    }

    private List<order> orders;
    public void setOrders(List<order> _orders) {
        orders.clear();
        for (order item: _orders) {
            if(!item.completed && item.accept) {
                if(item.order_active_timestamp != null)
                    item.order_active_time = item.order_active_timestamp.toDate();
                else
                    item.order_active_time  = new Date();
                orders.add(item);
            }
        }
        isCameraOnPerformer = orders.size() > 0;
    }

    public boolean isOrderInActiveState(String email){
        if(!isCameraOnPerformer)
            return false;
        order curr = orders.get(0);

        if(curr.performer_email.equals(email))
            return curr.order_active_time.before(new Date());

        return false;
    }

    // Получить все машины в радиусе
    public void getCarAround(Location location){
        geofireHelper.getLocations(location.getLatitude(), location.getLongitude(), 3000.5);
    }

}