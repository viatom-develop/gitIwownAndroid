package com.zeroner.bledemo.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.SportWatchCmdHelper;
import com.zeroner.bledemo.bean.sql.TB_60_data;
import com.zeroner.bledemo.bean.sql.TB_61_data;
import com.zeroner.bledemo.bean.sql.TB_62_data;
import com.zeroner.bledemo.bean.sql.TB_64_data;
import com.zeroner.bledemo.bean.sql.TB_68_data;
import com.zeroner.bledemo.bean.sql.TB_f1_index;
import com.zeroner.bledemo.bean.sql.TB_sum_61_62_64;
import com.zeroner.bledemo.data.IwownDataParsePresenter;
import com.zeroner.bledemo.data.MtkDataParsePresenter;
import com.zeroner.bledemo.data.ProtoBufDataParsePersenter;
import com.zeroner.bledemo.data.ZGDataParsePresenter;
import com.zeroner.bledemo.data.sync.MTKHeadSetSync;
import com.zeroner.bledemo.data.sync.MtkSync;
import com.zeroner.bledemo.data.sync.SyncData;
import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.MyLifecycleHandler;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bean.WristBand;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.task.BleWriteDataTask;
import com.zeroner.blemidautumn.task.ITask;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.HashMap;

import static com.zeroner.bledemo.utils.BluetoothUtil.context;

/**
 * Created by zm on 2016/10/12.
 */
public class BluetoothDataParseReceiver extends BluetoothCallbackReceiver {
    private static final String TAG = "BluetoothDataParseReceiver";
    private static final int CONNECT_TIME_OUT = 60000 * 60;
    private static int mConnectTimeout = CONNECT_TIME_OUT;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mDisconnectRunnable = new Runnable() {
        @Override
        public void run() {
            if (BaseActionUtils.isBackground && BluetoothUtil.isConnected()) {
                KLog.file("后台" + getTimeout() + "分钟断开连接");
                byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setUnbind();
                BleWriteDataTask task = new BleWriteDataTask(context, bytes, true);
                if (BackgroundThreadManager.getInstance().getQueueSize() > 0) {
                    ITask lastTask = BackgroundThreadManager.getInstance().getLastTask();
                    if (lastTask instanceof BleWriteDataTask && ((BleWriteDataTask) lastTask).isUnbind()) {
                        return;
                    }
                }
                BackgroundThreadManager.getInstance().addTask(task);

                SystemClock.sleep(5000);
                BluetoothUtil.setNeedReconnect(true);
                BluetoothUtil.connect();
            } else if (BaseActionUtils.isBackground && !BluetoothUtil.isConnected()) {
                SystemClock.sleep(5000);
                BluetoothUtil.setNeedReconnect(true);
                BluetoothUtil.connect();
            }
            judgeDisconnect();
        }
    };

    private int getTimeout() {
        return mConnectTimeout / 60000;
    }

    /**
     * 判断是否5m后断开
     */
    private void judgeDisconnect() {
//        if (BaseActionUtils.isBackground) {
//            mHandler.removeCallbacks(mDisconnectRunnable);
//            mHandler.postDelayed(mDisconnectRunnable, mConnectTimeout);
//        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        initData();
        super.onReceive(context, intent);
        if (BaseActionUtils.BLE_COMMON_SEND.equals(intent.getAction())) {
            if (BaseActionUtils.isBackground) {
                judgeDisconnect();
            }
        } else if (BaseActionUtils.ON_CHARACTERISTIC_CHANGE.equals(intent.getAction())) {
            if (BaseActionUtils.isBackground) {
                judgeDisconnect();
            }
        } else if (BaseActionUtils.ACTION_CONNECT_TIMEOUT.equals(intent.getAction())) {
            if (BaseActionUtils.isBackground) {
                KLog.e("进入后台 : " + getTimeout() + "分钟后断开连接");
                if (BluetoothUtil.isConnected()) {
                    judgeDisconnect();
                } else {
                    removeDisconnectRunnable();
                }
            } else {
                KLog.e("进入前台 : 移除" + getTimeout() + "分钟后断开连接");
                removeDisconnectRunnable();
            }
        } else if (BaseActionUtils.ON_BLUETOOTH_ERROR.equals(intent.getAction())) {
            if (!BaseActionUtils.isBackground) {
                KLog.e("出现257错误提示用户 isBackground : " + BaseActionUtils.isBackground);
//                BaseActionUtils.showToast(R.string.connect_error_257);
            }
        } else if (BaseActionUtils.BLE_CONNECT_STATUE.equals(intent.getAction())) {

        }
    }

    private void removeDisconnectRunnable() {
        mHandler.removeCallbacks(mDisconnectRunnable);
    }

    private void initData() {
    }

    @Override
    public void connectStatue(boolean isConnect) {
        super.connectStatue(isConnect);
        MyLifecycleHandler myLifecycleHandler = BleApplication.getInstance().getmMyLifecycleHandler();
        if (myLifecycleHandler != null) {
            KLog.e("background : " + myLifecycleHandler.isBackground());
        }

        WristBand wristBand = SuperBleSDK.createInstance(context).getWristBand();
        if (wristBand != null) {
            PrefUtil.save(context, BaseActionUtils.ACTION_DEVICE_NAME, wristBand.getName());
            PrefUtil.save(context, BaseActionUtils.ACTION_DEVICE_ADDRESS, wristBand.getAddress());
//            KLog.e(TAG, JsonTool.toJson(wristBand));
        } else {
            KLog.e(TAG, "wristBand==null");
        }

        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put(Event.Ble_Connect_Statue, isConnect);
        EventBus.getDefault().post(new Event(Event.Ble_Connect_Statue, dataMap));
    }

    @Override
    public void onDataArrived(Context context, int ble_sdk_type, int dataType, String data) {
        super.onDataArrived(context, ble_sdk_type, dataType, data);
        //新的数据解析
        switch (ble_sdk_type) {
            case ZGDataParsePresenter.Type:
                ZGDataParsePresenter.parseProtocolData(context, dataType, data);
                break;
            case IwownDataParsePresenter.Type:
                IwownDataParsePresenter.parseProtocolData(context, dataType, data);
                break;
            case MtkDataParsePresenter.Type:
                MtkDataParsePresenter.parseProtoclData(context, dataType, data);
                break;
            case ProtoBufDataParsePersenter.Type:
            case ProtoBufDataParsePersenter.Type2:
                ProtoBufDataParsePersenter.parseProtocolData(context, dataType, data);
                break;
            default:
                break;
        }
    }


    @Override
    public void onBluetoothInit() {
        super.onBluetoothInit();
        KLog.i(TAG, "onBluetoothInit");


        if (SyncData.getInstance().isSyncDataInfo() && !SuperBleSDK.isMtk(context)) {
            SyncData.getInstance().stopSyncDataAll();
        } else if ((!MtkSync.getInstance(context).isSyncDataInfo() || !MTKHeadSetSync.getInstance().isSyncDataInfo()) && SuperBleSDK.isMtk(context)) {
            DataSupport.deleteAll(TB_f1_index.class);
            DataSupport.deleteAll(TB_sum_61_62_64.class);
            DataSupport.deleteAll(TB_62_data.class);
            DataSupport.deleteAll(TB_60_data.class);
            DataSupport.deleteAll(TB_61_data.class);
            DataSupport.deleteAll(TB_64_data.class);
            DataSupport.deleteAll(TB_68_data.class);

//            you should get the data counts first and then get them from device
//            in this demo when we get response, we will save it in TB_sum_61_62_64 table
            String data_from = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";

            if (data_from.toUpperCase().contains("VOICE")) {
                KLog.e("voice18", "---------voice开始同步");
                MTKHeadSetSync.getInstance().syncDataInfo();
            } else {
                MtkSync.getInstance(context).getDatasIndexTables();
            }
        }

        if (SuperBleSDK.isIown(context)) {
            byte[] power = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getBattery();
            BackgroundThreadManager.getInstance().addWriteData(context, power);
            byte[] data7 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getFirmwareInformation();
            BackgroundThreadManager.getInstance().addWriteData(context, data7);
            byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTime();
            BackgroundThreadManager.getInstance().addWriteData(context, bytes);

            if (!BaseActionUtils.isBackground || TextUtils.isEmpty(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_ADDRESS))) {
                SyncData.getInstance().syncDataInfo();
            }
        } else if (SuperBleSDK.isMtk(context)) {
            SportWatchCmdHelper.getSomeBaseInfo(context.getApplicationContext());

        } else if (SuperBleSDK.isProtoBuf(context)) {
            byte[] battery = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getBattery();
            BackgroundThreadManager.getInstance().addWriteData(context, battery);
//            byte[] time = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTime();
//            BackgroundThreadManager.getInstance().addWriteData(context, time);
        }else if(SuperBleSDK.isZG(context)){
            byte[] hardwareFeatures = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getHardwareFeatures();
            BackgroundThreadManager.getInstance().addWriteData(context,hardwareFeatures);
        }
    }
}
