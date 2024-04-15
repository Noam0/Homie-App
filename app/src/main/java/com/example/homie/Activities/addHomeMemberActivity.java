package com.example.homie.Activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.LongDef;
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

import org.checkerframework.checker.units.qual.C;

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

    private String ScannedUid;


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
        final boolean[] syncUsers = {false};
        if (result.getContents() != null) {

            scannedUID = result.getContents();
            ScannedUid = result.getContents();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(scannedUID);
            String finalScannedUID = scannedUID;
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String scannedUserName = snapshot.child("name").getValue(String.class);
                        String scannedUrl = snapshot.child("image").getValue(String.class);
                        HomeData memberHomeData = snapshot.child("homeData").getValue(HomeData.class);
                        if (CurrentUser.getInstance().getUserProfile().getHomeMembersUid().contains(finalScannedUID)) {
                            showScannedUserData(scannedUserName, scannedUrl, false);
                        } else {
                            //CurrentUser.getInstance().getUserProfile().addHomeMember(finalScannedUID);
                            addUserToHomeMembers(finalScannedUID);
                            syncUsers[0] = true;
                            showScannedUserData(scannedUserName, scannedUrl, true);
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
            this.scannedUIDToGoBackWithToMainActivity = scannedUID;


            //getScannerGroceryItems(scannedUID);
            //getScannerUserAllTasks(scannedUID);
            //getScannerUserAllTransactions(scannedUID);

        }

    });

    private void addUserToHomeMembers(String finalScannedUID) {

        CurrentUser.getInstance().getUserProfile().getHomeMembersUid().add(finalScannedUID);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userUID);
        userRef.child("homeMembersUid").setValue(CurrentUser.getInstance().getUserProfile().getHomeMembersUid());

        for(String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()){
            DatabaseReference userRef2 = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember);
            userRef2.child("homeMembersUid").setValue(CurrentUser.getInstance().getUserProfile().getHomeMembersUid());
        }

        //synchData(scannedUIDToGoBackWithToMainActivity);

    }

    private void synchData(String finalScannedUID) {
        ArrayList<GroceryItem> allGroceries = new ArrayList<>();

        getScannerGroceryItems(allGroceries, finalScannedUID);
        getScannerUserAllTransactions(finalScannedUID);
        getScannerUserAllTasks(finalScannedUID);

        Log.d("188tag", CurrentUser.getInstance().getUserProfile().getHomeData().toString());

        updateDataBaseTransactions();
        updateDataBaseGroceries(allGroceries);
        updateDataBaseTasks();
    }

    private void getScannerGroceryItems(ArrayList<GroceryItem> allGroceries, String scannedUserID) {
        DatabaseReference scannedUserRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(scannedUserID).child("homeData").child("allGroceries");
        scannedUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot grocerySnapshot : snapshot.getChildren()) {
                    GroceryItem groceryItem = grocerySnapshot.getValue(GroceryItem.class);
                    if (groceryItem != null) {
                        allGroceries.add(groceryItem);
                        Log.d("ADDED223", groceryItem.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void getScannerUserAllTasks(String scannedUserID) {
        DatabaseReference scannedUserTasksRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(scannedUserID).child("homeData").child("allTasks");
        scannedUserTasksRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                    Task task = taskSnapshot.getValue(Task.class);
                    if (task != null) {
                        CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks().add(task);
                        Log.d("ADDED223", task.toString());
                    }
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getScannerUserAllTransactions(String scannedUserID) {
        DatabaseReference scannedUserTransactionsRef = FirebaseDatabase.getInstance().getReference("UserInfo")
                .child(scannedUserID).child("homeData").child("allTransactions");
        scannedUserTransactionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot transactionSnapshot : snapshot.getChildren()) {
                    Transaction transaction = transactionSnapshot.getValue(Transaction.class);
                    if (transaction != null) {
                        CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList().add(transaction);
                        Log.d("ADDED223", transaction.toString());
                    }
                }
                // Process or log allTransactions as needed

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


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
        intent.putExtra("SCANNED_USER_ID" ,ScannedUid);
        startActivity(intent);
    }



    private void updateDataBaseTasks() {

        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userUID);
        final DatabaseReference tasksRef = userRef.child("homeData").child("allTasks");

        tasksRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks());


        for(String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()){
            DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember);
            DatabaseReference memtasksRef = memRef.child("homeData").child("allTasks");
            memtasksRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getAllTasks());

        }



    }


    private void updateDataBaseGroceries(ArrayList<GroceryItem> allGroceries) {

        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userUID);
        final DatabaseReference groceriesRef = userRef.child("homeData").child("allGroceries");

        groceriesRef.setValue(allGroceries);


        for(String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()){
            DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember);
            DatabaseReference memtasksRef = memRef.child("homeData").child("allGroceries");
            memtasksRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList());

        }



    }

    private void updateDataBaseTransactions() {

        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userUID);
        final DatabaseReference transactionRef = userRef.child("homeData").child("allTransactions");
        transactionRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList());



        for(String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()){
            DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember);
            DatabaseReference memtasksRef = memRef.child("homeData").child("allTransactions");
            memtasksRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList());

        }

    }





}

