package com.example.homie.Models;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;

public class User {

    private String uid;
    private String name;
    private String image;
    private boolean isRegistered;

    private ArrayList<String> homeMembersUid;

    private HomeData homeData;

    public User() {
        this.homeMembersUid = new ArrayList<String>();
        this.homeMembersUid.add(uid);
        this.homeData = new HomeData();
    }

    public User(String uid, String name,String image) {
        this.image = image;
        this.uid = uid;
        this.name = name;
        this.homeData = new HomeData();
        this.homeMembersUid = new ArrayList<String>();
        homeMembersUid.add(uid);
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public User setImage(String image) {
        this.image = image;
        return this;
    }


    public boolean isRegistered() {
        return isRegistered;
    }

    public User setRegistered(boolean registered) {
        isRegistered = registered;
        return this;
    }

    public HomeData getHomeData() {
        return homeData;
    }

    public User setHomeData(HomeData homeData) {
        this.homeData = homeData;
        return this;
    }

    public ArrayList<String> getHomeMembersUid() {
        return homeMembersUid;
    }

    public User setHomeMembersUid(ArrayList<String> homeMembersUid) {
        this.homeMembersUid = homeMembersUid;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", image=" + image +
                ", isRegistered=" + isRegistered +
                ", homeData=" + homeData +
                '}';
    }

    public void addHomeMember(final String memberUid) {
        // Add the member UID to the list of home members
        this.homeMembersUid.add(memberUid);

        // Get a reference to the Firebase Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Construct a DatabaseReference pointing to the user's data location
        DatabaseReference userRef = databaseRef.child("UserInfo").child(uid);

        // Set the updated user object to the database
        userRef.setValue(this)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Data successfully saved to the database
                        // Now, for each existing member in the list, add the newly added member to their list
                        for (String existingMemberUid : homeMembersUid) {
                            if (!existingMemberUid.equals(memberUid) && !homeMembersUid.contains(existingMemberUid)) { // Ensure not to add the new member to their own list
                                addMemberToExistingUser(existingMemberUid, memberUid);
                            }
                            if(existingMemberUid.equals(memberUid)){
                                addMemberToExistingUser(existingMemberUid,uid);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle any errors that occurred while saving data
                    }
                });
    }

    private void addMemberToExistingUser(final String existingMemberUid, final String newMemberUid) {
        // Get a reference to the existing user's data location
        DatabaseReference existingUserRef = FirebaseDatabase.getInstance().getReference().child("UserInfo").child(existingMemberUid);

        // Fetch existing user's data and add the new member UID to their list
        existingUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch existing user's data
                    // Assuming you have a User class, replace User.class with your actual user class
                    User existingUser = dataSnapshot.getValue(User.class);

                    if (existingUser != null) {
                        // Add the new member UID to the existing user's list
                        existingUser.getHomeMembersUid().add(newMemberUid);

                        // Set the updated user object back to the database
                        existingUserRef.setValue(existingUser)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Data successfully saved to the database
                                        // Log.d(TAG, "New member added to existing user's list successfully");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle any errors that occurred while saving data
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}


