package com.example.henrique.workout.interfaces;

import com.example.henrique.workout.models.Exercise;

public interface IAddExercise {
    void onExerciseAdd(Exercise exercise,boolean editMode, int position);
}
