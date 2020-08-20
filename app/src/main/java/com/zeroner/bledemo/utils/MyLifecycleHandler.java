package com.zeroner.bledemo.utils;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.data.sync.SyncData;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

/**
 * 声明周期 判断程序前后台
 * 来决定手环是否继续推送
 * Created by Daemon on 2016/5/4.
 */
public class MyLifecycleHandler implements Application.ActivityLifecycleCallbacks {
    // I use four separate variables here. You can, of course, just use two and
    // increment/decrement them instead of using four and incrementing them all.
    private int started;
    private int createed;
    private int destoryed;
    public MyLifecycleHandler() {
    }

    private Handler mHandler=new Handler();
    private boolean isBackground = true;

    public boolean isBackground() {
        return isBackground;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ++createed;
        if (createed > destoryed) {
            //读取本地状态 如果是停止状态 就要发送数据给手环 开始同步
            boolean visible = PrefUtil.getBoolean(activity, "visible", false);
            if (!visible) {
                KLog.e("发送数据");
                BackgroundThreadManager.getInstance().removeUnbindTask();
                BaseActionUtils.isBackground = false;

                //如果本地保存是停止 说明已经停止了 现在开始要求同步
                if (BluetoothUtil.isConnected()) {
                    BackgroundThreadManager.getInstance().wakeUp();
                }
            } else {
                //现在就是开始状态 不需要发送同步命令
            }
            PrefUtil.save(activity, "visible", true);
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        ++destoryed;

        //不可见
        if (createed <= destoryed) {
            //读取本地状态 如果是前面是可见状态 就要发送数据给手环 停止
            boolean visible = PrefUtil.getBoolean(activity, "visible", false);

            if (visible) {
                BaseActionUtils.isBackground = true;
                //如果本地保存的是可见状态(上一个状态) 此时 需要停止
                //停止同步分时数据
                if (BluetoothUtil.isConnected()) {
                    SyncData.getInstance().stopSyncDataAll();
                }

            } else {
                //现在就是开始状态 不需要发送同步命令
            }

            //保存状态到本地 开始通知手环停止同步
            PrefUtil.save(activity, "visible", false);
        }
    }


    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }
    private int reConnectCount ;
    @Override
    public void onActivityStarted(final Activity activity) {
        ++started;
        if (started > 0 && isBackground) {
            isBackground = false;
            BaseActionUtils.isBackground = false;
            final Intent intent=new Intent(BaseActionUtils.ACTION_CONNECT_TIMEOUT);
            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            Log.e("test", "application status : " + (started > 0));
            BackgroundThreadManager.getInstance().removeUnbindTask();

            //判断蓝牙是否可用和本地是否存储了蓝牙地址和蓝牙名字
            if (BluetoothUtil.isEnabledBluetooth(activity) && !TextUtils.isEmpty( PrefUtil.getString(BluetoothUtil.context,BaseActionUtils.ACTION_DEVICE_ADDRESS))
                    && !TextUtils.isEmpty( PrefUtil.getString(BluetoothUtil.context,BaseActionUtils.ACTION_DEVICE_NAME))) {
                //判断蓝牙是否连接
                if (!BluetoothUtil.isConnected()) {
                    KLog.e("reconnect ");
                    if (BleApplication.getInstance().getIBle()==null)
                            {
                        KLog.e("Bluetooth reconnection: IBle has not yet completed initialization delay 2s after reconnection");
                        mHandler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                reConnectCount++;
                                if (reConnectCount>3){
                                    reConnectCount=0;
                                    return;
                                }
                                if (BleApplication.getInstance().getIBle()==null){
                                    KLog.e("Bluetooth reconnection: IBle has not yet completed initialization delay 2s after reconnection "+ reConnectCount);
                                    mHandler.postDelayed(this,2000);
                                }else {
                                    BluetoothUtil.connect();
                                    KLog.e("腐竹炒腩肉------重连一次");
                                }
                            }
                        },2000);
                    } else {
                        BluetoothUtil.connect();
                    }
                } else {
                    // sync data
                    SyncData.getInstance().syncDataInfo();
                }
            }
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }


    @Override
    public void onActivityStopped(Activity activity) {
        --started;
        if (started <= 0 && !isBackground) {
            isBackground = true;
            BaseActionUtils.isBackground = true;

            Intent intent=new Intent(BaseActionUtils.ACTION_CONNECT_TIMEOUT);
            LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
            Log.e("test", "application status : " + (started > 0));
        }

    }

}
