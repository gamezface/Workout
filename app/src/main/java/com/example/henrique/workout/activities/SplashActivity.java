package com.example.henrique.workout.activities;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.henrique.workout.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.splash);
        Handler handler = new Handler();
        handler.postDelayed(() -> showHomescreen(), 2000);
    }

    private void showHomescreen() {
        Intent intent;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            intent = new Intent(SplashActivity.this, HomescreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent = new Intent(SplashActivity.this,LogInActivity.class);
            Log.d("isLoggedIn", "onAuthStateChanged:signed_out");
        }
        startActivity(intent);
        finish();
    }
}
