package com.example.homie.Interfaces;

import com.example.homie.Models.GroceryItem;
import com.example.homie.Models.Task;

public interface GroceryCallBack {

    void editGroceryClicked(GroceryItem groceryItem, int position);
    void groceryCheckedClicked(GroceryItem groceryItem, int position);
}
