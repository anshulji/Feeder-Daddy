package com.dev.fd.feederdaddy.model;

public class RestaurantBanner {

    private String Restaurantid,Menuid,Foodid,name,image;

    public RestaurantBanner() {
    }

    public RestaurantBanner(String restaurantid, String menuid, String foodid, String name, String image) {
        Restaurantid = restaurantid;
        Menuid = menuid;
        Foodid = foodid;
        this.name = name;
        this.image = image;
    }

    public String getRestaurantid() {
        return Restaurantid;
    }

    public void setRestaurantid(String restaurantid) {
        Restaurantid = restaurantid;
    }

    public String getMenuid() {
        return Menuid;
    }

    public void setMenuid(String menuid) {
        Menuid = menuid;
    }

    public String getFoodid() {
        return Foodid;
    }

    public void setFoodid(String foodid) {
        Foodid = foodid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
