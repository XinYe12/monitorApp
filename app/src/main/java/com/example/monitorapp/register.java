package com.example.monitorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.monitorapp.userSessions.userSession;

import com.example.monitorapp.entity.User;

public class register extends AppCompatActivity {
    private static final String TAG = "mysql-register";
    EditText usernameInput = null;
    EditText password = null;
    EditText  company = null;
    EditText  address = null;
    EditText  phone = null;
    EditText  email = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        usernameInput = findViewById(R.id.editTextText3);
        password = findViewById(R.id.editTextText4);
        company = findViewById(R.id.editTextText6);
        address = findViewById(R.id.editTextTextEmailAddress);
        phone = findViewById(R.id.editTextPhone);
        Log.d(TAG, "registration started");

    }
    public void register(View v){
        String username = usernameInput.getText().toString();
        String passString = password.getText().toString();
        String companyName = company.getText().toString();
        String adrs = address.getText().toString();
        String phoneNum = phone.getText().toString();
        String userType = "user";//unless otherwise stated, there will only be user registered
        User user = new User();
        user.setUsername(username);
        user.setCompany(companyName);
        user.setPassword(passString);
        user.setAddress(adrs);
        user.setTelephone(phoneNum);
        user.setUserType(userType);
        new Thread(){
            @Override
            public void run(){
                int msg = 0;
                userSession userSession = new userSession();
                try {
                    boolean userExist = userSession.select(user.getUsername());
                    if (userExist){
                        boolean flag = userSession.register(user);
                        Log.e(TAG, "user exists");
                        msg = 1;
                    }else{
                        msg = 2;
                        Log.d(TAG, "valid username");
                    }
                    hand.sendEmptyMessage(msg);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        }.start();
    }
    @SuppressLint("HandlerLeak")
    final Handler hand = new Handler()
    {
        public void handleMessage(Message msg) {
            if(msg.what == 0) {
                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_LONG).show();
            } else if(msg.what == 1) {
                Toast.makeText(getApplicationContext(),"该账号已经存在，请换一个账号",Toast.LENGTH_LONG).show();
            } else if(msg.what == 2) {
                Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                //将想要传递的数据用putExtra封装在intent中
                intent.putExtra("a","注册");
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        }
    };

}