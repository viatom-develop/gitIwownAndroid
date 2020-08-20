package com.zeroner.bledemo.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.zeroner.bledemo.utils.FotaUtils;
import com.zeroner.blemidautumn.library.KLog;

import coms.mediatek.ctrl.fota.common.FotaOperator;
import coms.mediatek.ctrl.fota.common.FotaVersion;
import coms.mediatek.ctrl.fota.common.IFotaOperatorCallback;
import coms.mediatek.wearable.WearableManager;

/**
 * Created by cindy on 17/9/20.
 */

public class ZeronerFotaService extends Service {
    private String file_path = "";
    private String address = "";
    private static int INTENT_NULL_ERRO = -22;

    public static String EXTRA_FILE_PATH = "Fota_Service_EXTRA_FILE_PATH";
    public static String EXTRA_DEVICE_ADDRESS = "Fota_Service_EXTRA_DEVICE_ADDRESS";
    public static String BROADCAST_ERROR = "Fota_Service_BROADCAST_ERROR";
    public static String BROADCAST_PROGRESS = "Fota_Service_BROADCAST_PROGRESS";
    public static String EXTRA_DATA = "Fota_Service_EXTRA_DATA";
    public static String EXTRA_DEVICE_NAME = "Fota_Service_EXTRA_DEVICE_NAME";


    public static final int PROGRESS_CONNECTING = -1;
    public static final int PROGRESS_STARTING = -2;
    public static final int PROGRESS_ENABLING_DFU_MODE = -3;
    public static final int PROGRESS_VALIDATING = -4;
    public static final int PROGRESS_DISCONNECTING = -5;
    public static final int PROGRESS_COMPLETED = -6;
    public static final int PROGRESS_ABORTED = -7;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FotaOperator.getInstance(this).registerFotaCallback(mFotaCallback);
        KLog.d("ZeronerFotaService --onCreate");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null==intent) {
            sendErroBroadCast(INTENT_NULL_ERRO);
            stopSelf();
        }

        Log.e("licl", "ZeronerFotaService--onStartCommand");
        if (WearableManager.getInstance().isAvailable()) {
            Log.e("licl", "mtk手表已经连接上，直接去升级");
            KLog.d("mtk手表已经连接上，直接去升级");
            file_path = intent.getStringExtra(EXTRA_FILE_PATH);
            address = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);
            //先保证任务停掉
            cancelTransTask();
            mTransferTask.execute();
        }else {
            Log.e("licl", "mtk手表升级前进行连接");
            BluetoothDevice device = WearableManager.getInstance().getRemoteDevice();
            if (null==device) {
                device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(intent.getStringExtra(EXTRA_DEVICE_ADDRESS));
                WearableManager.getInstance().setRemoteDevice(device);
            }
            WearableManager.getInstance().connect();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        KLog.d("ZeronerFotaService --onDestroy");
        super.onDestroy();
        cancelTransTask();
        FotaOperator.getInstance(this).unregisterFotaCallback(mFotaCallback);
//        FotaOperator.getInstance(this).close();
    }


    private String TAG = this.getClass().getSimpleName();
    private AsyncTask<Void, Void, Void> mTransferTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... parameters) {

            KLog.d("========mtk固件升级文件开始写入===========");

            Log.e("licl", "[doInBackground] begin Fota Transferring--"+file_path);
            if (!TextUtils.isEmpty(file_path)) {
                FotaOperator.getInstance(ZeronerFotaService.this).sendFotaFirmwareData(FotaOperator.TYPE_FIRMWARE_FULL_BIN, file_path);
            } else{

                return null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.e("licl", "[mTransferTaks] onPostExecute called");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.e("licl", "[mTransferTaks] onCancelled is called, update UX");
        }
    };


    private Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mTransferViaBTErrorHappened = false;
    private IFotaOperatorCallback mFotaCallback = new IFotaOperatorCallback() {

        @Override
        public void onCustomerInfoReceived(String information) {
            Log.d(TAG, "[onCustomerInfoReceived] information : " + information);
        }

        @Override
        public void onFotaVersionReceived(FotaVersion version) {
            Log.d(TAG, "[onFotaVersionReceived] version : " + version);

        }

        @Override
        public void onStatusReceived(int status) {
            Log.d(TAG, "[onStatusReceived] status : " + status);

            switch(status) {
                case FotaUtils.FOTA_SEND_VIA_BT_SUCCESS:
                    Log.e(TAG, "[onStatusReceived] send succeed. update text view");
                    break;

                case FotaUtils.FOTA_UPDATE_VIA_BT_SUCCESS:
                    Log.e(TAG, "[onStatusReceived]  " + FotaUtils.FOTA_UPDATE_VIA_BT_SUCCESS);
                    break;

                case FotaUtils.FOTA_UPDATE_VIA_BT_COMMON_ERROR:
                case FotaUtils.FOTA_UPDATE_VIA_BT_WRITE_FILE_FAILED:
                case FotaUtils.FOTA_UPDATE_VIA_BT_DISK_FULL:
                case FotaUtils.FOTA_UPDATE_VIA_BT_DATA_TRANSFER_ERROR:
                    Log.d(TAG, "[onStatusReceived] transfer error happened, set mTransferViaBTErrorHappened to be TRUE");
                case FotaUtils.FOTA_UPDATE_VIA_BT_TRIGGER_FAILED:
                case FotaUtils.FOTA_UPDATE_VIA_BT_FAILED:
                case FotaUtils.FOTA_UPDATE_TRIGGER_FAILED_CAUSE_LOW_BATTERY:
                case FotaUtils.FILE_NOT_FOUND_ERROR:
                case FotaUtils.READ_FILE_FAILED:
                    Log.d(TAG, "[onStatusReceived] update failed!");
                    cancelTransTask();
                    String str = null;
                    if (status == FotaUtils.FOTA_UPDATE_TRIGGER_FAILED_CAUSE_LOW_BATTERY) {
                    } else {
                    }

                    Message msg2 = mHandler.obtainMessage();
                    if (status == FotaUtils.FILE_NOT_FOUND_ERROR) {
                        msg2.arg1 = FotaUtils.FILE_NOT_FOUND_ERROR;
                    } else if (status == FotaUtils.READ_FILE_FAILED) {
                        msg2.arg1 = FotaUtils.READ_FILE_FAILED;
                    } else {
                        msg2.arg1 = FotaUtils.MSG_ARG1_DOWNLOAD_FAILED;
                    }
                    mTransferViaBTErrorHappened = true;
                    //发送错误广播
                    sendErroBroadCast(status);
                    break;

                default:
                    break;
            }
        }

        @Override
        public void onConnectionStateChange(int newState) {

            Log.d(TAG, "[onConnectionStateChange] newState "+newState);

            if (newState == WearableManager.STATE_CONNECTING) {
                sendProgressBroadCast(PROGRESS_CONNECTING);
                return;
            }

            if (newState == WearableManager.STATE_DISCONNECTING) {
                sendProgressBroadCast(PROGRESS_DISCONNECTING);
            }

            if(newState == WearableManager.STATE_CONNECT_FAIL
                    ||newState== WearableManager.STATE_CONNECT_LOST
                    ||(!WearableManager.getInstance().isAvailable())){

                //连接失败
                cancelTransTask();
                sendErroBroadCast(newState);
            }else if(newState == WearableManager.STATE_CONNECTED) {
                //连接成功, 开始升级
                mTransferTask.execute();
                sendProgressBroadCast(PROGRESS_STARTING);
            }
        }

        @Override
        public void onProgress(int progress) {
            if (!mTransferViaBTErrorHappened) {
                Log.d(TAG, "[onProgress] progress : " + progress);
                Log.e("licl", "[onProgress] progress : " + progress);

                if (progress == 100) {
                    KLog.d("========mtk固件升级文件写入结束===========");
                    sendProgressBroadCast(PROGRESS_COMPLETED);
                    cancelTransTask();
                    stopSelf();
                }else {
                    sendProgressBroadCast(progress);
                }
            } else {
                Log.d(TAG, "[onProgress] mTransferViaBTErrorHappened is TRUE, no need to update progress");
                sendProgressBroadCast(PROGRESS_ABORTED);
            }
        }

    };

    private void sendProgressBroadCast(int progress) {
        Intent progressIntent = new Intent(BROADCAST_PROGRESS);
        progressIntent.putExtra(EXTRA_DATA, progress);
        progressIntent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        LocalBroadcastManager.getInstance(this).sendBroadcast(progressIntent);
    }

    private void sendErroBroadCast(int status) {
        KLog.d("mtk固件升级出现错误 code:"+status);
        Intent errorIntent = new Intent(BROADCAST_ERROR);
        errorIntent.putExtra(EXTRA_DEVICE_ADDRESS, address);
        errorIntent.putExtra(EXTRA_DATA, status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(errorIntent);
        cancelTransTask();
    }

    public void cancelTransTask(){
        if (!mTransferTask.isCancelled() && mTransferTask.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d(TAG, "[onStatusReceived] cancel the transfer action");
            mTransferTask.cancel(true);
        }
    }

}
