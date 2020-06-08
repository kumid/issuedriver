package com.ru.test.issuedriver.customer.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
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
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerV2Activity;
import com.ru.test.issuedriver.customer.ui.map.MapViewModel;
import com.ru.test.issuedriver.customer.ui.map.imHere;
import com.ru.test.issuedriver.customer.ui.order.OrderActivity;
import com.ru.test.issuedriver.customer.ui.orders_list.OrdersListViewModel;
import com.ru.test.issuedriver.data.place;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.firestoreHelper;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.Observer;

public class mapsUtils {
    // показывать маркер в центре экрана
    private static boolean isImgLocationPinUpOn = false;

    private static final String TAG = "myLogs";
    private static CustomerV2Activity mapActivity;
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

    public static void Init(CustomerV2Activity activity, MapView mMapView, MapViewModel _mapViewModel, ImageView imgLocationPinUp){

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

                placesUtils.Init(mapActivity, googleMap, true);

                googleMap.setOnMarkerClickListener(mapActivity);
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
                imHere.init(mapActivity);

                observe2performers();

//                View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
//                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
//                // position on right bottom
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
//                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//                rlp.setMargins(0, 180, 180, 0);


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


    private static void observe2performers() {
        mapViewModel.getUsers().observe(mapActivity, new Observer<List<user>>() {
            @Override
            public void onChanged(List<user> users) {
//                googleMap.clear();
                for (user item : users) {
                    if (item.position == null)
                        continue;

                    setUserMarker(item);
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

    // Визуализация маркеров машин
    private static void setUserMarker(user item) {
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

                if (item.position != null) {
                    animateMarker(item, markerMap.get(item.email).marker,
                            new LatLng(item.position.getLatitude(), item.position.getLongitude()),
                            false,
                            mapViewModel.isOrderInActiveState(item.email), true); // order - активный
                }
            }
        }
        else {
            // создать новый маркер
            setPerformerPosition(item);
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
            if(zoom != 0f)
                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));

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
            carId = item.is_busy() ? R.drawable.car_red2 : R.drawable.car_yellow1;
            size = 80;
        } else if(zoomLevel >= dotZoomLevel){
            carId = R.drawable.dot;
            size = 25;
        } else
            return null;

        return getBitmapDescriptor(carId, size, size);
    }

    private static void setPerformerPosition(user item) {
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
            float bearing = getBearing(marker.getPosition(), toPosition);
            marker.setRotation(bearing);
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


    public static boolean onMarkerClick(Marker marker, place currentPlace) {
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
                if(currentPlace != null)
                    intent.putExtra("place", currentPlace);

                intent.putExtra("performer_car_number", markerMap.get(item)._user.autonumber);
                mapActivity.startActivity(intent);
                return false;
            }
        }
        return false;
    }

    private static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
}
