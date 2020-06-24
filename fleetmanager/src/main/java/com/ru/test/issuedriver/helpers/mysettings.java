package com.ru.test.issuedriver.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.ru.test.issuedriver.data.Token;
import com.ru.test.issuedriver.data.user;

import androidx.appcompat.app.AppCompatActivity;

public class mysettings {

    // именя файла настроек
    public static final String APP_PREFERENCES = "mysettings";


    public static final String APP_PREFERENCES_EMAIL = "email";
    public static final String APP_PREFERENCES_PASS = "pass";

    public static  final String APP_PREFERENCES_USER = "user";
    public static  final String APP_PREFERENCES_TOKEN = "token";

    public static final String APP_PREFERENCES_POSITION = "position";

    public static final String APP_PREFERENCES_ORDERS = "orders";

    public static final String APP_PREFERENCES_TOKENSAVED = "tokensaved";

    private static SharedPreferences instance;
    public static SharedPreferences Init(AppCompatActivity activity) {
        if (instance == null)
            instance = activity.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        return instance;
    }

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

    public static void SetEmail(String email) {
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_EMAIL, email);
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

    public static void SetUser(user currnt) {
        currnt.fcmToken = GetFCMToken().getToken();
        Gson gson = new Gson();
        String json = gson.toJson(currnt);
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_USER, json);
        editor.apply();
    }

    public static user GetUser()
    {
        if(instance.contains(APP_PREFERENCES_USER)){
            String json = instance.getString(APP_PREFERENCES_USER, "");
            if(json.length() == 0)
                return null;
            Gson gson = new Gson();
            try {
                user current = gson.fromJson(json, user.class);
                return current;
            }
            catch (Exception ex){
                Log.e("myLogs", "Error settings - user convert");
                return null;
            }
        }
        return null;
    }



    public static void SetFCMToken(Token token) {
        Gson gson = new Gson();
        String json = gson.toJson(token);
        SharedPreferences.Editor editor = instance.edit();
        editor.putString(mysettings.APP_PREFERENCES_TOKEN, json);
        editor.putBoolean(mysettings.APP_PREFERENCES_TOKENSAVED, false);
        editor.apply();
    }
    public static Token GetFCMToken() {
        if(instance.contains(APP_PREFERENCES_TOKEN)){
            String json = instance.getString(APP_PREFERENCES_TOKEN, "");
            if(json.length() == 0)
                return null;
            Gson gson = new Gson();
            try {
                Token current = gson.fromJson(json, Token.class);
                return current;
            }
            catch (Exception ex){
                Log.e("myLogs", "Error settings - user convert");
                return null;
            }
        }
        return null;
    }

    public static boolean GetTokenSaved()
    {
        if(instance.contains(APP_PREFERENCES_TOKENSAVED))
            return instance.getBoolean(APP_PREFERENCES_TOKENSAVED, false);
        return false;
    }
}