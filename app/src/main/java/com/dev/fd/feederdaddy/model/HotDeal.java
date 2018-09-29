package com.dev.fd.feederdaddy.model;

public class HotDeal {
    private String Name,Image,Description,Price,Veg,Nonveg;

    public HotDeal(){

    }

    public HotDeal(String name, String image, String description, String price, String veg, String nonveg) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        Veg = veg;
        Nonveg = nonveg;
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

}
