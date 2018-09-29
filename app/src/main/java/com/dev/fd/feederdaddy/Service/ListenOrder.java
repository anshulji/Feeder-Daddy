package com.dev.fd.feederdaddy.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.dev.fd.feederdaddy.Common.Common;
import com.dev.fd.feederdaddy.Orders;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.model.Request;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import static com.dev.fd.feederdaddy.PlaceOrder.listenservice;

public class ListenOrder extends Service implements ChildEventListener{
    FirebaseDatabase db;
    Query requests;

    public ListenOrder() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences =getSharedPreferences("MyData",Context.MODE_PRIVATE);
        String phone =  sharedPreferences.getString("phone","N/A");

        db = FirebaseDatabase.getInstance();
        requests = db.getReference("CurrentRequests").orderByChild("customerphone").equalTo(phone);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        requests.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
        //trigger here
        Request request = dataSnapshot.getValue(Request.class);

        showNotification(dataSnapshot.getKey(),request);
    }

    private void showNotification(String key, Request request) {
        Intent intent = new Intent(getBaseContext(),Orders.class);
        intent.putExtra("userPhone",request.getCustomerphone());
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),0,intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext());

        builder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(request.getOrderstatusmessage()))
                .setContentText(request.getOrderstatusmessage())
                .setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.feeder_daddy_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.feeder_daddy_logo))
        .setContentTitle(Common.convertCodeToStatus(request.getOrderstatus()));

        NotificationManager notificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1,builder.build());

        if(request.getOrderstatus().equals("4"))
        {
            this.stopSelf();
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {

    }
}
