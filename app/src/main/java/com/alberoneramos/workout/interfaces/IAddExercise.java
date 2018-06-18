package com.alberoneramos.workout.interfaces;

import com.alberoneramos.workout.models.Exercise;

public interface IAddExercise {
    void onExerciseAdd(Exercise exercise,boolean editMode, int position);
}
