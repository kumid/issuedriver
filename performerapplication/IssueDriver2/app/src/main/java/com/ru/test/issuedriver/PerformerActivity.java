package com.ru.test.issuedriver;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.GeoPoint;
import com.google.gson.Gson;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.PerformerBackgroundService;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.performer.ui.history.HistoryViewModel;
import com.ru.test.issuedriver.performer.ui.notifications.NotificationsViewModel;
import com.ru.test.issuedriver.performer.ui.order.OrderPerformingActivity;
import com.ru.test.issuedriver.performer.ui.registration.RegistrationViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class PerformerActivity extends MyActivity {

    private static final String TAG = "myLogs";

    private static PerformerActivity inst;

    public static PerformerActivity getInstance(){
        return inst;
    }

    RegistrationViewModel registrationViewModel;
    NotificationsViewModel notificationsViewModel;
    HistoryViewModel historyViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer);

        inst = this;
        googleAuthManager.init(this);
        googleAuthManager.signIn();

        setupNavigation();

        initViewModels();

        if(getIntent().hasExtra("object")){
            String obj = getIntent().getStringExtra("object");
            Gson gson = new Gson();
            user curr = gson.fromJson(obj, user.class);
            if(curr != null)
                registrationViewModel.currentUser.setValue(curr);
        } else {
            registrationViewModel.getUserFromServer(googleAuthManager.getEmail());
        }
        checkPermissionGeo( this);
        
//        final Intent intent = new Intent(this.getApplication(), BackgroundService.class);
//        this.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();


    }

    private void setupNavigation() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_notifications, R.id.navigation_dashboard, R.id.navigation_registration)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    private void initViewModels() {
        registrationViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(RegistrationViewModel.class);
        notificationsViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(NotificationsViewModel.class);
        notificationsViewModel.initNotificationLoad(PerformerActivity.getInstance(), registrationViewModel.currentUser);
        historyViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(HistoryViewModel.class);
        historyViewModel.initNotificationLoad(PerformerActivity.getInstance(), registrationViewModel.currentUser);
    }


    FusedLocationProviderClient mFusedLocationClient;
    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, PerformerBackgroundService.class);
//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                PerformerActivity.this.startForegroundService(serviceIntent);
            }else{
                startService(serviceIntent);
            }
        }
    }
    private void getLastKnownLocation() {
        Log.d("TAG", "getLastKnownLocation: called.");
        if(!isLocationServiceRunning()){
            Log.d("TAG", "getLastKnownLocation: called.");
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    if(location == null)
                        return;
                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//                    mUserLocation.setGeo_point(geoPoint);
//                    mUserLocation.setTimestamp(null);
//                    saveUserLocation();
                    startLocationService();
                }
            }
        });

    }
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.codingwithmitch.googledirectionstest.services.LocationService".equals(service.service.getClassName())) {
                Log.d("TAG", "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d("TAG", "isLocationServiceRunning: location service is not running.");
        return false;
    }
    public static final int PERMISSIONS= 123;
    //check location permession for Android 5.0/+
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermissionGeo(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION) ) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
//            case REQUEST_CODE_PERMISSION_CALL_PHONE:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission granted
//                    if(this.checkSelfPermission()) {
//                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telNumber));
//                        startActivity(intent);
//                        telNumber = "";
//                    }
//                } else {
//                    // permission denied
//                }
//                return;
            case PERMISSIONS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                    getLastKnownLocation();
                }
                return;
        }
    }

    public void startOrderPerforme(order item) {
        Intent intent = new Intent(PerformerActivity.this, OrderPerformingActivity.class);
        intent.putExtra("customer_fio", registrationViewModel.currentUser.getValue().fio);
        intent.putExtra("customer_phone", registrationViewModel.currentUser.getValue().tel);
        intent.putExtra("customer_email", registrationViewModel.currentUser.getValue().email);
        intent.putExtra("performer_fio", item.performer_fio);
        intent.putExtra("performer_phone", item.performer_phone);
        intent.putExtra("performer_email", item.performer_email);
        intent.putExtra("performer_car", item.car);
        intent.putExtra("performer_car_number", item.car_number);


        intent.putExtra("from", item.from);
        intent.putExtra("to", item.to);
        intent.putExtra("purpose", item.purpose);
        intent.putExtra("comment", item.comment);
        intent.putExtra("order_id", item.id);

        startActivity(intent);
        Log.e(TAG, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.emetgency, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                Intent intent = new Intent(PerformerActivity.this, FeedbackActivity.class);
                startActivity(intent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
     }
}
