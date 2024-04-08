package com.example.homie.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homie.Interfaces.TransactionCallBack;
import com.example.homie.Models.Transaction;
import com.example.homie.Models.TransactionType;
import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Transaction> allTransactions;

    private TransactionCallBack transactionCallBack;
    //private TransactionCallBack transactionCallBackCallBack;
    public TransactionAdapter(Context context, ArrayList<Transaction> allTransactions){
        this.context = context;
        this.allTransactions = allTransactions;

    }

    public TransactionAdapter setTransactionCallback(TransactionCallBack transactionCallBack) {
        this.transactionCallBack = transactionCallBack;
        return this;
    }

    @NonNull
    @Override
    public TransactionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrowtransaction,parent,false);
        return new TransactionAdapter.MyViewHolder(view) ;

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if( allTransactions != null){

            Transaction transaction = allTransactions.get(position);
            if (transaction.getType() == TransactionType.INCOME) {
                holder.transaction_CV_cardViewBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.incomeColor));
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                holder.transaction_CV_cardViewBackground.setCardBackgroundColor(ContextCompat.getColor(context, R.color.expenseColor));
            }
            holder.row_RCV_transactionAmount.setText(transaction.getAmount()+"");

            String[] dateParts = transaction.getDate().split("-");
            holder.row_RCV_transactiondateDays.setText(dateParts[2]);
            holder.row_RCV_transactiondateMonths.setText(convertDateFormat(dateParts[1]));
            String photoUrl = transaction.getUrlImageOfTransactionMaker();
            Picasso.get().load(photoUrl).into(holder.row_RCV_user);

            holder.row_RCV_transactionDescription.setText(transaction.getDescription());


        }

    }

    private String convertDateFormat(String month) {
        String newMonth;

        // Convert the numeric month string to an integer
        int monthNumber = Integer.parseInt(month);

        // Convert the numeric month to its corresponding month name abbreviation
        switch (monthNumber) {
            case 1:
                newMonth = "Jan";
                break;
            case 2:
                newMonth = "Feb";
                break;
            case 3:
                newMonth = "Mar";
                break;
            case 4:
                newMonth = "Apr";
                break;
            case 5:
                newMonth = "May";
                break;
            case 6:
                newMonth = "Jun";
                break;
            case 7:
                newMonth = "Jul";
                break;
            case 8:
                newMonth = "Aug";
                break;
            case 9:
                newMonth = "Sep";
                break;
            case 10:
                newMonth = "Oct";
                break;
            case 11:
                newMonth = "Nov";
                break;
            case 12:
                newMonth = "Dec";
                break;
            default:
                newMonth = "Invalid Month";
                break;
        }

        return newMonth;
    }




    @Override
    public int getItemCount() {
        return allTransactions != null ? allTransactions.size() : 0;

    }
    public void changeArrayTransaction(ArrayList<Transaction> modifiedAllTransaction){
        this.allTransactions = modifiedAllTransaction;
        notifyDataSetChanged();
    }


    private Transaction getItem(int position) {
        return allTransactions.get(position);
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView row_RCV_user;
        TextView row_RCV_transactiondateMonths;
        TextView row_RCV_transactiondateDays;
        TextView row_RCV_transactionAmount;
        TextView row_RCV_transactionDescription;
        ShapeableImageView row_SIV_check;
        ShapeableImageView row_BTN_transactionEdit;

        CardView transaction_CV_cardViewBackground;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            row_RCV_user = itemView.findViewById(R.id.row_RCV_user);
            row_RCV_transactiondateMonths = itemView.findViewById(R.id.row_RCV_transactiondateMonths);
            row_RCV_transactiondateDays = itemView.findViewById(R.id.row_RCV_transactiondateDays);
            row_RCV_transactionAmount = itemView.findViewById(R.id.row_RCV_transactionAmount);
            row_RCV_transactionDescription = itemView.findViewById(R.id.row_RCV_transactionDescription);
            transaction_CV_cardViewBackground = itemView.findViewById(R.id.transaction_CV_cardViewBackground);
            row_SIV_check = itemView.findViewById(R.id.row_SIV_check);
            row_BTN_transactionEdit = itemView.findViewById(R.id.row_BTN_transactionEdit);

            row_BTN_transactionEdit.setOnClickListener(v -> {

                if(transactionCallBack != null){
                    transactionCallBack.editTransactionClicked(getItem(getAdapterPosition()),getAdapterPosition());
                }



            });


        }
    }
}
