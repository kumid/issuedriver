package com.ru.test.issuedriver.customer.ui.map;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.CustomerActivity;
import com.ru.test.issuedriver.customer.ui.order.OrderActivity;
import com.ru.test.issuedriver.customer.ui.registration.RegistrationViewModel;
import com.ru.test.issuedriver.data.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener {

    private class markerPair{
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
    private RegistrationViewModel registrationViewModel;

    Map<String, markerPair> markerMap = new HashMap<>();

    private MapView mMapView;
    private GoogleMap googleMap;
//    private MarkerOptions markerIm, markerBus ;
//    private Marker ImMarker, BusMarker ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapViewModel =
                ViewModelProviders.of(this).get(MapViewModel.class);
        registrationViewModel =
                ViewModelProviders.of(CustomerActivity.getInstance()).get(RegistrationViewModel.class);

        View root = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) root.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getContext());

        }catch (Exception e) {
            e.printStackTrace();
        }

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setOnMarkerClickListener(MapFragment.this);
                //setMyPosition(false);
                //setBusPosition("Автобус", Double.parseDouble("55.412433"), Double.parseDouble("42.525937"));
                Log.e("MapsLog", "onMapReady");
//                mt = new MyTask();
//                mt.execute();
                observe2performers();
            }
        });

        return root;
    }

    private void observe2performers() {
        mapViewModel.getUsers().observe(getViewLifecycleOwner(), new Observer<List<user>>() {
            @Override
            public void onChanged(List<user> users) {
//                googleMap.clear();
                for (user item : users) {
                    if(markerMap.containsKey(item.email)) {

                        if(markerMap.get(item.email)._user.is_busy != item.is_busy){
                            markerMap.get(item.email).marker.setVisible(false);

                            int car = item.is_busy ? R.drawable.car_red : R.drawable.car_yellow;
                            markerMap.get(item.email).markerOption.icon(BitmapDescriptorFactory
                                    .fromResource(car)); //).defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

                            MarkerOptions markerBus = new MarkerOptions().position(
                                    new LatLng(item.position.getLatitude(), item.position.getLongitude()))
                                    .title(item.fio);

                            Marker BusMarkerOK = googleMap.addMarker(markerMap.get(item.email).markerOption);
                            markerMap.get(item.email).marker = BusMarkerOK;
                            markerMap.get(item.email)._user = item;
                        } else {
                            animateMarker(item, markerMap.get(item.email).marker,
                                    new LatLng(item.position.getLatitude(), item.position.getLongitude()),
                                    false);
                        }
                    } else {
                        setPerformerPosition(item);
                    }
                }
            }
        });
    }

    private void setPerformerPosition(user item) {
        if (googleMap == null) {
            return;
        }
        MarkerOptions markerBus = new MarkerOptions().position(
                new LatLng(item.position.getLatitude(), item.position.getLongitude()))
                .title(item.fio);

        int car = item.is_busy ? R.drawable.car_red : R.drawable.car_yellow;
        markerBus.icon(BitmapDescriptorFactory
                .fromResource(car)); //).defaultMarker(BitmapDescriptorFactory.HUE_ROSE));

        // adding marker
        Marker BusMarker = googleMap.addMarker(markerBus);

        markerMap.put(item.email, new markerPair(markerBus, BusMarker, item));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(item.position.getLatitude(), item.position.getLongitude())).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        Log.e("MapsLog", "googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));");


    }

    public void animateMarker(user item, final Marker marker, final LatLng toPosition, final boolean hideMarker) {
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

//                CameraPosition cameraPosition = new CameraPosition.Builder()
//                        .target(toPosition).build();
//                googleMap.animateCamera(CameraUpdateFactory
//                        .newCameraPosition(cameraPosition));

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
        for (String item: markerMap.keySet()) {
            if(markerMap.get(item).marker.equals(marker)){
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                intent.putExtra("fio", registrationViewModel.currentUser.getValue().fio);
                intent.putExtra("customer", registrationViewModel.currentUser.getValue().email);
                intent.putExtra("performer", item);
                startActivity(intent);
                return false;
            }
        }
        return false;
    }
}
