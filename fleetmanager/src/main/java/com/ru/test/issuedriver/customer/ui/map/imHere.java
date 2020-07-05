package com.ru.test.issuedriver.customer.ui.map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.helpers.mysettings;

import androidx.core.app.ActivityCompat;

public class imHere {
    private static final String TAG = "myLogs";

    private FusedLocationProviderClient mFusedLocationClient;
    private final static long UPDATE_INTERVAL = 20 * 1000;  /* 30 secs */
    private final static long FASTEST_INTERVAL = 10000; /* 20 sec */

    Context ctx;
    static imHere instance;

    public static void init(Context ctx){
        instance = new imHere(ctx);
    }

    private imHere(Context ctx){
        this.ctx = ctx;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(ctx);
        getLocation();
    }

    private static Location lastLocation;
    public static place getMyPlace(){
        if(lastLocation == null)
            return null;
        return new place("мое местоположение", lastLocation.getLatitude(), lastLocation.getLongitude());
    }

    public static double getLat() {
        return  lastLocation.getLatitude();
    }
    public static double getLong() {
        return  lastLocation.getLongitude();
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(ctx,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {

//                            location.setLatitude(53.587740);
//                            location.setLongitude(34.341945);

                            if(lastLocation == null) {
                                Log.d(TAG, "onLocationResult: lastLocation == null");
                                lastLocation = location;
//                                mysettings.SetPosition(location);
                                if(myPositionChanged != null)
                                    myPositionChanged.callBack(location);
                            } else {
                                if (lastLocation.distanceTo(location) > 10) {
                                    if(myPositionChanged != null)
                                        myPositionChanged.callBack(location);
                                    lastLocation = location;
//                                    mysettings.SetPosition(location);
                                }
                            }
                         } else
                            Log.d(TAG, "onLocationResult: location == null");
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }
    public static OnMyPositionChanged myPositionChanged;
    public interface OnMyPositionChanged {
        void callBack(Location location);
    }
}
