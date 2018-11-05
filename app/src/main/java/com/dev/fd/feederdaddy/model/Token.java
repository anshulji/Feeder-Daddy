package com.dev.fd.feederdaddy.model;

public class Token {
    private String token;
    private String city_zone_isserver;

    public Token() {
    }

    public Token(String token, String city_zone_isserver) {
        this.token = token;
        this.city_zone_isserver = city_zone_isserver;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCity_zone_isserver() {
        return city_zone_isserver;
    }

    public void setCity_zone_isserver(String city_zone_isserver) {
        this.city_zone_isserver = city_zone_isserver;
    }
}
