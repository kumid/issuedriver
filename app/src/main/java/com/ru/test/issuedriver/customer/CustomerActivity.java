package com.ru.test.issuedriver.customer;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.ui.dashboard.HistoryViewModel;
import com.ru.test.issuedriver.customer.ui.notifications.NotificationsViewModel;
import com.ru.test.issuedriver.customer.ui.registration.RegistrationViewModel;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.googleAuthManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class CustomerActivity extends MyActivity {

    RegistrationViewModel registrationViewModel;
    private static CustomerActivity instance;
    public static CustomerActivity getInstance() {
        return instance;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        googleAuthManager.init(this);
        googleAuthManager.signIn();

        setContentView(R.layout.activity_customer);

        setupNavigation();

        initViewModels();

        if(getIntent().hasExtra("object")){
            String obj = getIntent().getStringExtra("object");
            Gson gson = new Gson();
            user curr = gson.fromJson(obj, user.class);
            if(curr != null)
                registrationViewModel.currentUser.setValue(curr);
        } else {
            registrationViewModel.getUserFromServer(googleAuthManager.getEmail());
        }

        checkPermission(this);

    }

    private void setupNavigation() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_notifications, R.id.navigation_dashboard, R.id.navigation_registration)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(false);
        else
            Log.e("Error", "actionBar == null");

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                navController.navigate(item.getItemId());
                actionBar.setDisplayHomeAsUpEnabled(false);
                if(item.getItemId() == R.id.navigation_map)
                    actionBar.setTitle("Выберите автомобиль");
                return false;
            }
        });
    }

    private void initViewModels() {
        registrationViewModel =
                ViewModelProviders.of(CustomerActivity.getInstance()).get(RegistrationViewModel.class);

        NotificationsViewModel notificationsViewModel =
                ViewModelProviders.of(CustomerActivity.getInstance()).get(NotificationsViewModel.class);
        notificationsViewModel.initNotificationLoad(CustomerActivity.getInstance(), registrationViewModel.currentUser);

        HistoryViewModel historyViewModel =
                ViewModelProviders.of(CustomerActivity.getInstance()).get(HistoryViewModel.class);
        historyViewModel.initNotificationLoad(CustomerActivity.getInstance(), registrationViewModel.currentUser);
    }

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
}
