package com.ru.test.issuedriver.customer.ui;

import android.content.DialogInterface;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerV2Activity;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

public class mapsUtils {
    private static final String TAG = "myLogs";
    private static CustomerV2Activity mapActivity;
    private static GoogleMap mMap;



    public static void Init(CustomerV2Activity _mapActivity, GoogleMap map, boolean _locationPermissionGranted){
        mapActivity = _mapActivity;
        mMap = map;
    }

}
