package com.alberoneramos.workout.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.view.fragment.QRScannerFragment;
import com.alberoneramos.workout.view.fragment.WorkoutListFragment;
import com.alberoneramos.workout.view.fragment.CalendarFragment;

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


    public void addWorkout(View view) {
        Intent intent = new Intent(HomescreenActivity.this, com.alberoneramos.workout.view.activity.AddWorkoutActivity.class);
        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

}
