package com.dev.fd.feederdaddy.Common;


import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

import com.dev.fd.feederdaddy.Remote.APIService;
import com.dev.fd.feederdaddy.Remote.RetrofitClient;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Common {

    public static String currentfragment="ordermeal";

    private static final String  BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static boolean isOpen(Context c)
    {
        SharedPreferences sharedPreferences = c.getSharedPreferences("MyData",Context.MODE_PRIVATE);
        String isopen = sharedPreferences.getString("isopen","N/A");
        if(isopen.equals("1"))
            return true;
        else
            return false;

    }

    public static String getCity(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("MyData",Context.MODE_PRIVATE);
        return sharedPreferences.getString("city","N/A");
    }

    public static String convertCodeToStatus(String status) {
        if(status.equals("1") || status.equals("11"))
            return "Order Placed";
        else if(status.equals("-2"))
            return "Calling Restaurant";
        else if(status.equals("3"))
            return "Arrived at Restaurant";
        else if(status.equals("4"))
            return "Order Dispatched";
        else if(status.equals("5"))
            return "Order Delivered";
        else if(status.equals("6"))
            return "Order Rated";
        else
            return "Order Cancelled";

    }

    public static boolean isConnectedToInternet(Context context)
    {ConnectivityManager cm =
            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd MMM yyyy, hh:mm a",calendar).toString());
        return date.toString();
    }

    public static String getPhone(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("MyData",Context.MODE_PRIVATE);
        return sharedPreferences.getString("phone","N/A");
    }
}
