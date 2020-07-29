package com.ru.test.issuedriver.helpers;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class utils {
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

    private static DecimalFormat df = new DecimalFormat("0.00");

    public static String getDoubleString(double input) {
        // DecimalFormat, default is RoundingMode.HALF_EVEN
        return df.format(input);      //1205.64

//        df.setRoundingMode(RoundingMode.DOWN);
//        System.out.println("salary : " + df.format(input));      //1205.63
//
//        df.setRoundingMode(RoundingMode.UP);
//        System.out.println("salary : " + df.format(input));      //1205.64

    }


}
