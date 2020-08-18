package com.ru.test.issuedriver.performer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.bottom_dialogs.OrderCancelBottonDialog;
import com.ru.test.issuedriver.bottom_dialogs.UserStateBottonDialog;
import com.ru.test.issuedriver.MainViewModel;
import com.ru.test.issuedriver.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.geofire.geofireHelper;
import com.ru.test.issuedriver.helpers.MyBroadcastReceiver;
import com.ru.test.issuedriver.helpers.fsm.sender;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.helpers.storage.fbStorageUploads;
import com.ru.test.issuedriver.helpers.storage.picturelib;
import com.ru.test.issuedriver.performer.ui.feedback.FeedbackActivity;
import com.ru.test.issuedriver.helpers.PerformerBackgroundService;
import com.ru.test.issuedriver.helpers.callBacks;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.performer.ui.orderPerforming.OrderPerformingActivity;
import com.ru.test.issuedriver.performer.ui.shina.ShinaActivity;
import com.ru.test.issuedriver.performer.ui.to.TOActivity;
import com.ru.test.issuedriver.ui.history.HistoryViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class PerformerActivity extends MyActivity implements UserStateBottonDialog.BottomSheetListener, OrderCancelBottonDialog.CancelBottomSheetListener , fbStorageUploads.setPhotoFBpathInterface{

    private static final String TAG = "myLogs";

    private static PerformerActivity inst;
//    private user currentUser;
    NavController navController;
    public BottomNavigationView navView;

    public static PerformerActivity getInstance(){
        return inst;
    }

//    RegistrationViewModel registrationViewModel;
    private MainViewModel mainViewModel;
    private OrdersListViewModel ordersListViewModel;
    private HistoryViewModel historyViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer);

        inst = this;
        googleAuthManager.init(this);
        googleAuthManager.signIn();

        setupNavigation();

        initViewModels();
        geofireHelper.init(null);

        // setDefaultUserState();

//        if(getIntent().hasExtra("object")){
//            String obj = getIntent().getStringExtra("object");
//            Gson gson = new Gson();
//            user curr = gson.fromJson(obj, user.class);
//            if(curr != null)
//                registrationViewModel.currentUser.setValue(curr);
//        } else {
//            registrationViewModel.getUserFromServer(googleAuthManager.getEmail());
//        }
        checkPermissionGeo( this);
        
//        final Intent intent = new Intent(this.getApplication(), BackgroundService.class);
//        this.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastKnownLocation();

//        registrationViewModel.currentUser.observe(this, new Observer<user>() {
//            @Override
//            public void onChanged(user user) {
//                currentUser = user;
//            }
//
//        });

        callBacks.callback4cancelOrder = new callBacks.CancelOrderInterface() {
            @Override
            public void callback(order order) {
                OrderCancelBottonDialog dialog = new OrderCancelBottonDialog(order, MyActivity.CurrentUser.is_performer);
                dialog.show(getSupportFragmentManager(), null);
            }
        };

        OnlineStateListen();

        picturelib.init(this);
        firestoreHelper.updateUserInfo(this);
    }

    boolean isInit = false;
    private void initExtra() {
        if(isInit)
            return;
        isInit = true;
        if(getIntent().hasExtra("msg_mode")) {
            int mode = getIntent().getExtras().getInt("msg_mode");
            switch (mode){
                case 3:
                case 7:
                case 8:
                    navController.navigate(R.id.navigation_notifications);
                    break;

                case 5:
                    navController.navigate(R.id.navigation_history);
                    break;

            }

        }
    }
    private void setupNavigation() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_notifications, R.id.navigation_history, R.id.navigation_registration)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    private void initViewModels() {
//        registrationViewModel =
//                ViewModelProviders.of(PerformerActivity.getInstance()).get(RegistrationViewModel.class);
        mainViewModel = ViewModelProviders.of(PerformerActivity.this).get(MainViewModel.class);
        mainViewModel.Init(CurrentUser);
        mainViewModel.getCurrentUserLiveData().observe(this, new Observer<user>() {
            @Override
            public void onChanged(user user) {
                MyActivity.CurrentUser = user;
                if(user == null)
                    googleAuthManager.signOut();
                else if(onlineStateItem != null)
                    setUserStateIcon();
            }
        });

        ordersListViewModel =
                ViewModelProviders.of(PerformerActivity.this).get(OrdersListViewModel.class);
        ordersListViewModel.initNotificationLoad(MyActivity.CurrentUser);

        historyViewModel =
                ViewModelProviders.of(PerformerActivity.this).get(HistoryViewModel.class);
        historyViewModel.initNotificationsHistoryLoad(CurrentUser);
    }

    private void setUserStateIcon() {
        if(MyActivity.CurrentUser.state == 0)
            onlineStateItem.setIcon(ContextCompat.getDrawable(PerformerActivity.this, R.drawable.online));
        else
            onlineStateItem.setIcon(ContextCompat.getDrawable(PerformerActivity.this, R.drawable.offline));
    }


    FusedLocationProviderClient mFusedLocationClient;
    private void startLocationService(){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, PerformerBackgroundService.class);
//        this.startService(serviceIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
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
//                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//                    mUserLocation.setGeo_point(geoPoint);
//                    mUserLocation.setTimestamp(null);
//                    saveUserLocation();
                    startLocationService();
                } else {
                    Log.d("TAG", "getLastKnownLocation: called.");
                }
            }
        });

    }
    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.ru.test.issuedriver.helpers.PerformerBackgroundService".equals(service.service.getClassName())) {
                Log.d("TAG", "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d("TAG", "isLocationServiceRunning: location service is not running.");
        return false;
    }
    public static final int PERMISSIONS= 123, PERMISSIONS10= 1230;
    //check location permession for Android 5.0/+
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermissionGeo(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION) ) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Разрешения");
                    alertBuilder.setMessage("Определение геопозиции устройства ВАЖНО для работы приложения");
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
        } else if(currentAPIVersion >= Build.VERSION_CODES.Q)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.ACCESS_FINE_LOCATION) ) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Разрешения");
                    alertBuilder.setMessage("Определение геопозиции устройства ВАЖНО для работы приложения");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSIONS10);

                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSIONS10);

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

        callBacks.callback4StartOrderPerforming = new callBacks.StartOrderPerformingInterface() {
            @Override
            public void callback(boolean success) {
                if(!success)
                    return;

                if(item.start_timestamp == null)
                    sender.send(item, sender.orderStateType.performing_order);

                Intent intent = new Intent(PerformerActivity.this, OrderPerformingActivity.class);
                intent.putExtra("order", item);
//                intent.putExtra("customer_uuid", item.customer_uuid);
//                intent.putExtra("customer_fio", item.customer_fio);
//                intent.putExtra("customer_phone", item.customer_phone);
//                intent.putExtra("customer_email", item.customer_email);
//                intent.putExtra("performer_uuid", item.performer_uuid);
//                intent.putExtra("performer_fio", item.performer_fio);
//                intent.putExtra("performer_phone", item.performer_phone);
//                intent.putExtra("performer_email", item.performer_email);
//                intent.putExtra("performer_car", item.car);
//                intent.putExtra("performer_car_number", item.car_number);
//                intent.putExtra("from", item.from);
//                intent.putExtra("to", item.to);

                if(item.from_position != null){
                    place fromPlace = new place(item.from, item.from_position.getLatitude(), item.from_position.getLongitude());
                    intent.putExtra("from_place", fromPlace);
                }

                if(item.to_position != null){
                    place curr = new place(item.to, item.to_position.getLatitude(), item.to_position.getLongitude());
                    intent.putExtra("to_place", curr);
                }

//                intent.putExtra("purpose", item.purpose);
//                intent.putExtra("comment", item.comment);
//                intent.putExtra("order_id", item.id);

                startActivity(intent);
                Log.e(TAG, "");
            }
        };

        ordersListViewModel.setOrderStartPerforming(item);

    }

    MenuItem onlineStateItem, onlineServerItem, action_msgItem;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.emetgency, menu);
        onlineStateItem = menu.findItem(R.id.action_state);
        onlineServerItem = menu.findItem(R.id.action_isonline);
        setUserStateIcon();
        return true;
//        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send:
                Intent intentF = new Intent(PerformerActivity.this, FeedbackActivity.class);
                intentF.putExtra("phone", CurrentUser.tel);
                startActivity(intentF);
                return true;
            case R.id.action_to:
                Intent intentTO = new Intent(PerformerActivity.this, TOActivity.class);
                intentTO.putExtra("phone", CurrentUser.tel);
                startActivity(intentTO);
                return true;
            case R.id.action_shina:
                Intent intentSH = new Intent(PerformerActivity.this, ShinaActivity.class);
                intentSH.putExtra("phone", CurrentUser.tel);
                startActivity(intentSH);
                return true;

            case R.id.action_state:




                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                         DatabaseReference myRef = database.getReference("articles").push();
                        Article article = new Article("title", "author");
                        myRef.setValue(article);






                if(ordersListViewModel.getNotifications().getValue() != null){
                   for(order elem: ordersListViewModel.getNotifications().getValue()) {
                       if (elem.accept_timestamp != null) {
                           showToast("Есть активный заказ", Toast.LENGTH_SHORT);
                           return true;
                       }

                   }
                }
                   UserStateBottonDialog dialog = new UserStateBottonDialog();
                dialog.show(getSupportFragmentManager(), null);
                return true;
            case R.id.action_isonline:
                fixLocationListener();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
     }

    private void fixLocationListener() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        if(!isLocationServiceRunning()){

        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            myRef.child("Errors").child(CurrentUser.UUID).setValue("ACCESS_FINE_LOCATION - ЗАПРЕЩЕНО");
            checkPermissionGeo(this);
            return;
        }

        getLastKnownLocation();
    }

    @Override
    public void onButtonClicked(int state) {
        callBacks.userStateChangedCalback = new callBacks.userStateChanged() {
            @Override
            public void callback(int callback_state) {
                if (callback_state == 99)
                    Log.e(TAG, "userStateChangedCalback -> ERROR");
                else {
                    Log.e(TAG, "userStateChangedCalback -> OK");
                    MyActivity.CurrentUser.state = state;
                    if(state == 0)
                        onlineStateItem.setIcon(ContextCompat.getDrawable(PerformerActivity.this, R.drawable.online));
                    else
                        onlineStateItem.setIcon(ContextCompat.getDrawable(PerformerActivity.this, R.drawable.offline));
                }
            }
        };
        firestoreHelper.setUserState(MyActivity.CurrentUser.email, state);
    }

    @Override
    public void onCancelButtonClicked(order item) {
        Log.e("myLogs", "");
        firestoreHelper.setOrderCancelState(item, 1, String.format("Отменен водителем: %s", item.cancel_reason));
        firestoreHelper.setUserState(item.performer_email, 0);
        //ordersListViewModel.setOrderDelete(item);

        sender.send(item, sender.orderStateType.cancel_order_from_performer);
    }

    private void OnlineStateListen() {
//        registerReceiver();

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    synchronized (this) {
                        try {
                            wait(10000);
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    onlineServerItem.setIcon(ContextCompat.getDrawable(PerformerActivity.this,R.drawable.server_offline));
                                }
                            });

                        } catch (Exception e) {
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initExtra();
        try {
            registerReceiver();
        } catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    MyBroadcastReceiver broadcastReceiver;
    private void registerReceiver() {
        broadcastReceiver = new MyBroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "BroadcastReceiver: onReceive");
                Object data = intent.getExtras().get("data");
                boolean online = intent.getBooleanExtra("online", false);

                onlineServerItem.setIcon(ContextCompat.getDrawable(PerformerActivity.this, online ? R.drawable.online_no_server:R.drawable.server_offline));

                if (data != null) {
                    Location pos = (Location) data;
                    if (pos != null
                            && callback4gpsposition != null) {
                        Log.d(TAG, "BroadcastReceiver: callback4gpsposition.callback(pos)" + data);
                        callback4gpsposition.callback(pos);
                        onlineServerItem.setIcon(ContextCompat.getDrawable(PerformerActivity.this, R.drawable.server_online));
                    }
                }

                if (callback4onlineState != null) {
                    Log.d(TAG, "BroadcastReceiver: callback4onlineState.callback()" + online);
                    callback4onlineState.callback(online);
                }

            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("com.ru.test.issuedriver.performer.ui.order.MY_NOTIFICATION"));
    }
    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (broadcastReceiver != null) {
                unregisterReceiver(broadcastReceiver);
            }
        } catch (Exception ex) {
            Log.d("TAG", "ERROR ");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri = picturelib.onActivityResult(requestCode, resultCode, data, resultCode == this.RESULT_OK, null);
    }

    @Override
    public void setPath(String path) {
        mainViewModel.CurrentUser.photoPath = path;
        CurrentUser.photoPath = path;
        if(callback4UpdatePhotoInterface != null)
            callback4UpdatePhotoInterface.callback(path);
    }
    public static UpdatePhotoInterface callback4UpdatePhotoInterface;
    public interface  UpdatePhotoInterface {
        void callback(String path);
    }
}



/*
    Menu menu = navView.getMenu();
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
@Override
public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.navigation_notifications){
        menu.findItem(R.id.navigation_notifications).setIcon(getResources().getDrawable(R.drawable.bell_extra));

        navView.setItemIconTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));

//        menu.findItem(R.id.navigation_notifications).co
//        navView.setItemIconTintList(ColorStateList.valueOf(Color.parseColor("#3F51B5")));
//        menu.findItem(R.id.navigation_notifications).setIcon(getResources().getDrawable(R.drawable.ic_notifications_black_24dp));

        }


        return false;
        }
        });
        */