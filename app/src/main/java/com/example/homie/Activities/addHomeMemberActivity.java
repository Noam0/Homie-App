package com.example.homie.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.GroceryItem;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;
import com.example.homie.Models.Transaction;
import com.example.homie.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class addHomeMemberActivity extends AppCompatActivity {
    private ImageView AMA_IMG_QRCode;
    private MaterialTextView AMA_MTV_scan;
    private String userUID;

    private MaterialTextView diaglog_user_info_headline;
    private String scannedUIDToGoBackWithToMainActivity;

    private AppCompatButton SCAN_ACB_goBack;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase firebaseDB = FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home_member);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        findViews();
        userUID = getIntent().getStringExtra("USER_UID");
        try {
            initQRcodeImage(userUID);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        AMA_MTV_scan.setOnClickListener(v->{

            scanCode();

        });

        SCAN_ACB_goBack.setOnClickListener(v ->{

            goToMainActivity();
        });

    }



    private void findViews() {
        AMA_IMG_QRCode = findViewById(R.id.AMA_IMG_QRCode);
        AMA_MTV_scan = findViewById(R.id.AMA_MTV_scan);
        SCAN_ACB_goBack = findViewById(R.id.SCAN_ACB_goBack);

    }

    private void initQRcodeImage(String userUID) throws WriterException {

        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(userUID, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            AMA_IMG_QRCode.setImageBitmap(bitmap);
        }catch (WriterException e) {
            throw new RuntimeException(e);
        }

    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        String scannedUID = null;
        if (result.getContents() != null) {
            scannedUID = result.getContents();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(scannedUID);
            String finalScannedUID = scannedUID;
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String scannedUserName = snapshot.child("name").getValue(String.class);
                        String scannedUrl = snapshot.child("image").getValue(String.class);
                        HomeData memberHomeData = snapshot.child("homeData").getValue(HomeData.class);
                        //Log.d("IMHERE133", memberHomeData.toString());
                        if (CurrentUser.getInstance().getUserProfile().getHomeMembersUid().contains(finalScannedUID)){
                            showScannedUserData(scannedUserName, scannedUrl,false);
                        }else {
                            CurrentUser.getInstance().getUserProfile().addHomeMember(finalScannedUID);
                            showScannedUserData(scannedUserName, scannedUrl, true);
                            getAllTasksSnapshot();
                            getAllGroceryItemsSnapshot();
                            getAllTransactionsSnapshot();
                            saveHomeDataToScannedUser();




                        }

                    } else {
                        showErrorMessage("User not found in the database.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    showErrorMessage("Error occurred while fetching user data.");
                }
            });
        }
        this.scannedUIDToGoBackWithToMainActivity = scannedUID;
    });







    private void showScannedUserData(String userName, String imageUrl ,boolean succesfull) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addHomeMemberActivity.this);

        // Inflate custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_info, null);

        // Find views in the custom layout
        CircleImageView userImageView = dialogView.findViewById(R.id.circular_image_view);
        TextView userNameTextView = dialogView.findViewById(R.id.userNameTextView);

        // Set user name and image
        userNameTextView.setText(userName);
        Picasso.get().load(imageUrl).into(userImageView);

        // Set custom view to the dialog
        builder.setView(dialogView);

       if(!succesfull){
          diaglog_user_info_headline = dialogView.findViewById(R.id.diaglog_user_info_headline);
          diaglog_user_info_headline.setText("already your home member");
        }

        // Set positive button
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
    }

    private void showErrorMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addHomeMemberActivity.this);
        builder.setTitle("Error");
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss()).show();
    }


    private void goToMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    //COMBINING THE DATA TO THE CURRENT USER CODE:
    private void getAllTasksSnapshot(){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(this.scannedUIDToGoBackWithToMainActivity)
                .child("homeData")
                .child("allTasks");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                        Task task = taskSnapshot.getValue(Task.class);
                        if (task != null) {
                            // Add the task to the current user's HomeData
                            CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().add(task);
                        }
                    }
                    // Once all tasks are added, save the updated tasks to the database
                    saveAllTasksToDatabase(CurrentUser.getInstance().getUid());
                } else {
                    // Handle case where the allTasks array doesn't exist for the scanned user
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    public void saveAllTasksToDatabase(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId).child("homeData").child("allTasks");
        ArrayList<Task> allTasks = CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks();
        userRef.setValue(allTasks).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {

            }

        });
    }


    private void getAllTransactionsSnapshot() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(scannedUIDToGoBackWithToMainActivity)
                .child("homeData")
                .child("allTransactions");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                        Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            // Add the transaction to the current user's HomeData
                            CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList().add(transaction);
                        }
                    }
                    // Once all transactions are added, save the updated transactions to the database
                    saveAllTransactionsToDatabase(CurrentUser.getInstance().getUid());
                } else {
                    // Handle case where the allTransactions array doesn't exist for the scanned user
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    public void saveAllTransactionsToDatabase(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(userId)
                .child("homeData")
                .child("allTransactions");
        ArrayList<Transaction> allTransactions = CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList();
        userRef.setValue(allTransactions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                // Handle completion if needed
            }
        });
    }



    private void getAllGroceryItemsSnapshot() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(scannedUIDToGoBackWithToMainActivity)
                .child("homeData")
                .child("allGroceries");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot groceryItemSnapshot : snapshot.getChildren()) {
                        GroceryItem groceryItem = groceryItemSnapshot.getValue(GroceryItem.class);
                        if (groceryItem != null) {
                            // Add the grocery item to the current user's HomeData
                            CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList().add(groceryItem);
                        }
                    }
                    // Once all grocery items are added, save the updated items to the database
                    saveAllGroceriesToDatabase(CurrentUser.getInstance().getUid());
                } else {
                    // Handle case where the allGroceries array doesn't exist for the scanned user
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }
    public void saveAllGroceriesToDatabase(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(userId)
                .child("homeData")
                .child("allGroceries");
        ArrayList<GroceryItem> allGroceries = CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList();
        userRef.setValue(allGroceries).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                // Handle completion if needed
            }
        });
    }


    public void saveAllTasksToDatabase(String userId, ArrayList<Task> allTasks) {
        DatabaseReference tasksRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(userId)
                .child("homeData")
                .child("allTasks");
        tasksRef.setValue(allTasks)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Handle completion if needed
                    } else {
                        // Handle failure
                    }
                });
    }

    public void saveAllGroceriesToDatabase(String userId, ArrayList<GroceryItem> allGroceries) {
        DatabaseReference groceriesRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(userId)
                .child("homeData")
                .child("allGroceries");
        groceriesRef.setValue(allGroceries)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Handle completion if needed
                    } else {
                        // Handle failure
                    }
                });
    }

    public void saveAllTransactionsToDatabase(String userId, ArrayList<Transaction> allTransactions) {
        DatabaseReference transactionsRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(userId)
                .child("homeData")
                .child("allTransactions");
        transactionsRef.setValue(allTransactions)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Handle completion if needed
                    } else {
                        // Handle failure
                    }
                });
    }


    private void saveHomeDataToScannedUser() {

            HomeData homeData = CurrentUser.getInstance().getUserProfile().getHomeData();

            for (String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()) {
                if (!homeMember.equals(CurrentUser.getInstance().getUid())) {
                    DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember).child("homeData");

                    // Save all tasks
                    DatabaseReference tasksRef = memRef.child("allTasks");
                    tasksRef.setValue(homeData.getAllTasks());

                    // Save all groceries
                    DatabaseReference groceriesRef = memRef.child("allGroceries");
                    groceriesRef.setValue(homeData.getGroceryItemsList());

                    // Save all transactions
                    DatabaseReference transactionsRef = memRef.child("allTransactions");
                    transactionsRef.setValue(homeData.getTransactionsList());
                }
            }
        }

}

