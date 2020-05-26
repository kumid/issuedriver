package com.ru.test.issuedriver.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

public class mysettings {

    // именя файла настроек
    public static final String APP_PREFERENCES = "mysettings";

    public static final String APP_PREFERENCES_EMAIL = "email";
    public static final String APP_PREFERENCES_PASS = "pass";

    public static final String APP_PREFERENCES_POSITION = "position";

    public static final String APP_PREFERENCES_ORDERS = "orders";


    private static SharedPreferences instance;
//    public static SharedPreferences Init(AppCompatActivity activity) {
//        if (instance == null)
//            instance = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
//
//        return instance;
//    }
    public static SharedPreferences Init(Context ctx) {
        if(instance == null)
            instance = PreferenceManager.getDefaultSharedPreferences(ctx);
        return instance;
    }

    public static String GetEmail()
    {
        if(instance.contains(APP_PREFERENCES_EMAIL))
           return instance.getString(APP_PREFERENCES_EMAIL, "");
        return "";
    }

    public static String GetPass()
    {
        if(instance.contains(APP_PREFERENCES_PASS))
            return instance.getString(APP_PREFERENCES_PASS, "");
        return "";
    }

    public static String GetPosition()
    {
        if(instance.contains(APP_PREFERENCES_POSITION))
            return instance.getString(APP_PREFERENCES_POSITION, "");
        return "";
    }

    public static String GetOrders()
    {
        if(instance != null
           && instance.contains(APP_PREFERENCES_ORDERS))
            return instance.getString(APP_PREFERENCES_ORDERS, "");
        return "";
    }

    public static void SetEmail(String phone) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_EMAIL, phone);
        editor.apply();
    }

    public static void SetPass(String pass) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_PASS, pass);
        editor.apply();
    }

    public static void SetPosition(String pos) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_POSITION, pos);
        editor.apply();
    }
    public static void SetOrders(String orders) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_ORDERS, orders);
        editor.apply();
    }



    public static final String APP_PREFERENCES_ACTIVITY = "activity";


     public static SharedPreferences Init(AppCompatActivity activity, String activity4start) {

        if (instance == null)
            instance = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        SetActivities(activity4start);

        return instance;
    }

    public static String GetActivities()
    {
        if(instance != null
                && instance.contains(APP_PREFERENCES_ACTIVITY))
            return instance.getString(APP_PREFERENCES_ACTIVITY, "");
        return "";
    }

    public static void SetActivities(String activity) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_ACTIVITY, activity);
        editor.apply();
    }
}
