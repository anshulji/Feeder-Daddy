package com.dev.fd.feederdaddy.Common;


public class Common {


    public static String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return "shipped";

    }
}
