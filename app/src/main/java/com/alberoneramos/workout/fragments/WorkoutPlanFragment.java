package com.alberoneramos.workout.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.Toolbar;

import com.alberoneramos.workout.R;
import com.alberoneramos.workout.activities.AddWorkoutActivity;
import com.alberoneramos.workout.adapters.ExerciseListAdapter;
import com.alberoneramos.workout.models.WorkoutPlan;
import com.alberoneramos.workout.utils.UrlBuilder;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;


public class WorkoutPlanFragment extends Fragment {

    private static final int EDIT_CODE = 31;

    private OnFragmentInteractionListener mListener;
    public WorkoutPlanFragment() {
        // Required empty public constructor
    }
    RecyclerView exerciseList;
    TextView title;
    ImageButton backButton;
    ImageButton moreButton;
    ImageView colorEllipse;
    Toolbar toolbar;
    Uri shortLink;
    WorkoutPlan workoutPlan;
    ToggleButton workoutToggleButton;
    // TODO: Rename and change types and number of parameters
    public static WorkoutPlanFragment newInstance() {
        WorkoutPlanFragment fragment = new WorkoutPlanFragment();
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
        colorEllipse.setColorFilter(Color.parseColor(String.format("#%06X", (0xFFFFFF & workoutPlan.getColorId()))));
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CODE && resultCode == Activity.RESULT_OK){
            DatabaseReference ref = FirebaseDatabase
                    .getInstance()
                    .getReference(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/workouts/"+workoutPlan.getWorkoutPlanId());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    workoutPlan = dataSnapshot.getValue(WorkoutPlan.class);
                    recyclerViewSetup(exerciseList,workoutPlan);
                    colorEllipse.setColorFilter(Color.parseColor(String.format("#%06X", (0xFFFFFF & workoutPlan.getColorId()))));
                    title.setText(workoutPlan.getName());
                    Log.d("Montanha",String.valueOf(workoutPlan.getName()));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("ReadError::","The read failed: " + databaseError.getCode());
                }
            });
        }

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
                        deleteDialogBuilder();
                        return true;
                    case R.id.edit:
                        showEditActivity();
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

    public void showEditActivity(){
        Intent intent = new Intent(getContext(), AddWorkoutActivity.class);
        intent.putExtra("WORKOUT_DATA",this.workoutPlan);
        getActivity().overridePendingTransition(R.anim.slide_in_bottom,R.anim.slide_out_bottom);
        intent.putExtra("EDIT_MODE",true);
        startActivityForResult(intent,EDIT_CODE);
    }

    public void deleteDialogBuilder(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.delete_workout_dialog_title);
        alertDialog.setMessage(getResources().getString(R.string.delete_workout_dialog_message));
        alertDialog.setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            database.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"/workouts").child(this.workoutPlan.getWorkoutPlanId()).removeValue();
            goBack();
        });
        alertDialog.setNegativeButton(R.string.dialog_close,(dialog,which) -> dialog.cancel());
        AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();
    }

    public void shareDialogBuilder(String uri){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle(R.string.share_workout_title);
        alertDialog.setMessage(getResources().getString(R.string.share_workout_message));
        LinearLayout layout = new LinearLayout(getContext());
        final EditText input = setupDialogInput(uri);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(input);
        ImageView qrCode = new ImageView(getContext());
        qrCode.setImageBitmap(UrlBuilder.generateQRCode(uri,800));
        layout.addView(qrCode);
        alertDialog.setPositiveButton(R.string.clipboard_copy, (dialog, which) -> {
            ClipboardManager clipboard = (ClipboardManager) this.getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("URI", uri);
            clipboard.setPrimaryClip(clip);
        });
        alertDialog.setNegativeButton(R.string.dialog_close,(dialog,which) -> dialog.cancel());
        alertDialog.setView(layout);
        AlertDialog dialog = alertDialog.create();
        dialog.setOnShowListener(arg0 -> {
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
        });
        dialog.show();
    }

    public EditText setupDialogInput(String uri){
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
        this.colorEllipse = rootView.findViewById(R.id.color_ellipse);
        this.moreButton = rootView.findViewById(R.id.more);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void goBack(){
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
