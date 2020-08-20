package com.zeroner.bledemo.data;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.TB_BP_data;
import com.zeroner.bledemo.bean.sql.TB_blue_gps;
import com.zeroner.bledemo.bean.sql.ZG_BaseInfo;
import com.zeroner.bledemo.bean.zg.ZGFirmwareUpgrade;
import com.zeroner.bledemo.eventbus.Event;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.blemidautumn.Constants;
import com.zeroner.blemidautumn.alarm_clock.ZGAlarmClockScheduleHandler;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.model.BleSpeed;
import com.zeroner.blemidautumn.bluetooth.model.C100AgpsData;
import com.zeroner.blemidautumn.bluetooth.model.ControlMusicPhoto;
import com.zeroner.blemidautumn.bluetooth.model.DeviceSetting;
import com.zeroner.blemidautumn.bluetooth.model.DeviceTime;
import com.zeroner.blemidautumn.bluetooth.model.Result;
import com.zeroner.blemidautumn.bluetooth.model.SportModeUpdateInfo;
import com.zeroner.blemidautumn.bluetooth.model.WelcomeBloodData;
import com.zeroner.blemidautumn.bluetooth.model.ZGHardwareInfo;
import com.zeroner.blemidautumn.bluetooth.model.ZgAgpsData;
import com.zeroner.blemidautumn.bluetooth.model.ZgAgpsStatus;
import com.zeroner.blemidautumn.bluetooth.model.ZgBPDetailParse;
import com.zeroner.blemidautumn.bluetooth.model.ZgGpsDetailInfo;
import com.zeroner.blemidautumn.bluetooth.model.bh_totalinfo;
import com.zeroner.blemidautumn.heart.model.zg.SevenDayStore;
import com.zeroner.blemidautumn.heart.model.zg.ZGHeartData;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.output.detail_sport.model.ZgDetailSportData;
import com.zeroner.blemidautumn.output.detail_sport.model.ZgDetailWalkData;
import com.zeroner.blemidautumn.output.sleep.model.ZgSleepData;
import com.zeroner.blemidautumn.utils.JsonTool;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.zeroner.bledemo.data.ZGBaseUtils.Over;

/**
 * Iwown Protocl Data parse
 * Created by Daemon on 2017/10/30 14:28.
 */

public class ZGDataParsePresenter {
    public static final int Type = Constants.Bluetooth.Zeroner_Zg_Sdk;
    public static String path = "";
    public static Handler myHandler = new Handler(Looper.getMainLooper());
    private static ThreadPoolExecutor fixedThreadPool = new ThreadPoolExecutor(2, 2, 10, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());
    private static String deviceName;
    public static int status = -1; //0-更新AGPS 非0-停止

    /**
     * @param context
     * @param dataType
     * @param data
     */
    public static void parseProtocolData(Context context, int dataType, String data) {
        switch (dataType) {
            case (byte) 0x88:
                KLog.e("parseProtocolData SevenDayStore" + " > " + data);
                SevenDayStore bh_storeinfo_t = JsonUtils.fromJson(data, SevenDayStore.class);
                ZGSysncFilter.initSyncCondition(context, bh_storeinfo_t);

                break;

            case SuperBleSDK.TYPE89_Total_81:
                KLog.e("parseProtocolData bh_totalinfo" + " > " + data);
                bh_totalinfo info = JsonUtils.fromJson(data, bh_totalinfo.class);

                ZGSysncFilter.syncTodayData(context, info);
                Zg2IwownHandler.converter2Walking(context, dataType, info);

                break;

            case SuperBleSDK.TYPE89_Walk_01:
                KLog.e("parseProtocolData ZgDetailWalkData" + " > " + data);
                ZgDetailWalkData zgDetailWalkData = JsonUtils.fromJson(data, ZgDetailWalkData.class);
                Zg2IwownHandler.getWalkingSport(context, zgDetailWalkData);
                break;

            case SuperBleSDK.TYPE89_Sleep_03:
                KLog.e("parseProtocolData ZgSleepData" + " > " + data);
                ZgSleepData zgSleepData = JsonUtils.fromJson(data, ZgSleepData.class);
                Zg2IwownHandler.zgSleepToV3(zgSleepData);
                break;

            case SuperBleSDK.TYPE89_Sport_04:
                KLog.e("parseProtocolData ZgDetailSportData" + " > " + data);
                ZgDetailSportData zgDetailSportData = JsonUtils.fromJson(data, ZgDetailSportData.class);
                Zg2IwownHandler.converter2sport(context, dataType, zgDetailSportData);

                break;

            case SuperBleSDK.TYPE89_Heart_02:
                KLog.e("parseProtocolData ZGHeartData" + " > " + data);
                ZGHeartData zgHeartData = JsonUtils.fromJson(data, ZGHeartData.class);
                Zg2IwownHandler.converter2HourHeart(context, dataType, zgHeartData);

                break;

            case (byte) 0x84:
                KLog.e("parseProtocolData ZGAlarmClockSchedule" + " > " + data);
                ZGAlarmClockScheduleHandler.ResponseClock_ScheduleData responseClock_scheduleData = JsonUtils.fromJson(data,
                        ZGAlarmClockScheduleHandler.ResponseClock_ScheduleData.class);


                break;

            case (byte) 0x82:

                parse82(context, dataType, data);
                break;

            case (byte) 0x86:
                parse86(context, dataType, data);
                break;

            case (byte) 0x83:
                parse83(context, dataType, data);
                break;

            case (byte) 0x85:
                parse85(context, dataType, data);
                break;
            case (byte) 0x06:
                EventBus.getDefault().post(new ZGFirmwareUpgrade(0));
                break;

            case (byte) 0x87:
                Result result = JsonUtils.fromJson(data, Result.class);
                if (result.getResult_code() == 0) {
//                    context.sendBroadcast(new Intent(com.healthy.zeroner_pro.util.Constants.ACTION_PHONE_STATUE_OUT));
                }
                break;
            case (byte) 0x8A:
                ZGBaseUtils.postSyncDataEventZg(Over, 0, 0, 0);
                break;
            case (byte) 0x0E:
                parse0E(context, data);
                break;
            case (byte) 0x0F:
                parse0F(data);
                break;
            case (byte) 0x8c:
                parse8C(data);
//                parse10(context,data);
                break;
            case (byte) 0x8E:
                ZgAgpsData zgAgpsData = JsonTool.fromJson(data, ZgAgpsData.class);
                deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
                if(deviceName.toUpperCase().contains("CR100")){
                    if(zgAgpsData.getType()==-2) {
                        if (zgAgpsData.getCode() == 0) {
                            //失败或者未更新过agps,写入agps
                            //开启Agps
//                            C100AgpsData c100AgpsData = new C100AgpsData();
//                            c100AgpsData.setIsStatus(0);
//                            c100AgpsData.setNum(0);
//                            EventBus.getDefault().post(c100AgpsData);
//                            C100AGPSPresenter.getInstance().openApgs();
                        } else {
                            C100AgpsData c100AgpsData = new C100AgpsData();
                            c100AgpsData.setIsStatus(3);
                            c100AgpsData.setCode(1);
                            EventBus.getDefault().post(c100AgpsData);
                        }
                        return;
                    }else{
                        C100AgpsData c100AgpsData = new C100AgpsData();
                        c100AgpsData.setIsStatus(3);
                        c100AgpsData.setCode(1);
                        EventBus.getDefault().post(c100AgpsData);
                        return;
                    }
                }
                com.socks.library.KLog.d("no2855--> 接受校验agps返回的数据: "+JsonUtils.toJson(zgAgpsData));

                if(zgAgpsData.getType()==-2){
                    if(zgAgpsData.getCode()==0){
                        //失败或者未更新过agps,写入agps
                        ZGBaseUtils.startAgps();
                    } else{
                        status = -1;
                        ZGBaseUtils.endAgps();
                        ZgAgpsStatus status = new ZgAgpsStatus();
                        status.setState(1);
                        EventBus.getDefault().post(status);
                    }
                }
                break;
            case (byte) 0x8b:
                try {
                    JsonTool.fromJson(data, SportModeUpdateInfo.class);
                }catch (Exception e){
                    parse8b(data);
                }

                break;
            case (byte) 0x8D:
                parse8D(data);
                break;
            case (byte) 0x90:
                parse90(context,data);
                break;
            default:
                break;
        }
    }


    private static void parse86(Context context, int dataType, String data) {
        ZGHardwareInfo zgHardwareInfo = JsonUtils.fromJson(data, ZGHardwareInfo.class);
        PrefUtil.save(context,BaseActionUtils.Action_device_version,zgHardwareInfo.getDev_version_s());
        PrefUtil.save(context,BaseActionUtils.Action_device_Model,zgHardwareInfo.getModel());
    }

    private static void parse83(Context context, int dataType, String data) {
        BleSpeed bleSpeed = JsonUtils.fromJson(data, BleSpeed.class);

        updateZGBaseInfo(ZG_BaseInfo.key_bleSpeed, JsonUtils.toJson(bleSpeed));

    }

    private static void parse85(Context context, int dataType, String data) {
        DeviceSetting deviceSetting = JsonTool.fromJson(data, DeviceSetting.class);
        updateZGBaseInfo(ZG_BaseInfo.key_deviceset, JsonUtils.toJson(deviceSetting));

        PrefUtil.save(BleApplication.getInstance(), BaseActionUtils.Action_device_Battery, String.valueOf(deviceSetting.getBatteryVolume()));
        HashMap<String, Object> dataMap = new HashMap<>();
        dataMap.put(Event.Ble_Connect_Statue, true);
        EventBus.getDefault().post(new Event(Event.Ble_Connect_Statue, dataMap));
    }


    /**
     * DeviceTime 解析
     *
     * @param context
     * @param dataType
     * @param data
     */
    private static void parse82(Context context, int dataType, String data) {
        DeviceTime deviceTime = JsonUtils.fromJson(data, DeviceTime.class);
        updateZGBaseInfo(ZG_BaseInfo.key_devicetime, JsonUtils.toJson(deviceTime));

    }

    public static void updateZGBaseInfo(String key_devicetime, String content) {
        if (TextUtils.isEmpty(PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME))) {
            return;
        }

        ZG_BaseInfo baseInfoByKey = SqlBizUtils.getBaseInfoByKey(key_devicetime,
                PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME));

        if (baseInfoByKey == null) {
            baseInfoByKey = new ZG_BaseInfo();
            baseInfoByKey.setData_form(PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME));
            baseInfoByKey.setKey(key_devicetime);
        }
        baseInfoByKey.setContent(content);
        baseInfoByKey.save();
    }


    public static ZG_BaseInfo getZGBaseInfoByKey(String key_devicetime) {
        return SqlBizUtils.getBaseInfoByKey(key_devicetime,
                PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME));
    }


    private static void parse0E(Context context, String data) {



        ZgAgpsData zgAgpsData = JsonUtils.fromJson(data, ZgAgpsData.class);
        com.socks.library.KLog.d("no2855--> 接受返回的数据: " + JsonUtils.toJson(zgAgpsData));
        if (zgAgpsData.getType() == 0) {
            if (zgAgpsData.getCode() == 1) {
                if(status == 1){
                    status = 0;
                    ZGBaseUtils.startAgps();
                    return;
                }
                if(status == 0) {
                    ZGBaseUtils.writeAgps(context, path);
                }
            } else if (zgAgpsData.getCode() == 5) {
                //开启失败，重开
//                status = 1;
//                ZGBaseUtils.endAgps();
            }
        }
//        else {
//                ZGDataParsePresenter.isStart = false;
//                //开启失败，重开
//                byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).agpsOperation(1);
//                BackgroundThreadManager.getInstance().addWriteData(context, bytes);
//                byte[] bytes1 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).agpsOperation(0);
//                BackgroundThreadManager.getInstance().addWriteData(context, bytes1);
//            }
//        }
        else {
            if (zgAgpsData.getCode() == 2) {
                if (0x80 < zgAgpsData.getType() && zgAgpsData.getType() <= 0x88) {
                    if (zgAgpsData.getType() < 0x88) {
                        //待定
                        com.socks.library.KLog.e("no2855 全部文件已结束");
                    } else {
                        if (zgAgpsData.getWriteCode() == 0) {
                            ZGBaseUtils.initAgpsData(true);
                            ZGBaseUtils.writeAgps2048(context);
                        } else if (zgAgpsData.getWriteCode() == 2) {
                            ZGBaseUtils.initAgpsData(false);
                            ZGBaseUtils.writeAgps2048(context);
                        }
                    }
                    com.socks.library.KLog.e("no2855 2048个包发送结束: 11111");
                    return;
                }
                if (zgAgpsData.getType() > 0) {
                    if (zgAgpsData.getWriteCode() == 2) {
                        ZGBaseUtils.writeAgps256Next(context, false);
                    } else {
                        ZGBaseUtils.writeAgps256Next(context, true);
                    }

                } else {

                }
                com.socks.library.KLog.e("no2855 2048个包发送结束: 计算结果: " + 0x80 + " == " + 0x88 + " == " + zgAgpsData.getType());

            }

        }
    }

    private static void parse0F(String data){
        C100AgpsData c100AgpsData = JsonUtils.fromJson(data, C100AgpsData.class);
        EventBus.getDefault().post(c100AgpsData);
    }

    private static void parse8b(String data) {
        List<ZgBPDetailParse.BPData> bpData = JsonUtils.getListJson(data, ZgBPDetailParse.BPData.class);
        if (bpData != null && bpData.size() > 0) {
            String dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
            for (ZgBPDetailParse.BPData bp : bpData) {
                TB_BP_data tbp;
                DateUtil d = new DateUtil(bp.getYear(), bp.getMonth(), bp.getDay(), bp.getHour(), bp.getMinute());
                tbp = DataSupport.where("dataFrom=? and bpTime=?", dataFrom, d.getUnixTimestamp() + "").findFirst(TB_BP_data.class);
                if (tbp == null) {
                    tbp = new TB_BP_data();
                }
                tbp.setDataFrom(dataFrom);
                tbp.setDbp(bp.getDbp());
                tbp.setSbp(bp.getSbp());
                tbp.setBpTime(d.getUnixTimestamp());
                tbp.saveOrUpdate("dataFrom=? and bpTime=?", dataFrom, d.getUnixTimestamp() + "");
            }
        }
    }

    private static void parse8D(String data) {
        String deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
        ZG_BaseInfo baseInfo = DataSupport.where("key=? and data_form=?", ZG_BaseInfo.key_welcome_blood, deviceName + "").findFirst(ZG_BaseInfo.class);
        WelcomeBloodData bloodData = JsonTool.fromJson(data, WelcomeBloodData.class);
        if (bloodData == null) {
            bloodData = new WelcomeBloodData();
        }
        if (baseInfo != null) {
            if (data != null && !data.equals(baseInfo.getContent()) && bloodData.getHeight() > 0) {
                baseInfo.setContent(JsonTool.toJson(bloodData));
                baseInfo.updateAll("key=? and data_form=?", ZG_BaseInfo.key_welcome_blood, deviceName + "");
            }
        } else {
            if (TextUtils.isEmpty(bloodData.getWelcome())) {
                bloodData.setWelcome("three");
            }

            ZGBaseUtils.setWelcomePageContent(bloodData.getWelcome(), bloodData.getTimeZone(), bloodData.getHeight(), bloodData.getGender());
        }
    }

    private static void parse90(Context context,final String data){
        ControlMusicPhoto controlMusicPhoto = JsonTool.fromJson(data, ControlMusicPhoto.class);
        //通过app控制音乐播放器(Control music player through app)
        if(controlMusicPhoto.getKeyType() == ControlMusicPhoto.KEY_NEXT){
            //设备点击了下一首(The device clicked on the next song)
            // your code

        }else if(controlMusicPhoto.getKeyType() == ControlMusicPhoto.KEY_PREVIOUS){
            //设备点击了上一首(The device clicked on the previous song)
            // your code

        }else if(controlMusicPhoto.getKeyType() == ControlMusicPhoto.KEY_PLAY_PAUSE){
            //设备点击了暂停播放(The device clicks pause)
            // your code

        }else if(controlMusicPhoto.getKeyType() == ControlMusicPhoto.KEY_PHOTO_PLAY){
            //设备点击了拍照(The device clicked to take a picture)
            //your code
        }
    }

    private static void parse8C(final String data) {
        int code = 0;
        try {
            JSONObject jb = new JSONObject(data);
            code = jb.optInt("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (code == 1) {
            com.socks.library.KLog.e("no2855->8C数据:  " + data);
            deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
            ZGSysncFilter.setGpsTotalDay(data);
        } else if (code == 2) {
            myHandler.removeCallbacks(gpsTimeout);
            myHandler.postDelayed(gpsTimeout, 4000);
//            KLog.e("no2855->具体数据: "+data);
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    ZgGpsDetailInfo detailData = JsonTool.fromJson(data, ZgGpsDetailInfo.class);
                    if (TextUtils.isEmpty(deviceName)) {
                        deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
                    }
                    if (detailData != null && detailData.getZgGpsDetailLists() != null) {
                        int mYear = detailData.getGPS_utc_year();
                        int mMonth = detailData.getGPS_utc_month();
                        int mDay = detailData.getGPS_utc_day();
                        for (ZgGpsDetailInfo.ZgGpsDetailList detailData1 : detailData.getZgGpsDetailLists()) {
                            TB_blue_gps zg_gps = new TB_blue_gps();
                            DateUtil dateUtil = new DateUtil(detailData.getGPS_utc_year(), detailData.getGPS_utc_month(), detailData.getGPS_utc_day(), detailData1.getGPS_utc_hour(), detailData1.getGPS_utc_minute(), detailData1.getGPS_utc_second());
                            zg_gps.setData_from(deviceName);
                            zg_gps.setTime(dateUtil.getUnixTimestamp());
                            zg_gps.setLat(detailData1.getGPS_Latitude());
                            zg_gps.setLon(detailData1.getGPS_Longitude());
                            zg_gps.saveOrUpdate("data_from=? and time=?", deviceName, zg_gps.getTime() + "");
                        }
                        ZGBaseUtils.postSyncDataEventZg(ZGBaseUtils.Sport_Gps, detailData.getGPS_utc_year(), detailData.getGPS_utc_month(), detailData.getGPS_utc_day());
                    }

                }
            });
        }
    }

    /**
     * 防止gps未收到结尾标志导致没有计算gps
     */
    public static Runnable gpsTimeout = new Runnable() {
        @Override
        public void run() {
            if (TextUtils.isEmpty(deviceName)) {
                deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
            }
        }
    };
}
