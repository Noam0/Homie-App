package com.example.homie.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
    private MaterialButton addTask_BTN;
    private RecyclerView Tasks_RCV_taskRCV;

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

    }

    private void initViews() {
        addTask_BTN.setOnClickListener(v -> {
            String newTask = editText_Task.getText().toString();
            addTask(newTask);
        });

        initTaskArray();
        TaskAdapter adapter = new TaskAdapter(getActivity().getApplicationContext(), allTaskAsArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Tasks_RCV_taskRCV.setAdapter(adapter);
        Tasks_RCV_taskRCV.setLayoutManager(linearLayoutManager);
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