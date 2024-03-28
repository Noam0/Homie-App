package com.example.homie.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.UUID;

public class TasksFragment extends Fragment {
private TextInputEditText editText_Task;
private MaterialButton addTask_BTN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment



        View view =  inflater.inflate(R.layout.fragment_tasks, container, false);
        findViews(view);
        initViews();
        return view;

    }

    private void findViews(View view) {
        editText_Task = view.findViewById(R.id.editText_Task);
        addTask_BTN = view.findViewById(R.id.addTask_BTN);

    }
    private void initViews(){
        addTask_BTN.setOnClickListener(v -> {
           String newTask = editText_Task.getText().toString();
            Log.d("newTask", newTask);
           addTask(newTask);
        });

    }

    private void addTask(String newTask) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference reference = db.getReference("home");
        //reference.setValue();
    }

    public TasksFragment() {
        // Required empty public constructor
    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }


}