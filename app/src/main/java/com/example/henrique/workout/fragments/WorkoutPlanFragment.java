package com.example.henrique.workout.fragments;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.example.henrique.workout.R;
import com.example.henrique.workout.adapters.ExerciseListAdapter;
import com.example.henrique.workout.adapters.WorkoutPlanAdapter;
import com.example.henrique.workout.models.Exercise;
import com.example.henrique.workout.models.WorkoutPlan;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkoutListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkoutListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkoutPlanFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public WorkoutPlanFragment() {
        // Required empty public constructor
    }
    RecyclerView exerciseList;
    TextView title;
    ImageButton backButton;
    ImageButton moreButton;
    Toolbar toolbar;
    ToggleButton workoutToggleButton;
    // TODO: Rename and change types and number of parameters
    public static WorkoutListFragment newInstance() {
        WorkoutListFragment fragment = new WorkoutListFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workoutplan_list, container, false);
        Bundle bundle = getArguments();
        WorkoutPlan workoutPlan = bundle.getParcelable("WORKOUT_PLAN");
        initVariables(rootView);
        recyclerViewSetup(this.exerciseList,workoutPlan);
        moreButton.setOnClickListener( l -> showPopup(l));
        backButton.setOnClickListener(l -> getActivity().getSupportFragmentManager().popBackStack());
        workoutToggleButton.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked)
                toggleToolbarColors(getResources().getColor(R.color.colorPrimary),getResources().getColor(android.R.color.white),
                                    getResources().getColor(android.R.color.white));
            else
                toggleToolbarColors(getResources().getColor(android.R.color.white), getResources().getColor(R.color.colorPrimary),
                                    getResources().getColor(android.R.color.black));
        }));
        title.setText(workoutPlan.getName());
        return rootView;
    }



    public void recyclerViewSetup(RecyclerView exerciseList,WorkoutPlan workoutPlan){
        exerciseList.setAdapter(new ExerciseListAdapter(getContext(),workoutPlan.getExercises()));
        exerciseList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions_workoutplan, popup.getMenu());
        popup.show();
    }

    public void toggleToolbarColors(int toolBarColor, int imageButtonColor, int titleColor){
        this.toolbar.setBackgroundColor(toolBarColor);
        this.backButton.setColorFilter(imageButtonColor);
        this.title.setTextColor(titleColor);
        this.moreButton.setColorFilter(imageButtonColor);
    }

    public void initVariables(View rootView){
        this.exerciseList = rootView.findViewById(R.id.exerciseList);
        this.backButton = rootView.findViewById(R.id.back);
        this.toolbar = rootView.findViewById(R.id.toolbar);
        this.workoutToggleButton = rootView.findViewById(R.id.toggleWorkout);
        this.title = rootView.findViewById(R.id.workoutTitle);
        this.moreButton = rootView.findViewById(R.id.more);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void goBack(View v){
        getFragmentManager().popBackStack();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
