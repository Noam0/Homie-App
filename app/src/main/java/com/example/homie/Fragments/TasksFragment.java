package com.example.homie.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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

import com.example.homie.Activities.MainActivity;
import com.example.homie.Adapters.TaskAdapter;
import com.example.homie.Interfaces.TaskCallBack;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

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

    private MaterialTextView buttonDeadline;
    private MaterialTextView buttonCategory;

    private MaterialTextView Task_MTV_done;


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
        buttonDeadline = view.findViewById(R.id.buttonDeadline);
        buttonCategory = view.findViewById(R.id.buttonCategory);
        Task_MTV_done = view.findViewById(R.id.Task_MTV_done);



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
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(Tasks_RCV_taskRCV);


        adapter.setTaskCallback(new TaskCallBack() {
            @Override
            public void editTaskClicked(Task task, int position) {
                showTaskFormat(task,position);


            }

            @Override
            public void taskCheckedClicked(Task task, int position) {
                taskCheckedUpdate(task,position);


            }
        });
        initSpinner();
        initSortingButtons();
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



    private void showTaskFormat(Task task, int position){

        AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.task_format, null);
        myDialogBuilder.setView(myView);

        final AlertDialog dialog = myDialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();

        // Find the cancel button inside the dialog view
        findViewsForDialog(myView);

        task_format_description.setText(task.getDescription());
        String month[] = task.getDeadline().split("-");
        int year = Integer.parseInt(month[0]);
        int monthOfYear = Integer.parseInt(month[1]) - 1; // Subtract 1 because months are 0-indexed in DatePicker
        int dayOfMonth = Integer.parseInt(month[2]);
        task_format_date.init(year, monthOfYear, dayOfMonth, null);
        categorySpinner.setSelection(categoryToInt(task.getCategory()));

        addTask_Btn_cancel = myView.findViewById(R.id.addTask_Btn_cancel);

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

                String category = getTaskCategoryFromSpinner();//getting category
                Date taskDeadline = new Date();
                taskDeadline = getDeadline();//getting date
                String taskDescription = "";
                taskDescription = GetTaskDescription();
                task_format_description.setText("");
                updateTask(task,position,category,taskDescription,taskDeadline);


            }
        });


    }

    private void updateTask(Task task, int position, String newCategory, String newDescription, Date newDate) {

        String niceDateFormat = formatDate(newDate);
        CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().get(position)
                .setDescription(newDescription)
                .setDeadline(niceDateFormat)
                .setCategory(newCategory);

        updateDataBaseTasks();
    }

    private void findViewsForDialog(View myView) {
        ShapeableImageView addTask_Btn_cancel = myView.findViewById(R.id.addTask_Btn_cancel);
        categorySpinner = myView.findViewById(R.id.task_format_category_spinner);
        task_format_description = myView.findViewById(R.id.task_format_description);
        task_format_date = myView.findViewById(R.id.task_format_date);
    }







    private int categoryToInt(String category){

        int categoryNum = 1;
        switch (category) {
            case "Cleaning":
                categoryNum = 0;
                break;
            case "Cooking":
                categoryNum = 1;
                break;
            case "Laundry":
                categoryNum = 2;
                break;
            case "Gardening":
                categoryNum = 3;
                break;
            case "Repairs":
                categoryNum = 4;
                break;
            default:
                categoryNum = 0;
                break;
        }
        return categoryNum;
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
        allTaskAsArrayList = CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks();
        }



    private void addTask(final String newTask, String category, Date date) {

        String niceDateFormat = formatDate(date);
        final Task task = new Task(newTask,category,niceDateFormat);

        CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().add(task);

        updateDataBaseTasks();




    }

    private void updateDataBaseTasks() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);
        final DatabaseReference tasksRef = userRef.child("homeData").child("allTasks");

        tasksRef.setValue( CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks());

        for(String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()){
            DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember);
            DatabaseReference memtasksRef = memRef.child("homeData").child("allTasks");
            memtasksRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks());

        }

        adapter.changeArrayTask(CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks());

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


    //Sorting Methods
    private void sortByCategory() {
        Collections.sort(allTaskAsArrayList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                return task1.getCategory().compareTo(task2.getCategory());
            }
        });
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }
    private void sortByDeadline() {
        Collections.sort(allTaskAsArrayList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                // Parse deadline strings to Date objects
                Date date1 = parseDate(task1.getDeadline());
                Date date2 = parseDate(task2.getDeadline());
                // Compare Date objects
                return date1.compareTo(date2);
            }
        });
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    // Helper method to parse deadline string to Date object
    private Date parseDate(String deadline) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return sdf.parse(deadline);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    private void initSortingButtons() {

        buttonCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                buttonCategory.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                buttonDeadline.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                Task_MTV_done.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                sortByCategory();
            }
        });

        buttonDeadline.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                buttonCategory.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonDeadline.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                Task_MTV_done.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                sortByDeadline();
            }
        });

        Task_MTV_done.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                buttonCategory.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonDeadline.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                Task_MTV_done.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                sortByFinished();
            }
        });
    }

    private void sortByFinished() {
        Collections.sort(allTaskAsArrayList, new Comparator<Task>() {
            @Override
            public int compare(Task task1, Task task2) {
                // Compare tasks based on their done status
                if (task1.isDone() && !task2.isDone()) {
                    return 1; // task1 is done, task2 is not done, so task1 comes after task2
                } else if (!task1.isDone() && task2.isDone()) {
                    return -1; // task1 is not done, task2 is done, so task1 comes before task2
                } else {
                    return 0; // Both tasks have the same done status, maintain their original order
                }
            }
        });

        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }



    private void taskCheckedUpdate(Task task, int position) {
        if(task.isDone()){
          task.setDone(false);
        }else{
            task.setDone(true);
        }
        CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().set(position,task);
        adapter.notifyDataSetChanged();
        updateDataBaseTasks();
        Log.d("imrighthere481", "taskCheckedUpdate: ");

    }




    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            final Task deletedTask = CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().remove(position);
            updateDataBaseTasks();
            adapter.notifyDataSetChanged();
            View rootView = getView();
            Snackbar.make(rootView, "Task deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onClick(View view) {
                            CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().add(position, deletedTask);
                            updateDataBaseTasks();
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(),R.color.delete_red_color))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .addSwipeLeftLabel("Delete Task")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(),R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

}