package com.ru.test.issuedriver.customer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.ru.test.issuedriver.SplashScreen;
import com.ru.test.issuedriver.history.HistoryActivity;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.ui.map.MapViewModel;
import com.ru.test.issuedriver.customer.ui.map.imHere;
import com.ru.test.issuedriver.customer.ui.order.OrderActivity;
import com.ru.test.issuedriver.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.data.order;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.orders.OrdersListActivity;
import com.ru.test.issuedriver.registration.RegistrationActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
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

public class CustomerV2Activity extends MyActivity implements NavigationView.OnNavigationItemSelectedListener, GoogleMap.OnMarkerClickListener {

    private static CustomerV2Activity instance;
    public static CustomerV2Activity getInstance() {
        return instance;
    }

    OrdersListViewModel ordersListViewModel;
    private final float carZoomLevel = 16f;
    private final float dotZoomLevel = 11f;
    private float zoomLevel = carZoomLevel;


    private class markerPair {
        public MarkerOptions markerOption;
        public Marker marker;
        public user _user;

        public markerPair(MarkerOptions markerBus, Marker busMarker, user _user) {
            markerOption = markerBus;
            marker = busMarker;
            this._user = _user;
        }
    }

    private MapViewModel mapViewModel;

    Map<String, markerPair> markerMap = new HashMap<>();

    private MapView mMapView;
    private GoogleMap googleMap;

    private static final String TAG = "myLogs";
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        setContentView(R.layout.activity_customer_v2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        try {
            MapsInitializer.initialize(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setOnMarkerClickListener(CustomerV2Activity.this);
                googleMap.setMinZoomPreference(16f);
                googleMap.setMaxZoomPreference(17f);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(Double.parseDouble("45.058403"), Double.parseDouble("38.983933"))).zoom(carZoomLevel).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                imHere.myPositionChanged = new imHere.OnMyPositionChanged() {
                    @Override
                    public void callBack(Location location) {
                        setMyPosition(location);
                        googleMap.setMinZoomPreference(10f);
                        googleMap.setMyLocationEnabled(true);
                    }
                };
                imHere.init(CustomerV2Activity.this);

                observe2performers();

//                View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//                // position on right bottom
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                rlp.setMargins(0, 180, 180, 0);


                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        float oldZoom = zoomLevel;
                        zoomLevel = googleMap.getCameraPosition().zoom;
                        setCarsVisibility(oldZoom, zoomLevel);
                    }
                });
            }
        });

        ImageView mMap_plus = findViewById(R.id.map_plus);
        ImageView mMap_minus = findViewById(R.id.map_minus);
        mMap_plus.setOnClickListener(clickZoom);
        mMap_minus.setOnClickListener(clickZoom);

        checkPermission(this);

//        test();
    }

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
    }

    private void setCarsVisibility(float oldZoom, float zoomLevel) {
         if(oldZoom == zoomLevel) {
            Log.d("myLogs", "Zoom = oldZoom = " + zoomLevel);
            return;
        }
        Log.d("myLogs", "Zoom = " + zoomLevel);
        boolean isInformed = false;
        for (String key:
                markerMap.keySet()) {

            markerPair item = markerMap.get(key);

            boolean inZeroOld = oldZoom < dotZoomLevel;
            boolean inDotOld = oldZoom >= dotZoomLevel && oldZoom < carZoomLevel;
            boolean inCarOld = oldZoom >= carZoomLevel;

            boolean inZero = zoomLevel < dotZoomLevel;
            boolean inDot = zoomLevel >= dotZoomLevel && zoomLevel < carZoomLevel;
            boolean inCar = zoomLevel >= carZoomLevel;

            if (inZero) {
                item.marker.setVisible(false);
                item.marker.remove();
                //googleMap.clear();
                continue;
            }

            boolean isNotChanged = (inDotOld && inDot) || (inCarOld && inCar);  //  (inZeroOld && inZero) ||

            if (!isNotChanged) {
                item.marker.setVisible(false);
                item.marker.remove();
                BitmapDescriptor car = getBitmapDescriptor(item._user);
//                item.markerOption = new MarkerOptions().position(
//                        new LatLng(item._user.position.getLatitude(), item._user.position.getLongitude()))
//                        .title(item._user.fio);
                item.markerOption.icon(car);

                Marker BusMarkerOK = googleMap.addMarker(item.markerOption);
                item.marker = BusMarkerOK;
                if (!isInformed) {
                    Log.d("myLogs", "Change car visibility");
                    isInformed = true;
                }
            }
        }
    }

    private void observe2performers() {
        mapViewModel.getUsers().observe(CustomerV2Activity.this, new Observer<List<user>>() {
            @Override
            public void onChanged(List<user> users) {
//                googleMap.clear();
                for (user item : users) {
                    if (item.position == null)
                        continue;

                    if (markerMap.containsKey(item.email)) {
                        if(zoomLevel <= dotZoomLevel){
                            markerMap.get(item.email).marker.setVisible(false);
                            markerMap.get(item.email).marker.remove();
                            continue;
                        }

                        if (markerMap.get(item.email)._user.is_busy != item.is_busy) {
                            markerMap.get(item.email).marker.setVisible(false);
                            markerMap.get(item.email).marker.remove();

//                            MarkerOptions markerBus = new MarkerOptions().position(
//                                    new LatLng(item.position.getLatitude(), item.position.getLongitude()))
//                                    .title(item.fio);

                            BitmapDescriptor car = getBitmapDescriptor(item);

                            markerMap.get(item.email).markerOption = new MarkerOptions().position(
                                    new LatLng(item.position.getLatitude(), item.position.getLongitude()))
                                    .title(item.fio);
                            markerMap.get(item.email).markerOption.icon(car);

                            Marker BusMarkerOK = googleMap.addMarker(markerMap.get(item.email).markerOption);
                            markerMap.get(item.email).marker = BusMarkerOK;
                            markerMap.get(item.email)._user = item;
                        } else {
                            if (item.position != null) {
                                animateMarker(item, markerMap.get(item.email).marker,
                                        new LatLng(item.position.getLatitude(), item.position.getLongitude()),
                                        false,
                                            mapViewModel.isOrderInActiveState(item.email)); // order - активный
                            }
                        }
                    } else {
                        setPerformerPosition(item);
                    }
                }
                // проверяем актуальность Водителей
                if(markerMap.size() != 0) {
                    for (Object key :  markerMap.keySet().toArray()) {
                        // получаем следующий маркер
                        boolean forDelete = true;
                        // проходим по списку пользователей (Водителей)
                        for (user item : users) {
                            if (item.email.equals(markerMap.get(key)._user.email)) {     // если водитель все еще в актуальном состоянии
                                forDelete = false;
                                break;
                            }
                        }

                        if (forDelete) {
//                        markerMap.get(key).markerOption.visible(false);
//                        markerMap.get(key).marker.setVisible(false);
                            markerMap.get(key).marker.remove();
                            markerMap.remove(key);
                        }
                    }
                }
            }
        });
    }

    @NotNull
    private BitmapDescriptor getBitmapDescriptor(user item) {
        int carId;
        int size;

        if(zoomLevel >= carZoomLevel){
            carId = item.is_busy ? R.drawable.car_red2 : R.drawable.car_yellow1;
            size = 70;
        } else if(zoomLevel >= dotZoomLevel){
            carId = R.drawable.dot;
            size = 25;
        } else
            return null;

        return getBitmapDescriptor(carId, size, size);
    }

    private void setPerformerPosition(user item) {
        if (googleMap == null
                || item.position == null) {
            return;
        }

        MarkerOptions markerBus = new MarkerOptions().position(
                new LatLng(item.position.getLatitude(), item.position.getLongitude()))
                .title(item.fio);

        BitmapDescriptor car = getBitmapDescriptor(item);
        markerBus.icon(car);

        //        markerBus.icon(BitmapDescriptorFactory
//                .fromResource(car)); //).defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        Marker BusMarker = googleMap.addMarker(markerBus);

        markerMap.put(item.email, new markerPair(markerBus, BusMarker, item));

//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(Double.parseDouble("45.058403"), Double.parseDouble("38.983933"))).zoom(15).build();
////               .target(new LatLng(item.position.getLatitude(), item.position.getLongitude())).zoom(15).build();
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));
        Log.e("MapsLog", "googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));");

    }

    @NotNull
    private BitmapDescriptor getBitmapDescriptor(int car, int height, int width) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    public void animateMarker(user item, final Marker marker, final LatLng toPosition, final boolean hideMarker, boolean cameraMoved) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 1500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if(cameraMoved) {
                    float zoom = googleMap.getCameraPosition().zoom;
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(toPosition).zoom(zoom).build();
                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        for (String item : markerMap.keySet()) {
            if (markerMap.get(item).marker.equals(marker)) {
                if (markerMap.get(item)._user.is_busy)
                    return false;

                Intent intent = new Intent(CustomerV2Activity.this, OrderActivity.class);
//                intent.putExtra("customer_fio", registrationViewModel.currentUser.getValue().fio);
//                intent.putExtra("customer_phone", registrationViewModel.currentUser.getValue().tel);
//                intent.putExtra("customer_email", registrationViewModel.currentUser.getValue().email);
                intent.putExtra("performer_fio", markerMap.get(item)._user.fio);
                intent.putExtra("performer_phone", markerMap.get(item)._user.tel);
                intent.putExtra("performer_email", markerMap.get(item)._user.email);
                intent.putExtra("performer_car", markerMap.get(item)._user.automodel);
                intent.putExtra("performer_car_number", markerMap.get(item)._user.autonumber);

                startActivity(intent);
                return false;
            }
        }
        return false;
    }

    private MarkerOptions markerOptionIm;
    private Marker ImMarker;

    public void setMyPosition(Location imHere) {
        if(googleMap == null)
            return;

        if (markerOptionIm == null) {
            markerOptionIm = new MarkerOptions()
                    .position(
                            new LatLng(imHere.getLatitude(),
                                    imHere.getLongitude()))
                    .title("Я");
            markerOptionIm.icon(getBitmapDescriptor(R.drawable.man, 80, 80));
            ImMarker = googleMap.addMarker(markerOptionIm);

            float zoom = googleMap.getCameraPosition().zoom;
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(imHere.getLatitude(),
                            imHere.getLongitude())).zoom(zoom).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else {
            animateMarker(null, ImMarker,
                    new LatLng(imHere.getLatitude(),
                            imHere.getLongitude()),
                    false,
                    !mapViewModel.isCameraOnPerformer);
        }
    }

    private View.OnClickListener clickZoom = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            float zoom = 0f;
            CameraPosition cameraPosition = null;
            switch (v.getId()){
                case R.id.map_plus:
                    zoom = googleMap.getCameraPosition().zoom + 1f;
                    cameraPosition = new CameraPosition.Builder()
                            .target(googleMap.getCameraPosition().target).zoom(zoom).build();
                    break;

                case R.id.map_minus:
                    zoom = googleMap.getCameraPosition().zoom - 1f;
                    cameraPosition = new CameraPosition.Builder()
                            .target(googleMap.getCameraPosition().target).zoom(zoom).build();
                    break;
                default:

                    break;
            }
            if(zoom != 0f)
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

        }
    };


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
