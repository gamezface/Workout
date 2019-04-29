package com.alberoneramos.workout.server;

import android.support.annotation.NonNull;
import android.util.Log;

import com.alberoneramos.workout.adapter.WorkoutPlanAdapter;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class FirebaseServer {
    public static void fetchDataFromFirebase(
            DatabaseReference ref, ChildEventListener dataListener,
            WorkoutPlanAdapter workoutPlanAdapter,
            List<WorkoutPlan> workouts) {
        ref = FirebaseDatabase.getInstance().getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/workouts");
        dataListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildKey) {
                int index = 0;
                if (previousChildKey != null)
                    index = getIndexForKey(previousChildKey, workouts) + 1;
                workouts.add(index, snapshot.getValue(WorkoutPlan.class));
                workoutPlanAdapter.notifyItemInserted(workouts.size());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, String previousChildKey) {
                int index = getIndexForKey(snapshot.getKey(), workouts);
                workouts.set(index, snapshot.getValue(WorkoutPlan.class));
                workoutPlanAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                int index = getIndexForKey(snapshot.getKey(), workouts);
                workouts.remove(index);
                workoutPlanAdapter.notifyItemRemoved(index);
                workoutPlanAdapter.notifyItemRangeChanged(0, workouts.size());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, String previousChildKey) {
                int oldIndex = getIndexForKey(snapshot.getKey(), workouts);
                workouts.remove(oldIndex);
                int newIndex = previousChildKey == null ? 0 : getIndexForKey(previousChildKey, workouts) + 1;
                workouts.add(newIndex, snapshot.getValue(WorkoutPlan.class));
                workoutPlanAdapter.notifyItemMoved(oldIndex, newIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseReadError", "The read failed: " + error.getCode());
            }
        };
        ref.addChildEventListener(dataListener);
    }

    private static int getIndexForKey(String key, List<WorkoutPlan> workouts) {
        int index = 0;
        for (WorkoutPlan workout : workouts) {
            if (workout.getWorkoutPlanId().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

}
