package com.example.homie.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homie.Models.Task;

import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder>{

    private Context context;
    private ArrayList<Task> allTasks;
    public TaskAdapter(Context context, ArrayList<Task> allTasks){
    this.context = context;
    this.allTasks = allTasks;

    }
    @NonNull
    @Override
    public TaskAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflating the layout, giving the look to each row

       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrowtask,parent,false);
        return new TaskAdapter.MyViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.MyViewHolder holder, int position) {
        // Assign values to the views based on the position of the recycler view
        if (allTasks != null) {
            Task currentTask = allTasks.get(position);
            holder.row_RCV_taskDescription.setText(currentTask.getDescription());
            holder.row_RCV_taskCategory.setText(currentTask.getCategory());
        }
    }

    @Override
    public int getItemCount() {
        return allTasks != null ? allTasks.size() : 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
       TextView row_RCV_dateMonths;
         TextView row_RCV_dateDays;
        TextView row_RCV_taskDescription;
        TextView row_RCV_taskCategory;
        ShapeableImageView row_BTN_taskDone;
        ShapeableImageView row_BTN_taskEdit;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            row_RCV_dateMonths = itemView.findViewById(R.id.row_RCV_dateMonths);
            row_RCV_dateDays= itemView.findViewById(R.id.row_RCV_dateDays);
            row_RCV_taskDescription= itemView.findViewById(R.id.row_RCV_taskDescription);
            row_RCV_taskCategory= itemView.findViewById(R.id.row_RCV_taskCategory);
            row_BTN_taskDone= itemView.findViewById(R.id.row_BTN_taskDone);
            row_BTN_taskEdit= itemView.findViewById(R.id.row_BTN_taskEdit);

        }



    }
}
