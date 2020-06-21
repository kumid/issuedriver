package com.ru.test.issuedriver.taxi.performer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ru.test.issuedriver.taxi.MainViewModel;
import com.ru.test.issuedriver.taxi.MyActivity;
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.customer.ui.map.MapViewModel;
import com.ru.test.issuedriver.taxi.customer.ui.map.imHere;
import com.ru.test.issuedriver.taxi.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.taxi.data.order;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class PerformerMapActivity extends MyActivity implements GoogleMap.OnMarkerClickListener {

    private static PerformerMapActivity instance;
    public static PerformerMapActivity getInstance() {
        return instance;
    }

    private MapView mMapView;
    ImageView mImgLocationPinUp;

    private MapViewModel mapViewModel;
    private MainViewModel mainViewModel;
    private OrdersListViewModel ordersListViewModel;

    private static final String TAG = "myLogs";
    private double lat = -1, lng = -1;
    private String riderID, token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_performer_map);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                SelectDirectionBottonDialog dialog = new SelectDirectionBottonDialog();
//                dialog.show(getSupportFragmentManager(), null);
//
//            }
//        });

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();
        else
            Log.e("Error", "actionBar == null");

        ImageView mCustomer_hamburger = findViewById(R.id.customer_hamburger);
        mCustomer_hamburger.setVisibility(View.GONE);
        mCustomer_hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initViewModels();

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        mImgLocationPinUp = findViewById(R.id.imgLocationPinUp);

        if (getIntent()!=null){
            lat=getIntent().getDoubleExtra("lat", -1.0);
            lng=getIntent().getDoubleExtra("lng", -1.0);
            riderID=getIntent().getStringExtra("rider");
            token=getIntent().getStringExtra("token");

        } else finish();

        performerMapsUtils.Init(this, mMapView, mapViewModel, mImgLocationPinUp, new LatLng(lat, lng));

        ImageView mMap_plus = findViewById(R.id.map_plus);
        ImageView mMap_minus = findViewById(R.id.map_minus);
        mMap_plus.setOnClickListener(performerMapsUtils.clickZoom);
        mMap_minus.setOnClickListener(performerMapsUtils.clickZoom);

        checkPermission(this);

    }


    RecyclerView rv;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_settings_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    private void initViewModels() {
        mainViewModel = ViewModelProviders.of(PerformerMapActivity.this).get(MainViewModel.class);
        mainViewModel.Init(CurrentUser);

        mapViewModel =
                ViewModelProviders.of(PerformerMapActivity.this).get(MapViewModel.class);

        ordersListViewModel =
                ViewModelProviders.of(PerformerMapActivity.this).get(OrdersListViewModel.class);
        ordersListViewModel.initNotificationLoad(MyActivity.CurrentUser);

        ordersListViewModel.getNotifications().observe(PerformerMapActivity.this, new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                mapViewModel.setOrders(orders);
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
            //return mapsUtils.onMarkerClick(marker, imHere.getMyPlace(), mainViewModel.currentPlace);
    }

    public static final int PERMISSIONS= 123;
    //check location permession for Android 5.0/+
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION) ) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Разрешения");
                    alertBuilder.setMessage("Разрешение необходимо для определения Вашей позиции на карте");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS);

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS);

                }
                return false;
            }
            else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                     imHere.init(PerformerMapActivity.this);
                } else {
                    // permission denied
                }
                return;
        }
    }
}
