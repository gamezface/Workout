package com.alberoneramos.workout.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.adapters.ExerciseListAdapter;
import com.alberoneramos.workout.models.Exercise;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;

public class WorkoutPlanShare extends AppCompatActivity {

    RecyclerView exerciseList;
    TextView title;
    ImageButton backButton;
    DatabaseReference ref;
    ImageButton addWorkoutButton;
    WorkoutPlan workoutPlan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_plan_share);
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnFailureListener(e -> {
            Log.e("LinkError",e.getMessage());
        }).addOnSuccessListener (e ->{
            Uri link = e.getLink();
            ref = FirebaseDatabase.getInstance().getReference(link.getQueryParameter("USER_ID"))
                    .child("/workouts")
                    .child(link.getQueryParameter("WORKOUT_ID"));
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    workoutPlan = dataSnapshot.getValue(WorkoutPlan.class);
                    if(workoutPlan == null){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.workout_not_found), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        exerciseList = findViewById(R.id.exerciseList);
                        title.setText(workoutPlan.getName());
                        recyclerViewSetup(exerciseList,workoutPlan);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("ReadError",databaseError.getMessage());
                }
            });
        });

        initVariables();
        backButton.setOnClickListener((l) -> openMainActivity());
        addWorkoutButton.setOnClickListener((l) -> addWorkout());
    }

    public void openMainActivity(){
        Intent intent = new Intent(WorkoutPlanShare.this, HomescreenActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        startActivity(intent);
    }

    public void addWorkout(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String key = database.getReference("workouts").push().getKey();
        workoutPlan.setWorkoutPlanId(key);
        database
                .getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/workouts")
                .child(key)
                .setValue(workoutPlan);
        openMainActivity();
    }

    public void initVariables(){
        this.addWorkoutButton = findViewById(R.id.add_workout);
        this.title = findViewById(R.id.workoutTitle);
        this.backButton = findViewById(R.id.back);
    }

    public void recyclerViewSetup(RecyclerView exerciseList,WorkoutPlan workoutPlan){
        ExerciseListAdapter adapter = new ExerciseListAdapter(this,workoutPlan.getExercises()){
            @Override
            public void onBindViewHolder(SimpleViewHolder holder, int position) {
                Exercise item = getItems().get(position);
                holder.getSwipeLayout().setRightSwipeEnabled(false);
                holder.getExerciseName().setText(item.getExerciseName());
                holder.getExerciseDescription().setText(item.getSets() + " sets x " + item.getRepetitions() + " reps x " + item.getWeight() + " kg");
                holder.getTargetMuscle().setText(item.getStringTargetMuscle());
            }
        };
        exerciseList.setAdapter(adapter);
        exerciseList.setLayoutManager(new LinearLayoutManager(this));
    }
}
