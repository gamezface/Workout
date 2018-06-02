package com.example.henrique.workout.activities;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.example.henrique.workout.R;
import com.example.henrique.workout.fragments.WorkoutListFragment;

public class HomescreenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        loadFragment(new WorkoutListFragment());
                        return true;
                    case R.id.navigation_calendar:
                        return true;
                }
                return false;
            };



    public void addWorkout(View view){
        Intent intent = new Intent(HomescreenActivity.this, AddWorkoutActivity.class);
        overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_bottom);
        startActivity(intent);
    }

    private boolean loadFragment(Fragment fragment){
        if (fragment != null) {
            int commit = getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

}
