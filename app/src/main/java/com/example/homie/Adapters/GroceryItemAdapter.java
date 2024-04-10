package com.example.homie.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homie.Interfaces.GroceryCallBack;
import com.example.homie.Interfaces.TransactionCallBack;
import com.example.homie.Models.GroceryItem;
import com.example.homie.Models.Task;
import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroceryItemAdapter extends RecyclerView.Adapter<GroceryItemAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<GroceryItem> allGroceries;

    private GroceryCallBack groceryCallBack;

    public GroceryItemAdapter(Context context, ArrayList<GroceryItem> allGroceries){
        this.context = context;
        this.allGroceries = allGroceries;

    }
    public GroceryItemAdapter setGroceryCallback(GroceryCallBack groceryCallBack) {
        this.groceryCallBack = groceryCallBack;
        return this;
    }





    @NonNull
    @Override
    public GroceryItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout, giving the look to each row
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrowgroceryitem,parent,false);
        return new GroceryItemAdapter.MyViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull GroceryItemAdapter.MyViewHolder holder, int position) {

        // Assign values to the views based on the position of the recycler view
        if (allGroceries != null) {
            GroceryItem currentGrocery = allGroceries.get(position);
            holder.row_RCV_GroceryDescription.setText(currentGrocery.getName());
            if (currentGrocery.isWasBought()) {
                holder.row_SIV_GroceryAddedToCart.setVisibility(View.VISIBLE);
            } else {
                holder.row_SIV_GroceryAddedToCart.setVisibility(View.INVISIBLE);
            }

            String photoUrl = currentGrocery.getUrlImageOfTransactionMaker();
            Picasso.get().load(photoUrl).into(holder.row_RCV_user);

            holder.row_RCV_GroceryAmount.setText(currentGrocery.getAmount() +"");

        }
    }




    @Override
    public int getItemCount() {
        return allGroceries != null ? allGroceries.size() : 0;

    }

    private GroceryItem getItem(int position) {
        return allGroceries.get(position);
    }


    public void changeArrayGroceries(ArrayList<GroceryItem> modifiedAllGroceries){
        this.allGroceries = modifiedAllGroceries;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView Grocery_CV_cardViewBackground;
        private CircleImageView row_RCV_user;
        private TextView row_RCV_GroceryDescription;
        private ShapeableImageView row_BTN_GroceryEdit;

        private ShapeableImageView row_BTN_GroceryAddedFill;
        private ShapeableImageView row_SIV_GroceryAddedToCart;

        private MaterialTextView row_RCV_GroceryAmount;

        private FrameLayout grocery_FL_checker;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Grocery_CV_cardViewBackground = itemView.findViewById(R.id.Grocery_CV_cardViewBackground);
            row_RCV_user = itemView.findViewById(R.id.row_RCV_user);
            row_RCV_GroceryDescription = itemView.findViewById(R.id.row_RCV_GroceryDescription);
            row_BTN_GroceryEdit = itemView.findViewById(R.id.row_BTN_GroceryEdit);
            row_BTN_GroceryAddedFill = itemView.findViewById(R.id.row_BTN_GroceryAddedFill);
            row_SIV_GroceryAddedToCart = itemView.findViewById(R.id.row_SIV_GroceryAddedToCart);
            row_RCV_GroceryAmount = itemView.findViewById(R.id.row_RCV_GroceryAmount);
            grocery_FL_checker = itemView.findViewById(R.id.grocery_FL_checker);

            row_BTN_GroceryEdit.setOnClickListener(v -> {

                if (groceryCallBack != null) {
                    groceryCallBack.editGroceryClicked(getItem(getAdapterPosition()), getAdapterPosition());
                }
            });


            row_BTN_GroceryAddedFill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (groceryCallBack != null) {
                        Log.d("imrighthere223", "taskCheckedClicked: ");
                        groceryCallBack.groceryCheckedClicked(getItem(getAdapterPosition()), getAdapterPosition());
                    }
                }
            });
        }
    }

}
