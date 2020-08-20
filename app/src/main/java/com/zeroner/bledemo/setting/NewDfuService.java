package com.zeroner.bledemo.setting;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.socks.library.KLog;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.utils.BluetoothUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import no.nordicsemi.android.dialog.BluetoothGattReceiver;
import no.nordicsemi.android.dialog.BluetoothGattSingleton;
import no.nordicsemi.android.dialog.SuotaManager;
import no.nordicsemi.android.dialog.async.DeviceConnectTask;
import no.nordicsemi.android.dialog.data.File;
import no.nordicsemi.android.dialog.data.Statics;
import no.nordicsemi.android.error.GattError;

/**
 * Created by zm on 2016/9/24.
 */
public class NewDfuService extends Service {

    /**
     * The address of the device to update.
     */
    public static final String EXTRA_DEVICE_ADDRESS = "no.nordicsemi.android.dfu.extra.EXTRA_DEVICE_ADDRESS";
    /**
     * The optional device name. This name will be shown in the notification.
     */
    public static final String EXTRA_DEVICE_NAME = "no.nordicsemi.android.dfu.extra.EXTRA_DEVICE_NAME";
    public static final String EXTRA_DEVICE = "no.NewDfuService.android.dfu.extra.EXTRA_DEVICE";
    /**
     * A boolean indicating whether to disable the progress notification in the status bar. Defaults to false.
     */
    public static final String EXTRA_DISABLE_NOTIFICATION = "no.nordicsemi.android.dfu.extra.EXTRA_DISABLE_NOTIFICATION";

    /**
     * An extra field to send the progress or error information in the DFU notification. The value may contain:
     * <ul>
     * <li>Value 0 - 100 - percentage progress value</li>
     * <li>One of the following status constants:
     * <ul>
     * <li>{@link #PROGRESS_CONNECTING}</li>
     * <li>{@link #PROGRESS_STARTING}</li>
     * <li>{@link #PROGRESS_ENABLING_DFU_MODE}</li>
     * <li>{@link #PROGRESS_VALIDATING}</li>
     * <li>{@link #PROGRESS_DISCONNECTING}</li>
     * <li>{@link #PROGRESS_COMPLETED}</li>
     * <li>{@link #PROGRESS_ABORTED}</li>
     * </ul>
     * </li>
     * <li>An error code with {@link #ERROR_MASK} if initialization error occurred</li>
     * <li>An error code with {@link #ERROR_REMOTE_MASK} if remote DFU target returned an error</li>
     * <li>An error code with {@link #ERROR_CONNECTION_MASK} if connection error occurred (f.e. GATT error (133) or Internal GATT Error (129))</li>
     * </ul>
     * To check if error occurred use:<br />
     * {@code boolean error = progressValue >= DfuBaseService.ERROR_MASK;}
     */
    public static final String EXTRA_PROGRESS = "no.nordicsemi.android.dfu.extra.EXTRA_PROGRESS";
    /**
     * If this bit is set than the progress value indicates an error. Use {@link GattError#parse(int)} to obtain error name.
     */
    public static final int ERROR_MASK = 0x1000;
    public static final int ERROR_DEVICE_DISCONNECTED = ERROR_MASK; // | 0x00;
    public static final int ERROR_FILE_NOT_FOUND = ERROR_MASK | 0x01;
    /**
     * Thrown if service was unable to open the file ({@link IOException} has been thrown).
     */
    public static final int ERROR_FILE_ERROR = ERROR_MASK | 0x02;
    /**
     * Thrown when input file is not a valid HEX or ZIP file.
     */
    public static final int ERROR_FILE_INVALID = ERROR_MASK | 0x03;
    /**
     * Thrown when {@link IOException} occurred when reading from file.
     */
    public static final int ERROR_FILE_IO_EXCEPTION = ERROR_MASK | 0x04;
    /**
     * Error thrown when {@code gatt.discoverServices();} returns false.
     */
    public static final int ERROR_SERVICE_DISCOVERY_NOT_STARTED = ERROR_MASK | 0x05;

    /**
     * Flag set then the DFU target returned a DFU error. Look for DFU specification to get error codes.
     */
    public static final int ERROR_REMOTE_MASK = 0x2000;

    /**
     * The flag set when one of {@link BluetoothGattCallback} methods was called with status other than {@link BluetoothGatt#GATT_SUCCESS}.
     */
    public static final int ERROR_CONNECTION_MASK = 0x4000;
    /**
     * The flag set when the {@link BluetoothGattCallback#onConnectionStateChange(BluetoothGatt, int, int)} method was called with
     * status other than {@link BluetoothGatt#GATT_SUCCESS}.
     */
    public static final int ERROR_CONNECTION_STATE_MASK = 0x8000;
    /**
     * Service is connecting to the remote DFU target.
     */
    public static final int PROGRESS_CONNECTING = -1;
    /**
     * Service is enabling notifications and starting transmission.
     */
    public static final int PROGRESS_STARTING = -2;
    /**
     * Service has triggered a switch to bootloader mode. Now the service waits for the link loss event (this may take up to several seconds) and will connect again
     * to the same device, now started in the bootloader mode.
     */
    public static final int PROGRESS_ENABLING_DFU_MODE = -3;
    /**
     * Service is sending validation request to the remote DFU target.
     */
    public static final int PROGRESS_VALIDATING = -4;
    /**
     * Service is disconnecting from the DFU target.
     */
    public static final int PROGRESS_DISCONNECTING = -5;
    /**
     * The connection is successful.
     */
    public static final int PROGRESS_COMPLETED = -6;
    /**
     * The upload has been aborted. Previous software version will be restored on the target.
     */
    public static final int PROGRESS_ABORTED = -7;
    /**
     * An extra field with progress and error information used in broadcast events.
     */
    public static final String EXTRA_DATA = "no.nordicsemi.android.dfu.extra.EXTRA_DATA";
    /**
     * This optional extra parameter may contain a file type. Currently supported are:
     * <ul>
     * <li>{@link #TYPE_SOFT_DEVICE} - only Soft Device update</li>
     * <li>{@link #TYPE_BOOTLOADER} - only Bootloader update</li>
     * <li>{@link #TYPE_APPLICATION} - only application update</li>
     * </ol>
     */
    public static final String EXTRA_FILE_TYPE = "no.nordicsemi.android.dfu.extra.EXTRA_FILE_TYPE";

    /**
     * The number of currently transferred part. The SoftDevice and Bootloader may be send together as one part. If user wants to upload them together with an application it has to be sent
     * in another connection as the second part.
     *
     * @see no.nordicsemi.android.dfu.DfuBaseService#EXTRA_PARTS_TOTAL
     */
    public static final String EXTRA_PART_CURRENT = "no.nordicsemi.android.dfu.extra.EXTRA_PART_CURRENT";
    /**
     * Number of parts in total.
     *
     * @see no.nordicsemi.android.dfu.DfuBaseService#EXTRA_PART_CURRENT
     */
    public static final String EXTRA_PARTS_TOTAL = "no.nordicsemi.android.dfu.extra.EXTRA_PARTS_TOTAL";

    /**
     * The current upload speed in bytes/millisecond.
     */
    public static final String EXTRA_SPEED_B_PER_MS = "no.nordicsemi.android.dfu.extra.EXTRA_SPEED_B_PER_MS";
    /**
     * The average upload speed in bytes/millisecond for the current part.
     */
    public static final String EXTRA_AVG_SPEED_B_PER_MS = "no.nordicsemi.android.dfu.extra.EXTRA_AVG_SPEED_B_PER_MS";
    public static final int TYPE_SOFT_DEVICE = 0x01;
    /**
     * <p>
     * The file contains a new version of Bootloader.
     * </p>
     * <p>
     * Since DFU Library 7.0 all firmware may contain an Init packet. The Init packet is required if Extended Init Packet is used by the DFU bootloader (SDK 7.0+).
     * The Init packet for the bootloader must be placed in the .dat file.
     * </p>
     *
     * @see #EXTRA_FILE_TYPE
     */
    public static final int TYPE_BOOTLOADER = 0x02;
    /**
     * <p>
     * The file contains a new version of Application.
     * </p>
     * <p>
     * Since DFU Library 0.5 all firmware may contain an Init packet. The Init packet is required if Extended Init Packet is used by the DFU bootloader (SDK 7.0+).
     * The Init packet for the application must be placed in the .dat file.
     * </p>
     *
     * @see #EXTRA_FILE_TYPE
     */
    public static final int TYPE_APPLICATION = 0x04;

    /**
     * The action extra. It may have one of the following values: {@link #ACTION_PAUSE}, {@link #ACTION_RESUME}, {@link #ACTION_ABORT}.
     */
    public static final String EXTRA_ACTION = "no.nordicsemi.android.dfu.extra.EXTRA_ACTION";

    /**
     * A path to the file with the new firmware. It may point to a HEX, img or a ZIP file.
     */
    public static final String EXTRA_FILE_PATH = "no.nordicsemi.android.dfu.extra.EXTRA_FILE_PATH";

    /** Pauses the upload. The service will wait for broadcasts with the action set to {@link #ACTION_RESUME} or {@link #ACTION_ABORT}. */
    public static final int ACTION_PAUSE = 0;
    /** Resumes the upload that has been paused before using {@link #ACTION_PAUSE}. */
    public static final int ACTION_RESUME = 1;
    /**
     * Aborts the upload. The service does not need to be paused before.
     * After sending {@link #BROADCAST_ACTION} with extra {@link #EXTRA_ACTION} set to this value the DFU bootloader will restore the old application
     * (if there was already an application). Be aware that uploading the Soft Device will erase the application in order to make space in the memory.
     * In case there is no application, or the application has been removed, the DFU bootloader will be started and user may try to send the application again.
     * The bootloader may advertise with the address incremented by 1 to prevent caching services.
     */
    public static final int ACTION_ABORT = 2;
    /**
     * Activity may broadcast this broadcast in order to pause, resume or abort DFU process.
     * Use {@link #EXTRA_ACTION} extra to pass the action.
     */
    public static final String BROADCAST_ACTION = "no.nordicsemi.android.dfu.broadcast.BROADCAST_ACTION";

    /**
     * The broadcast error message contains the following extras:
     * <ul>
     * <li>{@link #EXTRA_DATA} - the error number. Use {@link GattError#parse(int)} to get String representation</li>
     * <li>{@link #EXTRA_DEVICE_ADDRESS} - the target device address</li>
     * </ul>
     */
    public static final String BROADCAST_ERROR = "no.nordicsemi.android.dfu.broadcast.BROADCAST_ERROR";

    /**
     * The type of the error. This extra contains information about that kind of error has occurred. Connection state errors and other errors may share the same numbers.
     * For example, the {@link BluetoothGattCallback#onCharacteristicWrite(BluetoothGatt, BluetoothGattCharacteristic, int)} method may return a status code 8 (GATT INSUF AUTHORIZATION),
     * while the status code 8 returned by {@link BluetoothGattCallback#onConnectionStateChange(BluetoothGatt, int, int)} is a GATT CONN TIMEOUT error.
     */
    public static final String EXTRA_ERROR_TYPE = "no.nordicsemi.android.dfu.extra.EXTRA_ERROR_TYPE";
    public static final int ERROR_TYPE_OTHER = 0;
    public static final int ERROR_TYPE_COMMUNICATION_STATE = 1;
    public static final int ERROR_TYPE_COMMUNICATION = 2;
    public static final int ERROR_TYPE_DFU_REMOTE = 3;

    /**
     * The broadcast message contains the following extras:
     * <ul>
     * <li>{@link #EXTRA_DATA} - the progress value (percentage 0-100) or:
     * <ul>
     * <li>{@link #PROGRESS_CONNECTING}</li>
     * <li>{@link #PROGRESS_STARTING}</li>
     * <li>{@link #PROGRESS_ENABLING_DFU_MODE}</li>
     * <li>{@link #PROGRESS_VALIDATING}</li>
     * <li>{@link #PROGRESS_DISCONNECTING}</li>
     * <li>{@link #PROGRESS_COMPLETED}</li>
     * <li>{@link #PROGRESS_ABORTED}</li>
     * </ul>
     * </li>
     * <li>{@link #EXTRA_DEVICE_ADDRESS} - the target device address</li>
     * <li>{@link #EXTRA_PART_CURRENT} - the number of currently transmitted part</li>
     * <li>{@link #EXTRA_PARTS_TOTAL} - total number of parts that are being sent, f.e. if a ZIP file contains a Soft Device, a Bootloader and an Application,
     * the SoftDevice and Bootloader will be send together as one part. Then the service will disconnect and reconnect to the new Bootloader and send the
     * application as part number two.</li>
     * <li>{@link #EXTRA_SPEED_B_PER_MS} - current speed in bytes/millisecond as float</li>
     * <li>{@link #EXTRA_AVG_SPEED_B_PER_MS} - the average transmission speed in bytes/millisecond as float</li>
     * </ul>
     */
    public static final String BROADCAST_PROGRESS = "no.nordicsemi.android.dfu.broadcast.BROADCAST_PROGRESS";

    private boolean mDisableNotification;
    private String mDeviceAddress;
    private String mDeviceName;
    private int mPartsTotal;
    private int mPartCurrent;
    private int mFileType;
    public static final int NOTIFICATION_ID = 283; // a random number
    private long mLastProgressTime;
    private long mBytesSent;
    private long mLastBytesSent;
    private long mStartTime;
    private SuotaManager mSuotaManager;
    private BluetoothGattReceiver bluetoothGattReceiver;
    private BluetoothGattReceiver connectionStateReceiver;
    private Handler mHandler;
    private int mLastProgress;
    private int mLastProgress1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        this.bluetoothGattReceiver = new BluetoothGattReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                mSuotaManager.processStep(intent);
            }
        };
        this.connectionStateReceiver = new BluetoothGattReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                super.onReceive(context, intent);
                int connectionState = intent.getIntExtra("state", 0);
                connectionStateChanged(connectionState);
            }
        };
        registerReceiver(
                this.bluetoothGattReceiver,
                new IntentFilter(Statics.BLUETOOTH_GATT_UPDATE));

        registerReceiver(
                this.connectionStateReceiver,
                new IntentFilter(Statics.CONNECTION_STATE_UPDATE));
        try {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("11111","ForegroundServiceChannel",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(false);
                channel.enableLights(false);
                channel.setSound(null,null);


                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                manager.createNotificationChannel(channel);

                Notification notification = new Notification.Builder(getApplicationContext(),"11111").build();
                startForeground(1, notification);
            }else {
                startForeground(1, new Notification());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connectionStateChanged(int connectionState) {
        if (connectionState == BluetoothProfile.STATE_DISCONNECTED) {
//            Toast.makeText(DeviceActivity.this, this.bluetoothManager.getDevice_name().getName() + " disconnected.", Toast.LENGTH_LONG).show();
//            updateProgressNotification(PROGRESS_DISCONNECTING,0,0);
            if (mLastProgress!=PROGRESS_COMPLETED){
                updateProgressNotification(ERROR_CONNECTION_MASK,0,0);
            }
            if (BluetoothGattSingleton.getGatt() != null)
                BluetoothGattSingleton.getGatt().close();
            if (!mSuotaManager.isFinished()) {
                if (!mSuotaManager.getError()) {
                    //                    finish();
                }

            }
        } else if (connectionState == BluetoothProfile.STATE_CONNECTED) {
//            mHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (BluetoothGattSingleton.getGatt() != null)
//                        startUpdate();
//                }
//            }, 2000);

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            try {
                BluetoothUtil.isConnected();
                BluetoothUtil.disconnect();
                BluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
                String filePath = intent.getStringExtra(EXTRA_FILE_PATH);
                mSuotaManager = new SuotaManager(this);
                mSuotaManager.setDevice(device);

                mSuotaManager.setFile(new File(new FileInputStream(filePath)));
                mDeviceAddress = device.getAddress();
                mDeviceName = device.getName();
                updateProgressNotification(PROGRESS_CONNECTING, 0, 0);
                DeviceConnectTask connectTask = new DeviceConnectTask(this, device, mSuotaManager) {
                    @Override
                    protected void onProgressUpdate(BluetoothGatt... gatt) {
                        BluetoothGattSingleton.setGatt(gatt[0]);
                    }
                };
                connectTask.execute();
            } catch (IOException e) {
                terminateConnection(ERROR_FILE_ERROR);
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startUpdate() {
        int fileBlockSize = 1;
        if (mSuotaManager.type == SuotaManager.TYPE) {
//            fileBlockSize = Integer.parseInt(blockSize.getText().toString());
            fileBlockSize = 240;
        }
        mSuotaManager.getFile().setFileBlockSize(fileBlockSize);
        Intent intent = new Intent();
        intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
        intent.putExtra("step", 1);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothGattReceiver);
        unregisterReceiver(connectionStateReceiver);
    }


    /**
     * Creates or updates the notification in the Notification Manager. Sends broadcast with given progress or error state to the activity.
     *
     * @param progress the current progress state or an error number, can be one of {@link #PROGRESS_CONNECTING}, {@link #PROGRESS_STARTING}, {@link #PROGRESS_ENABLING_DFU_MODE},
     *                 {@link #PROGRESS_VALIDATING}, {@link #PROGRESS_DISCONNECTING}, {@link #PROGRESS_COMPLETED} or {@link #ERROR_FILE_ERROR}, {@link #ERROR_FILE_INVALID} , etc
     * @param blockCounter
     * @param numberOfBlocks
     */
    @TargetApi(Build.VERSION_CODES.O)
    public void updateProgressNotification(final int progress, int blockCounter, int numberOfBlocks) {
        if (mLastProgress!=progress){
            KLog.e("  progress  " +progress);
            mLastProgress = progress;
        }else {
            return;
        }

        // send progress or error broadcast
        if (progress < ERROR_MASK)
            sendProgressBroadcast(progress,blockCounter,numberOfBlocks);
        else
            sendErrorBroadcast(progress);

        if (mDisableNotification) return;
        // create or update notification:

        final String deviceAddress = mDeviceAddress;
        final String deviceName = mDeviceName != null ? mDeviceName : getString(R.string.dfu_unknown_name);

        // final Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_notify_dfu); <- this looks bad on Android 5

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(android.R.drawable.stat_sys_upload).setOnlyAlertOnce(true);//.setLargeIcon(largeIcon);
        // Android 5

//		builder.setColor(Color.GRAY);


//		builder.setColor(Color.GRAY);

        switch (progress) {
            case PROGRESS_CONNECTING:
                builder.setOngoing(true).setContentTitle(getString(R.string.dfu_status_connecting)).setContentText(getString(R.string.dfu_status_connecting_msg, deviceName)).setProgress(100, 0, true);
                break;
            case PROGRESS_STARTING:
//				builder.setOngoing(true).setContentTitle(getString(R.string.dfu_status_starting)).setContentText(getString(R.string.dfu_status_starting_msg, deviceName)).setProgress(100, 0, true);
                break;
            case PROGRESS_ENABLING_DFU_MODE:
//				builder.setOngoing(true).setContentTitle(getString(R.string.dfu_status_switching_to_dfu)).setContentText(getString(R.string.dfu_status_switching_to_dfu_msg, deviceName))
//						.setProgress(100, 0, true);
                break;
            case PROGRESS_VALIDATING:
//				builder.setOngoing(true).setContentTitle(getString(R.string.dfu_status_validating)).setContentText(getString(R.string.dfu_status_validating_msg, deviceName)).setProgress(100, 0, true);
                break;
            case PROGRESS_DISCONNECTING:
                builder.setOngoing(true).setContentTitle(getString(R.string.dfu_status_disconnecting)).setContentText(getString(R.string.dfu_status_disconnecting_msg, deviceName))
                        .setProgress(100, 0, true);
                break;
            case PROGRESS_COMPLETED:
                builder.setOngoing(false).setContentTitle(getString(R.string.dfu_status_completed)).setSmallIcon(android.R.drawable.stat_sys_upload_done)
                        .setContentText(getString(R.string.dfu_status_completed_msg)).setAutoCancel(true).setColor(0xFF00B81A);
                break;
            case PROGRESS_ABORTED:
                builder.setOngoing(false).setContentTitle(getString(R.string.dfu_status_aborted)).setSmallIcon(android.R.drawable.stat_sys_upload_done)
                        .setContentText(getString(R.string.dfu_status_aborted_msg)).setAutoCancel(true);
                break;
            default:
                if (progress >= ERROR_MASK) {
                    // progress is an error number
                    builder.setOngoing(false).setContentTitle(getString(R.string.dfu_status_error)).setSmallIcon(android.R.drawable.stat_sys_upload_done)
                            .setContentText(getString(R.string.dfu_status_error_msg)).setAutoCancel(true).setColor(Color.RED);
                } else {
                    // progress is in percents
                    final String title = getString(R.string.dfu_status_uploading);
                    final String text = getString(R.string.dfu_status_uploading_msg, deviceName);
                    builder.setOngoing(true).setContentTitle(title).setContentText(text).setProgress(100, progress, false);
                }
        }

        // update the notification
        final Intent intent = new Intent(this, getNotificationTarget());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress);
        intent.putExtra(EXTRA_DEVICE_NAME, deviceName);
        intent.putExtra(EXTRA_PROGRESS, progress); // this may contains ERROR_CONNECTION_MASK bit!
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        // Add Abort action to the notification
        if (progress != PROGRESS_ABORTED && progress != PROGRESS_COMPLETED && progress < ERROR_MASK) {
            final Intent abortIntent = new Intent(BROADCAST_ACTION);
            abortIntent.putExtra(EXTRA_ACTION, ACTION_ABORT);
            final PendingIntent pendingAbortIntent = PendingIntent.getBroadcast(this, 1, abortIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.addAction(R.mipmap.ic_launcher, getString(R.string.dfu_action_abort), pendingAbortIntent);
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                NotificationChannel channel = new NotificationChannel("11111","ForegroundServiceChannel",
                        NotificationManager.IMPORTANCE_HIGH);
                channel.enableVibration(false);
                channel.enableLights(false);
                channel.setSound(null,null);
                manager.createNotificationChannel(channel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    public void sendProgressBroadcast(final int progress, int blockCounter, int numberOfBlocks) {
        final long now = SystemClock.elapsedRealtime();
        final float speed = now - mLastProgressTime != 0 ? (float) (mBytesSent - mLastBytesSent) / (float) (now - mLastProgressTime) : 0.0f;
        final float avgSpeed = now - mStartTime != 0 ? (float) mBytesSent / (float) (now - mStartTime) : 0.0f;
        mLastProgressTime = now;
        mLastBytesSent = mBytesSent;

        final Intent broadcast = new Intent(BROADCAST_PROGRESS);
        broadcast.putExtra(EXTRA_DATA, progress);
        broadcast.putExtra(EXTRA_DEVICE_ADDRESS, mDeviceAddress);
        broadcast.putExtra(EXTRA_PART_CURRENT, blockCounter);
        broadcast.putExtra(EXTRA_PARTS_TOTAL, numberOfBlocks);
        broadcast.putExtra(EXTRA_SPEED_B_PER_MS, speed);
        broadcast.putExtra(EXTRA_AVG_SPEED_B_PER_MS, avgSpeed);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    public void sendErrorBroadcast(final int error) {
        final Intent broadcast = new Intent(BROADCAST_ERROR);
        if ((error & ERROR_CONNECTION_MASK) > 0) {
            broadcast.putExtra(EXTRA_DATA, error & ~ERROR_CONNECTION_MASK);
            broadcast.putExtra(EXTRA_ERROR_TYPE, ERROR_TYPE_COMMUNICATION);
        } else if ((error & ERROR_CONNECTION_STATE_MASK) > 0) {
            broadcast.putExtra(EXTRA_DATA, error & ~ERROR_CONNECTION_STATE_MASK);
            broadcast.putExtra(EXTRA_ERROR_TYPE, ERROR_TYPE_COMMUNICATION_STATE);
        } else if ((error & ERROR_REMOTE_MASK) > 0) {
            broadcast.putExtra(EXTRA_DATA, error);
            broadcast.putExtra(EXTRA_ERROR_TYPE, ERROR_TYPE_DFU_REMOTE);
        } else {
            broadcast.putExtra(EXTRA_DATA, error);
            broadcast.putExtra(EXTRA_ERROR_TYPE, ERROR_TYPE_OTHER);
        }
        broadcast.putExtra(EXTRA_DEVICE_ADDRESS, mDeviceAddress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
        disconnect(BluetoothGattSingleton.getGatt());
    }

    protected Class<? extends Activity> getNotificationTarget() {
        /*
         * As a target activity the NotificationActivity is returned, not the MainActivity. This is because the notification must create a new task:
		 *
		 * intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 *
		 * when user press it. Using NotificationActivity we can check whether the new activity is a root activity (that means no other activity was open before)
		 * or that there is other activity already open. In the later case the notificationActivity will just be closed. System will restore the previous activity from
		 * this application - the MainActivity. However if nRF Beacon has been closed during upload and user click the notification a NotificationActivity will
		 * be launched as a root activity. It will create and start the MainActivity and finish itself.
		 *
		 * This method may be used to restore the target activity in case the application was closed or is open. It may also be used to recreate an activity history (see NotificationActivity).
		 */
        return NotificationActivity.class;
    }

    private void disconnect(final BluetoothGatt gatt) {
        if (gatt == null) {
            return;
        }
        KLog.d("Disconnecting...");
        updateProgressNotification(PROGRESS_DISCONNECTING, 0, 0);


        KLog.d("Disconnecting from the device...");
        KLog.d("gatt.disconnect()");
        gatt.disconnect();

        // We have to wait until device gets disconnected or an error occur
        KLog.d("Disconnected");

    }

    public void terminateConnection(final int error) {

        BluetoothGatt gatt = BluetoothGattSingleton.getGatt();
        if (gatt == null) return;
        disconnect(gatt);
        // Close the device
        refreshDeviceCache(gatt, false); // This should be set to true when DFU Version is 0.5 or lower
        close(gatt);
        updateProgressNotification(error, 0, 0);
//        BluetoothGattSingleton.setGatt(null);
    }

    private void refreshDeviceCache(final BluetoothGatt gatt, final boolean force) {
        /*
		 * If the device is bonded this is up to the Service Changed characteristic to notify Android that the services has changed.
		 * There is no need for this trick in that case.
		 * If not bonded, the Android should not keep the services cached when the Service Changed characteristic is present in the target device database.
		 * However, due to the Android bug (still exists in Android 5.0.1), it is keeping them anyway and the only way to clear services is by using this hidden refresh method.
		 */
        if (force || gatt.getDevice().getBondState() == BluetoothDevice.BOND_NONE) {
			/*
			 * There is a refresh() method in BluetoothGatt class but for now it's hidden. We will call it using reflections.
			 */
            try {
                final Method refresh = gatt.getClass().getMethod("refresh");
                if (refresh != null) {
                    final boolean success = (Boolean) refresh.invoke(gatt);
                    KLog.d("Refreshing result: " + success);
                }
            } catch (Exception e) {
                KLog.e("An exception occurred while refreshing device", e.toString());
            }
        }
    }

    private void close(final BluetoothGatt gatt) {
        KLog.d("gatt.close()");
        gatt.close();
    }

    public void startUp() {
        startUpdate();
    }
}
