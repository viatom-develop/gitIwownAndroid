package com.zeroner.bledemo.utils;

import android.content.Context;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.receiver.BluetoothCallbackReceiver;

/**
 * Created by zm on 2017/2/16.
 * 绑定或者解绑的帮助类
 */
public class BleReceiverHelper {

    /**
     * 绑定接收蓝牙广播
     * @param context
     * @param bleReceiver
     */
    public static void registerBleReceiver(Context context, BluetoothCallbackReceiver bleReceiver){
        registerBleReceiver(context,bleReceiver, BaseActionUtils.getIntentFilter());
    }

    public static void registerBleReceiver(Context context, BluetoothCallbackReceiver bleReceiver, IntentFilter intentFilter){
        LocalBroadcastManager.getInstance(context).registerReceiver(bleReceiver,intentFilter);
    }

    public static void unregisterBleReceiver(Context context, BluetoothCallbackReceiver bleReceiver){
        LocalBroadcastManager.getInstance(context).unregisterReceiver(bleReceiver);
    }
}
