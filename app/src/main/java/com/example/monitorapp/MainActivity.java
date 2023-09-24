package com.example.monitorapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.monitorapp.databinding.ActivityMainBinding;
import com.example.monitorapp.userInterfaces.adminDashboards;
import com.example.monitorapp.userInterfaces.ui.dashboard.DashboardFragment;
import com.example.monitorapp.userSessions.userSession;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    private String TAG = "mysql-main";
    private ActivityMainBinding binding;

    private Connection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_errorAlert)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //NavigationUI.setupWithNavController(binding.navView, navController);


    }
    /**
     * function: 登录
     * this function doesn't require a onClick() 'cause it is setup already in the .xml
     * */
    public void login(View v){
        EditText inputUsername = findViewById(R.id.editTextText5);
        EditText inputPassword =  findViewById(R.id.editTextTextPassword3);

        Button btnGet = (Button) findViewById(R.id.button5);
        //EditText etid = (EditText) findViewById(R.id.editTextText);


        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            userSession userSession = new userSession();
            try {
                //boolean test = userSession.select(inputUsername.getText().toString());
                int loginStatus = userSession.determineLoginStatus(inputUsername.getText().toString(), inputPassword.getText().toString());

                //Log.d(TAG, " succeeded from main");

                if( loginStatus == 1){
                    Log.d(TAG, "Login good");
                    passUsername(inputUsername.getText().toString());
                    startMainUI();

                }else{
                    Log.e(TAG, "bad login");
                }
                handler.sendEmptyMessage(loginStatus);
            } catch (Exception e) {
                Log.e(TAG, "test failed from main" + e.getMessage());
            }
        }).start();


    }

    @SuppressLint("handlerLeak")
    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if (msg.what < 0){
                Toast.makeText(getApplicationContext(), "系统错误 登录失败", Toast.LENGTH_LONG).show();
            } else if (msg.what == 1) {
                Toast.makeText(getApplicationContext(), "登录成功", Toast.LENGTH_LONG).show();
            } else if (msg.what == 2){
                Toast.makeText(getApplicationContext(), "密码错误", Toast.LENGTH_LONG).show();
            } else if (msg.what == 3){
                Toast.makeText(getApplicationContext(), "账号不存在", Toast.LENGTH_LONG).show();
            }
        }
    };
    public void startRegister(View v){
        startActivity(new Intent(getApplicationContext(), register.class));
    }
    public void startMainUI(){
        Intent intent = new Intent(this, adminDashboards.class);
        startActivity(intent);
        finish();
    }
    public void passUsername(String username){
        DashboardFragment dashboardFragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putString("username", username);
        dashboardFragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main, dashboardFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}