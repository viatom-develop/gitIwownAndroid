package com.zeroner.bledemo.notification;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.zeroner.bledemo.R;

/**
 * Created by Administrator on 2017/2/27.
 */
public class HeartService extends IntentService {

    public static final int ZERONER_HEALTH_NOTIFICATION_ID = 553029;
    private  static NotificationManager nm;

    public HeartService() {
        super("HeartService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sendNotification("keep heart warm");

//        MyAlarmReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        createNotification();
    }
    public void createNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setTicker(null)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setOngoing(true);
        Notification notification = mBuilder.build();
        nm.notify(ZERONER_HEALTH_NOTIFICATION_ID,notification);
        nm.cancel(ZERONER_HEALTH_NOTIFICATION_ID);
    }
}
