package com.ru.test.issuedriver.taxi.performer;

import android.content.Intent;
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
import com.ru.test.issuedriver.taxi.R;
import com.ru.test.issuedriver.taxi.customer.ui.map.MapViewModel;
import com.ru.test.issuedriver.taxi.customer.ui.map.imHere;
import com.ru.test.issuedriver.taxi.customer.ui.order.OrderActivity;
import com.ru.test.issuedriver.taxi.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.taxi.customer.ui.placesUtils;
import com.ru.test.issuedriver.taxi.data.place;
import com.ru.test.issuedriver.taxi.data.user;
import com.ru.test.issuedriver.taxi.helpers.callBacks;
import com.ru.test.issuedriver.taxi.helpers.firestoreHelper;
import com.ru.test.issuedriver.taxi.helpers.geofireHelper;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

public class performerMapsUtils {
    // показывать маркер в центре экрана
    private static boolean isImgLocationPinUpOn = false;

    //
    private static boolean isDriversClickable = false;

    private static final String TAG = "myLogs";
    private static AppCompatActivity mapActivity;
    private static GoogleMap googleMap;
    private static ImageView mImgLocationPinUp;
    private static Marker markerPin;

    private static MapViewModel mapViewModel;

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

    public static void Init(AppCompatActivity activity, MapView mMapView, MapViewModel _mapViewModel, ImageView imgLocationPinUp, LatLng riderPosition){

        mapActivity = activity;
        mapViewModel = _mapViewModel;
        mImgLocationPinUp = imgLocationPinUp;
        if(!isImgLocationPinUpOn)
            mImgLocationPinUp.setVisibility(View.GONE);

        try {
            MapsInitializer.initialize( mapActivity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
//                geofireHelper.init(googleMap);

                //placesUtils.Init(mapActivity, googleMap, true);

                GoogleMap.OnMarkerClickListener markerClickListener = (GoogleMap.OnMarkerClickListener)mapActivity;
                if(markerClickListener != null)
                    googleMap.setOnMarkerClickListener(markerClickListener);
                googleMap.setMinZoomPreference(9f);
                googleMap.setMaxZoomPreference(17f);

                MarkerOptions markerOptions = new MarkerOptions().position(riderPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                        .title("Пассажир");
                // adding marker
                googleMap.addMarker(markerOptions);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(riderPosition).zoom(carZoomLevel).build();
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMapToolbarEnabled(false);

                imHere.myPositionChanged = new imHere.OnMyPositionChanged() {
                    @Override
                    public void callBack(Location location) {
//                        googleMap.setMinZoomPreference(5f);
                        //googleMap.setMyLocationEnabled(true);
//                        setMyPosition(location);
//                        mapViewModel.getCarAround(location);
                    }
                };
                imHere.init(mapActivity);

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
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
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
            if(zoom != 0f)
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

        }
    };

    @NotNull
    private static BitmapDescriptor getBitmapDescriptor(user item) {
        int carId;
        int size;

        if(zoomLevel >= carZoomLevel){
            carId = item.is_busy() ? R.drawable.car_red2 : R.drawable.car_yellow1;
            size = 80;
        } else if(zoomLevel >= dotZoomLevel){
            carId = R.drawable.dot;
            size = 25;
        } else
            return null;

        return getBitmapDescriptor(carId, size, size);
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
                intent.putExtra("performer_email", markerMap.get(item)._user.email);
                intent.putExtra("performer_car", markerMap.get(item)._user.automodel);
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
