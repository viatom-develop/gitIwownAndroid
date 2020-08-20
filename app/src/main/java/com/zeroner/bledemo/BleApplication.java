package com.zeroner.bledemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.stetho.Stetho;
import com.socks.library.KLog;
import com.zeroner.bledemo.bean.sql.BleLog;
import com.zeroner.bledemo.receiver.BluetoothDataParseReceiver;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.MyLifecycleHandler;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bean.WristBand;
import com.zeroner.blemidautumn.bluetooth.IBle;
import com.zeroner.blemidautumn.bluetooth.IDataReceiveHandler;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.impl.AbsBle;
import com.zeroner.blemidautumn.bluetooth.impl.BleService;
import com.zeroner.blemidautumn.utils.ByteUtil;
import com.zeroner.blemidautumn.utils.SingleThreadUtil;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * 作者：hzy on 2017/12/19 19:03
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class BleApplication extends LitePalApplication{

    private IBle mBle;
    private BleService mService;
    private static final Object sObject = new Object();
    private BluetoothDataParseReceiver bluetoothDataParseReceiver;
    private MyLifecycleHandler mMyLifecycleHandler;
    private static BleApplication context;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        context=this;
        LitePal.initialize(this);
        Stetho.initializeWithDefaults(this);
        Intent bindIntent = new Intent(this, BleService.class);
        stopService(bindIntent);
        bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        initMTK_BLE4();
        //注册广播接收蓝牙数据
        IntentFilter intentFilter = BaseActionUtils.getIntentFilter();
        bluetoothDataParseReceiver = new BluetoothDataParseReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(bluetoothDataParseReceiver, intentFilter);

        mMyLifecycleHandler = new MyLifecycleHandler();
        registerActivityLifecycleCallbacks(mMyLifecycleHandler);

        SuperBleSDK.addBleListener(this, new IDataReceiveHandler() {

            @Override
            public void onDataArrived(int ble_sdk_type, int dataType, String data) {
                KLog.e(data);
                Intent intent = new Intent(BaseActionUtils.ON_DATA_ARRIVED);
                intent.putExtra(BaseActionUtils.BLE_SDK_TYPE, ble_sdk_type);
                intent.putExtra(BaseActionUtils.BLE_DATA_TYPE, dataType);
                intent.putExtra(BaseActionUtils.BLE_ARRIVED_DATA, data);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onScanResult(WristBand dev) {
                KLog.i(dev.toString());
                Intent intent = new Intent(BaseActionUtils.ON_SCAN_RESULT);
                intent.putExtra(BaseActionUtils.BLE_SCAN_RESULT_DEVICE, dev);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onBluetoothInit() {
                KLog.i("===onBluetoothInit==="+true);
                Intent intent = new Intent(BaseActionUtils.ON_BLUETOOTH_INIT);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void connectStatue(boolean isConnect) {
                KLog.i("连接状态：" + isConnect);
                Intent intent = new Intent(BaseActionUtils.ON_CONNECT_STATUE);
                intent.putExtra(BaseActionUtils.BLE_CONNECT_STATUE, isConnect);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onDiscoverService(String serviceUUID) {
                Intent intent = new Intent(BaseActionUtils.ON_DISCOVER_SERVICE);
                intent.putExtra(BaseActionUtils.BLE_SERVICE_UUID, serviceUUID);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onDiscoverCharacter(String characterUUID) {
                Intent intent = new Intent(BaseActionUtils.ON_DISCOVER_CHARACTER);
                intent.putExtra(BaseActionUtils.BLE_CHARACTER_UUID, characterUUID);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onCommonSend(final byte[] data) {
                Intent intent = new Intent(BaseActionUtils.ON_COMMON_SEND);
                intent.putExtra(BaseActionUtils.BLE_COMMON_SEND, data);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                SingleThreadUtil.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        //debug
                        BleLog log=new BleLog();
                        log.setTime(System.currentTimeMillis());
                        log.setDataFrom(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME));
                        log.setType(1);
                        log.setCmd(ByteUtil.bytesToString(data));
                        log.save();
                    }
                });

            }

            @Override
            public void onCmdReceive(final byte[] data) {
                Intent intent = new Intent(BaseActionUtils.ON_COMMON_RECEIVER);
                intent.putExtra(BaseActionUtils.ON_COMMON_RECEIVER, data);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                SingleThreadUtil.getExecutorService().execute(new Runnable() {
                    @Override
                    public void run() {
                        //debug
                        BleLog log=new BleLog();
                        log.setTime(System.currentTimeMillis());
                        log.setDataFrom(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME));
                        log.setType(2);
                        log.setCmd(ByteUtil.bytesToString(data));
                        log.save();
                    }
                });

            }

            @Override
            public void onCharacteristicChange(String address) {
                Intent intent = new Intent(BaseActionUtils.ON_CHARACTERISTIC_CHANGE);
                intent.putExtra(BaseActionUtils.BLE_BLUETOOTH_ADDRESS, address);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onBluetoothError() {
                Intent intent = new Intent(BaseActionUtils.ON_BLUETOOTH_ERROR);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onPreConnect() {
                Intent intent = new Intent(BaseActionUtils.BLE_PRE_CONNECT);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void noCallback() {
                Intent intent = new Intent(BaseActionUtils.BLE_NO_CALLBACK);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onConnectionStateChanged(int state, int newState) {
                KLog.e("licl", "state/newState: "+ state+"/"+newState);
            }

            @Override
            public void onSdkAutoReconnectTimesOut() {

            }

        });
    }


    private void initMTK_BLE4() {
        // wearable init
//        boolean isSuccess = WearableManager.getInstance().init(true, getApplicationContext(), null, R.xml.wearable_config);
//        KLog.d( "WearableManager init " + isSuccess);

    }



    // 实例化一次
    public static BleApplication getInstance() {
        return context;
    }


    public MyLifecycleHandler getmMyLifecycleHandler() {
        return mMyLifecycleHandler;
    }

    public static Object getObject() {
        return sObject;
    }

    public IBle getIBle() {
        return mBle;
    }

    public BleService getmService() {
        return mService;
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder rawBinder) {
            try {
                mService = ((BleService.LocalBinder) rawBinder).getService();
                mBle = mService.getBle();
                KLog.e("蓝牙进行重连 : 绑定服务成功");
                if (!TextUtils.isEmpty( PrefUtil.getString(BluetoothUtil.context,BaseActionUtils.ACTION_DEVICE_NAME)) && !TextUtils.isEmpty( PrefUtil.getString(BluetoothUtil.context,BaseActionUtils.ACTION_DEVICE_ADDRESS))) {
                    ((AbsBle) mBle).setWristBand(new WristBand(PrefUtil.getString(BluetoothUtil.context,BaseActionUtils.ACTION_DEVICE_NAME),  PrefUtil.getString(BluetoothUtil.context,BaseActionUtils.ACTION_DEVICE_ADDRESS)));
                }
                synchronized (getObject()) {
                    getObject().notifyAll();
                }
            } catch (Exception e) {
                KLog.e("初始化异常 : " + e.toString());
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName classname) {
            KLog.i("onServiceDisconnected");
            mService = null;
        }
    };
}
