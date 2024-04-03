package com.example.homie.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homie.Models.CurrentUser;
import com.example.homie.Models.HomeData;
import com.example.homie.Models.Task;

import com.example.homie.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
            holder.row_RCV_categoryIMG.setImageResource(getCategoryImage(currentTask));
            String[] dateParts = currentTask.getDeadline().split("-");
            holder.row_RCV_dateDays.setText(dateParts[2]);
            holder.row_RCV_dateMonths.setText(convertDateFormat(dateParts[1]));
        }
    }

    private int getCategoryImage(Task task) {
        int imageResource;

        switch(task.getCategory()) {
            case "Cleaning":
                imageResource = R.drawable.cleaning; // Replace category1_image with your actual image resource
                break;
            case "Cooking":
                imageResource = R.drawable.fryingpan; // Replace category2_image with your actual image resource
                break;
            case "Laundry":
                imageResource = R.drawable.laundry; // Replace category1_image with your actual image resource
                break;
            case "Gardening":
                imageResource = R.drawable.gardening; // Replace category2_image with your actual image resource
                break;
            case "Repairs":
                imageResource = R.drawable.screwdriver; // Replace category1_image with your actual image resource
                break;
            // Add more cases for other categories as needed
            default:
                imageResource = R.drawable.screwdriver; // Replace default_image with a default image resource
                break;
        }

        return imageResource;
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
        return allTasks != null ? allTasks.size() : 0;

    }

    public void changeArrayTask(ArrayList<Task> modifiedAllTasks){
        this.allTasks = modifiedAllTasks;
        notifyDataSetChanged();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder{
       TextView row_RCV_dateMonths;
         TextView row_RCV_dateDays;
        TextView row_RCV_taskDescription;
        ShapeableImageView row_RCV_categoryIMG;
        ShapeableImageView row_BTN_taskDone;
        ShapeableImageView row_BTN_taskEdit;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            row_RCV_dateMonths = itemView.findViewById(R.id.row_RCV_dateMonths);
            row_RCV_dateDays= itemView.findViewById(R.id.row_RCV_dateDays);
            row_RCV_taskDescription= itemView.findViewById(R.id.row_RCV_taskDescription);
            row_RCV_categoryIMG= itemView.findViewById(R.id.row_RCV_categoryIMG);
            row_BTN_taskDone= itemView.findViewById(R.id.row_BTN_taskDone);
            row_BTN_taskEdit= itemView.findViewById(R.id.row_BTN_taskEdit);

        }



    }
}
