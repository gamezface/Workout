package com.example.henrique.workout.adapters;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.example.henrique.workout.R;
import com.example.henrique.workout.fragments.WorkoutPlanFragment;
import com.example.henrique.workout.models.WorkoutPlan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class WorkoutPlanAdapter extends RecyclerSwipeAdapter<WorkoutPlanAdapter.SimpleViewHolder> {

    public class SimpleViewHolder extends RecyclerView.ViewHolder {
        public TextView workoutName;
        public TextView numberOfExercises;
        SwipeLayout swipeLayout;
        ImageButton buttonDelete;

        public SimpleViewHolder(View itemView) {
            super(itemView);
            swipeLayout = itemView.findViewById(R.id.swipe);
            workoutName = itemView.findViewById(R.id.workoutName);
            numberOfExercises = itemView.findViewById(R.id.numberOfExercises);
            buttonDelete = itemView.findViewById(R.id.delete_button);
        }

    }
    private List<WorkoutPlan> items;
    private Context mContext;
    FragmentTransaction mFragmentTransaction;

    public WorkoutPlanAdapter(Context context,List<WorkoutPlan> items) {
        this.mContext = context;
        this.items = items;
    }

    private void removeRow(final View row, final int position) {
        final int initialHeight = row.getHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime,
                                               Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                int newHeight = (int) (initialHeight * (1 - interpolatedTime));
                if (newHeight > 0) {
                    row.getLayoutParams().height = newHeight;
                    row.requestLayout();
                }
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                row.getLayoutParams().height = initialHeight;
                row.requestLayout();
                items.remove(position);
                notifyDataSetChanged();
            }
        });
        animation.setDuration(300);
        row.startAnimation(animation);
    }


    public void updateList(List<WorkoutPlan> list){
        this.items = list;
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        WorkoutPlan item = items.get(position);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.delete_button));
            }
        });
        holder.buttonDelete.setOnClickListener(view -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/workouts").child(item.getWorkoutPlanId()).removeValue();
        });
        holder.swipeLayout.getSurfaceView().setOnClickListener(pos -> openDetailActivity(items.get(position)));
        holder.workoutName.setText(item.getName());
        holder.numberOfExercises.setText(String.valueOf(String.valueOf(item.getExercises().size())));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void openDetailActivity(WorkoutPlan workoutPlan) {
        Bundle bundle = new Bundle();
        FragmentManager fragmentManager = ((FragmentActivity)mContext).getSupportFragmentManager();
        mFragmentTransaction = fragmentManager.beginTransaction();
        bundle.putParcelable("WORKOUT_PLAN",workoutPlan);
        Fragment masterDetailFragment = new WorkoutPlanFragment();
        masterDetailFragment.setArguments(bundle);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.setCustomAnimations(R.anim.slide_in,R.anim.slide_out);
        mFragmentTransaction.replace(R.id.fragment_container, masterDetailFragment).commit();
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout,parent, false);
        return new SimpleViewHolder(v);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}