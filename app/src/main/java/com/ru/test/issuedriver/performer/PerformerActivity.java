package com.ru.test.issuedriver.performer;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.ru.test.issuedriver.MyActivity;
import com.ru.test.issuedriver.R;
import com.ru.test.issuedriver.data.user;
import com.ru.test.issuedriver.helpers.googleAuthManager;
import com.ru.test.issuedriver.performer.ui.registration.RegistrationViewModel;

import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class PerformerActivity extends MyActivity {

    RegistrationViewModel registrationViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performer);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_registration)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        registrationViewModel =
                ViewModelProviders.of(PerformerActivity.getInstance()).get(RegistrationViewModel.class);

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

}
