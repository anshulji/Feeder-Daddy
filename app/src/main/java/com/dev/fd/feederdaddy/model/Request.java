package com.dev.fd.feederdaddy.model;

import java.util.List;

public class Request {
    List<Order> foods;

    private String Deliveryboyname,Didrestaurantaccepted,Deliveryboyphone,Restaurantname,Restaurantareaname,Restaurantid,Restaurantimage,Customername,Customerphone,Customeraddress,Customerzone,Orderid,Timeinms,Totalamount,Deliverycharge,Orderstatus,Orderstatusmessage,Orderreceivetime,Paymentmethod;

    public Request() {
    }

    public Request(List<Order> foods, String deliveryboyname, String didrestaurantaccepted, String deliveryboyphone, String restaurantname, String restaurantareaname, String restaurantid, String restaurantimage, String customername, String customerphone, String customeraddress, String customerzone, String orderid, String timeinms, String totalamount, String deliverycharge, String orderstatus, String orderstatusmessage, String orderreceivetime, String paymentmethod) {
        this.foods = foods;
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
        Customerzone = customerzone;
        Orderid = orderid;
        Timeinms = timeinms;
        Totalamount = totalamount;
        Deliverycharge = deliverycharge;
        Orderstatus = orderstatus;
        Orderstatusmessage = orderstatusmessage;
        Orderreceivetime = orderreceivetime;
        Paymentmethod = paymentmethod;
    }

    public String getCustomerzone() {
        return Customerzone;
    }

    public void setCustomerzone(String customerzone) {
        Customerzone = customerzone;
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

    public String getDeliveryboyname() {
        return Deliveryboyname;
    }

    public void setDeliveryboyname(String deliveryboyname) {
        Deliveryboyname = deliveryboyname;
    }

    public String getOrderstatusmessage() {
        return Orderstatusmessage;
    }

    public void setOrderstatusmessage(String orderstatusmessage) {
        Orderstatusmessage = orderstatusmessage;
    }

    public String getRestaurantareaname() {
        return Restaurantareaname;
    }

    public void setRestaurantareaname(String restaurantareaname) {
        Restaurantareaname = restaurantareaname;
    }

    public String getPaymentmethod() {
        return Paymentmethod;
    }

    public void setPaymentmethod(String paymentmethod) {
        Paymentmethod = paymentmethod;
    }

    public String getOrderreceivetime() {
        return Orderreceivetime;
    }

    public void setOrderreceivetime(String orderreceivetime) {
        Orderreceivetime = orderreceivetime;
    }

    public String getRestaurantimage() {
        return Restaurantimage;
    }

    public void setRestaurantimage(String restaurantimage) {
        Restaurantimage = restaurantimage;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getRestaurantname() {
        return Restaurantname;
    }

    public void setRestaurantname(String restaurantname) {
        Restaurantname = restaurantname;
    }

    public String getRestaurantid() {
        return Restaurantid;
    }

    public void setRestaurantid(String restaurantid) {
        Restaurantid = restaurantid;
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

    public String getOrderid() {
        return Orderid;
    }

    public void setOrderid(String orderid) {
        Orderid = orderid;
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
}
