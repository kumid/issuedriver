package com.ru.test.issuedriver.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

public class mysettings {

    // именя файла настроек
    public static final String APP_PREFERENCES = "mysettings";

    public static final String APP_PREFERENCES_PHONE = "phone";
    public static final String APP_PREFERENCES_PASS = "pass";

    public static final String APP_PREFERENCES_POSITION = "position";

    public static final String APP_PREFERENCES_ORDERS = "orders";


    private static SharedPreferences instance;
    public static SharedPreferences Init(AppCompatActivity activity) {
        if (instance == null)
            instance = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        return instance;
    }

    public static String GetPhone()
    {
        if(instance.contains(APP_PREFERENCES_PHONE))
           return instance.getString(APP_PREFERENCES_PHONE, "");
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

    public static void SetPhone(String phone) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_PHONE, phone);
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
}
