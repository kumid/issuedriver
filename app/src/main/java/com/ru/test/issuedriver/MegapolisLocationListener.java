package com.ru.test.issuedriver;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;


import java.util.List;

import androidx.annotation.RequiresApi;

public class MegapolisLocationListener implements LocationListener {

    public static Location imHere; // здесь будет всегда доступна самая последняя информация о местоположении пользователя.
    private static String preferer;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void SetUpLocationListener(Context context) // это нужно запустить в самом начале работы программы
    {
//        if (!MainPassangerActivity.checkPermission(context)) {
//            return;
//        }

        LocationManager mgr = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);

        for (String pov: mgr.getAllProviders()){

        }

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        List<String> providers = mgr.getProviders(criteria, true);
        if(providers == null || providers.size() == 0){
            Toast.makeText(context, "Невозможно открыть GPS сервис", Toast.LENGTH_SHORT).show();
            return;
        }
        preferer = providers.get(0);

        LocationListener locationListener = new MegapolisLocationListener();

        mgr.requestLocationUpdates(
                preferer,
                2000,
                10,
                locationListener); // здесь можно указать другие более подходящие вам параметры

    }

    @Override
    public void onLocationChanged(Location loc) {
        imHere = loc;
    }
    @Override
    public void onProviderDisabled(String provider) {}
    @Override
    public void onProviderEnabled(String provider) {}
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}
}