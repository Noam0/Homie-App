package com.example.homie.Models;

import java.util.Date;

public class Transaction {

    private double amount;
    private String description;
    private String date;
    private TransactionType type;
    private String urlImageOfTransactionMaker;


    public Transaction(){

    }

    public Transaction(double amount, String description, String date, TransactionType type, String urlImageOfTransactionMaker) {
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
        this.urlImageOfTransactionMaker = urlImageOfTransactionMaker;
    }

    public String getUrlImageOfTransactionMaker() {
        return urlImageOfTransactionMaker;
    }

    public Transaction setUrlImageOfTransactionMaker(String uidOfTransactionMaker) {
        this.urlImageOfTransactionMaker = uidOfTransactionMaker;
        return this;
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



    public String getDate() {
        return date;
    }

    public Transaction setDate(String date) {
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
