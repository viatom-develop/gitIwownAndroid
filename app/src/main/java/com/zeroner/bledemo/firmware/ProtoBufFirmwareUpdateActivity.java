package com.zeroner.bledemo.firmware;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.receiver.BluetoothCallbackReceiver;
import com.zeroner.bledemo.setting.DfuService;
import com.zeroner.bledemo.setting.ScannerServiceParser;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BleReceiverHelper;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.cmdimpl.ProtoBufSendBluetoothCmdImpl;
import com.zeroner.blemidautumn.bluetooth.impl.BleService;
import com.zeroner.blemidautumn.library.KLog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

/**
 * https://github.com/NordicSemiconductor/Android-nRF-Toolbox
 */
public class ProtoBufFirmwareUpdateActivity extends AppCompatActivity {
    private static final int REQUEST_BLUETOOTH = 222;
    @BindView(R.id.toolbar_device_firmware)
    Toolbar toolbarDeviceFirmware;
    @BindView(R.id.progress_bar_2)
    RingProgressBar progressBar2;
    @BindView(R.id.button_select_file)
    Button buttonSelectFile;
    @BindView(R.id.button_start)
    Button buttonStart;
    @BindView(R.id.dfu_statue)
    TextView dfuStatue;
    private Handler mHandler = new Handler(Looper.myLooper());
    private static final long SCAN_DURATION = 10000;

    int REQUESTCODE_FROM_ACTIVITY = 1000;
    @BindView(R.id.file_path)
    TextView filePath;
    private Context context;

    private String firmware_file;

    private BluetoothAdapter mBluetoothAdapter;
    private long mLastTime = 0;
    private long clickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firmware_update);
        ButterKnife.bind(this);
        context = this;
        initView();
        initListener();
    }

    private void initView() {
        setSupportActionBar(toolbarDeviceFirmware);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarDeviceFirmware.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
        if (mBluetoothAdapter == null) {
            return;
        }
    }

    @OnClick({R.id.button_select_file, R.id.button_start})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_select_file:
                new LFilePicker()
                        .withActivity((Activity) context)
                        .withRequestCode(REQUESTCODE_FROM_ACTIVITY)
                        .withMutilyMode(false)
                        .withFileFilter(new String[]{".zip", ".hex", ".img"})
                        .start();
                break;
            case R.id.button_start:
                //设置不进行蓝牙重连
                if (System.currentTimeMillis() - clickTime <= 6000) {
                    return;
                }
                if (!checkBluetooth()) {
                    return;
                }
                writeDfuCmd();
                clickTime = System.currentTimeMillis();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUESTCODE_FROM_ACTIVITY) {
                //If it is a file selection mode, you need to get the path collection of all the files selected
                //List<String> list = data.getStringArrayListExtra(Constant.RESULT_INFO);//Constant.RESULT_INFO == "paths"
                List<String> list = data.getStringArrayListExtra("paths");
                if (list.size() > 0) {
                    firmware_file = list.get(0);
                    if (TextUtils.isEmpty(firmware_file)) {
                        buttonStart.setVisibility(View.GONE);
                        buttonStart.setClickable(false);
                    } else {
                        filePath.setText(getString(R.string.sync_progress_text_path, firmware_file));
                        buttonStart.setVisibility(View.VISIBLE);
                        buttonStart.setClickable(true);
                    }
                }
                //If it is a folder selection mode, you need to get the folder path of your choice
                //String path = data.getStringExtra("path");
            }
        }
    }

    private boolean checkBluetooth() {
        if (mBluetoothAdapter.isEnabled()) {
            return true;
        }
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_BLUETOOTH);
        return false;
    }

    private void initListener() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(mDfuUpdateReceiver, makeDfuUpdateIntentFilter());
        localBroadcastManager.registerReceiver(bleReceiver, new IntentFilter(BleService.BLE_CHARACTERISTIC_WRITE));
    }

    private BroadcastReceiver mDfuUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            // DFU is in progress or an error occurred
            final String action = intent.getAction();
            if (DfuService.BROADCAST_PROGRESS.equals(action)) {
                final int progress = intent.getIntExtra(DfuService.EXTRA_DATA, 0);
                final int currentPart = intent.getIntExtra(DfuService.EXTRA_PART_CURRENT, 1);
                final int totalParts = intent.getIntExtra(DfuService.EXTRA_PARTS_TOTAL, 1);
                KLog.e("num : " + progress);
                updateProgressBar(progress, currentPart, totalParts, false);
            } else if (DfuService.BROADCAST_ERROR.equals(action)) {
                final int error = intent.getIntExtra(DfuService.EXTRA_DATA, 0);
                updateProgressBar(error, 0, 0, true);
                // We have to wait a bit before canceling notification. This is
                // called before DfuService creates the last notification.
                // new Handler().postDelayed(new Runnable() {
                // @Override
                // public void run() {
                // // if this activity is still open and upload process was
                // // completed, cancel the notification
                // final NotificationManager manager = (NotificationManager)
                // getSystemService(Context.NOTIFICATION_SERVICE);
                // manager.cancel(DfuService.NOTIFICATION_ID);
                // }
                // }, 200);
            }
        }
    };

    private IntentFilter makeDfuUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DfuService.BROADCAST_PROGRESS);
        intentFilter.addAction(DfuService.BROADCAST_ERROR);
        intentFilter.addAction(DfuService.BROADCAST_LOG);
        return intentFilter;
    }

    private void startScan() {
        if (isBack()) {
            return;
        }
        if (BluetoothUtil.isScanning()) {
            BluetoothUtil.stopScan();
        }
        Intent intent = new Intent(this, DfuService.class);
        stopService(intent);
        KLog.e("固件升级 startScan   " + Thread.currentThread().getId());
        mBluetoothAdapter.startLeScan(mLEScanCallback);
        mHandler.postDelayed(mScanTimeOutRunnable, SCAN_DURATION);
        mLastTime = 0;
    }

    private void stopScan() {
        mHandler.removeCallbacks(mScanTimeOutRunnable);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    KLog.e("firmware stopScan    " + Thread.currentThread().getId());
                    if (mLEScanCallback == null) {
                        KLog.e("firmware stopScan    mLEScanCallback==null");
                    }
                    mBluetoothAdapter.stopLeScan(mLEScanCallback);
                } catch (Exception e) {
                    KLog.e("firmware error : " + e.toString());
                }
            }
        });
    }

    Runnable mScanTimeOutRunnable = new Runnable() {
        @Override
        public void run() {
            stopScan();
        }
    };

    /**
     * Callback for scanned devices class {@link} will be
     * used to filter devices with custom BLE service UUID then the device will
     * be added in a list.
     */
    private BluetoothAdapter.LeScanCallback mLEScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            String address = device.getAddress();
            String name = device.getName();
            if (TextUtils.isEmpty(name)) {
                return;
            }
            KLog.i("----------" + address + "---------" + name);
            //nordic
            if (ScannerServiceParser.decodeDeviceAdvData(scanRecord, BaseActionUtils.NODIC_UPDATE_SERVICE) || ScannerServiceParser.decodeDeviceAdvData(scanRecord, BaseActionUtils.UPDATE_SERVICE_MAIN_DFU)) {
                if (judgeRepeat(address)) {
                    return;
                }
                mHandler.removeCallbacks(mScanTimeOutRunnable);
                stopScan();
                dfuStatue.setText(R.string.update_step_connect_device);
                Intent updateIntent = new Intent(context, DfuService.class);
                updateIntent.putExtra(DfuService.EXTRA_DEVICE_ADDRESS, address);
                KLog.e("(nordic) DFU　search MAC ：" + address);
                updateIntent.putExtra(DfuService.EXTRA_DEVICE_NAME, device.getName());
                updateIntent.putExtra(DfuService.EXTRA_DEVICE_ADDRESS, address);
//						updateIntent.putExtra(DfuService.EXTRA_FILE_MIME_TYPE, DfuService.MIME_TYPE_OCTET_STREAM);
                updateIntent.putExtra(DfuService.EXTRA_FILE_PATH, firmware_file);
                updateIntent.putExtra(DfuService.EXTRA_FILE_TYPE, DfuService.TYPE_AUTO);
//						updateIntent.putExtra(DfuService.EXTRA_FILE_URI, uri);
                startService(updateIntent);
            }
        }
    };

    private synchronized boolean judgeRepeat(String address) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastTime < 60000) {
            return true;
        }
        mLastTime = nowTime;
        if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_ADDRESS))) {
            if (address.equals(getNewMac(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_ADDRESS), 1))) {
                return false;
            } else {
                mLastTime = 0;
                return true;
            }
        }
        return false;
    }

    private String getNewMac(String mac, int type) {
        if (isDialog()) {
            return mac;
        }
        String newMac = "";
        String oneMac = mac.substring(0, mac.lastIndexOf(":") + 1);
        String twoMac = mac.substring(mac.lastIndexOf(":") + 1, mac.length());
        int newTwoMac = Integer.parseInt(twoMac, 16);
        if (type == 1) {
            if (newTwoMac == 0xff) {
                newTwoMac = 0;
            } else {
                newTwoMac = newTwoMac + 1;
            }
        } else if (type == 2) {
            if (newTwoMac == 0) {
                newTwoMac = 0xff;
            } else {
                newTwoMac = newTwoMac - 1;
            }
        }
        String last = Integer.toHexString(newTwoMac);
        newMac = oneMac + (last.length() == 1 ? (0 + last) : last);
//        newMac = oneMac + last;

        KLog.e("lod mac====>" + mac + "new mac" + newMac);
        return newMac.toUpperCase();
    }

    private boolean isBack() {
        return isDestroyed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDfuUpdateReceiver);
        BleReceiverHelper.unregisterBleReceiver(this, bleReceiver);
    }

    public boolean isDialog() {
        return getModel().contains("I6HR") || getModel().contains("I6NH") || getModel().contains("I6PB") || getModel().contains("I6H9");
    }

    public String getModel() {
        return PrefUtil.getString(context, BaseActionUtils.Action_device_Model);
    }

    private void writeDfuCmd() {
        if (BluetoothUtil.isConnected()) {
            final boolean flag = ProtoBufSendBluetoothCmdImpl.getInstance().setUpgradeNotification();
            BluetoothUtil.setNeedReconnect(false);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (flag) {
                        ProtoBufSendBluetoothCmdImpl.getInstance().setUpgradeCmd();
                        mHandler.removeCallbacks(mWriteDFUTimeoutRunnable);
                        mHandler.postDelayed(mWriteDFUTimeoutRunnable, 5000);
                    }
                }
            }, 3000);
            return;
        } else {
            //already dfu
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isDestroyed()) {
                        return;
                    }
                    startScan();
                }
            }, 1000);
        }
    }

    Runnable mWriteDFUTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            dfuStatue.setText(R.string.dfu_command_write_fail);
        }
    };

    private BluetoothCallbackReceiver bleReceiver = new BluetoothCallbackReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            String action = intent.getAction();
            if (BleService.BLE_CHARACTERISTIC_WRITE.equals(action)) {
                byte[] data = intent.getByteArrayExtra(BleService.EXTRA_DATA);
                if (data.length == 1 && data[0] == 1) {
                    com.socks.library.KLog.e("---------protobuf写入DFU指令成功");
                    //成功写入dfu指令
                    mHandler.removeCallbacks(mWriteDFUTimeoutRunnable);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isDestroyed()) {
                                return;
                            }
                            startScan();
                        }
                    }, 3000);
                }
            }
        }
    };

    private void updateProgressBar(final int progress, final int part, final int total, final boolean error) {
        if (isDestroyed()) {
            return;
        }
        if (error) {
            updateUI(true);
//            updateUI(mNowStep, true);
        }
        switch (progress) {
            case DfuService.PROGRESS_CONNECTING:
                stopScan();
                // mProgressBar.setIndeterminate(true);
                // mProgressBar.setProgressText(getString(R.string.dfu_status_connecting));
//                mProgressBar.setProgressText(getString(R.string.dfu_status_connecting));
                break;
            case DfuService.PROGRESS_STARTING:
                // mProgressBar.setIndeterminate(true);
                // mProgressBar.setProgressText(getString(R.string.dfu_status_starting));
                dfuStatue.setText(getString(R.string.dfu_status_starting));
                break;
            case DfuService.PROGRESS_VALIDATING:
                // mProgressBar.setIndeterminate(true);
                // mTextPercentage.setText(R.string.dfu_status_validating);
                // mProgressBar.setProgressText(getString(R.string.dfu_status_validating));
                dfuStatue.setText(getString(R.string.dfu_status_validating));
                break;
            case DfuService.PROGRESS_DISCONNECTING:
                // mProgressBar.setIndeterminate(true);
                // mTextPercentage.setText(R.string.dfu_status_disconnecting);
                // mProgressBar.setProgressText(getString(R.string.dfu_status_disconnecting));
                dfuStatue.setText(getString(R.string.dfu_status_disconnecting));
//			startScan();
                break;
            case DfuService.PROGRESS_COMPLETED:
                // mProgressBar.setProgress(100);
                // mProgressBar.setCircleColor(false);
                // mProgressBar.setProgressText(getString(R.string.activity_update_over));
                // UserConfig.getInstance(mContext).setDerviceAddress(deviceName);
                // UserConfig.getInstance(mContext).save(mContext);
                // mTextPercentage.setText(R.string.dfu_status_completed);
                // let's wait a bit until we cancel the notification. When canceled
                // immediately it will be recreated by service again.
                // new Handler().postDelayed(new Runnable() {
                // @Override
                // public void run() {
                // onTransferCompleted();
                // // if this activity is still open and upload process was
                // // completed, cancel the notification
                // final NotificationManager manager = (NotificationManager)
                // getSystemService(Context.NOTIFICATION_SERVICE);
                // manager.cancel(DfuService.NOTIFICATION_ID);
                // }
                // }, 200);
                dfuStatue.setText(getString(R.string.activity_update_over));
                finish();
                break;
            case DfuService.PROGRESS_ABORTED:
                // mTextPercentage.setText(R.string.dfu_status_aborted);
                // let's wait a bit until we cancel the notification. When canceled
                // immediately it will be recreated by service again.
                // new Handler().postDelayed(new Runnable() {
                // @Override
                // public void run() {
                // onUploadCanceled();
                // // if this activity is still open and upload process was
                // // completed, cancel the notification
                // final NotificationManager manager = (NotificationManager)
                // getSystemService(Context.NOTIFICATION_SERVICE);
                // manager.cancel(DfuService.NOTIFICATION_ID);
                // }
                // }, 200);
                break;
            default:
                if (error) {
//                    updateUI(STEP_WRITE_HARDWARE_TO_DEVICE, true);
                    updateUI(true);
                } else {
                    int result = (int) (progress);
                    progressBar2.setVisibility(View.VISIBLE);
                    progressBar2.setProgress(result);
                    updateUI(false);
//                    mTvStep.setUpdateText(getString(R.string.update_step_write_device_progress, progress));
                    dfuStatue.setText(R.string.update_step_write_device);
                    if (progress >= 100) {
                        progressBar2.setProgress(100);
                    }
                }
                break;
        }
    }


    private void updateUI(boolean flag) {
        if (flag) {
            buttonStart.setClickable(true);
            buttonSelectFile.setClickable(true);
            buttonStart.setText(R.string.update_step_write_retry);
        } else {
            buttonStart.setClickable(false);
            buttonSelectFile.setClickable(false);
        }
    }


}
