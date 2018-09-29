package com.dev.fd.feederdaddy.Common;


import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Common {

    public static String currentfragment="ordermeal";


    public static String convertCodeToStatus(String status) {
        if(status.equals("0") || status.equals("1"))
            return "Order Placed";
        else if(status.equals("2"))
            return "Arrived at Restaurant";
        else if(status.equals("3"))
            return "Order Dispatched";
        else if(status.equals("4"))
            return "Order Delivered";
        else if(status.equals("5"))
            return "Order Rated";
        else
            return "Order Cancelled";

    }

    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager!=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info!=null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }

            }
        }
        return false;
    }

    public static String getDate(long time)
    {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        StringBuilder date = new StringBuilder(android.text.format.DateFormat.format("dd MMM yyyy, hh:mm a",calendar).toString());
        return date.toString();
    }

}
