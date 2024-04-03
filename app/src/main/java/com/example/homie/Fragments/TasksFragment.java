package com.example.homie.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.homie.Adapters.TaskAdapter;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class TasksFragment extends Fragment {

    private ArrayList<Task> allTaskAsArrayList;
    private AppCompatButton addTask_BTN_confirm;
    private RecyclerView Tasks_RCV_taskRCV;
    private TaskAdapter adapter;
    private ShapeableImageView Tasks_BTN_add;
    private EditText task_format_description;
    private Spinner categorySpinner;
    private ArrayList<String> taskCategories;
    private ShapeableImageView addTask_Btn_cancel;

    private DatePicker task_format_date;

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



        Tasks_RCV_taskRCV = view.findViewById(R.id.Tasks_RCV_taskRCV);
        Tasks_BTN_add = view.findViewById(R.id.Tasks_BTN_add);
        addTask_Btn_cancel = view.findViewById(R.id.addTask_Btn_cancel);
        categorySpinner = view.findViewById(R.id.task_format_category_spinner);




    }

    private void addTaskOnClickListener() {


            String category = getTaskCategoryFromSpinner();//getting category
            Date taskDeadline = new Date();
            taskDeadline = getDeadline();//getting date
            String taskDescription = "";
            if(task_format_description.getText().length() != 0){
            taskDescription = GetTaskDescription();
            addTask(taskDescription,category,taskDeadline);
            Toast.makeText(getActivity(), "Task successfully added!", Toast.LENGTH_SHORT).show();
            }else {
            Toast.makeText(getActivity(), "Task description cannot be empty", Toast.LENGTH_SHORT).show();

            }
            task_format_description.setText("");



        
    }

    private Date getDeadline() {
        // Get the selected date from the DatePicker
        int day = task_format_date.getDayOfMonth();
        int month = task_format_date.getMonth();
        int year = task_format_date.getYear();

        // Use a Calendar object to set the selected date
        Calendar calendar = Calendar.getInstance();
        calendar.clear(); // Clear the calendar to reset all fields
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);

        // Return the Date object
        return calendar.getTime();
    }
    private String getTaskCategoryFromSpinner() {
        return categorySpinner.getSelectedItem().toString();
    }

    private String GetTaskDescription() {
        return task_format_description.getText().toString();
    }



    private void initViews() {
        //setVisibilityToElements();
        initTaskArray();
        initButtonAddAndCancel();
        adapter = new TaskAdapter(getActivity().getApplicationContext(), allTaskAsArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Tasks_RCV_taskRCV.setAdapter(adapter);
        Tasks_RCV_taskRCV.setLayoutManager(linearLayoutManager);
        initSpinner();
    }

    private void initSpinner() {
        // Inflate the layout containing the Spinner
        View spinnerLayout = getLayoutInflater().inflate(R.layout.task_format, null);
        // Find the Spinner within the inflated layout
        Spinner categorySpinner = spinnerLayout.findViewById(R.id.task_format_category_spinner);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        categorySpinner.setAdapter(arrayAdapter);

        // Set the item selection listener for the spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                // Handle item selection here if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
    }


    private void initButtonAddAndCancel() {
        Tasks_BTN_add.setOnClickListener(v -> {
            AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myView = inflater.inflate(R.layout.task_format, null);
            myDialogBuilder.setView(myView);

            final AlertDialog dialog = myDialogBuilder.create();
            dialog.setCancelable(false);
            dialog.show();

            // Find the cancel button inside the dialog view
            ShapeableImageView addTask_Btn_cancel = myView.findViewById(R.id.addTask_Btn_cancel);
            categorySpinner = myView.findViewById(R.id.task_format_category_spinner);
            task_format_description = myView.findViewById(R.id.task_format_description);
            task_format_date = myView.findViewById(R.id.task_format_date);



            addTask_Btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });

            AppCompatButton addTask_BTN_confirm = myView.findViewById(R.id.addTask_BTN_confirm);

            addTask_BTN_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addTaskOnClickListener();
                }
            });


            });

        ;
    }


    private void initTaskArray() {

        allTaskAsArrayList = new ArrayList<Task>();
        allTaskAsArrayList = CurrentUser.getInstance().getUserProfile().getHomeData().convertTasksToList();
        }



    private void addTask(final String newTask, String category, Date date) {
        // Get the UID of the currently logged-in user
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);
        // Generate a unique ID for the new task
        final String taskId = UUID.randomUUID().toString();
        String niceDateFormat = formatDate(date);

        // Create a new Task object with the provided task description
        final Task task = new Task(newTask,category,niceDateFormat); // Assuming you have a category for the task
        // Get a reference to the tasks node in the user's data
        final DatabaseReference tasksRef = userRef.child("homeData").child("allTasks");
        // Push the new task to the database using the generated task ID
        tasksRef.child(taskId).setValue(task).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Data successfully saved to the database
                // Now, for each existing member in the list, add the new task to their list
                for (String memberUid : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()) {
                    addTaskToMember(memberUid, taskId, task);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle any errors that occurred while saving data
            }
        });

        allTaskAsArrayList.add(task);
        CurrentUser.getInstance().getUserProfile().getHomeData().addTask(task,taskId);
        adapter.changeArrayTask(allTaskAsArrayList);
    }

    private void addTaskToMember(String memberUid, String taskId, Task task) {
        // Get a reference to the member's data location
        DatabaseReference memberRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(memberUid);
        // Get a reference to the tasks node in the member's data
        DatabaseReference tasksRef = memberRef.child("homeData").child("allTasks");
        // Push the new task to the member's list of tasks
        tasksRef.child(taskId).setValue(task);

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

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }







}