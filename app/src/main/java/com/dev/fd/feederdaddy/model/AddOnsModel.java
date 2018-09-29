package com.dev.fd.feederdaddy.model;

public class AddOnsModel {
    private String addonitemname,addonitemprice;

    public AddOnsModel(){

    }

    public AddOnsModel(String addonitemname, String addonitemprice) {
        this.addonitemname = addonitemname;
        this.addonitemprice = addonitemprice;
    }

    public String getAddonitemname() {
        return addonitemname;
    }

    public void setAddonitemname(String addonitemname) {
        this.addonitemname = addonitemname;
    }

    public String getAddonitemprice() {
        return addonitemprice;
    }

    public void setAddonitemprice(String addonitemprice) {
        this.addonitemprice = addonitemprice;
    }
}
