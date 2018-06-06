package com.example.henrique.workout.fragments;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.henrique.workout.R;
import com.example.henrique.workout.adapters.WorkoutPlanAdapter;
import com.example.henrique.workout.models.Exercise;
import com.example.henrique.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
public class WorkoutListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    public WorkoutListFragment() {
        // Required empty public constructor
    }
    RecyclerView workoutList;
    TextView emptyText;
    List<WorkoutPlan> workouts;
    EditText recyclerFilter;
    WorkoutPlanAdapter workoutPlanAdapter;
    ChildEventListener dataListener;
    DatabaseReference ref;

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
        View rootView = inflater.inflate(R.layout.fragment_workout_list, container, false);
        bindVariablesToLayout(rootView);
        recyclerFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
        return rootView;
    }

    public void bindVariablesToLayout(View rootView){
        workoutList = rootView.findViewById(R.id.list);
        emptyText = rootView.findViewById(android.R.id.empty);
        recyclerFilter = rootView.findViewById(R.id.editText);
    }

    public void fetchDataFromFirebase(){
        ref = FirebaseDatabase.getInstance().getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/workouts");
        dataListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildKey) {
                int index = 0;
                if (previousChildKey != null)
                    index = getIndexForKey(previousChildKey) + 1;
                workouts.add(index, snapshot.getValue(WorkoutPlan.class));
                workoutPlanAdapter.notifyItemInserted(workouts.size());
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildKey) {
                int index = getIndexForKey(snapshot.getKey());
                workouts.set(index, snapshot.getValue(WorkoutPlan.class));
                workoutPlanAdapter.notifyItemChanged(index);
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                int index = getIndexForKey(snapshot.getKey());
                workouts.remove(index);
                workoutPlanAdapter.notifyItemRemoved(index);
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildKey) {
                int oldIndex = getIndexForKey(snapshot.getKey());
                workouts.remove(oldIndex);
                int newIndex = previousChildKey == null ? 0 : getIndexForKey(previousChildKey) + 1;
                workouts.add(newIndex, snapshot.getValue(WorkoutPlan.class));
                workoutPlanAdapter.notifyItemMoved(oldIndex,newIndex);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("FirebaseReadError","The read failed: " + error.getCode());
            }
        };
        ref.addChildEventListener(dataListener);
    }


    @Override
    public void onResume(){
        super.onResume();
        workouts = new ArrayList<WorkoutPlan>();
        workoutPlanAdapter = new WorkoutPlanAdapter(getContext(),workouts);
        workoutList.setAdapter(workoutPlanAdapter);
        workoutList.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchDataFromFirebase();
    }

    void filter(String text){
        List<WorkoutPlan> temp = new ArrayList();
        for(WorkoutPlan d: workouts){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(d.getName().contains(text)){
                temp.add(d);
            }
        }
        //update recyclerview
        workoutPlanAdapter.updateList(temp);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void goBack(View v){
        getFragmentManager().popBackStack();
    }

    public void onPause(){
        super.onPause();
        ref.removeEventListener(dataListener);
    }

    private int getIndexForKey(String key) {
        int index = 0;
        for (WorkoutPlan workouts : workouts) {
            if (workouts.getWorkoutPlanId().equals(key)) {
                return index;
            } else {
                index++;
            }
        }
        throw new IllegalArgumentException("Key not found");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
