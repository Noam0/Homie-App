package com.example.homie.Fragments;

import android.app.AlertDialog;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ShoppingFragment extends Fragment {

    private RecyclerView Shopping_RCV_transactionRCV;
    private ShapeableImageView Shopping_BTN_add;

    private ArrayList<GroceryItem> allGroceries;

    private GroceryItemAdapter adapter;

    private MaterialTextView MTV_amount;
    private MaterialTextView MTV_bought;
    private MaterialTextView MTV_abc ;

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
         initSortingButtons();
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
        String groceryDescription = Grocery_format_name.getText().toString();
        String groceryAmount = Grocery_format_amount.getText().toString();

        // Check if groceryDescription and groceryAmount are not empty
        if (!TextUtils.isEmpty(groceryDescription) && !TextUtils.isEmpty(groceryAmount)) {
            // Check if groceryAmount contains only digits
            if (TextUtils.isDigitsOnly(groceryAmount)) {
                // It's a number, proceed with adding the grocery item
                addGrocery(groceryDescription, groceryAmount);
                Toast.makeText(getActivity(), "Shopping Item successfully added!", Toast.LENGTH_SHORT).show();
                Grocery_format_name.setText("");
                Grocery_format_amount.setText("");
            } else {
                // Show an error message indicating invalid input for grocery amount
                Toast.makeText(getActivity(), "Please enter a valid number for item amount", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show an error message indicating that description or amount cannot be empty
            Toast.makeText(getActivity(), "Shopping item description or item amount cannot be empty", Toast.LENGTH_SHORT).show();
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
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(Shopping_RCV_transactionRCV);


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
                String groceryDescription = Grocery_format_name.getText().toString();
                String groceryAmount = Grocery_format_amount.getText().toString();

                // Check if groceryAmount contains only digits
                if (!TextUtils.isEmpty(groceryAmount) && TextUtils.isDigitsOnly(groceryAmount)) {
                    // It's a number, proceed with updating transaction
                    updateTransaction(groceryItem, position, groceryDescription, groceryAmount);
                    Grocery_format_name.setText("");
                    Grocery_format_amount.setText("");
                } else {
                    // Show an error message indicating invalid input
                    Toast.makeText(getContext(), "Please enter a valid number for grocery amount", Toast.LENGTH_SHORT).show();
                }
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
        MTV_amount = view.findViewById(R.id.MTV_amount);
        MTV_bought = view.findViewById(R.id.MTV_bought);
        MTV_abc  = view.findViewById(R.id.MTV_abc);

    }



    private void initSortingButtons() {

        MTV_amount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                MTV_amount.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                MTV_abc.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                MTV_bought.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                sortByAmount();
            }
        });

        MTV_abc.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MTV_amount.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                MTV_abc.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                MTV_bought.setTextColor(ContextCompat.getColor(getContext(), R.color.white));

                sortByABC();
            }
        });

        MTV_bought.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                MTV_amount.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                MTV_abc.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                MTV_bought.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                sortByBought();
            }
        });
    }
    private void sortByAmount() {
        Collections.sort(allGroceries, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem item1, GroceryItem item2) {
                // Compare items based on their amount
                return item1.getAmount() - item2.getAmount();
            }
        });
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    private void sortByABC() {
        Collections.sort(allGroceries, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem item1, GroceryItem item2) {
                // Compare items based on their name alphabetically
                return item1.getName().compareToIgnoreCase(item2.getName());
            }
        });
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    private void sortByBought() {
        Collections.sort(allGroceries, new Comparator<GroceryItem>() {
            @Override
            public int compare(GroceryItem item1, GroceryItem item2) {
                // Compare items based on whether they were bought or not
                return Boolean.compare(item1.isWasBought(), item2.isWasBought());
            }
        });
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0 , ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            // Remove the item from the list
            final GroceryItem deletedItem = CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList().remove(position);
            updateDataBaseGroceries(CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList());
            adapter.notifyDataSetChanged();

            // Get the root view of the fragment layout
            View rootView = getView();

            // Create and show the Snackbar with an action to undo the deletion
            Snackbar.make(rootView, "Grocery deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Add the deleted item back to the list at the original position
                            CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList().add(position, deletedItem);
                            updateDataBaseGroceries(CurrentUser.getInstance().getUserProfile().getHomeData().getGroceryItemsList());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        }
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(),R.color.delete_red_color))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .addSwipeLeftLabel("Delete item")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(),R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }


    };

}