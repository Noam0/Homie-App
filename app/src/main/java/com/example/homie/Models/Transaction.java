package com.example.homie.Models;

import java.util.Date;

public class Transaction {

    private double amount;
    private String description;
    private String category;
    private Date date;
    private TransactionType type;

    public Transaction(){

    }

    public double getAmount() {
        return amount;
    }

    public Transaction setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Transaction setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Transaction setCategory(String category) {
        this.category = category;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Transaction setDate(Date date) {
        this.date = date;
        return this;
    }

    public TransactionType getType() {
        return type;
    }

    public Transaction setType(TransactionType type) {
        this.type = type;
        return this;
    }
}
