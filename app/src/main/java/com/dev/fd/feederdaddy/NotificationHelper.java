package com.dev.fd.feederdaddy;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.dev.fd.feederdaddy.model.Notification;

public class NotificationHelper extends ContextWrapper{

    private static final String EDMT_CHANNEL_ID  = "com.dev.fd.feederdaddy";
    private static final String EDMT_CHANNEL_NAME = "EDMTDEV CHANNEL";
    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }
    private void createChannels() {

        NotificationChannel edmtchannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            edmtchannel = new NotificationChannel(EDMT_CHANNEL_ID,EDMT_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            edmtchannel.enableLights(true);
            edmtchannel.enableVibration(true);
            edmtchannel.setLightColor(Color.GREEN);

            getManager().createNotificationChannel(edmtchannel);

        }
        //edmtchannel.setLockscreenVisibility(NO);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        return manager;
    }
}
