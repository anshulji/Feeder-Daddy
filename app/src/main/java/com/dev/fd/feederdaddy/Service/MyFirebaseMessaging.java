package com.dev.fd.feederdaddy.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.dev.fd.feederdaddy.MainActivity;
import com.dev.fd.feederdaddy.Orders;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.SplashActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

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

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Uri alarmsound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+ getPackageName()+"/raw/customer");
        Uri sound = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.customer);
        if(notificationManager==null){
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel notificationChannel = new NotificationChannel("3", "ccname", importance);
            notificationChannel.setDescription("ccdescription");
            notificationChannel.enableLights(true);
            notificationChannel.shouldVibrate();
            notificationChannel.setShowBadge(true);
            notificationChannel.canBypassDnd();
            notificationChannel.shouldShowLights();
            AudioAttributes att = new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).setUsage(AudioAttributes.USAGE_ALARM).build();
            notificationChannel.setSound(sound, att);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);

            if (notificationManager != null) {
                List<NotificationChannel> channelList = notificationManager.getNotificationChannels();
                for (int i = 0; channelList != null && i < channelList.size(); i++)
                    notificationManager.deleteNotificationChannel(channelList.get(i).getId());
            }

            //if(notificationManager!=null)
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,Orders.class),PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"3")
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(notification.getBody()))
                        .setContentText(notification.getBody())
                        .setContentIntent(pendingIntent)
                        .setColor(Color.TRANSPARENT)
                        .setSmallIcon(R.drawable.feeder_daddy_logo)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                                R.drawable.feeder_daddy_logo))
                        .setContentTitle(notification.getTitle())
                        .setSound(sound).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

                //NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//            stackBuilder.addNextIntent(intent);
//            PendingIntent resultpi = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
//            builder.setContentIntent(resultpi);

            builder.setChannelId("3");
        assert notificationManager != null;
        notificationManager.notify(0, builder.build());



  /*      NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.feeder_daddy_logo)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
*/

            /*else {
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,new Intent(this,Orders.class),PendingIntent.FLAG_UPDATE_CURRENT);

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
                    .setSound(alarmsound);

            //NotificationManager noti = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, builder.build());
        }*/

    }
}
