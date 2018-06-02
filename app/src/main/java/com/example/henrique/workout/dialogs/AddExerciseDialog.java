package com.example.henrique.workout.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.henrique.workout.R;
import com.example.henrique.workout.interfaces.IAddExercise;
import com.example.henrique.workout.models.Exercise;

public class AddExerciseDialog extends DialogFragment {
    Spinner bodyPartSpinner;
    AutoCompleteTextView exerciseName;
    private IAddExercise mCallback;
    EditText setsText;
    EditText repsText;
    EditText weightText;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (IAddExercise) activity;
        }
        catch (ClassCastException e) {
            Log.d("MyDialog", "Activity doesn't implement the ISelectedData interface");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_exercise, null);
        initViews(view);
        builder.setView(view)
                .setPositiveButton(R.string.exercise_dialog_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        boolean editMode;
                        Bundle arguments = getArguments();
                        int position;
                        if(arguments != null && arguments.containsKey("EDIT_MODE")) {
                            editMode = arguments.getBoolean("EDIT_MODE");
                            position = arguments.getInt("POSITION");
                        } else{
                            editMode = false;
                            position = 0;
                        }
                        mCallback.onExerciseAdd( new Exercise(bodyPartSpinner.getSelectedItemPosition(),
                                exerciseName.getText().toString(),
                                Integer.parseInt(setsText.getText().toString()),
                                Integer.parseInt(repsText.getText().toString()),
                                Integer.parseInt(weightText.getText().toString())),editMode,position);
                        AddExerciseDialog.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddExerciseDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void initViews(View view){
        bodyPartSpinner = view.findViewById(R.id.body_part_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.body_parts));
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodyPartSpinner.setAdapter(spinnerArrayAdapter);
        exerciseName = view.findViewById(R.id.exercise_name);
        setsText = view.findViewById(R.id.sets);
        repsText = view.findViewById(R.id.reps);
        weightText = view.findViewById(R.id.weight);
        if(getArguments() != null && getArguments().getBoolean("EDIT_MODE")){
            Exercise exercise = getArguments().getParcelable("EXERCISE");
            exerciseName.setText(exercise.getExerciseName());
            setsText.setText(String.valueOf(exercise.getSets()));
            repsText.setText(String.valueOf(exercise.getRepetitions()));
            weightText.setText(String.valueOf(exercise.getWeight()));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}
