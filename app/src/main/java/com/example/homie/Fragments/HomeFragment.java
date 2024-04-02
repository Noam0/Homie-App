package com.example.homie.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.Models.User;
import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

private MaterialTextView home_MTV_UserName;
private MaterialTextView home_MTV_upComingTask;
private CircleImageView circular_image_view;

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        findViewsById(view);

        initHomeFragmentUI();

        return view;
    }



    private void findViewsById(View view) {
        home_MTV_upComingTask = view.findViewById(R.id.home_MTV_upComingTask);
        circular_image_view = view.findViewById(R.id.circular_image_view);
        home_MTV_UserName = view.findViewById(R.id.home_MTV_UserName);

    }
    private void initHomeFragmentUI() {
        initHomeDataUi();
        initHomeUserUI();
    }

    private void initHomeUserUI() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("UserInfo").child(CurrentUser.getInstance().getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        loadingImageUrl();
                        home_MTV_UserName.setText(setFirstNameOfUser(user.getName()));
                    }
                } else {
                    // Handle case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }



    private void initHomeDataUi(){
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("UserInfo").child(CurrentUser.getInstance().getUid()).child("homeData");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HomeData homeData = snapshot.getValue(HomeData.class);
                    if (homeData != null) {
                        String taskCount = String.valueOf(homeData.getAllTasks().size());
                        home_MTV_upComingTask.setText("You have " + taskCount + " upcoming tasks");


                    }
                } else {
                    // Handle case where the data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors
            }
        });
    }


    private void loadingImageUrl(){
        FirebaseAuth auth;
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String photoUrl = String.valueOf(currentUser.getPhotoUrl());
        Picasso.get().load(photoUrl).into(circular_image_view);
    }


    private String setFirstNameOfUser(String fullName){
        String[] nameParts = fullName.split(" ");
        String firstName = nameParts[0];
        return firstName;
    }
}
