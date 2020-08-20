package no.nordicsemi.android.dialog;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;

import com.socks.library.KLog;

import java.math.BigInteger;
import no.nordicsemi.android.dialog.async.DeviceConnectTask;
import no.nordicsemi.android.dialog.data.Statics;

/**
 * Created by wouter on 9-10-14.
 */
public class Callback extends BluetoothGattCallback {
    public static String TAG = "Callback";
    DeviceConnectTask task;

    private BluetoothManager mBluetoothManager;
    private Context mContext;


    public Callback(Context context,BluetoothManager bluetoothManager,DeviceConnectTask task) {
        mContext=context;
        mBluetoothManager=bluetoothManager;
        this.task = task;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                        int newState) {
        KLog.i(TAG, "le onConnectionStateChange [" + newState + "]");
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            KLog.i(TAG, "le device connected");
            gatt.discoverServices();

            /*LogUtil.i(TAG, "onServicesDiscovered");
			BluetoothGattSingleton.setGatt(gatt);
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("step", 0);
            task.context.sendBroadcast(intent);*/

        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            KLog.i(TAG, "le device disconnected");

        }
        Intent intent = new Intent();
        intent.setAction(Statics.CONNECTION_STATE_UPDATE);
        intent.putExtra("state", newState);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        KLog.i(TAG, "onServicesDiscovered");
        BluetoothGattSingleton.setGatt(gatt);
        Intent intent = new Intent();
        intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
        intent.putExtra("step", 0);
        mContext.sendBroadcast(intent);
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        boolean sendUpdate = true;
        int index = -1;
        int step = -1;

        if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MANUFACTURER_NAME_STRING)) {
            index = 0;
        } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_MODEL_NUMBER_STRING)) {
            index = 1;
        } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_FIRMWARE_REVISION_STRING)) {
            index = 2;
        } else if (characteristic.getUuid().equals(Statics.ORG_BLUETOOTH_CHARACTERISTIC_SOFTWARE_REVISION_STRING)) {
            index = 3;
        }
        // SPOTA
        else if (characteristic.getUuid().equals(Statics.SPOTA_MEM_INFO_UUID)) {
//			int memInfoValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
//			LogUtil.d("mem info", memInfoValue + "");
//			DeviceActivity.getInstance().logMemInfoValue(memInfoValue);
            step = 5;
        } else {
            sendUpdate = false;
        }

        if (sendUpdate) {
            KLog.d(TAG, "onCharacteristicRead: " + index);
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            if (index >= 0) {
                intent.putExtra("characteristic", index);
                intent.putExtra("value", new String(characteristic.getValue()));
            } else {
                intent.putExtra("step", step);
                intent.putExtra("value", characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0));
            }
            mContext.sendBroadcast(intent);
        }

        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//        LogUtil.d_no(TAG, "onCharacteristicWrite: " + characteristic.getUuid().toString());

        if (status == BluetoothGatt.GATT_SUCCESS) {
//            LogUtil.d_no(TAG, "write succeeded");
            int step = -1;
            // Step 3 callback: write SPOTA_GPIO_MAP_UUID value
            if (characteristic.getUuid().equals(Statics.SPOTA_GPIO_MAP_UUID)) {
                step = 4;
            }
            // Step 4 callback: set the patch length, default 240
            else if (characteristic.getUuid().equals(Statics.SPOTA_PATCH_LEN_UUID)) {
                step =mBluetoothManager.type == SuotaManager.TYPE ? 5 : 7;
            } else if (characteristic.getUuid().equals(Statics.SPOTA_MEM_DEV_UUID)) {
            }
            else if (characteristic.getUuid().equals(Statics.SPOTA_PATCH_DATA_UUID)
                    //&& DeviceActivity.getInstance().bluetoothManager.type == SuotaManager.TYPE
                    && mBluetoothManager.chunkCounter != -1
                    ) {
                //step = DeviceActivity.getInstance().bluetoothManager.type == SuotaManager.TYPE ? 5 : 7;
                /*DeviceActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DeviceActivity.getInstance().bluetoothManager.sendBlock();
                    }
                });*/
//                LogUtil.d_no(TAG, "Next block in chunk " + mBluetoothManager.chunkCounter);
                mBluetoothManager.sendBlock();
            }

            if (step > 0) {
                Intent intent = new Intent();
                intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
                intent.putExtra("step", step);
                mContext.sendBroadcast(intent);
            }
        } else {
            com.zeroner.blemidautumn.library.KLog.file("write failed: " + status);
        }
        super.onCharacteristicWrite(gatt, characteristic, status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        KLog.d(TAG, "onDescriptorWrite");
        if (descriptor.getCharacteristic().getUuid().equals(Statics.SPOTA_SERV_STATUS_UUID)) {
            int step = 2;

            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("step", step);
            mContext.sendBroadcast(intent);
        }
        task.publishProgess(gatt);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        int value = new BigInteger(characteristic.getValue()).intValue();
        String stringValue = String.format("%#10x", value);
        KLog.d("changed :", stringValue);

        int step = -1;
        int error = -1;
        int memDevValue = -1;
        // Set memtype callback
        if (stringValue.trim().equals("0x10")) {
            step = 3;
        }
        // Successfully sent a block, send the next one
        else if (stringValue.trim().equals("0x2")) {
            step = mBluetoothManager.type == SuotaManager.TYPE ? 5 : 8;
        } else if (stringValue.trim().equals("0x3") || stringValue.trim().equals("0x1")) {
            memDevValue = value;
        } else {
            error = Integer.parseInt(stringValue.trim().replace("0x", ""));
        }
        if (step >= 0 || error >= 0 || memDevValue >= 0) {
            Intent intent = new Intent();
            intent.setAction(Statics.BLUETOOTH_GATT_UPDATE);
            intent.putExtra("step", step);
            intent.putExtra("error", error);
            intent.putExtra("memDevValue", memDevValue);
            mContext.sendBroadcast(intent);
        }

    }
}