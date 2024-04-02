package com.example.homie.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import com.example.homie.Activities.addHomeMemberActivity;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.User;
import com.example.homie.R;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

private MaterialTextView home_MTV_UserName;
private MaterialTextView home_MTV_upComingTask;
private CircleImageView circular_image_view;

private CircleImageView[] circularImageViews;
private GridLayout Home_GridLayout_homeMembers;


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
        findViews(view);
        initHomeFragmentUI();


        return view;
    }



    private void findViews(View view) {
        home_MTV_upComingTask = view.findViewById(R.id.home_MTV_upComingTask);
        circular_image_view = view.findViewById(R.id.circular_image_view);
        home_MTV_UserName = view.findViewById(R.id.home_MTV_UserName);
        Home_GridLayout_homeMembers = view.findViewById(R.id.Home_GridLayout_homeMembers);

    }


    private void initHomeFragmentUI() {
        createCircleImageViewHomeMembersArray();
        initHomeDataUi();
        initHomeUserUI();
        initHomeMembersPictures();
    }

    private void initHomeMembersPictures() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = db.getReference("UserInfo").child(CurrentUser.getInstance().getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        ArrayList<String> homeMembersUid = user.getHomeMembersUid();
                        if (homeMembersUid != null) {
                            for (int i = 0; i < homeMembersUid.size(); i++) {
                                final int index = i; // Create a final copy of i
                                String homeMemberUid = homeMembersUid.get(i);

                                // Get reference to the user's data in the database
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMemberUid);
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            User homeMember = snapshot.getValue(User.class);
                                            if (homeMember != null) {
                                                String photoUrl = homeMember.getImage(); // Assuming getImage() returns the photo URL
                                                // Set the image URL to the corresponding circular image view
                                                Picasso.get().load(photoUrl).into(circularImageViews[index]);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        // Handle errors
                                    }
                                });
                            }

                            putAddUserImage(homeMembersUid.size());
                        }
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

    private void putAddUserImage(int indexOfPhoto) {
       circularImageViews[indexOfPhoto].setImageResource(R.drawable.addfamilymember);

       //create the add member setonClickListener
        addUserChangeIntent(indexOfPhoto);

    }

    public void addUserChangeIntent(int indexOfLastCircularImage){
        circularImageViews[indexOfLastCircularImage].setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), addHomeMemberActivity.class);

            // Pass the userUID to the next activity
            intent.putExtra("USER_UID", CurrentUser.getInstance().getUid());
            // Start the activity
            startActivity(intent);


        });
    }

    private void createCircleImageViewHomeMembersArray() {
        int rowCount = Home_GridLayout_homeMembers.getRowCount();
        int columnCount = Home_GridLayout_homeMembers.getColumnCount();

        // Calculate the total number of CircleImageViews
        int totalCircleImageViews = rowCount * columnCount;

        // Initialize the CircleImageView array
        circularImageViews = new CircleImageView[totalCircleImageViews];

        // Loop through each cell in the GridLayout
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < columnCount; j++) {
                // Calculate the index for the circularImageViews array
                int index = i * columnCount + j;

                // Get the CircleImageView from the GridLayout
                CircleImageView circleImageView = (CircleImageView) Home_GridLayout_homeMembers.getChildAt(index);

                // Add the CircleImageView to the circularImageViews array
                circularImageViews[index] = circleImageView;
            }
        }
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
