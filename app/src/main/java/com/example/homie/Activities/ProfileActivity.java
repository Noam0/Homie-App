package com.example.homie.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.homie.Models.CurrentUser;
import com.example.homie.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {


    private CircleImageView user_CIV_userimage;
   private TextInputLayout user_TIL_firstName;
    private TextInputLayout user_TIL_lastName;
    private MaterialButton user_BTN_save;
    private String currentUserUid;
    private MaterialTextView user_MTV_addHomeMembers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getCurrentUserUid();
        findViews();
        initViews();

    }

    private void getCurrentUserUid() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_UID")) {
            currentUserUid = intent.getStringExtra("USER_UID");
        }
    }

    private void initViews() {
        user_BTN_save.setOnClickListener(v -> saveUserProfile());
        initFirstNameAndLastNameViews();
        user_MTV_addHomeMembers.setOnClickListener(v -> goToAddHomeMemberActivity());
        Picasso.get().load(CurrentUser.getInstance().getUserProfile().getImage()).into(user_CIV_userimage);
    }

    private void goToAddHomeMemberActivity() {
        Intent intent = new Intent(ProfileActivity.this, addHomeMemberActivity.class);
        intent.putExtra("USER_UID", currentUserUid);
        startActivity(intent);
    }

    private void initFirstNameAndLastNameViews() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(currentUserUid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Retrieve the full name from the database
                    String fullName = snapshot.child("name").getValue(String.class);

                    // Split the full name into first name and last name
                    String[] parts = fullName.split(" ");
                    String firstName = parts[0];
                    String lastName = ""; // Initialize last name as empty string

                    // Concatenate all parts after the first one to form the last name
                    for (int i = 1; i < parts.length; i++) {
                        lastName += parts[i] + " ";
                    }
                    lastName = lastName.trim(); // Remove trailing space

                    // Set the retrieved first name and last name to the TextInputLayouts
                    user_TIL_firstName.getEditText().setText(firstName);
                    user_TIL_lastName.getEditText().setText(lastName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }
    private void findViews() {
        user_CIV_userimage = findViewById(R.id.user_CIV_userimage);
        user_TIL_firstName = findViewById(R.id.user_TIL_firstName);
        user_TIL_lastName = findViewById(R.id.user_TIL_lastName);
        user_BTN_save = findViewById(R.id.user_BTN_save);
        user_MTV_addHomeMembers = findViewById(R.id.user_MTV_addHomeMembers);
    }

    private void saveUserProfile() {
        String firstName = user_TIL_firstName.getEditText().getText().toString().trim();
        String lastName = user_TIL_lastName.getEditText().getText().toString().trim();
        String fullName = firstName + " " +  lastName;



        // Check if first name and last name are not empty
        if (!firstName.isEmpty() && !lastName.isEmpty()) {
            // Get reference to the Firebase Realtime Database
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(currentUserUid);

            // Update user profile in the database
            userRef.child("name").setValue(fullName);

            // Display success message
            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            // Display error message if first name or last name is empty
            Toast.makeText(ProfileActivity.this, "First name and last name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

}