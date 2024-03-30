package com.example.homie.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HomeData {

    private Map<String, Task> allTasks; // Change from ArrayList<Task> to Map<String, Task>

    private ArrayList<Transaction> transactionsList;

    private ArrayList<Event> eventsList;

    private ArrayList<GroceryItem> groceryItemsList;


    public HomeData() {
        allTasks = new HashMap<>(); // Initialize as a HashMap
        transactionsList = new ArrayList<>();
        eventsList = new ArrayList<>();
        groceryItemsList = new ArrayList<>();
    }

    public Map<String , Task> getAllTasks() {
        return allTasks;
    }

    public void setAllTasks(Map<String, Task> allTasks) {
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



    public void addTask(Task task,String taskId){
        allTasks.put(taskId,task);

    }

    @Override
    public String toString() {
        return "HomeData{" +
                "allTasks=" + allTasks +
                ", transactionsList=" + transactionsList +
                ", eventsList=" + eventsList +
                ", groceryItemsList=" + groceryItemsList +
                '}';
    }

    public ArrayList<Task> convertTasksToList() {
        ArrayList<Task> taskList = new ArrayList<>(allTasks.values());
        return taskList;
    }
}