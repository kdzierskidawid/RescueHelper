package com.example.bright.RescueHelper;

public class Friends {

    public String date;

    public Friends(){


    }

    public Friends(String date) {
        this.date = date; // taka sama nazwa jak w Firebase
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
