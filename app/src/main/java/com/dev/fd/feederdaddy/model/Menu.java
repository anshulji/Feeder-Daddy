package com.dev.fd.feederdaddy.model;

public class Menu {
    private String Name,Image,MenuId,Veg,Nonveg,Rating,Totalrates;

    public Menu(){

    }

    public Menu(String name, String image, String menuId, String veg, String nonveg, String rating, String totalrates) {
        Name = name;
        Image = image;
        MenuId = menuId;
        Veg = veg;
        Nonveg = nonveg;
        Rating = rating;
        Totalrates = totalrates;
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

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
