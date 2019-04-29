package com.alberoneramos.workout.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.adapter.WorkoutPlanAdapter;
import com.alberoneramos.workout.models.EmptyRecyclerView;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.alberoneramos.workout.server.FirebaseServer;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WorkoutListFragment extends Fragment {

    public WorkoutListFragment() {
    }

    @BindView(R.id.list)
    EmptyRecyclerView workoutList;
    @BindView(R.id.empty)
    TextView emptyText;
    @BindView(R.id.editText)
    EditText recyclerFilter;
    List<WorkoutPlan> workouts;
    WorkoutPlanAdapter workoutPlanAdapter;
    ChildEventListener dataListener;
    DatabaseReference ref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout_list, container, false);
        ButterKnife.bind(this,view);
        recyclerFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        workouts = new ArrayList<>();
        workoutPlanAdapter = new WorkoutPlanAdapter(getContext(), workouts);
        workoutList.setAdapter(workoutPlanAdapter);
        workoutList.setEmptyView(emptyText);
        workoutList.setLayoutManager(new LinearLayoutManager(getContext()));
        FirebaseServer.fetchDataFromFirebase(ref, dataListener, workoutPlanAdapter, workouts);
    }

    void filter(String text) {
        List<WorkoutPlan> temp = new ArrayList<>();
        for (WorkoutPlan d : workouts)
            if (d.getName().toLowerCase().contains(text.toLowerCase())) temp.add(d);
        workoutPlanAdapter.updateList(temp);
    }

    @Override
    public void onPause() {
        super.onPause();
        ref.removeEventListener(dataListener);
    }

}
