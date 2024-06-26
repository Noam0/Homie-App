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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.homie.Adapters.TaskAdapter;
import com.example.homie.Adapters.TransactionAdapter;
import com.example.homie.Interfaces.TransactionCallBack;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.GroceryItem;
import com.example.homie.Models.Task;
import com.example.homie.Models.Transaction;
import com.example.homie.Models.TransactionType;
import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class BudgetFragment extends Fragment {
    private MaterialTextView buttonIncome;
    private MaterialTextView buttonOutCome;
    private MaterialTextView MTV_user;
    private ShapeableImageView Budget_BTN_add;
    private RecyclerView Budget_RCV_transactionRCV;
    private ArrayList<Transaction> allTransactions;
    private TransactionAdapter adapter;

    private ShapeableImageView Transaction_Btn_cancel;
    private EditText transaction_format_description;
    private EditText transaction_format_amount;
    private Spinner transaction_format_category_spinner;
    private AppCompatButton addTransaction_BTN_confirm;

    public BudgetFragment(ArrayList<Transaction> allTransactionsList) {
        this.allTransactions = allTransactionsList;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        findViews(view);
        initRecyclerView();
        initSortingButtons();
        //initTransactionList();
        initSpinner();
        initButtonAddAndCancel();

        return view;

    }



    private void findViews(View view) {
        buttonIncome = view.findViewById(R.id.buttonIncome);
        buttonOutCome = view.findViewById(R.id.buttonOutCome);
        MTV_user = view.findViewById(R.id.MTV_user);
        Budget_BTN_add = view.findViewById(R.id.Budget_BTN_add);
        Budget_RCV_transactionRCV = view.findViewById(R.id.Budget_RCV_transactionRCV);
    }

    private void initSortingButtons() {
        buttonIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIncome.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                buttonOutCome.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                MTV_user.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                sortByIncome();
            }
        });

        buttonOutCome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIncome.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonOutCome.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                MTV_user.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                sortByOutcome();
            }
        });


        MTV_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonIncome.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                buttonOutCome.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                MTV_user.setTextColor(ContextCompat.getColor(getContext(), R.color.clicked_text_color));
                sortByUser();
            }
        });
    }

    private void sortByUser() {

        Log.d("IMHERE130", allTransactions.toString());
        Collections.sort(allTransactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction transaction1, Transaction transaction2) {
                // Compare transactions based on their URL image
                return transaction1.getUrlImageOfTransactionMaker().compareTo(transaction2.getUrlImageOfTransactionMaker());
            }
        });
        Log.d("IMHERE130", allTransactions.toString());
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    private void sortByIncome() {
        Collections.sort(allTransactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction transaction1, Transaction transaction2) {
                // Debugging: Log transaction types
                Log.d("Transaction Type 1", transaction1.getType().toString());
                Log.d("Transaction Type 2", transaction2.getType().toString());

                // Compare transactions based on their type (Income first)
                if (transaction1.getType() == TransactionType.INCOME && transaction2.getType() == TransactionType.EXPENSE) {
                    return -1;
                } else if (transaction1.getType() == TransactionType.EXPENSE && transaction2.getType() == TransactionType.INCOME) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }

    private void sortByOutcome() {
        Collections.sort(allTransactions, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction transaction1, Transaction transaction2) {
                // Debugging: Log transaction types
                Log.d("Transaction Type 1", transaction1.getType().toString());
                Log.d("Transaction Type 2", transaction2.getType().toString());

                // Compare transactions based on their type (Outcome first)
                if (transaction1.getType() == TransactionType.EXPENSE && transaction2.getType() == TransactionType.INCOME) {
                    return -1;
                } else if (transaction1.getType() == TransactionType.INCOME && transaction2.getType() == TransactionType.EXPENSE) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        adapter.notifyDataSetChanged(); // Notify adapter of data change
    }






    private void initRecyclerView() {
        adapter = new TransactionAdapter(getActivity().getApplicationContext(), allTransactions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Budget_RCV_transactionRCV.setAdapter(adapter);
        Budget_RCV_transactionRCV.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(Budget_RCV_transactionRCV);

        adapter.setTransactionCallback(new TransactionCallBack() {
            @Override
            public void editTransactionClicked(Transaction transaction, int position) {
                showTransactionFormat(transaction,position);

            }
        });
    }

    private void showTransactionFormat(Transaction transaction, int position) {


            AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myView = inflater.inflate(R.layout.transaction_format, null);

            myDialogBuilder.setView(myView);

            final AlertDialog dialog = myDialogBuilder.create();
            dialog.setCancelable(false);
            dialog.show();

            // Find the cancel button inside the dialog view
            findViewsForDialog(myView);

            transaction_format_description.setText(transaction.getDescription());
            transaction_format_amount.setText(transaction.getAmount()+"");

            int spinnerPosition = transaction.getType() == TransactionType.EXPENSE ? 0 : 1;
            transaction_format_category_spinner.setSelection(spinnerPosition);
            Transaction_Btn_cancel = myView.findViewById(R.id.Transaction_Btn_cancel);

            Transaction_Btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });

            addTransaction_BTN_confirm = myView.findViewById(R.id.addTransaction_BTN_confirm);

            addTransaction_BTN_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String incomeOrOutcome = getTransactionkCategoryFromSpinner();
                    String transactionDescription = transaction_format_description.getText().toString();
                    String transactionAmount = transaction_format_amount.getText().toString();

                    // Check if transactionDescription and transactionAmount are not empty
                    if (!TextUtils.isEmpty(transactionDescription) && !TextUtils.isEmpty(transactionAmount)) {
                        // Check if transactionAmount contains only digits
                        if (TextUtils.isDigitsOnly(transactionAmount)) {
                            // It's a number, proceed with updating the transaction
                            updateTransaction(transaction, position, incomeOrOutcome, transactionDescription, transactionAmount);
                            Toast.makeText(getActivity(), "Transaction successfully updated!", Toast.LENGTH_SHORT).show();
                            transaction_format_amount.setText("");
                            transaction_format_description.setText("");
                        } else {
                            // Show an error message indicating invalid input for transaction amount
                            Toast.makeText(getActivity(), "Please enter a valid number for transaction amount", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Show an error message indicating that description or amount cannot be empty
                        Toast.makeText(getActivity(), "Transaction description or transaction amount cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    }

    private void updateTransaction(Transaction transaction, int position, String incomeOrOutcome, String transactionDescription, String transactionAmount) {



        TransactionType type = incomeOrOutcome.equalsIgnoreCase("EXPENSE") ? TransactionType.EXPENSE : TransactionType.INCOME;

        double amount = Double.parseDouble(transactionAmount);
        transaction.setDescription(transactionDescription);
        transaction.setType(type);
        transaction.setAmount(amount);

        // Update the transaction in the list
        CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList().set(position, transaction);

        // Update the database
        updateDataBaseTransaction(CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList());



    }

    private int categoryToInt(String category){

        int categoryNum = 1;
        switch (category) {
            case "Expense":
                categoryNum = 0;
                break;
            case "Income":
                categoryNum = 1;
                break;
            default:
                categoryNum = 0;
                break;
        }
        return categoryNum;
    }

    private void findViewsForDialog(View myView) {


        transaction_format_description = myView.findViewById(R.id.transaction_format_description);
        transaction_format_amount = myView.findViewById(R.id.transaction_format_amount);
        transaction_format_category_spinner = myView.findViewById(R.id.transaction_format_category_spinner);

    }

    private void initTransactionList() {

        allTransactions = new ArrayList<Transaction>();
        allTransactions = CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList();
    }



    private void initButtonAddAndCancel() {

        Budget_BTN_add.setOnClickListener(v -> {
            AlertDialog.Builder myDialogBuilder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View myView = inflater.inflate(R.layout.transaction_format, null);
            myDialogBuilder.setView(myView);

            final AlertDialog dialog = myDialogBuilder.create();
            dialog.setCancelable(false);
            dialog.show();

            // Find the cancel button inside the dialog view
            transaction_format_description = myView.findViewById(R.id.transaction_format_description);
            transaction_format_amount = myView.findViewById(R.id.transaction_format_amount);
            transaction_format_category_spinner = myView.findViewById(R.id.transaction_format_category_spinner);
            ShapeableImageView Transaction_Btn_cancel = myView.findViewById(R.id.Transaction_Btn_cancel);




            Transaction_Btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Dismiss the dialog
                    dialog.dismiss();
                }
            });

            AppCompatButton addTransaction_BTN_confirm = myView.findViewById(R.id.addTransaction_BTN_confirm);

            addTransaction_BTN_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                   addTransactionOnClickListener();
                }
            });


        });

        ;


    }

    private void addTransactionOnClickListener() {
        String incomeOrOutCome = getTransactionkCategoryFromSpinner(); // Getting category
        String transactionDescription = transaction_format_description.getText().toString();
        String transactionAmount = transaction_format_amount.getText().toString();

        // Check if transactionDescription and transactionAmount are not empty
        if (!TextUtils.isEmpty(transactionDescription) && !TextUtils.isEmpty(transactionAmount)) {
            // Check if transactionAmount contains only digits
            if (TextUtils.isDigitsOnly(transactionAmount)) {
                // It's a number, proceed with adding the transaction
                addTransaction(transactionDescription, transactionAmount, incomeOrOutCome);
                Toast.makeText(getActivity(), "Transaction successfully added!", Toast.LENGTH_SHORT).show();
                transaction_format_description.setText("");
                transaction_format_amount.setText("");
            } else {
                // Show an error message indicating invalid input for transaction amount
                Toast.makeText(getActivity(), "Please enter a valid number for transaction amount", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Show an error message indicating that description or amount cannot be empty
            Toast.makeText(getActivity(), "Transaction description or transaction amount cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
    private void addTransaction(final String transactionDescription,String transactionAmount,String inComeOrOutCome) {

        double amount = Double.parseDouble(transactionAmount);
        Date currentDate = new Date();

        String dateAsAString = formatDateToString(currentDate);

        TransactionType type = TransactionType.fromString(inComeOrOutCome);
        Transaction transaction = new Transaction(amount,transactionDescription,dateAsAString,type,CurrentUser.getInstance().getUserProfile().getImage());
        allTransactions = CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList();
        allTransactions.add(transaction);

        updateDataBaseTransaction(allTransactions);




    }


    private String formatDateToString(Date date) {
        // Create a SimpleDateFormat object with the desired date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("DATE183", dateFormat.format(date));

        // Format the date object using the SimpleDateFormat
        return dateFormat.format(date);
    }

    private void updateDataBaseTransaction(ArrayList<Transaction> allTransactions) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Get a reference to the user's data in the database
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(userId);
        final DatabaseReference transactionRef = userRef.child("homeData").child("allTransactions");

        transactionRef.setValue(allTransactions);

        for(String homeMember : CurrentUser.getInstance().getUserProfile().getHomeMembersUid()){
            DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("UserInfo").child(homeMember);
            DatabaseReference memtasksRef = memRef.child("homeData").child("allTransactions");
            memtasksRef.setValue(CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList());

        }

       adapter.changeArrayTransaction(allTransactions);

    }


    private String getTransactionDescription() {
        return transaction_format_description.getText().toString();
    }
    private String getTransactionAmount() {
        return transaction_format_amount.getText().toString();
    }

    private String getTransactionkCategoryFromSpinner() {
        return transaction_format_category_spinner.getSelectedItem().toString();
    }



    private void initSpinner() {
        // Inflate the layout containing the Spinner
        View spinnerLayout = getLayoutInflater().inflate(R.layout.transaction_format, null);
        // Find the Spinner within the inflated layout
        Spinner categorySpinner = spinnerLayout.findViewById(R.id.transaction_format_category_spinner);


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item);
        arrayAdapter.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        categorySpinner.setAdapter(arrayAdapter);

        // Set the item selection listener for the spinner
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String item = adapterView.getItemAtPosition(position).toString();
                // Handle item selection here if needed
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle nothing selected if needed
            }
        });
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
            final Transaction deletedItem = CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList().remove(position);
            updateDataBaseTransaction(CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList());
            adapter.notifyDataSetChanged();

            // Get the root view of the fragment layout
            View rootView = getView();

            // Create and show the Snackbar with an action to undo the deletion
            Snackbar.make(rootView, "Transaction deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Add the deleted item back to the list at the original position
                            CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList().add(position, deletedItem);
                            updateDataBaseTransaction(CurrentUser.getInstance().getUserProfile().getHomeData().getTransactionsList());
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .show();
        }
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(),R.color.delete_red_color))
                    .addSwipeLeftActionIcon(R.drawable.delete)
                    .addSwipeLeftLabel("Delete Transaction")
                    .setSwipeLeftLabelColor(ContextCompat.getColor(getContext(),R.color.white))
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

    };




}