package com.alberoneramos.workout.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.fragments.QRScannerFragment;
import com.alberoneramos.workout.fragments.WorkoutListFragment;
import com.alberoneramos.workout.fragments.CalendarFragment;

public class HomescreenActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        loadFragment(new WorkoutListFragment());
                        return true;
                    case R.id.navigation_calendar:
                        loadFragment(new CalendarFragment());
                        return true;
                    case R.id.navigation_qr_scanner:
                        loadFragment(new QRScannerFragment());
                        return true;
                }
                return false;
            };



    public void addWorkout(View view){
        Intent intent = new Intent(HomescreenActivity.this, com.alberoneramos.workout.activities.AddWorkoutActivity.class);
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
