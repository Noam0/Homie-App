package com.example.homie.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;

import com.example.homie.Fragments.BudgetFragment;
import com.example.homie.Fragments.CalendarFragment;
import com.example.homie.Fragments.HomeFragment;
import com.example.homie.Fragments.ShoppingFragment;
import com.example.homie.Fragments.TasksFragment;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.User;
import com.example.homie.R;
import com.example.homie.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        // Initialize CurrentUser if the user is already logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            User user = new User(currentUser.getUid(), currentUser.getDisplayName());
            CurrentUser.getInstance().setUserProfile(user);
            // You may also load user data from the database here if needed
        } else {
            // Handle if user is not logged in
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (item.getItemId() == R.id.shopping) {
                replaceFragment(new ShoppingFragment());
            } else if (item.getItemId() == R.id.budget) {
                replaceFragment(new BudgetFragment());
            } else if (item.getItemId() == R.id.task) {
                replaceFragment(new TasksFragment());
            } else if (item.getItemId() == R.id.calendar) {
                replaceFragment(new CalendarFragment());
            }
            return true;
        });


    }
    private void replaceFragment(Fragment fragment){

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.Main_frame_layout,fragment);
        fragmentTransaction.commit();
    }




}