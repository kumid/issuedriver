package com.ru.test.issuedriver.customer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.firebase.geofire.GeoLocation;
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
import com.google.firebase.firestore.GeoPoint;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.ui.map.MapViewModel;
import com.ru.test.issuedriver.customer.ui.map.imHere;
import com.ru.test.issuedriver.customer.ui.map.mapGeofireViewModel;
import com.ru.test.issuedriver.customer.ui.order.OrderActivity;
import com.ru.test.issuedriver.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.customer.ui.placesUtils;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.geofire.geofireCallBacks;
import com.ru.test.issuedriver.geofire.geofireHelper;
import com.ru.test.issuedriver.helpers.firestoreHelper;
import com.ru.test.issuedriver.helpers.mysettings;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class customerMapsUtils {
    // показывать маркер в центре экрана
    public static boolean isImgLocationPinUpOn = false;

    //
    private static boolean isDriversClickable = true;

    private static final String TAG = "myLogs";
    private static AppCompatActivity mapActivity;
    private static GoogleMap googleMap;
    private static ImageView mImgLocationPinUp;
    private static Marker markerPin;

    private static mapGeofireViewModel mapViewModel;

    OrdersListViewModel ordersListViewModel;
    private static final float carZoomLevel = 16f;
    private static final float dotZoomLevel = 11f;
    private static float zoomLevel = carZoomLevel;


    private static MarkerOptions markerOptionIm;
    private static Marker ImMarker;

    public static LatLng getMarkerPinPosition() {
        return markerPin.getPosition();
    }

    private static class markerPair {
        public MarkerOptions markerOption;
        public Marker marker;
        public user _user;

        public markerPair(MarkerOptions markerBus, Marker busMarker, user _user) {
            markerOption = markerBus;
            marker = busMarker;
            this._user = _user;
        }
    }

    private static Map<String, markerPair> markerMap = new HashMap<>();
    static LatLng cameraPos;
    public static void Init(AppCompatActivity activity, MapView mMapView, mapGeofireViewModel _mapViewModel, ImageView imgLocationPinUp) {

        mapActivity = activity;
        mapViewModel = _mapViewModel;
        mImgLocationPinUp = imgLocationPinUp;
        if (!isImgLocationPinUpOn)
            mImgLocationPinUp.setVisibility(View.GONE);

        cameraPos = new LatLng(Double.parseDouble("45.058403"), Double.parseDouble("38.983933"));

        try {
            Location lastPos = mysettings.GetPosition();
            if (lastPos != null
                && lastPos.getLatitude() > 0)
                cameraPos = new LatLng(lastPos.getLatitude(), lastPos.getLongitude());
        } catch (Exception ex){
            Log.e("myError", "0");
        }

        try {
            MapsInitializer.initialize( mapActivity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                try {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(cameraPos).zoom(carZoomLevel).build();
                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                } catch (Exception ex){
                    Log.e("myError", "Error 2");
                }

                googleMap.getUiSettings().setCompassEnabled(false);

                if(true)
                    return;

                geofireHelper.init(googleMap);
                placesUtils.Init(mapActivity, googleMap, true);

                GoogleMap.OnMarkerClickListener markerClickListener = (GoogleMap.OnMarkerClickListener) mapActivity;
                if (markerClickListener != null)
                    googleMap.setOnMarkerClickListener(markerClickListener);
                googleMap.setMinZoomPreference(9f);
                googleMap.setMaxZoomPreference(17f);



                imHere.myPositionChanged = new imHere.OnMyPositionChanged() {
                    @Override
                    public void callBack(Location location) {
                        googleMap.setMinZoomPreference(5f);
                        if (ActivityCompat.checkSelfPermission(mapActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(mapActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        googleMap.setMyLocationEnabled(true);
                        setMyPosition(location);
                        mapViewModel.getCarAround(location);
                    }
                };
                imHere.init(mapActivity);

                observe2performers();

                googleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
                    @Override
                    public void onCameraMove() {
                        if(!isImgLocationPinUpOn)
                            return;

                        mImgLocationPinUp.setVisibility(View.VISIBLE);
                        if(markerPin!=null)
                            markerPin.remove();
                    }
                });

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        float oldZoom = zoomLevel;
                        zoomLevel = googleMap.getCameraPosition().zoom;
                        setCarsVisibility(oldZoom, zoomLevel);

                        if (isImgLocationPinUpOn) {
                            mImgLocationPinUp.setVisibility(View.GONE);
                            MarkerOptions markerOptions = new MarkerOptions().position(googleMap.getCameraPosition().target)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                                    .title("Я");
                            // adding marker
                            markerPin = googleMap.addMarker(markerOptions);
                        }
                    }
                });
            }
        });
    }


    public static void setMyPosition(Location imHere) {
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
try {
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));
            } catch (Exception ex) {
                Log.e("myError", "Error 2");
            }
        } else {
            animateMarker(null, ImMarker,
                    new LatLng(imHere.getLatitude(),
                            imHere.getLongitude()),
                    false,
                    !mapViewModel.isCameraOnPerformer,
                    false);
        }
    }

    private static Map<String, GeoLocation> mapGeohash = new HashMap<>();

    private static void observe2performers() {

        geofireCallBacks.callback4geofireItemRecieve = new geofireCallBacks.geofireItemRecieveInterface() {
            @Override
            public void callback(String key, GeoLocation location) {

                geofireCallBacks.callback4AvaibleUserAdd = new geofireCallBacks.AvaibleUserAddInterface() {
                    @Override
                    public void callback(user addedUser) {
                        Handler mHandler = new Handler(Looper.getMainLooper());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setUserMarker(addedUser, new GeoPoint(location.latitude, location.longitude));
                            }
                        });
                    }
                };
                mapViewModel.addAvaibleUser2List(key);
            }
        };

        geofireCallBacks.callback4geofireItemExitRecieve = new geofireCallBacks.geofireItemExitRecieveInterface() {
            @Override
            public void callback(String key) {

                String email = mapViewModel.removeAvaibleUserFromList(key);
                if(email == null)
                    return;
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        removeUserMarker(email);
                    }
                });

            }
        };


//        callBacks.callback4geofireFinishRecieve= new callBacks.geofireFinishRecieveInterface() {
//            @Override
//            public void callback() {
//                GeoLocation tmp;
//                for(user item: mapViewModel.getUsers().getValue()) {
//                    tmp = mapGeohash.get(item.UUID);
//                    if (mapGeohash.containsKey(item.UUID)) {
//                        item.position = new GeoPoint(tmp.latitude, tmp.longitude);
//                        setUserMarker(item, new GeoPoint(tmp.latitude, tmp.longitude));
//                    }
//                }
//             }
//        };
    }

    private static void removeUserMarker(String email) {
        if (markerMap.containsKey(email)) {
            markerMap.get(email).marker.setVisible(false);
            markerMap.get(email).marker.remove();
            markerMap.remove(email);
        }
    }

    // Визуализация маркеров машин
    private static void setUserMarker(user item, GeoPoint pos) {
        if (markerMap.containsKey(item.email)) {
            // если маркер пользователя есть на карте
            if(zoomLevel <= dotZoomLevel){
                // если карта слишком
                markerMap.get(item.email).marker.setVisible(false);
                markerMap.get(item.email).marker.remove();
                return;
            }

            if (markerMap.get(item.email)._user.is_busy() != item.is_busy()) {
                markerMap.get(item.email).marker.setVisible(false);
                markerMap.get(item.email).marker.remove();

                BitmapDescriptor car = getBitmapDescriptor(item);

                markerMap.get(item.email).markerOption = new MarkerOptions().position(
                        new LatLng(item.position.getLatitude(), item.position.getLongitude()))
                        .title(item.fio);
                markerMap.get(item.email).markerOption.icon(car);

                Marker BusMarkerOK = googleMap.addMarker(markerMap.get(item.email).markerOption);
                markerMap.get(item.email).marker = BusMarkerOK;
                markerMap.get(item.email)._user = item;
            } else {

                if (pos != null) {
                    animateMarker(item, markerMap.get(item.email).marker,
                            new LatLng(pos.getLatitude(), pos.getLongitude()),
                            false,
                            mapViewModel.isOrderInActiveState(item.email), true); // order - активный
                }
            }
        }
        else {
            // создать новый маркер
            setPerformerPosition(item, pos);
        }
    }

    public static View.OnClickListener clickZoom = new View.OnClickListener() {
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
if(zoom != 0f) {
                try {
                    googleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                } catch (Exception ex){
                Log.e("myError", "Error 2");
            }

        }
        }
    };


    private static void setCarsVisibility(float oldZoom, float zoomLevel) {
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

    @NotNull
    private static BitmapDescriptor getBitmapDescriptor(user item) {
        int carId;
        int size;

        if(zoomLevel >= carZoomLevel){
            carId = item.is_busy() ? R.drawable.car_red2 : R.drawable.car1;
            size = 100;
        } else if(zoomLevel >= dotZoomLevel){
            carId = R.drawable.dot;
            size = 25;
        } else
            return null;

        return getBitmapDescriptor(carId, size, size);
    }

    private static void setPerformerPosition(user item, GeoPoint pos) {
if (googleMap == null
                || item.position == null) {
            return;
        }

        MarkerOptions markerBus = new MarkerOptions().position(
                new LatLng(pos.getLatitude(), pos.getLongitude()))
                .flat(true)
                .title(item.fio);

        BitmapDescriptor car = getBitmapDescriptor(item);
        markerBus.icon(car);

        // adding marker
        Marker BusMarker = googleMap.addMarker(markerBus);

        markerMap.put(item.email, new markerPair(markerBus, BusMarker, item));

        Log.e("MapsLog", "googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));");

    }

    @NotNull
    private static BitmapDescriptor getBitmapDescriptor(int car, int height, int width) {
        BitmapDrawable bitmapdraw = (BitmapDrawable) mapActivity.getResources().getDrawable(car);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
        return BitmapDescriptorFactory.fromBitmap(smallMarker);
    }

    public static void animateMarker(user item, final Marker marker, final LatLng toPosition, final boolean hideMarker, boolean cameraMoved, boolean isRotation) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = googleMap.getProjection();

        if(isRotation) {
            marker.setAnchor(0.5f, 0.5f);
            double bearing = getBearing(marker.getPosition(), toPosition);
            marker.setRotation((float) bearing);
        }

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
try {
                        googleMap.animateCamera(CameraUpdateFactory
                                .newCameraPosition(cameraPosition));
                    }catch (Exception ex) {
                        Log.e("myError", "Error 2");
                    }
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

    public static boolean onMarkerClick(Marker marker, place fromPlace, place toPlace) {
        if(!isDriversClickable)
            return false;

        for (String item : markerMap.keySet()) {
            if (markerMap.get(item).marker.equals(marker)) {
                if (markerMap.get(item)._user.is_busy())
                    return false;

                //firestoreHelper.setUserBusy(markerMap.get(item)._user.email, true);
                firestoreHelper.setUserHalfBusy(markerMap.get(item)._user.UUID, markerMap.get(item)._user.email, true);

                Intent intent = new Intent(mapActivity, OrderActivity.class);
//                intent.putExtra("customer_fio", registrationViewModel.currentUser.getValue().fio);
//                intent.putExtra("customer_phone", registrationViewModel.currentUser.getValue().tel);
//                intent.putExtra("customer_email", registrationViewModel.currentUser.getValue().email);
                intent.putExtra("performer_uuid", markerMap.get(item)._user.UUID);
                intent.putExtra("performer_fio", markerMap.get(item)._user.fio);
                intent.putExtra("performer_phone", markerMap.get(item)._user.tel);
                intent.putExtra("performer_photo", markerMap.get(item)._user.photoPath);
                intent.putExtra("performer_email", markerMap.get(item)._user.email);
                intent.putExtra("performer_token", markerMap.get(item)._user.fcmToken);
                intent.putExtra("performer_car",  String.format("%s %s", markerMap.get(item)._user.automarka,  markerMap.get(item)._user.automodel).trim());
                intent.putExtra("performer_car_number", markerMap.get(item)._user.autonumber);
                if(fromPlace != null)
                    intent.putExtra("from_place", fromPlace);
                if(toPlace != null)
                    intent.putExtra("to_place", toPlace);

                intent.putExtra("performer_car_number", markerMap.get(item)._user.autonumber);
                mapActivity.startActivity(intent);
                return false;
            }
        }
        return false;
    }

    private static double getBearing(LatLng begin, LatLng end) {
        double PI = 3.14159;
        double lat1 = begin.latitude * PI / 180;
        double long1 = begin.longitude * PI / 180;
        double lat2 = end.latitude * PI / 180;
        double long2 = end.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;

        return brng;
    }
}
