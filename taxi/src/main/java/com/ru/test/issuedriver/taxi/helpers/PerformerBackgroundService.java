package com.ru.test.issuedriver.taxi.helpers;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.ru.test.issuedriver.taxi.data.user;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

public class PerformerBackgroundService extends Service {

    private static final String TAG = "myLogsPBService";

    FirebaseDatabase database;
    MyTask mt;

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
        geofireHelper.init(null);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        database = FirebaseDatabase.getInstance();
        mysettings.Init(getApplicationContext());
            user user = mysettings.GetUser();
            if(user != null) {
                DatabaseReference myRef = database.getReference();
                myRef.child("users").child(user.UUID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String currentDate = dataSnapshot.getValue(String.class);

                        if(callBacks.callback4exitTask!=null)
                            callBacks.callback4exitTask.callback();

                        SimpleDateFormat sfd = new SimpleDateFormat("yyyyMMddHHmm");
                        Date d;
                        try {
                            d = sfd.parse(String.format("%s", currentDate));
                            mt = new MyTask(user.UUID, user.email, d);
                            mt.execute();
                            Log.d(TAG, "OK");

                        } catch (ParseException ex) {
                            Log.d(TAG, "ERROR format");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if(callBacks.callback4exitTask!=null)
                            callBacks.callback4exitTask.callback();
                        Log.d(TAG, "ERROR");
                    }
                });
            }

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

//                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                            if(lastLocation == null) {
//                                Log.d(TAG, "onLocationResult: lastLocation == null");
                                lastLocation = location;
                                saveUserLocation(geoPoint, location, 0);
                            } else {
                                float dist = lastLocation.distanceTo(location);
                                if (dist > 50) {
                                    saveUserLocation(geoPoint, location, dist);
                                    lastLocation = location;
                                } else {
                                    sendMyBroadcastMessage(null, 1);
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
                intent.putExtra("online", true);
                intent.putExtra("data", location);
                break;
        }


        sendBroadcast(intent);
//        Log.d(TAG, "sendMyBroadcastMessage: sended");
    }

    private void saveUserLocation(final GeoPoint userLocation, Location location, float dist){

        dist += mysettings.GetDistance();
        mysettings.SetDistance((int)dist);

        Log.d("Distance", "new distance = " + dist);

//        if(0 != counter%5)
//        {
//            counter++;
//            return;
//        }
//        counter = 1;
        try {
            mysettings.Init(getApplicationContext());
            user user = mysettings.GetUser();
            if(user != null){
                geofireHelper.setLocation(user.UUID, userLocation.getLatitude(), userLocation.getLongitude());
            }

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
                        sendMyBroadcastMessage(location, 2);
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




    class MyTask extends AsyncTask<Void, Void, Void> {

        Date exit;
        boolean isCompleate = false;
        String userUUID, userEmail;
        public MyTask(String uuid, String email, Date dat) {
            exit = dat;
            userUUID = uuid;
            userEmail = email;
            callBacks.callback4exitTask = new callBacks.exitTaskInterface() {
                @Override
                public void callback() {
                    isCompleate = true;
                    Log.e(TAG, "isCompleate = true");
                }
            };
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        int i ;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                TimeUnit.SECONDS.sleep(3);
                i = 0;
                while (true){
                    if(isCompleate) {
                        Log.e(TAG, "isCompleate = true -> break");
                        break;
                    }

                    if(exit.before(new Date())){
                        firestoreHelper.setUserHalfBusy(userUUID, userEmail, false);
                        Log.e(TAG, "setUserRemoveHalfBusy");
                        break;
                    }
                    i++;
                    Log.e(TAG, "Counter - " + i);
                    TimeUnit.SECONDS.sleep(10);
                }

            } catch (InterruptedException e) {
                 e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}