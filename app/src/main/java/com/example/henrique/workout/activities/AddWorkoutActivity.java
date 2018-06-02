package com.example.henrique.workout.activities;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dpro.widgets.WeekdaysPicker;
import com.example.henrique.workout.R;
import com.example.henrique.workout.adapters.ExerciseListAddAdapter;
import com.example.henrique.workout.dialogs.AddExerciseDialog;
import com.example.henrique.workout.interfaces.IAddExercise;
import com.example.henrique.workout.models.EmptyRecyclerView;
import com.example.henrique.workout.models.Exercise;
import com.example.henrique.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.myaghobi.inlinecolorpicker.InLineColorPicker;

import java.util.ArrayList;
import java.util.List;

public class AddWorkoutActivity extends AppCompatActivity implements IAddExercise{

    InLineColorPicker colorPicker;
    EditText workoutName;
    WeekdaysPicker weekdayPicker;
    Button addExercise;
    WorkoutPlan workoutPlan;
    EmptyRecyclerView exerciseList;
    ExerciseListAddAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        colorPicker = findViewById(R.id.inline_picker);
        workoutPlan = new WorkoutPlan();
        workoutName = findViewById(R.id.workout_name);
        colorPicker.setOnColorChangeListener((color,hex) -> colorPicker.setBorderColor(Color.parseColor("#60"+hex.substring(2))));
        exerciseList = findViewById(R.id.list);
        addExercise = findViewById(R.id.add_exercise_button);
        adapter = new ExerciseListAddAdapter(this,workoutPlan.getExercises());
        recyclerViewSetup();
        addExercise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AddExerciseDialog addExerciseDialog = new AddExerciseDialog();
                addExerciseDialog.show(getFragmentManager(),"AddExercise");

            }

        });
        weekdayPicker = findViewById(R.id.weekdays);
        weekdayPicker.setSelectedDays(new ArrayList<Integer>());

    }

    public void recyclerViewSetup(){
        exerciseList.setAdapter(this.adapter);
        exerciseList.setEmptyView(findViewById(R.id.empty));
        exerciseList.setLayoutManager(new LinearLayoutManager(this));
    }

    public void goBack(View v){
        finish();
    }

    @Override
    public void onExerciseAdd(Exercise exercise,boolean editMode,int position) {
        if(editMode){
            workoutPlan.editExercise(position,exercise);
            adapter.notifyItemChanged(position);
        }
        else{
            workoutPlan.addExercise(exercise);
            adapter.notifyItemInserted(workoutPlan.getExercises().size()-1);
        }
    }

    public void createWorkout(View v){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference("workouts").push().getKey();
        database
                .getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/workouts")
                .child(key)
                .setValue(new WorkoutPlan(workoutName.getText().toString(),
                                          adapter.getItems(),colorPicker.getSelectedColorInt(),weekdayPicker.getSelectedDays(),key));
        finish();
    }
}
