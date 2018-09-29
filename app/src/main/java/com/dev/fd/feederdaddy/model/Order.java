package com.dev.fd.feederdaddy.model;

public class Order {

    private String Foodid,Foodname,Quantity, Price,Image,Restaurantid,Menuid;
    private int ID;

    public Order(){

    }

    /*public Order(String foodid, String foodname, String quantity, String price, String image, String restaurantid, String menuid) {
        Foodid = foodid;
        Foodname = foodname;
        Quantity = quantity;
        Price = price;
        Image = image;
        Restaurantid = restaurantid;
        Menuid = menuid;
    }*/

    public Order( int ID,String foodid, String foodname, String quantity, String price, String image, String restaurantid, String menuid) {
        Foodid = foodid;
        Foodname = foodname;
        Quantity = quantity;
        Price = price;
        Image = image;
        Restaurantid = restaurantid;
        Menuid = menuid;
        this.ID = ID;
    }

    public String getFoodid() {
        return Foodid;
    }

    public void setFoodid(String foodid) {
        Foodid = foodid;
    }

    public String getMenuid() {
        return Menuid;
    }

    public void setMenuid(String menuid) {
        Menuid = menuid;
    }

    public String getFoodname() {
        return Foodname;
    }

    public void setFoodname(String foodname) {
        Foodname = foodname;
    }

    public String getRestaurantid() {
        return Restaurantid;
    }

    public void setRestaurantid(String restaurantid) {
        Restaurantid = restaurantid;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
}
