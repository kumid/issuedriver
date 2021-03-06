package com.ru.test.issuedriver.taxi.customer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.ru.test.issuedriver.taxi.MainViewModel;
import com.ru.test.issuedriver.taxi.MyActivity;
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.bottom_dialogs.SelectDirectionBottonDialog;
import com.ru.test.issuedriver.taxi.customer.ui.map.MapViewModel;
import com.ru.test.issuedriver.taxi.customer.ui.map.imHere;
import com.ru.test.issuedriver.taxi.customer.ui.map.placesAdapter;
import com.ru.test.issuedriver.taxi.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.taxi.data.Pickup;
import com.ru.test.issuedriver.taxi.data.order;
import com.ru.test.issuedriver.taxi.data.place;
import com.ru.test.issuedriver.taxi.helpers.googleAuthManager;
import com.ru.test.issuedriver.taxi.helpers.PickupHelper;
import com.ru.test.issuedriver.taxi.helpers.mysettings;
import com.ru.test.issuedriver.taxi.ui.history.HistoryActivity;
import com.ru.test.issuedriver.taxi.ui.orders.OrdersListActivity;
import com.ru.test.issuedriver.taxi.ui.registration.RegistrationActivity;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerActivity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener,
                                                             SelectDirectionBottonDialog.BottomSheetListener {

    private static CustomerActivity instance;
    public static CustomerActivity getInstance() {
        return instance;
    }

    private MapView mMapView;
    ImageView mImgLocationPinUp;

    private MapViewModel mapViewModel;
    private MainViewModel mainViewModel;
    private OrdersListViewModel ordersListViewModel;

    private static final String TAG = "myLogs";
    private AppBarConfiguration mAppBarConfiguration;

    private static final int AUTOCOMPLETE_REQUEST_CODE = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_customer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SelectDirectionBottonDialog dialog = new SelectDirectionBottonDialog();
                dialog.show(getSupportFragmentManager(), null);

            }
        });

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.hide();
        else
            Log.e("Error", "actionBar == null");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_orders, R.id.nav_history, R.id.nav_cabinet)
                .setDrawerLayout(drawer)
                .build();
        ImageView mCustomer_hamburger = findViewById(R.id.customer_hamburger);
        mCustomer_hamburger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        initViewModels();
        PickupHelper.init();

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        mImgLocationPinUp = findViewById(R.id.imgLocationPinUp);
        customerMapsUtils.Init(this, mMapView, mapViewModel, mImgLocationPinUp);

        ImageView mMap_plus = findViewById(R.id.map_plus);
        ImageView mMap_minus = findViewById(R.id.map_minus);
        mMap_plus.setOnClickListener(customerMapsUtils.clickZoom);
        mMap_minus.setOnClickListener(customerMapsUtils.clickZoom);


            final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment)getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Log.e(TAG, "onPlaceSelected");
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.e(TAG, "onError");
            }
        });
        checkPermission(this);

        rv = findViewById(R.id.places_rv);
        mainViewModel.getPlacesLiveData().observe(this, new Observer<List<place>>() {
            @Override
            public void onChanged(List<place> places) {
                rv.setAdapter(new placesAdapter(mainViewModel, places));
            }
        });

    }


    RecyclerView rv;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_settings_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.nav_orders:
                intent = new Intent(CustomerActivity.this, OrdersListActivity.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Вы выбрали home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_history:
                intent = new Intent(CustomerActivity.this, HistoryActivity.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Вы выбрали galery", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_cabinet:
                intent = new Intent(CustomerActivity.this, RegistrationActivity.class);
                intent.putExtra("user", CurrentUser.email);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Вы выбрали slide show", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_exit:
                googleAuthManager.signOut();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void initViewModels() {
        mainViewModel = ViewModelProviders.of(CustomerActivity.this).get(MainViewModel.class);
        mainViewModel.Init(CurrentUser);

        mapViewModel =
                ViewModelProviders.of(CustomerActivity.this).get(MapViewModel.class);

        ordersListViewModel =
                ViewModelProviders.of(CustomerActivity.this).get(OrdersListViewModel.class);
        ordersListViewModel.initNotificationLoad(MyActivity.CurrentUser);

        ordersListViewModel.getNotifications().observe(CustomerActivity.this, new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                mapViewModel.setOrders(orders);
            }
        });

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return customerMapsUtils.onMarkerClick(marker, imHere.getMyPlace(), mainViewModel.currentPlace);
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
                     imHere.init(CustomerActivity.this);
                } else {
                    // permission denied
                }
                return;
        }
    }

    @Override
    public void onButtonClicked(SelectDirectionBottonDialog.DirectionMode mode) {
        switch (mode){
            case SHORT:
                PickupHelper.init();
                Pickup curr = new Pickup();
                curr.setToken(mysettings.GetFCMToken());
                curr.setLastLocation( new LatLng(imHere.getLat(), imHere.getLong()));

//                if(mainViewModel.currentPlace != null) {
//
//                }
                PickupHelper.sendRequest(curr, mapViewModel.getAvaibleUsers()); //PickupHelper.sendRequest(curr, mapViewModel.getUsers().getValue());
                break;

            case FAR:

                break;

            default:
                break;
        }
    }
}
