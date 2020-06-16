package com.ru.test.issuedriver.taxi.helpers;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.customer.CustomerActivity;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

public class geofireHelper {
    private static final String TAG = "myLogs";
    private static FirebaseDatabase database;
    private static DatabaseReference refGeoFires, refUserActivity;
    private static GeoFire geoFire;
    private static  GeoQuery geoQuery;
    private static Map<String, Marker> stringMarkerMap;

    private static boolean isInit = false;
    private static GoogleMap googleMap;
    public static void init(GoogleMap _googleMap) {
        if(isInit)
            return;

        googleMap = _googleMap;

        database = FirebaseDatabase.getInstance();
        refUserActivity = database.getReference(String.format("d_active"));
        refGeoFires = database.getReference(String.format("d_geo"));
        geoFire = new GeoFire(refGeoFires);
        isInit = true;

//        geofireHelper.setLocation("088abfd7-96fa-4b45-befc-25166867bbcd", 53.597293, 34.336765);
//        geofireHelper.setLocation("7f23cfe1-be09-488c-9d34-fb709e37e150", 53.596889, 34.337670);
//        geofireHelper.setLocation("85763eb6-9ba0-498d-8535-a457f8000940", 53.595609, 34.338947);

//        geofireHelper.setLocation("1676a894-b6ae-4aeb-9bbd-c7e2d7fa6fc9", 53.597293, 34.336765);
//        geofireHelper.setLocation("1676a894-b6ae-4aeb-9bbd-c7e2d7fa6fc9", 53.596889, 34.337670);
//        geofireHelper.setLocation("1676a894-b6ae-4aeb-9bbd-c7e2d7fa6fc9", 53.595609, 34.338947);


        //MyTask mt = new MyTask();
       // mt.execute();

        stringMarkerMap = Collections.synchronizedMap(new HashMap<>());
    }

    private static int index = 0;

    public static void setLocation(String id, double latitude, double longtitude){
        if(!isInit)
            return;

        switch (index){
            case 1:
                latitude = 53.597293;
                longtitude = 34.336765;
                break;
            case 2:
                latitude = 53.596889;
                longtitude = 34.337670;
                break;
            case 3:
                latitude = 53.595609;
                longtitude = 34.338947;
                break;
            case 4:
                latitude = 53.594864;
                longtitude = 34.336404;
                index = -1;
                break;
        }
        index++;

        geoFire.setLocation(id, new GeoLocation(latitude, longtitude), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                if (error != null) {
                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {
                    System.out.println("Location saved on server successfully!");

                    refUserActivity.child(id).setValue(new Date().getTime())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Write was successful!
                            Log.e(TAG, "OK");
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
            }
        });
    }

    public static void getLocationsNew(double latitude, double longtitude, double radius){

        geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longtitude), radius);
        geoQuery.removeAllListeners();

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, longtitude))
                            .flat(true)
                            .title(key)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
                Log.d(TAG, "onDataEntered");
            }

            @Override
            public void onKeyExited(String key) {

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "onDataEntered");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });
    }
    public static void getLocations(double latitude, double longtitude, double radius){
        if(!isInit)
            return;

//        if(geoQuery != null)
//            geoQuery.removeAllListeners();

        geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude, longtitude), radius);
        geoQuery.addGeoQueryEventListener(geoQueryEventListener);

        if(true)
            return;

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(callBacks.callback4geofireItemRecieve != null)
                    callBacks.callback4geofireItemRecieve.callback(key, location);
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
                if(callBacks.callback4geofireItemRecieve != null)
                    callBacks.callback4geofireItemRecieve.callback(key, location);
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {

            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.d(TAG, "onDataEntered");
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataExited");
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.d(TAG, "onDataMoved");
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                Log.d(TAG, "onDataChanged");
            }

            @Override
            public void onGeoQueryReady() {
                Log.d(TAG, "onGeoQueryReady");
                if(callBacks.callback4geofireFinishRecieve != null)
                    callBacks.callback4geofireFinishRecieve.callback();
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(TAG, "onGeoQueryError");
            }

        });
    }

    static class MyTask extends AsyncTask<Void, Void, Void> {


        public MyTask( ) {
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
                    geofireHelper.setLocation("1676a894-b6ae-4aeb-9bbd-c7e2d7fa6fc9", 53.597293, 34.336765);
                    Log.e(TAG, "Counter - " + i);
                    TimeUnit.SECONDS.sleep(5);
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





    private static final GeoQueryEventListener geoQueryEventListener = new GeoQueryEventListener() {

        @Override
        public void onKeyEntered(String key, GeoLocation location) {
            LatLng latLng = new LatLng(location.latitude, location.longitude);

            Marker marker = addMarker(latLng, key);

            //retrieve the user from the database with an async task


            //update number of people connected
//            incTotalUser();
        }


        @Override
        public void onKeyExited(String key) {

            Marker marker = stringMarkerMap.remove(key);
            marker.remove();
            //update number of people connected
//            decTotalUser();
        }


        @Override
        public void onKeyMoved(String key, GeoLocation location) {

            Marker marker = stringMarkerMap.get(key);
            LatLng position = new LatLng(location.latitude, location.longitude);
            updateMarkerPosition(marker, position);
//            drawCenteredCircle(position, key);
        }

        @Override
        public void onGeoQueryReady() {
            Log.d(TAG, "onGeoQueryReady: All initial data has been loaded and events have been fired!");
        }

        @Override
        public void onGeoQueryError(DatabaseError error) {
            Log.w(TAG, "onGeoQueryError: There was an error with this query: ", error.toException());
        }
    };

    private static void updateMarkerPosition(Marker marker, LatLng position) {
        marker.setPosition(position);
        marker.showInfoWindow();
    }
    @NonNull
    private static Marker addMarker(LatLng latLng, String title) {
        //Create maker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);

        Marker marker = googleMap.addMarker(markerOptions);

        // save the user id for future use
        //marker.setTag(userProfile.getId());

        //marker.showInfoWindow();//show the windows


        //drawCenteredCircle(latLng, userProfile.getId());
        //Save the reference in the map
        stringMarkerMap.put(title, marker);
        return marker;
    }
}
