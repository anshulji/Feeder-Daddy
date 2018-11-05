package com.dev.fd.feederdaddy.model;

import java.util.List;

public class Request {
    List<Order> foods;

    private String City,Zone,Deliveryboyname,Didrestaurantaccepted,Deliveryboyphone,Restaurantname,Restaurantareaname,Restaurantid,Restaurantimage,Customername,Customerphone,Customeraddress,Timeinms,Totalamount,Deliverycharge,Orderstatus,Orderstatusmessage,Orderreceivetime,Paymentmethod,Adminstatus,City_zone_status,Restaurantphone,Promocode,Promoamount;

    public Request() {
    }

    public Request(List<Order> foods, String city, String zone, String deliveryboyname, String didrestaurantaccepted, String deliveryboyphone, String restaurantname, String restaurantareaname, String restaurantid, String restaurantimage, String customername, String customerphone, String customeraddress, String timeinms, String totalamount, String deliverycharge, String orderstatus, String orderstatusmessage, String orderreceivetime, String paymentmethod, String adminstatus, String city_zone_status, String restaurantphone, String promocode, String promoamount) {
        this.foods = foods;
        City = city;
        Zone = zone;
        Deliveryboyname = deliveryboyname;
        Didrestaurantaccepted = didrestaurantaccepted;
        Deliveryboyphone = deliveryboyphone;
        Restaurantname = restaurantname;
        Restaurantareaname = restaurantareaname;
        Restaurantid = restaurantid;
        Restaurantimage = restaurantimage;
        Customername = customername;
        Customerphone = customerphone;
        Customeraddress = customeraddress;
        Timeinms = timeinms;
        Totalamount = totalamount;
        Deliverycharge = deliverycharge;
        Orderstatus = orderstatus;
        Orderstatusmessage = orderstatusmessage;
        Orderreceivetime = orderreceivetime;
        Paymentmethod = paymentmethod;
        Adminstatus = adminstatus;
        City_zone_status = city_zone_status;
        Restaurantphone = restaurantphone;
        Promocode = promocode;
        Promoamount = promoamount;
    }

    public String getRestaurantphone() {
        return Restaurantphone;
    }

    public void setRestaurantphone(String restaurantphone) {
        Restaurantphone = restaurantphone;
    }

    public String getZone() {
        return Zone;
    }

    public void setZone(String zone) {
        Zone = zone;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getDeliveryboyname() {
        return Deliveryboyname;
    }

    public void setDeliveryboyname(String deliveryboyname) {
        Deliveryboyname = deliveryboyname;
    }

    public String getDidrestaurantaccepted() {
        return Didrestaurantaccepted;
    }

    public void setDidrestaurantaccepted(String didrestaurantaccepted) {
        Didrestaurantaccepted = didrestaurantaccepted;
    }

    public String getDeliveryboyphone() {
        return Deliveryboyphone;
    }

    public void setDeliveryboyphone(String deliveryboyphone) {
        Deliveryboyphone = deliveryboyphone;
    }

    public String getRestaurantname() {
        return Restaurantname;
    }

    public void setRestaurantname(String restaurantname) {
        Restaurantname = restaurantname;
    }

    public String getRestaurantareaname() {
        return Restaurantareaname;
    }

    public void setRestaurantareaname(String restaurantareaname) {
        Restaurantareaname = restaurantareaname;
    }

    public String getRestaurantid() {
        return Restaurantid;
    }

    public void setRestaurantid(String restaurantid) {
        Restaurantid = restaurantid;
    }

    public String getRestaurantimage() {
        return Restaurantimage;
    }

    public void setRestaurantimage(String restaurantimage) {
        Restaurantimage = restaurantimage;
    }

    public String getCustomername() {
        return Customername;
    }

    public void setCustomername(String customername) {
        Customername = customername;
    }

    public String getCustomerphone() {
        return Customerphone;
    }

    public void setCustomerphone(String customerphone) {
        Customerphone = customerphone;
    }

    public String getCustomeraddress() {
        return Customeraddress;
    }

    public void setCustomeraddress(String customeraddress) {
        Customeraddress = customeraddress;
    }

    public String getTimeinms() {
        return Timeinms;
    }

    public void setTimeinms(String timeinms) {
        Timeinms = timeinms;
    }

    public String getTotalamount() {
        return Totalamount;
    }

    public void setTotalamount(String totalamount) {
        Totalamount = totalamount;
    }

    public String getDeliverycharge() {
        return Deliverycharge;
    }

    public void setDeliverycharge(String deliverycharge) {
        Deliverycharge = deliverycharge;
    }

    public String getOrderstatus() {
        return Orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        Orderstatus = orderstatus;
    }

    public String getOrderstatusmessage() {
        return Orderstatusmessage;
    }

    public void setOrderstatusmessage(String orderstatusmessage) {
        Orderstatusmessage = orderstatusmessage;
    }

    public String getOrderreceivetime() {
        return Orderreceivetime;
    }

    public void setOrderreceivetime(String orderreceivetime) {
        Orderreceivetime = orderreceivetime;
    }

    public String getPaymentmethod() {
        return Paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        Paymentmethod = paymentmethod;
    }

    public String getAdminstatus() {
        return Adminstatus;
    }

    public void setAdminstatus(String adminstatus) {
        Adminstatus = adminstatus;
    }

    public String getCity_zone_status() {
        return City_zone_status;
    }

    public void setCity_zone_status(String city_zone_status) {
        City_zone_status = city_zone_status;
    }

    public String getPromocode() {
        return Promocode;
    }

    public void setPromocode(String promocode) {
        Promocode = promocode;
    }

    public String getPromoamount() {
        return Promoamount;
    }

    public void setPromoamount(String promoamount) {
        Promoamount = promoamount;
    }
}

