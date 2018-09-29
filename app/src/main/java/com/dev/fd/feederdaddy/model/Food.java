package com.dev.fd.feederdaddy.model;

import java.util.List;

public class Food {
    private String Name,Image,Description,Quarterprice,Halfprice,Fullprice,Quarterdiscount,Halfdiscount,Fulldiscount,Veg,Nonveg,Rating,Totalrates;

   // private List<AddOnsModel> Addons;

    public Food(){

    }

    public Food(String name, String image, String description, String quarterprice, String halfprice, String fullprice, String quarterdiscount, String halfdiscount, String fulldiscount, String veg, String nonveg, String rating, String totalrates
            //, List<AddOnsModel> addons
                ) {
        Name = name;
        Image = image;
        Description = description;
        Quarterprice = quarterprice;
        Halfprice = halfprice;
        Fullprice = fullprice;
        Quarterdiscount = quarterdiscount;
        Halfdiscount = halfdiscount;
        Fulldiscount = fulldiscount;
        Veg = veg;
        Nonveg = nonveg;
        Rating = rating;
        Totalrates = totalrates;
       // Addons = addons;
    }

    /*public List<AddOnsModel> getAddons() {
        return Addons;
    }

    public void setAddons(List<AddOnsModel> addons) {
        Addons = addons;
    }*/

    public String getQuarterprice() {
        return Quarterprice;
    }

    public void setQuarterprice(String quarterprice) {
        Quarterprice = quarterprice;
    }

    public String getHalfprice() {
        return Halfprice;
    }

    public void setHalfprice(String halfprice) {
        Halfprice = halfprice;
    }

    public String getFullprice() {
        return Fullprice;
    }

    public void setFullprice(String fullprice) {
        Fullprice = fullprice;
    }

    public String getQuarterdiscount() {
        return Quarterdiscount;
    }

    public void setQuarterdiscount(String quarterdiscount) {
        Quarterdiscount = quarterdiscount;
    }

    public String getHalfdiscount() {
        return Halfdiscount;
    }

    public void setHalfdiscount(String halfdiscount) {
        Halfdiscount = halfdiscount;
    }

    public String getFulldiscount() {
        return Fulldiscount;
    }

    public void setFulldiscount(String fulldiscount) {
        Fulldiscount = fulldiscount;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


}
