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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.homie.Adapters.TaskAdapter;
import com.example.homie.Adapters.TransactionAdapter;
import com.example.homie.Interfaces.TransactionCallBack;
import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.Task;
import com.example.homie.Models.Transaction;
import com.example.homie.Models.TransactionType;
import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BudgetFragment extends Fragment {
    private AppCompatButton buttonIncome;
    private AppCompatButton buttonOutCome;
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
       // initTransactionList();
        initSpinner();
        initButtonAddAndCancel();
        return view;

    }



    private void findViews(View view) {
        buttonIncome = view.findViewById(R.id.buttonIncome);
        buttonOutCome = view.findViewById(R.id.buttonOutCome);
        Budget_BTN_add = view.findViewById(R.id.Budget_BTN_add);
        Budget_RCV_transactionRCV = view.findViewById(R.id.Budget_RCV_transactionRCV);
    }

    private void initRecyclerView() {
        adapter = new TransactionAdapter(getActivity().getApplicationContext(), allTransactions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        Budget_RCV_transactionRCV.setAdapter(adapter);
        Budget_RCV_transactionRCV.setLayoutManager(linearLayoutManager);

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
                    String transactionDescription = "";
                    transactionDescription = transaction_format_description.getText()+"";

                    String transactionAmount = "";
                    transactionAmount = transaction_format_amount.getText()+"";
                    updateTransaction(transaction,position,incomeOrOutcome,transactionDescription,transactionAmount);
                    transaction_format_amount.setText("");
                    transaction_format_description.setText("");
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

        String incomeOrOutCome = getTransactionkCategoryFromSpinner();//getting category
        Date transactionDate = new Date();
        //taskDeadline = getDeadline();//getting date
        String transactionDescription = "";
        String transactionAmount = "";
        if(transaction_format_description.getText().length() != 0 && transaction_format_amount.getText().length() != 0){
            transactionDescription = getTransactionDescription();
            transactionAmount = getTransactionAmount();
            addTransaction(transactionDescription,transactionAmount,incomeOrOutCome);
            Toast.makeText(getActivity(), "Transaction successfully added!", Toast.LENGTH_SHORT).show();
            transaction_format_description.setText("");
            transaction_format_amount.setText("");
        }else {
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





}