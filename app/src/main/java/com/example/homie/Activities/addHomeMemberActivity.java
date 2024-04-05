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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.R;
import com.google.android.material.textview.MaterialTextView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class addHomeMemberActivity extends AppCompatActivity {
    private ImageView AMA_IMG_QRCode;
    private MaterialTextView AMA_MTV_scan;
    private String userUID;

    private String scannedUIDToGoBackWithToMainActivity;

    private AppCompatButton SCAN_ACB_goBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_home_member);
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
                        //HomeData memberHomeData = snapshot.child("HomeData").getValue(HomeData.class);
                        showScannedUserData(scannedUserName, scannedUrl);
                        CurrentUser.getInstance().getUserProfile().addHomeMember(finalScannedUID);

                        //synchHomeMembersData(finalScannedUID,memberHomeData);
                        //addHomeMemberUseScanned(scannedUID);
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

    private void synchHomeMembersData(String scannedUID, HomeData newHomeData) {





    }

    private void showScannedUserData(String userName, String imageUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(addHomeMemberActivity.this);

        // Inflate custom layout for the dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_info, null);

        // Find views in the custom layout
        CircleImageView userImageView = dialogView.findViewById(R.id.circular_image_view);
        TextView userNameTextView = dialogView.findViewById(R.id.userNameTextView);

        // Set user name and image
        userNameTextView.setText(userName);
        Picasso.get().load(imageUrl).into(userImageView); // Assuming you are using Picasso for image loading

        // Set custom view to the dialog
        builder.setView(dialogView);

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

}