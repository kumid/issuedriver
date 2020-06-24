package com.ru.test.issuedriver.customer;

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
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.Marker;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.ru.test.issuedriver.MainViewModel;
import com.ru.test.issuedriver.customer.ui.map.placesAdapter;
import com.ru.test.issuedriver.customer.ui.mapsUtils;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.helpers.callBacks;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.ui.history.HistoryActivity;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.ui.map.MapViewModel;
import com.ru.test.issuedriver.customer.ui.map.imHere;
import com.ru.test.issuedriver.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.ui.orders.OrdersListActivity;
import com.ru.test.issuedriver.ui.registration.RegistrationActivity;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

public class CustomerV2Activity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener {

    private static CustomerV2Activity instance;
    public static CustomerV2Activity getInstance() {
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
        setContentView(R.layout.activity_customer_v2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//String address = placesUtils.getAddressFromLocation(mapsUtils.getMarkerPinPosition());
//                Snackbar.make(view, address, Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
//        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
//            @Override
//            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
//                Log.d(TAG, "");
//            }
//        });
        initViewModels();

        mMapView = (MapView) findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        mImgLocationPinUp = findViewById(R.id.imgLocationPinUp);

        mapsUtils.Init(CustomerV2Activity.this, mMapView, mapViewModel, mImgLocationPinUp);

        ImageView mMap_plus = findViewById(R.id.map_plus);
        ImageView mMap_minus = findViewById(R.id.map_minus);
        mMap_plus.setOnClickListener(mapsUtils.clickZoom);
        mMap_minus.setOnClickListener(mapsUtils.clickZoom);


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

//        test();
    }
    RecyclerView rv;
//    private void test() {
//        Intent intent=new Intent(this, SplashScreen.class);
//        String channel_id="fleet_channel";
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),channel_id)
//                .setSmallIcon(R.drawable.logo_small)
//                .setSound(uri)
//                .setAutoCancel(true)
//                .setVibrate(new long[]{1000,1000,1000,1000,1000})
//                .setOnlyAlertOnce(true)
//                .setContentIntent(pendingIntent);
//
//             builder=builder.setContentTitle("title")
//                    .setContentText("message")
//                    .setSmallIcon(R.drawable.logo_small);
//
//        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//            NotificationChannel notificationChannel = new NotificationChannel(channel_id,"fleet_app", NotificationManager.IMPORTANCE_HIGH);
//            notificationChannel.setSound(uri,null);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        notificationManager.notify(100, builder.build());
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_v2, menu);
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
                intent = new Intent(CustomerV2Activity.this, OrdersListActivity.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Вы выбрали home", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_history:
                intent = new Intent(CustomerV2Activity.this, HistoryActivity.class);
                startActivity(intent);
//                Toast.makeText(getApplicationContext(), "Вы выбрали galery", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_cabinet:
                intent = new Intent(CustomerV2Activity.this, RegistrationActivity.class);
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
        mainViewModel = ViewModelProviders.of(CustomerV2Activity.this).get(MainViewModel.class);
        mainViewModel.Init(CurrentUser);

        mapViewModel =
                ViewModelProviders.of(CustomerV2Activity.this).get(MapViewModel.class);

        ordersListViewModel =
                ViewModelProviders.of(CustomerV2Activity.this).get(OrdersListViewModel.class);
        ordersListViewModel.initNotificationLoad(MyActivity.CurrentUser);

        ordersListViewModel.getNotifications().observe(CustomerV2Activity.this, new Observer<List<order>>() {
            @Override
            public void onChanged(List<order> orders) {
                mapViewModel.setOrders(orders);
            }
        });

        mapViewModel.setOrders(ordersListViewModel.getNotifications().getValue());
        callBacks.callback4orderListChangedInterface = new callBacks.orderListChangedInterface() {
            @Override
            public void callback() {
                mapsUtils.refreshCarsOnMap();
            }
        };
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        return mapsUtils.onMarkerClick(marker, imHere.getMyPlace(), mainViewModel.currentPlace);
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
                     imHere.init(CustomerV2Activity.this);
                } else {
                    // permission denied
                }
                return;
        }
    }
}
