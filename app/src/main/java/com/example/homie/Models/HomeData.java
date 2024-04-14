package com.example.homie.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeData {

    private ArrayList allTasks;

    private ArrayList<Transaction> transactionsList;


    private ArrayList<GroceryItem> groceryItemsList;


    public HomeData() {
        allTasks = new ArrayList(); // Initialize as a HashMap
        transactionsList = new ArrayList<>();
        groceryItemsList = new ArrayList<>();
    }

    public ArrayList<Task> getAllTasks() {
        return allTasks;
    }

    public void setAllTasks(ArrayList<Task> allTasks) {
        this.allTasks = allTasks;
    }

    public ArrayList<Transaction> getTransactionsList() {
        return transactionsList;
    }

    public void setTransactionsList(ArrayList<Transaction> transactionsList) {
        this.transactionsList = transactionsList;
    }



    public ArrayList<GroceryItem> getGroceryItemsList() {
        return groceryItemsList;
    }

    public void setGroceryItemsList(ArrayList<GroceryItem> groceryItemsList) {
        this.groceryItemsList = groceryItemsList;
    }



    public void addTask(Task task){
        allTasks.add(task);

    }
    public void addTransaction(Transaction transaction){
        allTasks.add(transaction);

    }
    @Override
    public String toString() {
        return "HomeData{" +
                "allTasks=" + allTasks +
                ", transactionsList=" + transactionsList +
                ", groceryItemsList=" + groceryItemsList +
                '}';
    }


}