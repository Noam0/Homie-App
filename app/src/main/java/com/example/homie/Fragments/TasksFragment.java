package com.example.homie.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homie.Adapters.TaskAdapter;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class TasksFragment extends Fragment {

    private ArrayList<Task> allTaskAsArrayList;
    private TextInputEditText editText_Task;
    private AppCompatButton addTask_BTN;
    private RecyclerView Tasks_RCV_taskRCV;
    private TaskAdapter adapter;
    private ShapeableImageView Tasks_BTN_add;

    private CardView Tasks_CV_addCardView;

    private AppCompatButton addTask_Btn_cancel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        findViews(view);


        initViews();
        return view;

    }

    private void findViews(View view) {
        editText_Task = view.findViewById(R.id.editText_Task);
        addTask_BTN = view.findViewById(R.id.addTask_BTN);
        Tasks_RCV_taskRCV = view.findViewById(R.id.Tasks_RCV_taskRCV);
        Tasks_BTN_add = view.findViewById(R.id.Tasks_BTN_add);
        Tasks_CV_addCardView = view.findViewById(R.id.Tasks_CV_addCardView);
        addTask_Btn_cancel = view.findViewById(R.id.addTask_Btn_cancel);


    }

    private void initViews() {
        addTask_BTN.setOnClickListener(v -> {
            String newTask = editText_Task.getText().toString();
            if(newTask.length() != 0) {
                addTask(newTask);
            }
            editText_Task.setText("");
            //MakeToast
        });

        addTask_Btn_cancel.setOnClickListener(v ->{

            removeVisibilityToElements();
        });

        setVisibilityToElements();
        initTaskArray();
        initButtonAdd();


        adapter = new TaskAdapter(getActivity().getApplicationContext(), allTaskAsArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Tasks_RCV_taskRCV.setAdapter(adapter);
        Tasks_RCV_taskRCV.setLayoutManager(linearLayoutManager);
    }

    private void setVisibilityToElements() {
        Tasks_CV_addCardView.setVisibility(View.GONE);
        editText_Task.setVisibility(View.GONE);
        addTask_BTN.setVisibility(View.GONE);
    }

    private void initButtonAdd(){
        Tasks_BTN_add.setOnClickListener(v->{

            ObjectAnimator fadeInAnimator = ObjectAnimator.ofFloat(Tasks_CV_addCardView, "alpha", 0f, 1f);
            fadeInAnimator.setDuration(500); // Animation duration in milliseconds

// Define animation for editText_Task
            ObjectAnimator editTextFadeInAnimator = ObjectAnimator.ofFloat(editText_Task, "alpha", 0f, 1f);
            editTextFadeInAnimator.setDuration(500); // Animation duration in milliseconds

// Define animation for addTask_BTN
            ObjectAnimator addTaskBtnFadeInAnimator = ObjectAnimator.ofFloat(addTask_BTN, "alpha", 0f, 1f);
            addTaskBtnFadeInAnimator.setDuration(500); // Animation duration in milliseconds

// Show the pop-up card view with animations
            Tasks_CV_addCardView.setVisibility(View.VISIBLE);
            editText_Task.setVisibility(View.VISIBLE);
            addTask_BTN.setVisibility(View.VISIBLE);

            fadeInAnimator.start();
            editTextFadeInAnimator.start();
            addTaskBtnFadeInAnimator.start();




        });
    }

    private void removeVisibilityToElements() {
        // Define animation for the popupCardView
        ObjectAnimator fadeOutAnimator = ObjectAnimator.ofFloat(Tasks_CV_addCardView, "alpha", 1f, 0f);
        fadeOutAnimator.setDuration(500); // Animation duration in milliseconds

        // Define animation for editText_Task
        ObjectAnimator editTextFadeOutAnimator = ObjectAnimator.ofFloat(editText_Task, "alpha", 1f, 0f);
        editTextFadeOutAnimator.setDuration(500); // Animation duration in milliseconds

        // Define animation for addTask_BTN
        ObjectAnimator addTaskBtnFadeOutAnimator = ObjectAnimator.ofFloat(addTask_BTN, "alpha", 1f, 0f);
        addTaskBtnFadeOutAnimator.setDuration(500); // Animation duration in milliseconds

        // Create an AnimatorSet to synchronize animations
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(fadeOutAnimator, editTextFadeOutAnimator, addTaskBtnFadeOutAnimator);

        // Start the fade-out animations
        animatorSet.start();


    }

    private void initTaskArray() {

        allTaskAsArrayList = new ArrayList<Task>();
        allTaskAsArrayList = CurrentUser.getInstance().getUserProfile().getHomeData().convertTasksToList();
        }



    private void addTask(String newTask) {
        // Get the UID of the currently logged-in user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);
        // Generate a unique ID for the new task
        String taskId = UUID.randomUUID().toString();
        // Create a new Task object with the provided task description
        Task task = new Task(newTask, "category"); // Assuming you have a category for the task
        // Get a reference to the tasks node in the user's data
        DatabaseReference tasksRef = userRef.child("homeData").child("allTasks");
        // Push the new task to the database using the generated task ID
        tasksRef.child(taskId).setValue(task);
        CurrentUser.getInstance().getUserProfile().getHomeData().addTask(task,taskId);
        Log.d("problem", CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().size()+"");
        allTaskAsArrayList.add(task);
        adapter.notifyItemInserted(allTaskAsArrayList.size()-1);

    }

    public TasksFragment(ArrayList<Task> allTaskAsArrayList) {
        this.allTaskAsArrayList = allTaskAsArrayList;

    }


    public ArrayList<Task> getAllTaskAsArrayList() {
        return allTaskAsArrayList;
    }

    public TasksFragment setAllTaskAsArrayList(ArrayList<Task> allTaskAsArrayList) {
        this.allTaskAsArrayList = allTaskAsArrayList;
        return this;
    }







}