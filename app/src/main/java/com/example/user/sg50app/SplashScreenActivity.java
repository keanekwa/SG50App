package com.example.user.sg50app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.parse.ParseUser;


public class SplashScreenActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if (ParseUser.getCurrentUser()==null){
                        Intent intent = new Intent (SplashScreenActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    Intent i = new Intent(SplashScreenActivity.this,MainActivity.class);
                    startActivity(i);
                }
            }
        };
        timerThread.start();

    }
}
