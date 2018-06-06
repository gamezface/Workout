package com.example.henrique.workout.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.example.henrique.workout.R;
import com.example.henrique.workout.adapters.ExerciseListAdapter;
import com.example.henrique.workout.adapters.WorkoutPlanAdapter;
import com.example.henrique.workout.models.Exercise;
import com.example.henrique.workout.models.WorkoutPlan;
import com.example.henrique.workout.utils.UrlBuilder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

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
    Uri shortLink;
    WorkoutPlan workoutPlan;
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
        Bundle bundle = getArguments();
        workoutPlan = bundle.getParcelable("WORKOUT_PLAN");
        buildUrl();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_workoutplan_list, container, false);
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
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.share:
                        shareDialogBuilder(shortLink.toString());
                        return true;
                    case R.id.delete:
                        return true;
                    case R.id.edit:
                        return true;
                    default:
                        return false;
                }
            }
        });
    }
    public void buildUrl(){
        UrlBuilder builder = new UrlBuilder();
        Uri longUri = builder.buildWorkoutUrl(this.workoutPlan.getWorkoutPlanId());
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(longUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        shortLink = task.getResult().getShortLink();

                });
    }

    public void shareDialogBuilder(String uri){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.share_workout_title);
        alertDialog.setMessage(getResources().getString(R.string.share_workout_message));
        final EditText input = setupInputDialog(uri);
        alertDialog.setView(input);
        ImageView qrCode = new ImageView(getContext());
        qrCode.setImageBitmap(UrlBuilder.generateQRCode(uri));
        alertDialog.setView(qrCode);
        alertDialog.setPositiveButton(R.string.clipboard_copy, (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("URI", uri);
            clipboard.setPrimaryClip(clip);
        });
        alertDialog.setNegativeButton(R.string.dialog_close,(dialog,which) -> dialog.cancel());
        AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();
    }

    public EditText setupInputDialog(String uri){
        EditText input = new EditText(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(uri);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8,8,8,8);
        input.setLayoutParams(params);
        input.setInputType(InputType.TYPE_NULL);
        input.setSelectAllOnFocus(true);
        input.setMaxLines(1);
        input.setHorizontallyScrolling(true);
        input.setFilters(new InputFilter[] {
                (src, start, end, dst, dstart, dend) -> src.length() < 1 ? dst.subSequence(dstart, dend) : ""
        });
        return input;
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
