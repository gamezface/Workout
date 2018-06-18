package com.alberoneramos.workout.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Exercise implements Parcelable{
    public static final int CHEST = 0;
    public static final int ABDOMINALS = 1;
    public static final int CALVES = 2;
    public static final int LATS = 3;
    public static final int MIDDLEBACK = 4;
    public static final int NECK = 5;
    public static final int QUADRICEPS = 6;
    public static final int TRICEPS = 7;
    public static final int BICEPS = 8;

    private int targetMuscle;
    private int weight;
    private int repetitions;
    private int sets;
    private String exerciseName;


    public Exercise(int targetMuscle, String exerciseName,int sets, int repetitions, int weight) {
        this.targetMuscle = targetMuscle;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.repetitions = repetitions;
        this.sets = sets;
    }

    public Exercise(){}

    public Exercise(Parcel exercise){
        this.exerciseName = exercise.readString();
        this.targetMuscle = exercise.readInt();
        this.weight = exercise.readInt();
        this.repetitions = exercise.readInt();
        this.sets = exercise.readInt();
    }

    public Exercise(Exercise exercise){
        this(exercise.getExerciseType(),exercise.getExerciseName(),exercise.getWeight(),exercise.getRepetitions(),exercise.getSets());
    }

    public int getTargetMuscle() {
        return targetMuscle;
    }

    public void setTargetMuscle(int targetMuscle) {
        this.targetMuscle = targetMuscle;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getStringTargetMuscle(){
        switch(this.targetMuscle) {
            case ABDOMINALS:
                return "Abdominals";
            case CALVES:
                return "Calves";
            case LATS:
                return "Lats";
            case MIDDLEBACK:
                return "Middleback";
            case NECK:
                return "Neck";
            case QUADRICEPS:
                return "Quadriceps";
            case TRICEPS:
                return "Triceps";
            case BICEPS:
                return "Biceps";
            default:
                return "Chest";
        }
    }

    public int getRepetitions() {
        return repetitions;
    }

    public void setRepetitions(int repetitions) {
        this.repetitions = repetitions;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getExerciseType() {
        return targetMuscle;
    }

    public void setExerciseType(int targetMuscle) {
        this.targetMuscle = targetMuscle;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.exerciseName);
        dest.writeInt(this.targetMuscle);
        dest.writeInt(this.weight);
        dest.writeInt(this.repetitions);
        dest.writeInt(this.sets);
    }

    public static final Parcelable.Creator<Exercise> CREATOR = new Parcelable.Creator<Exercise>() {
        public Exercise createFromParcel(Parcel in) {
            return new Exercise(in);
        }

        public Exercise[] newArray(int size) {
            return new Exercise[size];
        }
    };

    @Override
    public String toString() {
        return exerciseName;
    }
}
