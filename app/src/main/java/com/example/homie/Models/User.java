package com.example.homie.Models;

import android.net.Uri;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class User {

    private String uid;
    private String name;
    private Uri image;

    private HomeData homeData;

    public User() {
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

    public Uri getImage() {
        return image;
    }

    public User setImage(Uri image) {
        this.image = image;
        return this;
    }




    public HomeData getHomeData() {
        return homeData;
    }

    public User setHomeData(HomeData homeData) {
        this.homeData = homeData;
        return this;
    }
}


