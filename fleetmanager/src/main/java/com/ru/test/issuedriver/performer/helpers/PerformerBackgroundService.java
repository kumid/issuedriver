package com.ru.test.issuedriver.performer.helpers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.data.user_position;
import com.ru.test.issuedriver.helpers.mysettings;

import java.sql.Timestamp;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class PerformerBackgroundService extends Service {

    private static final String TAG = "myLogs";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 5 * 1000;  /* 30 secs */
    private final static long FASTEST_INTERVAL = 2000; /* 20 sec */

    private static int counter = 0;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "performer_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Performer Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Fleet manager")
                    .setContentText("gps tracker")
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                    .build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: called.");
        getLocation();
        return START_NOT_STICKY;
    }

    private static Location lastLocation;

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                            if(lastLocation == null) {
                                Log.d(TAG, "onLocationResult: lastLocation == null");
                                lastLocation = location;
                                saveUserLocation(geoPoint);
                            } else {
                                sendMyBroadcastMessage(location, 2);
                                if (lastLocation.distanceTo(location) > 50) {
                                    saveUserLocation(geoPoint);
                                    lastLocation = location;
                                }
                            }
                            //                            User user = ((UserClient)(getApplicationContext())).getUser();

//                            UserLocation userLocation = new UserLocation(user, geoPoint, null);
//                            saveUserLocation(userLocation);

                        } else
                            Log.d(TAG, "onLocationResult: location == null");
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }

    /// mode = 1 = send position to activity
    /// mode = 1 = send onlineState
    private void sendMyBroadcastMessage(Location location, int mode) {
        Intent intent = new Intent();
        intent.setAction("com.ru.test.issuedriver.performer.ui.order.MY_NOTIFICATION");
        switch (mode){
            case 0:
                intent.putExtra("online", false);
                break;
                case 1:
                    intent.putExtra("online", true);
                break;
            default:
                intent.putExtra("data", location);
                break;
        }


        sendBroadcast(intent);
        Log.d(TAG, "sendMyBroadcastMessage: sended");
    }

    private void saveUserLocation(final GeoPoint userLocation){
//        if(0 != counter%5)
//        {
//            counter++;
//            return;
//        }
//        counter = 1;
        try {

//            mysettings.Init(getApplicationContext());
//            user user = mysettings.GetUser();
//            if(user == null)
//                return;
//
//            user_position pos = new user_position(userLocation.getLatitude(), userLocation.getLongitude(), (new Date()).getTime());
//            // Write a pos to the database
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference myRef = database.getReference();
//            myRef.child("positions").child(user.UUID).setValue(pos)
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            // Write was successful!
//                            sendMyBroadcastMessage(null, 1);
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            // Write failed
//                            Log.e(TAG, "Error");
//                        }
//                    });

            DocumentReference locationRef = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

            locationRef.update("position", userLocation,
                    "last_geo_time", FieldValue.serverTimestamp()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: \ninserted user location into database." +
                                "\n latitude: " + userLocation.getLatitude() +
                                "\n longitude: " + userLocation.getLongitude());
                        sendMyBroadcastMessage(null, 1);
                    }
                }
            });
        }catch (NullPointerException e){
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
            Log.e(TAG, "saveUserLocation: NullPointerException: "  + e.getMessage() );
            //stopSelf();
            sendMyBroadcastMessage(null, 0);
        }
        catch (Exception e){
            Log.e(TAG, "saveUserLocation: User instance is null, stopping location service.");
            sendMyBroadcastMessage(null, 0);
        }
    }
}