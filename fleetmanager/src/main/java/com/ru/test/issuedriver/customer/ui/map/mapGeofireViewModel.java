package com.ru.test.issuedriver.customer.ui.map;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.geofire.geofireCallBacks;
import com.ru.test.issuedriver.geofire.geofireHelper;
import com.ru.test.issuedriver.helpers.callBacks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class mapGeofireViewModel extends ViewModel {

    private static final String TAG = "myLogs";
    FirebaseFirestore db;

    private MutableLiveData<List<user>> userList;
    private List<user> avaibleUserList;

    public boolean isCameraOnPerformer = false;
    public mapGeofireViewModel() {
        db = FirebaseFirestore.getInstance();
        userList = new MutableLiveData<>();
        avaibleUserList = new ArrayList<>();
        //initUsersData();
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

    public List<user> getAvaibleUsers() {
        return avaibleUserList;
    }

    private List<order> orders;
    public void setOrders(List<order> _orders) {
        orders.clear();
        if(_orders == null)
            return;

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
        geofireHelper.getLocations(location.getLatitude(), location.getLongitude(), 10.5);
    }

    // Поиск в списке и добавление элемента если он отсутствует
    public void addAvaibleUser2List(String key) {
        if (avaibleUserList.size() != 0) {

            for (user elem : avaibleUserList) {
                if (elem.UUID.equals(key)) {
                    if(geofireCallBacks.callback4AvaibleUserAdd!=null)
                        geofireCallBacks.callback4AvaibleUserAdd.callback(elem);
                    return;
                }
            }
        }

        db.collection("users")
                .whereEqualTo("UUID", key)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                user curr = document.toObject(user.class);
                                avaibleUserList.add(curr);
                                if(geofireCallBacks.callback4AvaibleUserAdd!=null)
                                    geofireCallBacks.callback4AvaibleUserAdd.callback(curr);
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

    public String removeAvaibleUserFromList(String key) {
        user forRemove = null;
        for (user elem : avaibleUserList) {
            if (elem.UUID.equals(key)) {
                forRemove = elem;
                break;
            }
        }
        if(forRemove == null)
            return null;

        avaibleUserList.remove(forRemove);
        userList.postValue(avaibleUserList);

        return forRemove.email;
    }
}