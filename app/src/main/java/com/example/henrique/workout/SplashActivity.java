package com.example.henrique.workout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showHomescreen();
            }
        }, 2000);
    }

    private void showHomescreen() {
        Intent intent = new Intent(
                SplashActivity.this,LogIn.class
        );
        startActivity(intent);
        finish();
    }
}
