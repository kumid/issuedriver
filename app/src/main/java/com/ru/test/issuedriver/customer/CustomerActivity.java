package com.ru.test.issuedriver.customer;

import android.os.Bundle;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.customer.ui.dashboard.HistoryViewModel;
import com.ru.test.issuedriver.customer.ui.notifications.NotificationsViewModel;
import com.ru.test.issuedriver.customer.ui.registration.RegistrationViewModel;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.googleAuthManager;

import java.util.EventListener;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

        setContentView(R.layout.activity_customer);
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
}


/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actions, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;

            case R.id.action_exit:
                googleAuthManager.signOut(CustomerActivity.this);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
*/
