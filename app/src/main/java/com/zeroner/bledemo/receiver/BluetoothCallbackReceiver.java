package com.zeroner.bledemo.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.blemidautumn.bean.WristBand;

public class BluetoothCallbackReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Bundle extras = intent.getExtras();
        switch (action) {
            case BaseActionUtils.ON_SCAN_RESULT:
                final WristBand device = extras.getParcelable(BaseActionUtils.BLE_SCAN_RESULT_DEVICE);
                onScanResult(device);
                break;
            case BaseActionUtils.ON_BLUETOOTH_INIT:
                onBluetoothInit();
                break;
            case BaseActionUtils.ON_COMMON_SEND:
                byte[] commons = extras.getByteArray(BaseActionUtils.BLE_COMMON_SEND);
                onCommonSend(commons);
            case BaseActionUtils.ON_COMMON_RECEIVER:
                byte[] receivers = extras.getByteArray(BaseActionUtils.ON_COMMON_RECEIVER);
                onCmdReceiver(receivers);
                break;
            case BaseActionUtils.ON_CONNECT_STATUE:
                boolean connected = extras.getBoolean(BaseActionUtils.BLE_CONNECT_STATUE);
                connectStatue(connected);
                break;
            case BaseActionUtils.ON_DATA_ARRIVED:
                int sdk = extras.getInt(BaseActionUtils.BLE_SDK_TYPE, 0);
                int dataType = extras.getInt(BaseActionUtils.BLE_DATA_TYPE, 0);
                String data = extras.getString(BaseActionUtils.BLE_ARRIVED_DATA);
                onDataArrived(context,sdk, dataType, data);
                break;
            case BaseActionUtils.ON_CHARACTERISTIC_CHANGE:
                onCharacteristicChange();
                break;
            case BaseActionUtils.ON_BLUETOOTH_ERROR:
                onBluetoothError();
                break;
            case BaseActionUtils.BLE_PRE_CONNECT:
                onPreConnect();
                break;

        }
    }

    public void onScanResult(WristBand device) {
    }

    public void onDataArrived(Context context, int ble_sdk_type, int dataType, String data) {


    }

    public void connectStatue(boolean isConnect) {
    }

    public void onCharacteristicChange() {
    }

    public void onBluetoothError() {
    }

    public void onBluetoothInit() {
    }

    public void onCommonSend(byte[] data) {
    }

    public void onCmdReceiver(byte[] data){

    }

    public void onPreConnect(){

    }
}
