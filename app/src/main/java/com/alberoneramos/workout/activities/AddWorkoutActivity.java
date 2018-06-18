package com.alberoneramos.workout.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.dpro.widgets.WeekdaysPicker;
import com.alberoneramos.workout.R;
import com.alberoneramos.workout.adapters.ExerciseListAddAdapter;
import com.alberoneramos.workout.dialogs.AddExerciseDialog;
import com.alberoneramos.workout.interfaces.IAddExercise;
import com.alberoneramos.workout.models.EmptyRecyclerView;
import com.alberoneramos.workout.models.Exercise;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.myaghobi.inlinecolorpicker.InLineColorPicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddWorkoutActivity extends AppCompatActivity implements IAddExercise{

    InLineColorPicker colorPicker;
    EditText workoutName;
    WeekdaysPicker weekdayPicker;
    Button addExercise;
    WorkoutPlan workoutPlan;
    EmptyRecyclerView exerciseList;
    TextView emptyText;
    ExerciseListAddAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_workout);
        initVariables();
        recyclerViewSetup();
    }

    public void initVariables(){
        Bundle parameters = getIntent().getExtras();
        this.colorPicker = findViewById(R.id.inline_picker);
        if(parameters != null && parameters.getBoolean("EDIT_MODE")) this.workoutPlan =  parameters.getParcelable("WORKOUT_DATA");
        else
            this.workoutPlan = new WorkoutPlan();
        this.workoutName = findViewById(R.id.workout_name);
        this.colorPicker.setOnColorChangeListener((color,hex) -> colorPicker.setBorderColor(Color.parseColor("#60"+hex.substring(2))) );
        this.exerciseList = findViewById(R.id.list);
        this.addExercise = findViewById(R.id.add_exercise_button);
        this.addExercise.setOnClickListener(view -> {
            AddExerciseDialog addExerciseDialog = new AddExerciseDialog();
            addExerciseDialog.show(getFragmentManager(),"AddExercise");

        });
        weekdayPicker = findViewById(R.id.weekdays);
        this.colorPicker.setDefaultColor(workoutPlan.getColorId());
        if(parameters != null && parameters.getBoolean("EDIT_MODE")) colorPicker.setBorderColor(Color.parseColor("#60"+colorPicker.getSelectedColorHex().substring(2)));
        weekdayPicker.setSelectedDays(workoutPlan.getWeekdays());
        workoutName.setText(workoutPlan.getName());
        this.adapter = new ExerciseListAddAdapter(this,workoutPlan.getExercises());
    }


    public void recyclerViewSetup(){
        emptyText = findViewById(R.id.empty);
        exerciseList.setAdapter(this.adapter);
        exerciseList.setEmptyView(emptyText);
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

    public void saveWorkout(View v){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/workouts");
        Bundle parameters = getIntent().getExtras();
        if( parameters != null && parameters.getBoolean("EDIT_MODE")){
            Map<String,Object> workoutMap = new HashMap<String,Object>();
            workoutMap.put("exercises",adapter.getItems());
            workoutMap.put("weekdays",weekdayPicker.getSelectedDays());
            workoutMap.put("name",workoutName.getText().toString());
            workoutMap.put("colorId",colorPicker.getSelectedColorInt());
            ref.child(workoutPlan.getWorkoutPlanId()).updateChildren(workoutMap);
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_OK,returnIntent);
        }
        else{
            String key = database.getReference("workouts").push().getKey();
            ref.child(key).setValue(new WorkoutPlan(workoutName.getText().toString(),
                                                    adapter.getItems(),
                                                    colorPicker.getSelectedColorInt(),
                                                    weekdayPicker.getSelectedDays(),
                                                    key));
        }

        finish();
    }
}
