package com.example.bright.RescueHelper;

public class FriendCoord {
    public String latitude;
    public String longitude;

    public FriendCoord(){

    }

    public FriendCoord(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}