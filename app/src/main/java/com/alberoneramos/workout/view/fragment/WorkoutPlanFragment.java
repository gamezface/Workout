package com.alberoneramos.workout.view.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
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
import com.alberoneramos.workout.controller.WorkoutPlanController;
import com.alberoneramos.workout.view.activity.AddWorkoutActivity;
import com.alberoneramos.workout.adapter.ExerciseListAdapter;
import com.alberoneramos.workout.adapter.ExerciseListSetsAdapter;
import com.alberoneramos.workout.models.Exercise;
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

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;


public class WorkoutPlanFragment extends Fragment {

    private static final int EDIT_CODE = 31;


    @BindView(R.id.exerciseList)
    RecyclerView exerciseList;
    @BindView(R.id.exerciseListRepetition)
    RecyclerView exerciseListRepetition;
    @BindView(R.id.workoutTitle)
    TextView title;
    @BindView(R.id.back)
    ImageButton backButton;
    @BindView(R.id.more)
    ImageButton moreButton;
    @BindView(R.id.toggleWorkout)
    ToggleButton workoutToggleButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.color_ellipse)
    ImageView colorEllipse;

    private Uri shortLink;
    private WorkoutPlan workoutPlan;
    private WorkoutPlanController wPController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            workoutPlan = bundle.getParcelable("WORKOUT_PLAN");
        }
        buildUrl();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workoutplan_list, container, false);
        ButterKnife.bind(this, view);
        recyclerViewSetup();
        moreButton.setOnClickListener(this::showPopup);
        backButton.setOnClickListener(l -> Objects.requireNonNull(getActivity()).getSupportFragmentManager().popBackStack());
        workoutToggleButton.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (isChecked) {
                toggleToolbarColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(android.R.color.white),
                        getResources().getColor(android.R.color.white));
                exerciseListRepetition.setVisibility(View.VISIBLE);
                exerciseListRepetition.invalidate();
                exerciseList.setVisibility(View.GONE);
                exerciseList.invalidate();
            } else {
                toggleToolbarColors(getResources().getColor(android.R.color.white), getResources().getColor(R.color.colorPrimary),
                        getResources().getColor(android.R.color.black));
                exerciseListRepetition.setVisibility(View.GONE);
                exerciseList.setVisibility(View.VISIBLE);
                exerciseListRepetition.invalidate();
                exerciseList.invalidate();
            }
        }));
        title.setText(workoutPlan.getName());
        colorEllipse.setColorFilter(Color.parseColor(String.format("#%06X", (0xFFFFFF & workoutPlan.getColorId()))));
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_CODE && resultCode == Activity.RESULT_OK) {
            DatabaseReference ref = FirebaseDatabase
                    .getInstance()
                    .getReference(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/workouts/" + workoutPlan.getWorkoutPlanId());
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    workoutPlan = dataSnapshot.getValue(WorkoutPlan.class);
                    recyclerViewSetup();
                    colorEllipse.setColorFilter(Color.parseColor(String.format("#%06X", (0xFFFFFF & workoutPlan.getColorId()))));
                    title.setText(workoutPlan.getName());
                    Log.d("Montanha", String.valueOf(workoutPlan.getName()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("ReadError::", "The read failed: " + databaseError.getCode());
                }
            });
        }

    }

    private void recyclerViewSetup() {
        List<Exercise> exercises = workoutPlan.getExercises();
        exerciseList.setAdapter(new ExerciseListAdapter(getContext(), exercises));
        ExerciseListSetsAdapter adapter = new ExerciseListSetsAdapter(getContext(), exercises);
        exerciseListRepetition.setAdapter(adapter);
        exerciseListRepetition.setLayoutManager(new LinearLayoutManager(getContext()));
        StickyHeaderDecoration decor = new StickyHeaderDecoration(adapter);
        exerciseListRepetition.addItemDecoration(decor);
        exerciseList.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showPopup(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions_workoutplan, popup.getMenu());
        wPController = new WorkoutPlanController(getActivity(), workoutPlan);
        popup.show();
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.share:
                    wPController.shareDialogBuilder(shortLink.toString());
                    return true;
                case R.id.delete:
                    wPController.deleteDialogBuilder();
                    return true;
                case R.id.edit:
                    wPController.showEditActivity(EDIT_CODE);
                    return true;
                default:
                    return false;
            }
        });
    }

    private void buildUrl() {
        UrlBuilder builder = new UrlBuilder();
        Uri longUri = builder.buildWorkoutUrl(this.workoutPlan.getWorkoutPlanId());
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(longUri)
                .buildShortDynamicLink()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        shortLink = Objects.requireNonNull(task.getResult()).getShortLink();

                });
    }

    private void toggleToolbarColors(int toolBarColor, int imageButtonColor, int titleColor) {
        this.toolbar.setBackgroundColor(toolBarColor);
        this.backButton.setColorFilter(imageButtonColor);
        this.title.setTextColor(titleColor);
        this.moreButton.setColorFilter(imageButtonColor);
    }
//
//    public void showEditActivity() {
//        Intent intent = new Intent(getContext(), AddWorkoutActivity.class);
//        intent.putExtra("WORKOUT_DATA", this.workoutPlan);
//        Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom);
//        intent.putExtra("EDIT_MODE", true);
//        startActivityForResult(intent, EDIT_CODE);
//    }
//
//    public void deleteDialogBuilder() {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
//        alertDialog.setTitle(R.string.delete_workout_dialog_title);
//        alertDialog.setMessage(getResources().getString(R.string.delete_workout_dialog_message));
//        alertDialog.setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            database.getReference().child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid() + "/workouts").child(this.workoutPlan.getWorkoutPlanId()).removeValue();
//            goBack();
//        });
//        alertDialog.setNegativeButton(R.string.dialog_close, (dialog, which) -> dialog.cancel());
//        AlertDialog dialog = alertDialog.create();
//        dialog.setOnShowListener(arg0 -> {
//            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//        });
//        dialog.show();
//    }
//
//    public void shareDialogBuilder(String uri) {
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
//        alertDialog.setTitle(R.string.share_workout_title);
//        alertDialog.setMessage(getResources().getString(R.string.share_workout_message));
//        LinearLayout layout = new LinearLayout(getContext());
//        final EditText input = setupDialogInput(uri);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        layout.addView(input);
//        ImageView qrCode = new ImageView(getContext());
//        qrCode.setImageBitmap(UrlBuilder.generateQRCode(uri, 800));
//        layout.addView(qrCode);
//        alertDialog.setPositiveButton(R.string.clipboard_copy, (dialog, which) -> {
//            getContext();
//            ClipboardManager clipboard = (ClipboardManager) Objects.requireNonNull(this.getContext()).getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("URI", uri);
//            if (clipboard != null) {
//                clipboard.setPrimaryClip(clip);
//            }
//        });
//        alertDialog.setNegativeButton(R.string.dialog_close, (dialog, which) -> dialog.cancel());
//        alertDialog.setView(layout);
//        AlertDialog dialog = alertDialog.create();
//        dialog.setOnShowListener(arg0 -> {
//            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
//        });
//        dialog.show();
//    }
//
//    public EditText setupDialogInput(String uri) {
//        EditText input = new EditText(getContext());
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT);
//        input.setLayoutParams(lp);
//        input.setText(uri);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        params.setMargins(8, 8, 8, 8);
//        input.setLayoutParams(params);
//        input.setInputType(InputType.TYPE_NULL);
//        input.setSelectAllOnFocus(true);
//        input.setMaxLines(1);
//        input.setHorizontallyScrolling(true);
//        input.setFilters(new InputFilter[]{
//                (src, start, end, dst, dstart, dend) -> src.length() < 1 ? dst.subSequence(dstart, dend) : ""
//        });
//        return input;
//    }

//
//    public void goBack() {
//        if (getFragmentManager() != null) {
//            getFragmentManager().popBackStack();
//        }
//    }

}
