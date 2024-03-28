package com.example.homie.Models;

public class GroceryItem {
    private String name;
    private double price;
    private String category;

    public GroceryItem(){

    }

    public String getName() {
        return name;
    }

    public GroceryItem setName(String name) {
        this.name = name;
        return this;
    }

    public double getPrice() {
        return price;
    }

    public GroceryItem setPrice(double price) {
        this.price = price;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public GroceryItem setCategory(String category) {
        this.category = category;
        return this;
    }
}
