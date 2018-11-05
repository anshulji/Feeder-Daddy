package com.dev.fd.feederdaddy.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.dev.fd.feederdaddy.MainActivity;
import com.dev.fd.feederdaddy.Orders;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage);
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        String click_action = remoteMessage.getNotification().getClickAction();
        Intent intent = new Intent(click_action);
        intent.putExtra("FCM","1");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,Orders.class),PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

  /*      NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.feeder_daddy_logo)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
*/

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notification.getBody()))
                .setContentText(notification.getBody())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.feeder_daddy_logo)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.feeder_daddy_logo))
                .setContentTitle(notification.getTitle())
                .setSound(defaultSoundUri);

        NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0,builder.build());


    }
}
