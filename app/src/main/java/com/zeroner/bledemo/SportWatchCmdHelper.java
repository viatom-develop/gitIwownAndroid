package com.zeroner.bledemo;

import android.content.Context;

import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.task.BleWriteDataTask;

/**
 * Created by cindy on 18/4/16.
 */

public class SportWatchCmdHelper {

    public static void getSomeBaseInfo(Context context){

        byte[] power = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getBattery();
        BackgroundThreadManager.getInstance().addWriteData(context, power);
        byte[] data7 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getFirmwareInformation();
        BackgroundThreadManager.getInstance().addWriteData(context, data7);

        byte[] time= SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTime();
        BackgroundThreadManager.getInstance().addWriteData(context, time);

        byte[] data12 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getDeviceStateDate();
        BleWriteDataTask task12 = new BleWriteDataTask(context, data12);
        BackgroundThreadManager.getInstance().addTask(task12);
    }
}
