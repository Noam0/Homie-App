package com.example.homie.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.homie.Activities.LoginActivity;
import com.example.homie.Activities.MainActivity;
import com.example.homie.Activities.addHomeMemberActivity;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.GroceryItem;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.Models.Transaction;
import com.example.homie.Models.TransactionType;
import com.example.homie.Models.User;
import com.example.homie.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


public class HomeFragment extends Fragment {

    private MaterialTextView home_MTV_UserName;
    private MaterialTextView home_MTV_upComingTask;
    private CircleImageView circular_image_view;

    private CircleImageView[] circularImageViews;
    private GridLayout Home_GridLayout_homeMembers;

    private double totalExpensesThisMonth = 0;
    private double totalIncomeThisMonth = 0;

    private MaterialTextView Home_MTV_monthlyIncome;
    private MaterialTextView Home_MTV_monthlyExpense;
    private MaterialTextView Home_MTV_monthlyTotal ;

    private MaterialTextView home_MTV_GroceryItems;

    private MaterialTextView home_MTV_date;

    private MaterialTextView diaglog_user_info_headline;

    private ShapeableImageView home_SIV_signOut;

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

    @Override
    public void onResume() {
        super.onResume();
        initCurrentUser();
        initHomeFragmentUI();

    }

    private void initCurrentUser() {
        // Check if the current user is already initialized
            // If not initialized, fetch the user data from Firebase
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {
                String userId = currentUser.getUid();
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // User data exists in the database
                            User user = snapshot.getValue(User.class);
                            // Set the current user profile in the singleton instance
                            CurrentUser.getInstance().setUserProfile(user);
                        } else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {

            }
    }

    private void findViews(View view) {
        home_MTV_upComingTask = view.findViewById(R.id.home_MTV_upComingTask);
        circular_image_view = view.findViewById
                (R.id.circular_image_view);
        home_MTV_UserName = view.findViewById(R.id.home_MTV_UserName);
        Home_GridLayout_homeMembers = view.findViewById(R.id.Home_GridLayout_homeMembers);

        Home_MTV_monthlyIncome  = view.findViewById(R.id.Home_MTV_monthlyIncome);
        Home_MTV_monthlyExpense = view.findViewById(R.id.Home_MTV_monthlyExpense);
        Home_MTV_monthlyTotal = view.findViewById(R.id.Home_MTV_monthlyTotal);
        home_MTV_GroceryItems = view.findViewById(R.id.home_MTV_GroceryItems);
        home_MTV_date = view.findViewById(R.id.home_MTV_date);
        home_SIV_signOut = view.findViewById(R.id.home_SIV_signOut);

    }



    private void initHomeFragmentUI() {
        createCircleImageViewHomeMembersArray();
        initHomeDataUi();
        initHomeUserUI();
        initHomeMembersPictures();
        initDate();
        initOnClickListenerOnImages();
        initLogOut();

    }

    private void initLogOut() {
        home_SIV_signOut.setOnClickListener(v->{
            AuthUI.getInstance()
                    .signOut(getContext())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            startActivity(new Intent(getActivity(), LoginActivity.class));
                            getActivity().finish();

                        }

                    });
        });

    }


    private void initDate() {
        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", Locale.ENGLISH);
        String formattedDate = dateFormat.format(currentDate);
        home_MTV_date.setText(formattedDate);

    }



    private void InitViewsOfIncomeAndExpense() {
        Home_MTV_monthlyIncome.setText(totalIncomeThisMonth +"");
        Home_MTV_monthlyExpense.setText(totalExpensesThisMonth+"");
        double total = totalIncomeThisMonth - totalExpensesThisMonth;
        Home_MTV_monthlyTotal.setText(total +"");

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
                        CalculateExpenseAndIncome();
                        InitViewsOfIncomeAndExpense();

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
                        CurrentUser.getInstance().getUserProfile().setHomeData(homeData);
                        //Log.d("IMHERE222", homeData.getAllTasks().toString());

                        //getting All Transactions
                        loadTransactionsFromSnapshot(snapshot);
                        loadGroceriesFromSnapshot(snapshot);

                        initNumberOfItmesInShopView();

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

    private void initNumberOfItmesInShopView() {

        int numberOfItems = 0;

        for(GroceryItem groceryItem : CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList()){
            if(!groceryItem.isWasBought())
                numberOfItems++;
        }

        home_MTV_GroceryItems.setText(numberOfItems + " items in shopping list ");


    }


    private void loadTransactionsFromSnapshot(DataSnapshot snapshot) {
        DataSnapshot transactionsSnapshot = snapshot.child("allTransactions");
        ArrayList<Transaction> allTransactions = new ArrayList<>();
        for (DataSnapshot transactionSnapshot : transactionsSnapshot.getChildren()) {
            Transaction transaction = transactionSnapshot.getValue(Transaction.class);
            allTransactions.add(transaction);
        }
        CurrentUser.getInstance().getUserProfile().getHomeData().setTransactionsList(allTransactions);
    }


    private void loadGroceriesFromSnapshot(DataSnapshot snapshot) {
        DataSnapshot grocerysSnapshot = snapshot.child("allGroceries");
        ArrayList<GroceryItem> allGroceries = new ArrayList<>();
        for (DataSnapshot groceryItemSnapshot : grocerysSnapshot.getChildren()) {
            GroceryItem groceryItem = groceryItemSnapshot.getValue(GroceryItem.class);
            allGroceries.add(groceryItem);
        }
        CurrentUser.getInstance().getUserProfile().getHomeData().setGroceryItemsList(allGroceries);
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


    private void CalculateExpenseAndIncome() {

        for(Transaction transaction : CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList()){
            if(transaction.getType() == TransactionType.EXPENSE ){
                totalExpensesThisMonth += transaction.getAmount();
            }else{
                totalIncomeThisMonth += transaction.getAmount();

            }
        }
    }

    private void showRemoveUserFromHomeDialog(String userUid) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userUid);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String scannedUserName = snapshot.child("name").getValue(String.class);
                    String scannedUrl = snapshot.child("image").getValue(String.class);

                    // Create and display the AlertDialog inside onDataChange
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_info, null);
                    diaglog_user_info_headline = dialogView.findViewById(R.id.diaglog_user_info_headline);
                    diaglog_user_info_headline.setText("Remove From Home?");
                    CircleImageView userImageView = dialogView.findViewById(R.id.circular_image_view);
                    Picasso.get().load(scannedUrl).into(userImageView);
                    TextView userNameTextView = dialogView.findViewById(R.id.userNameTextView);
                    userNameTextView.setText(scannedUserName);

                    // Add a negative button for removing the user
                    builder.setNegativeButton("REMOVE", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle removal of the user
                            removeUserFromHome(userUid);
                        }
                    });

                    builder.setView(dialogView);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    // Handle case where user data doesn't exist
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled event
            }
        });
    }

    private void removeUserFromHome(String userUid) {
        // Remove the user ID from the end user's home members list
        DatabaseReference user2Ref = FirebaseDatabase.getInstance().getReference("UserInfo").child(userUid);
        ArrayList<String> tempUids = new ArrayList<>(CurrentUser.getInstance().getUserProfile().getHomeMembersUid());
        tempUids.remove(CurrentUser.getInstance().getUid());
        user2Ref.child("homeMembersUid").setValue(tempUids);

        // Remove the user ID from the main current user's home members list
        DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(CurrentUser.getInstance().getUid());
        currentUserRef.child("homeMembersUid").child(userUid).removeValue();

        // Update the current user's local homeMembersUid list
        CurrentUser.getInstance().getUserProfile().getHomeMembersUid().remove(userUid);
        currentUserRef.child("homeMembersUid").setValue(CurrentUser.getInstance().getUserProfile().getHomeMembersUid());


        initHomeFragmentUI();
    }

    private void initOnClickListenerOnImages() {
        for (int i = 0; i < circularImageViews.length; i++) {
            int index = i; // Need to use final or effectively final variable in lambda expression
            int finalI = i;
            if(circularImageViews[i]!= null) {
                circularImageViews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String memberUid = CurrentUser.getInstance().getUserProfile().getHomeMembersUid().get(finalI);
                        if(!memberUid.equals(CurrentUser.getInstance().getUid())){
                            showRemoveUserFromHomeDialog(memberUid);
                        }
                    }
                });

            }
        }


    }
}
