package com.example.homie.Models;

import java.util.Date;

public class Event {

    private String description;
    private Date date;

    public Event(){


    }

    public String getDescription() {
        return description;
    }

    public Event setDescription(String description) {
        this.description = description;
        return this;
    }

    public Date getDate() {
        return date;
    }

    public Event setDate(Date date) {
        this.date = date;
        return this;
    }
}
