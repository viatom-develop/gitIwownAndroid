package com.zeroner.bledemo.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Scow on 9/22/16.
 */

public class MyBootReceiver extends BroadcastReceiver {
    MyAlarmReceiver alarm = new MyAlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            alarm.setAlarm(context);
        }
    }
}