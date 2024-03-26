package com.example.homie.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.homie.Fragments.BudgetFragment;
import com.example.homie.Fragments.CalendarFragment;
import com.example.homie.Fragments.HomeFragment;
import com.example.homie.Fragments.ShoppingFragment;
import com.example.homie.Fragments.TasksFragment;
import com.example.homie.R;
import com.example.homie.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

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