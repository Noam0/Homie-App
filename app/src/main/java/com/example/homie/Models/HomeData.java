package com.example.homie.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeData {

    private ArrayList<Task> allTasks;
    private ArrayList<Transaction> transactionsList;
    private ArrayList<Event> eventsList;
    private ArrayList<GroceryItem> groceryItemsList;



    public HomeData() {
        allTasks = new ArrayList<>();
        transactionsList = new ArrayList<>();
        eventsList = new ArrayList<>();
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

    public ArrayList<Event> getEventsList() {
        return eventsList;
    }

    public void setEventsList(ArrayList<Event> eventsList) {
        this.eventsList = eventsList;
    }

    public ArrayList<GroceryItem> getGroceryItemsList() {
        return groceryItemsList;
    }

    public void setGroceryItemsList(ArrayList<GroceryItem> groceryItemsList) {
        this.groceryItemsList = groceryItemsList;
    }

}