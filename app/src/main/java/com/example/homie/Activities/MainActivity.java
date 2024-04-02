package com.example.homie.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.homie.Fragments.BudgetFragment;
import com.example.homie.Fragments.CalendarFragment;
import com.example.homie.Fragments.HomeFragment;
import com.example.homie.Fragments.ShoppingFragment;
import com.example.homie.Fragments.TasksFragment;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.Models.User;
import com.example.homie.R;
import com.example.homie.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseUser firebaseUser;
    ArrayList<Task> allTasksAsArrayList;

    private String newHomeMemberToAdd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        // Initialize CurrentUser if the user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            User user = new User(currentUser.getUid(), currentUser.getDisplayName(), currentUser.getPhotoUrl().toString());
            CurrentUser.getInstance().setUserProfile(user);

        }


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.shopping) {
                replaceFragment(new ShoppingFragment());
            } else if (item.getItemId() == R.id.budget) {
                replaceFragment(new BudgetFragment());
            } else if (item.getItemId() == R.id.task) {
                ArrayList<Task> allTaskAsArrayList = new ArrayList<Task>();
                allTaskAsArrayList = CurrentUser.getInstance().getUserProfile().getHomeData().convertTasksToList();
                TasksFragment tasksFragment = new TasksFragment(allTaskAsArrayList);
                replaceFragment(tasksFragment);
            } else if (item.getItemId() == R.id.calendar) {
                replaceFragment(new CalendarFragment());
            }
            return true;
        });

        //hide purpule bar at the top
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);






    }


    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Main_frame_layout,fragment);
        fragmentTransaction.commit();
    }



    }



