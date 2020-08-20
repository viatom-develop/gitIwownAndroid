package com.zeroner.bledemo.utils;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.blemidautumn.bean.WristBand;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.impl.AbsBle;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import coms.mediatek.wearable.WearableManager;

/**
 * Created by zm on 2016/10/28.
 * 蓝牙操作工具类
 */
public class BluetoothUtil {
    public static Context context= BleApplication.getInstance();
    /**
     * 蓝牙是否可用
     *
     * @return true 可用 false 不可用
     */
    public static boolean isEnabledBluetooth() {
        return isEnabledBluetooth(context);
    }

    /**
     * 蓝牙功能是否已经启用
     *
     * @return false or true
     */
    public static boolean isEnabledBluetooth(Context context) {
        BluetoothAdapter adapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        // 不支持BLE
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            return false;
        }
        // 不支持蓝牙
        if (adapter == null) {
            return false;
        }
        // 蓝牙未打开
        return adapter.isEnabled();
    }

    public static boolean checkBluetooth(Activity activity,int requestCode){
        BluetoothAdapter bluetoothAdapter = ((BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (bluetoothAdapter!=null){
            if (!bluetoothAdapter.isEnabled()){
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(intent, requestCode);
                return false;
            }else {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否连接
     *
     * @return false or true
     */
    public static boolean isConnected() {
        return isIBleNotNull() &&  SuperBleSDK.createInstance(context).isConnected();
    }


    public static boolean isUnbind(){
        if (TextUtils.isEmpty(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS))&&TextUtils.isEmpty(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME))) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 是否正在连接
     *
     * @return false or true
     */
    public static boolean isConnecting() {
        return isIBleNotNull() && SuperBleSDK.createInstance(context).isConnecting();
    }

    /**
     * 是否正在扫描
     *
     * @return false or true
     */
    public static boolean isScanning() {
        return isIBleNotNull() && SuperBleSDK.createInstance(context).isScanning();
    }

    /**
     * 连接蓝牙
     */
    public static void connect() {
        if (isHaveAddress() && isIBleNotNull()) {
            if (((AbsBle) BleApplication.getInstance().getIBle()).getWristBand() == null) {
                KLog.e("蓝牙进行重连 : 设置设备");
                ((AbsBle) BleApplication.getInstance().getIBle()).setWristBand(
                        new WristBand(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME), PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS)));
            }
            SuperBleSDK.createInstance(context).setWristBand(new WristBand(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME), PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS)));
            SuperBleSDK.createInstance(context).connect();
            setNeedReconnect(true);
        } else {
            KLog.e("isHaveAddress : " + isHaveAddress() + "   isIBleNotNull : " + isIBleNotNull());
        }
    }

    /**
     * 连接蓝牙
     * @param wristBand
     */
    public static void connect(WristBand wristBand){
        if( isIBleNotNull()){
            setNeedReconnect(true);
            SuperBleSDK.createInstance(context).setWristBand(wristBand);
            SuperBleSDK.createInstance(context).connect();
        }
    }

    /**
     * 连接蓝牙不需要判断UserConfig中存储地址是否为空
     */
    public static void connectNoAddress() {
        if (isIBleNotNull()) {
            KLog.e("解除绑定后连接");
            SuperBleSDK.createInstance(context).connect();
            setNeedReconnect(false);
        }
    }

    /**
     * 本地是否存储了蓝牙地址
     *
     * @return false or true
     */
    public static boolean isHaveAddress() {
        return !TextUtils.isEmpty(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME)) && !TextUtils.isEmpty( PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS));
    }

    /**
     * IBle是否为空
     *
     * @return true 不为空 false 空
     */
    public static boolean isIBleNotNull() {
        return BleApplication.getInstance().getIBle() != null;
    }

    /**
     * 断开连接
     */
    public static void disconnect() {
        KLog.d("gavin--->断链 "+isIBleNotNull());
        if (isIBleNotNull()) {
            KLog.e("licl"+PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS));
            SuperBleSDK.createInstance(context).disconnect(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS),true);
        }
    }

    /**
     * 断开连接
     */
    public static void disconnect(boolean isNeedReconnect) {
        if (isIBleNotNull()) {
            setNeedReconnect(isNeedReconnect);
            disconnect();
            setWristBand(null);
            BackgroundThreadManager.getInstance().clearQueue();
        }
    }

    public static void setWristBand(WristBand dev) {
        if (isIBleNotNull()) {
            SuperBleSDK.createInstance(context).setWristBand(dev);
        }
    }

    /**
     * 开始扫描
     */
    public static void startScan() {
        KLog.i("=====================开始扫描");
        if (isIBleNotNull()) {
            SuperBleSDK.createInstance(context).startScan(false);
        }
    }

    /**
     * 停止扫描
     */
    public static void stopScan() {
        if (isIBleNotNull()) {
            SuperBleSDK.createInstance(context).stopScan();
        }
    }

    public static WristBand getWristBand() {
        if (isIBleNotNull()) {
           return SuperBleSDK.createInstance(context).getWristBand();
        }
        return null;
    }

    public static String getDeviceAddress() {
        return getWristBand() != null ? getWristBand().getAddress() : null;
    }

    public static void unbindDevice() {
        KLog.e("licl", "unbindDevice11");
        if (isIBleNotNull()) {
//            if(ZGBaseUtils.isZG()){
//                disconnect();
//            }else {
//                byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setUnbind();
//                BackgroundThreadManager.getInstance().addWriteData(context,bytes);
//            }

            KLog.e("licl", "unbindDevice22");

            if (!SuperBleSDK.isMtk(context)) {
                SuperBleSDK.createInstance(context).unbindDevice();
                KLog.e("licl", "unbindDevice33");
            }else {
                KLog.e("licl", "unbindDevice44");
                disconnect(false);
            }
        }
    }

    /**
     * 向设备发送解绑指令
     * @param isNeedReconnect 是否需要重连
     */
    public static void unbindDevice(boolean isNeedReconnect){
        if (BluetoothUtil.isConnected()) {
            BluetoothUtil.setNeedReconnect(isNeedReconnect);
            BackgroundThreadManager.getInstance().clearQueue();
            byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setUnbind();
            BackgroundThreadManager.getInstance().addWriteData(context,bytes);
        }
    }


    /**
     * 设置是否需要在连接出错时重连
     *
     * @param isNeedReconnect true 重连 false 只连接一次不会重连
     */
    public static void setNeedReconnect(boolean isNeedReconnect) {
        if (isIBleNotNull()) {
            SuperBleSDK.createInstance(context).setNeedReconnect(isNeedReconnect);
        }
    }


    public static boolean isSupportBT(Context context){
        BluetoothAdapter mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter == null) {
            return false;
        }else {
            return true;
        }
    }

    public static boolean isBLTOpen(Context context){
        BluetoothAdapter mBluetoothAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            return true;
        }else {
            return false;
        }
    }


    public static void disconnectWhenUnbindTimeOut(boolean isNeedReconnect) {

        disconnect();
        setWristBand(null);
        WearableManager.getInstance().setRemoteDevice(null);
        BackgroundThreadManager.getInstance().clearQueue();
        setNeedReconnect(isNeedReconnect);
    }


}
