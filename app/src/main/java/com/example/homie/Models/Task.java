package com.example.homie.Models;

import java.util.Date;

public class Task {

    private String description;

    private Date deadline;

    private boolean done;
    private String category;

    public Task(){

    }

    public Task(String description,  String category) {
        this.description = description;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public Task setDescription(String description) {
        this.description = description;
        return this;
    }


    public Date getDeadline() {
        return deadline;
    }

    public Task setDeadline(Date deadline) {
        this.deadline = deadline;
        return this;
    }

    public boolean isDone() {
        return done;
    }

    public Task setDone(boolean done) {
        this.done = done;
        return this;
    }

    public String getCategory() {
        return category;
    }

    public Task setCategory(String category) {
        this.category = category;
        return this;
    }
}
