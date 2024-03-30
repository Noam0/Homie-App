package com.example.homie.Models;

import android.net.Uri;

import java.net.URI;
import java.net.URL;

public class User {

    private String uid;
    private String name;
    private String image;
    private boolean isRegistered;

    private HomeData homeData;

    public User() {

        this.homeData = new HomeData();
    }

    public User(String uid, String name,String image) {
        this.image = image;
        this.uid = uid;
        this.name = name;
        this.homeData = new HomeData();
    }

    public String getUid() {
        return uid;
    }

    public User setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getImage() {
        return image;
    }

    public User setImage(String image) {
        this.image = image;
        return this;
    }


    public boolean isRegistered() {
        return isRegistered;
    }

    public User setRegistered(boolean registered) {
        isRegistered = registered;
        return this;
    }

    public HomeData getHomeData() {
        return homeData;
    }

    public User setHomeData(HomeData homeData) {
        this.homeData = homeData;
        return this;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", image=" + image +
                ", isRegistered=" + isRegistered +
                ", homeData=" + homeData +
                '}';
    }



}


