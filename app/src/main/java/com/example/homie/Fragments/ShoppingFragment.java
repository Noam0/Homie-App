package com.example.homie.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.homie.Adapters.GroceryItemAdapter;
import com.example.homie.Adapters.TransactionAdapter;
import com.example.homie.Interfaces.GroceryCallBack;
import com.example.homie.Interfaces.TransactionCallBack;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.GroceryItem;
import com.example.homie.Models.Transaction;
import com.example.homie.Models.TransactionType;
import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;

public class ShoppingFragment extends Fragment {

    private RecyclerView Shopping_RCV_transactionRCV;
    private ShapeableImageView Shopping_BTN_add;

    private ArrayList<GroceryItem> allGroceries;

    private GroceryItemAdapter adapter;


    private ShapeableImageView Grocery_Btn_cancel;
    private EditText Grocery_format_name;
    private EditText Grocery_format_amount;
    private AppCompatButton Grocery_BTN_confirm;

    public ShoppingFragment(ArrayList<GroceryItem> allGroceries) {
    this.allGroceries = allGroceries;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_shopping, container, false);
         findViews(view);
         initRecyclerView();
         initButtonAddAndCancel();
         return view;

    }

    private void initButtonAddAndCancel() {
        Shopping_BTN_add.setOnClickListener(v -> {
            AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myView = inflater.inflate(R.layout.groceryitem_format, null);
            myDialogBuilder.setView(myView);

            final AlertDialog dialog = myDialogBuilder.create();
            dialog.setCancelable(false);
            dialog.show();

            // Find the cancel button inside the dialog view
            Grocery_format_name = myView.findViewById(R.id.Grocery_format_name);
            Grocery_format_amount = myView.findViewById(R.id.Grocery_format_amount);
            ShapeableImageView Grocery_Btn_cancel = myView.findViewById(R.id.Grocery_Btn_cancel);


            Grocery_Btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });

            AppCompatButton Grocery_BTN_confirm = myView.findViewById(R.id.Grocery_BTN_confirm);

            Grocery_BTN_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    addGroceryItemOnClickListener();
                }
            });


        });

        ;
    }

    private void addGroceryItemOnClickListener() {

        String groceryDescription = "";
        String groceryAmount = "";
        if(Grocery_format_name.getText().length() != 0 && Grocery_format_amount.getText().length() != 0){
            groceryDescription = Grocery_format_name.getText().toString();
            groceryAmount = Grocery_format_amount.getText().toString();
            addGrocery(groceryDescription,groceryAmount);
            Toast.makeText(getActivity(), "Transaction successfully added!", Toast.LENGTH_SHORT).show();
            Grocery_format_name.setText("");
            Grocery_format_amount.setText("");
        }else {
            Toast.makeText(getActivity(), "Transaction description or transaction amount cannot be empty", Toast.LENGTH_SHORT).show();

        }

    }

    private void addGrocery(String groceryDescription, String groceryAmount){

        int amount = Integer.parseInt(groceryAmount);


        GroceryItem groceryItem = new GroceryItem(groceryDescription,amount,CurrentUser.getInstance().getUserProfile().getImage());
        allGroceries = CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList();
        allGroceries.add(groceryItem);

        updateDataBaseGroceries(allGroceries);


    }

    private void updateDataBaseGroceries(ArrayList<GroceryItem> allGroceries) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);
        final DatabaseReference grocerysRef = userRef.child("homeData").child("allGroceries");

        grocerysRef.setValue(allGroceries);

        for(String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()){
            DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember);
            DatabaseReference memtasksRef = memRef.child("homeData").child("allGroceries");
            memtasksRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList());

        }

        adapter.changeArrayGroceries(allGroceries);

    }

    private void initRecyclerView() {
            adapter = new GroceryItemAdapter(getActivity().getApplicationContext(), allGroceries);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            Shopping_RCV_transactionRCV.setAdapter(adapter);
            Shopping_RCV_transactionRCV.setLayoutManager(linearLayoutManager);


            adapter.setGroceryCallback(new GroceryCallBack() {
                @Override
                public void editGroceryClicked(GroceryItem groceryItem, int position) {
                    showGroceryItemFormat(groceryItem,position);

                }

                @Override
                public void groceryCheckedClicked(GroceryItem groceryItem, int position) {
                    groceryCheckedUpdated(groceryItem,position);

                }
            });
    }

    private void groceryCheckedUpdated(GroceryItem groceryItem, int position) {
        if(groceryItem.isWasBought()){
            groceryItem.setWasBought(false);
        }else{
            groceryItem.setWasBought(true);
        }
        CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList().set(position,groceryItem);
        adapter.notifyDataSetChanged();
        Log.d("IMHERE189", CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList().get(position).toString());
        updateDataBaseGroceries(CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList());

    }

    private void showGroceryItemFormat(GroceryItem groceryItem, int position) {


        AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View myView = inflater.inflate(R.layout.groceryitem_format, null);

        myDialogBuilder.setView(myView);

        final AlertDialog dialog = myDialogBuilder.create();
        dialog.setCancelable(false);
        dialog.show();

        // Find the cancel button inside the dialog view
        findViewsForDialog(myView);

        Grocery_format_name.setText(groceryItem.getName());
        Grocery_format_amount.setText(groceryItem.getAmount()+"");


        Grocery_Btn_cancel = myView.findViewById(R.id.Grocery_Btn_cancel);

        Grocery_Btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        Grocery_BTN_confirm = myView.findViewById(R.id.Grocery_BTN_confirm);

        Grocery_BTN_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String groceryDescription = "";
                groceryDescription = Grocery_format_name.getText()+"";

                String groceryAmount = "";
                groceryAmount = Grocery_format_amount.getText()+"";
                updateTransaction(groceryItem,position,groceryDescription,groceryAmount);
                Grocery_format_name.setText("");
                Grocery_format_amount.setText("");
            }
        });



    }

    private void updateTransaction(GroceryItem groceryItem, int position, String groceryDescription, String groceryAmount) {


        int amount = Integer.parseInt(groceryAmount);
        groceryItem.setName(groceryDescription);
        groceryItem.setAmount(amount);

        // Update the groceries in the list
        CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList().set(position, groceryItem);

        // Update the database
        updateDataBaseGroceries(CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList());


    }

    private void findViewsForDialog(View myView) {

        Grocery_format_name = myView.findViewById(R.id.Grocery_format_name);
        Grocery_format_amount = myView.findViewById(R.id.Grocery_format_amount);

    }

    private void findViews(View view) {
        Shopping_RCV_transactionRCV = view.findViewById(R.id.Shopping_RCV_transactionRCV);
        Shopping_BTN_add = view.findViewById(R.id.Shopping_BTN_add);

    }


}