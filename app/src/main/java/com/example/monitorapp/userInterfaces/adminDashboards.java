package com.example.monitorapp.userInterfaces;

import android.os.Bundle;
import com.example.monitorapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.monitorapp.databinding.ActivityAdminDashboardsBinding;



public class adminDashboards extends AppCompatActivity {
    private ActivityAdminDashboardsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAdminDashboardsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_errorAlert)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_admin_dashboards);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


    }
}

