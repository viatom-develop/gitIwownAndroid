package com.zeroner.bledemo.utils;

import android.content.IntentFilter;

import com.zeroner.bledemo.BuildConfig;

import java.util.UUID;

/**
 * 作者：hzy on 2017/12/20 14:53
 * <p>
 * 邮箱：hezhiyuan@zeroner.com
 */

public class BaseActionUtils {

    public static class FilePath{
        //61数据ble日志
        public static final String Mtk_Ble_61_Data_Log_Dir="/Zeroner/sdk/blelog/61_data/";
        //61数据睡眠
        public static final String Mtk_Ble_61_Sleep_Dir="/Zeroner/sdk/sleep/";
        //62数据ble日志
        public static final String Mtk_Ble_62_Data_Log_Dir = "/Zeroner/sdk/blelog/62_data/";

        public static final String ProtoBuf_Ble_Data_Log_Dir = "/Zeroner/sdk/sleep/";
        public static final String ProtoBuf_Ble_80_Sleep_Dir = "/Zeroner/sdk/protobuf/sleep/";

    }



    public static boolean isBackground;

    /**
     * nordic firmware upgrade UUID
     */

    public static final UUID NODIC_UPDATE_SERVICE = UUID.fromString("00001530-0000-1000-8000-00805f9b34fb");
    public static final UUID UPDATE_SERVICE_MAIN_DFU = UUID.fromString("0000fe59-0000-1000-8000-00805f9b34fb"); // 新协议手环主GATT服务

    /**
     * bluetooth action
     */
    public static final String ON_SCAN_RESULT = "com.zeroner.app.ON_SCAN_RESULT";
    public static final String ON_DATA_ARRIVED = "com.zeroner.app.ON_DATA_ARRIVED";
    public static final String ON_BLUETOOTH_INIT = "com.zeroner.app.ON_BLUETOOTH_INIT";
    public static final String ON_CONNECT_STATUE = "com.zeroner.app.ON_CONNECT_STATUE";
    public static final String ON_DISCOVER_SERVICE = "com.zeroner.app.ON_DISCOVER_SERVICE";
    public static final String ON_DISCOVER_CHARACTER = "com.zeroner.app.ON_DISCOVER_CHARACTER";
    public static final String ON_COMMON_SEND = "com.zeroner.app.ON_COMMON_SEND";
    public static final String ON_COMMON_RECEIVER = "com.zeroner.app.ON_COMMON_RECEIVER";
    public static final String ON_CHARACTERISTIC_CHANGE = "com.zeroner.app.ON_CHARACTERISTIC_CHANGE";
    public static final String ON_BLUETOOTH_ERROR = "com.zeroner.app.ON_BLUETOOTH_ERROR";
    public static final String BLE_SDK_TYPE = "com.zeroner.app.BLE_SDK_TYPE";
    public static final String BLE_DATA_TYPE = "com.zeroner.app.BLE_DATA_TYPE";
    public static final String BLE_ARRIVED_DATA = "com.zeroner.app.BLE_ARRIVED_DATA";
    public static final String BLE_SCAN_RESULT_DEVICE = "com.zeroner.app.BLE_SCAN_RESULT_DEVICE";
    public static final String BLE_CONNECT_STATUE = "com.zeroner.app.BLE_CONNECT_STAUE";
    public static final String BLE_SERVICE_UUID = "com.zeroner.app.BLE_SERVICE_UUID";
    public static final String BLE_CHARACTER_UUID = "com.zeroner.app.CHARACTER_UUID";
    public static final String BLE_COMMON_SEND = "com.zeroner.app.BLE_COMMON_SEND";
    public static final String BLE_BLUETOOTH_ADDRESS = "com.zeroner.app.BLE_BLUETOOTH_ADDRESS";
    public static final String BLE_PRE_CONNECT= "com.zeroner.app.BLE_PRE_CONEECT";
    public static final String BLE_NO_CALLBACK= "com.zeroner.app.BLE_NO_CALLBACK";
    public final static String ACTION_CONNECT_TIMEOUT = "com.zeroner.app.ACTION_CONNECT_TIMEOUT";
    public final static String Action_Phone_Statue_Out = "com.kunekt.healthy.ACTION_PHONE_STATUE_OUT";


    /**
     * bluetooth intentFilter
     * @return
     */
    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ON_SCAN_RESULT);
        intentFilter.addAction(ON_DATA_ARRIVED);
        intentFilter.addAction(ON_BLUETOOTH_INIT);
        intentFilter.addAction(ON_CONNECT_STATUE);
        intentFilter.addAction(ON_DISCOVER_SERVICE);
        intentFilter.addAction(ON_DISCOVER_CHARACTER);
        intentFilter.addAction(ON_COMMON_SEND);
        intentFilter.addAction(ON_CHARACTERISTIC_CHANGE);
        intentFilter.addAction(ON_BLUETOOTH_ERROR);
        intentFilter.addAction(BLE_DATA_TYPE);
        intentFilter.addAction(BLE_ARRIVED_DATA);
        intentFilter.addAction(BLE_SCAN_RESULT_DEVICE);
        intentFilter.addAction(BLE_CONNECT_STATUE);
        intentFilter.addAction(BLE_SERVICE_UUID);
        intentFilter.addAction(BLE_CHARACTER_UUID);
        intentFilter.addAction(BLE_COMMON_SEND);
        intentFilter.addAction(BLE_SDK_TYPE);
        intentFilter.addAction(BLE_BLUETOOTH_ADDRESS);
        intentFilter.addAction(BLE_PRE_CONNECT);
        intentFilter.addAction(BLE_NO_CALLBACK);
        intentFilter.addAction(ON_COMMON_RECEIVER);
        return intentFilter;
    }

    //action device
    public static final String APP_SDK_UPDATE_Content="com.zeroner.app.APP_SDK_UPDATE_Content";
    public final static String ACTION_DEVICE_NAME = "com.zeroner.app.ACTION_DEVICE_NAME";
    public final static String ACTION_DEVICE_ADDRESS = "com.zeroner.app.ACTION_DEVICE_ADDRESS";
    public final static String Action_device_Battery = "com.zeroner.app.ACTION_DEVICE_Battery";
    public final static String Action_device_Model = "com.zeroner.app.Action_device_Model";
    public final static String Action_device_FirmwareInfo = "com.zeroner.app.Action_device_FirmwareInfo";
    public final static String Action_device_Settings = "com.zeroner.app.Action_device_Settings";

    public final static String Action_device_version = "com.zeroner.app.Action_device_version";


    //action settings
    public final static String Action_Setting_Shake = "com.zeroner.app.Action_Setting_Shake";
    public final static String Action_Setting_Time_Format = "com.zeroner.app.Action_Setting_Time_Format";
    public final static String Action_Setting_Date_Format = "com.zeroner.app.Action_Setting_Date_Format";
    public final static String Action_Setting_Unit = "com.zeroner.app.Action_Setting_Unit";
    public final static String Action_Setting_Weather_Unit = "com.zeroner.app.Action_Setting_Weather_Unit";
    public final static String Action_Setting_Roll = "com.zeroner.app.Action_Setting_Roll";
    public final static String Action_Setting_Roll_Time = "com.zeroner.app.Action_Setting_Roll_Time";
    public final static String Action_Setting_Hand = "com.zeroner.app.Action_Setting_Hand";
    public final static String Action_Setting_Language = "com.zeroner.app.Action_Setting_Language";

    //action data
    public final static String Action_Data_Sleep = "com.zeroner.app.Action_Data_Sleep";
    public final static String Action_Last_Sync_Data_Time = "com.zeroner.app.Action_Last_Sync_Data_Time";

    //
    public final static String HAS_SELECT_SDK_FIRST = "com.zeroner.app.HAS_SELECT_SDK_FIRST";


    public final static String PROTOBUF_MTU_INFO = "com.zeroner.app.PROTOBUF_MTU_INFO";




}
