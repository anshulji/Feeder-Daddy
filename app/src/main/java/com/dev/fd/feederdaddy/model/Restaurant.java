package com.dev.fd.feederdaddy.model;

public class Restaurant {

    private String Name,Image,Veg,Nonveg,Latitude,Longitude,Rating,Totalrates,Isopen,id,Opentime,Isbakery;

    public Restaurant() {
    }

    public Restaurant(String name, String image, String veg, String nonveg, String latitude, String longitude, String rating, String totalrates, String isopen, String id, String opentime, String isbakery) {
        Name = name;
        Image = image;
        Veg = veg;
        Nonveg = nonveg;
        Latitude = latitude;
        Longitude = longitude;
        Rating = rating;
        Totalrates = totalrates;
        Isopen = isopen;
        this.id = id;
        Opentime = opentime;
        Isbakery = isbakery;

    }

    public String getIsbakery() {
        return Isbakery;
    }

    public void setIsbakery(String isbakery) {
        Isbakery = isbakery;
    }

    public String getOpentime() {
        return Opentime;
    }

    public void setOpentime(String opentime) {
        Opentime = opentime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public String getTotalrates() {
        return Totalrates;
    }

    public void setTotalrates(String totalrates) {
        Totalrates = totalrates;
    }

    public String getIsopen() {
        return Isopen;
    }

    public void setIsopen(String isopen) {
        Isopen = isopen;
    }

    public String getVeg() {
        return Veg;
    }

    public void setVeg(String veg) {
        Veg = veg;
    }

    public String getNonveg() {
        return Nonveg;
    }

    public void setNonveg(String nonveg) {
        Nonveg = nonveg;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
