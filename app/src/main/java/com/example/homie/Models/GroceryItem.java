package com.example.homie.Models;

public class GroceryItem {
    private String name;

    private boolean wasBought;

    private String urlImageOfTransactionMaker;

    private int amount;
    public GroceryItem(){

    }

    public GroceryItem(String name,int amount, String urlImageOfTransactionMaker) {
        this.name = name;
        this.amount = amount;
        this.urlImageOfTransactionMaker = urlImageOfTransactionMaker;
    }

    public int getAmount() {
        return amount;
    }

    public GroceryItem setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public String getUrlImageOfTransactionMaker() {
        return urlImageOfTransactionMaker;
    }

    public GroceryItem setUrlImageOfTransactionMaker(String urlImageOfTransactionMaker) {
        this.urlImageOfTransactionMaker = urlImageOfTransactionMaker;
        return this;
    }

    public boolean isWasBought() {
        return wasBought;
    }

    public GroceryItem setWasBought(boolean wasBought) {
        this.wasBought = wasBought;
        return this;
    }

    public String getName() {
        return name;
    }

    public GroceryItem setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public String toString() {
        return "GroceryItem{" +
                "name='" + name + '\'' +
                ", wasBought=" + wasBought +
                ", urlImageOfTransactionMaker='" + urlImageOfTransactionMaker + '\'' +
                ", amount=" + amount +
                '}';
    }
}
