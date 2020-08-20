package com.zeroner.bledemo.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.TB_Alarmstatue;
import com.zeroner.bledemo.bean.sql.TB_schedulestatue;
import com.zeroner.bledemo.bean.sql.ZG_BaseInfo;
import com.zeroner.bledemo.data.sync.SyncData;
import com.zeroner.bledemo.eventbus.SyncDataEvent;
import com.zeroner.bledemo.setting.schedule.ScheduleUtil;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.ExecutorUtils;
import com.zeroner.bledemo.utils.FileIOUtils;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.Constants;
import com.zeroner.blemidautumn.alarm_clock.ZGAlarmClockScheduleHandler;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.model.WelcomeBloodData;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;
import com.zeroner.blemidautumn.utils.ByteUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daemon on 2017/11/6 11:16.
 */

public class ZGBaseUtils {

    public static int Heart = 100;
    public static int Sport = 200;
    public static int Sleep = 300;
    public static int Walking = 400;
    public static int Walking_2_Sport = 600;
    public static int Over = 700;
    public static int Sport_Gps = 800;
    public static int agpsCount = 0;
    public static int count2048 = 0;
    private static byte[] allAgps;

    private static Map<String, HashSet<String>> has_update_datas = new HashMap<>();

    public static Set<String> getHashUpdatDateSets() {
        return has_update_datas.get(PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_NAME));
    }

    private static final String TAG = ZGBaseUtils.class.getName();

    public static int progress_date = 0;

    private static Handler mHandler = new Handler(Looper.getMainLooper());
    public static int alarm_mode1 = -1;
    public static int alarm_number1 = -1;

    public static void postSyncDataEventZg(int data_type, int year, int month, int day) {
        KLog.e("postSyncDataEventZg " + data_type + " " + year + " " + month + " " + day);
        if (year == 0 && month == 0 && day == 0) {
            mHandler.removeCallbacksAndMessages(null);
            setProgress_date(0);

            try {
                PrefUtil.save(BleApplication.getInstance(), BaseActionUtils.Action_Last_Sync_Data_Time, System.currentTimeMillis());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (data_type == Over) {
                //If today's synchronization is over to record today's time after each update will only be greater than or equal to today's
                ZGDataParsePresenter.updateZGBaseInfo(ZG_BaseInfo.key_data_last_day, new DateUtil().getY_M_D());
                has_update_datas.clear();
            }

            if (data_type == 0) {
                //timeout
                EventBus.getDefault().post(new SyncDataEvent(-1, true));
            } else {
                EventBus.getDefault().post(new SyncDataEvent(0, true));
            }

            return;
        }


        DateUtil dateUtil = new DateUtil(year, month, day);

        if (data_type == Walking_2_Sport) {
            //The last record of the daily data has been updated when disconnected again after the update
            // Determine if synchronization is required
            HashSet<String> strings = has_update_datas.get(PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_NAME));
            if (strings == null) {
                strings = new HashSet<>();
                has_update_datas.put(PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_NAME), strings);
            }
            strings.add(dateUtil.getY_M_D());
            EventBus.getDefault().post(new SyncDataEvent((int) dateUtil.getUnixTimestamp(), false));
            KLog.e(dateUtil.getUnixTimestamp() + "----sync ok ===" + dateUtil.getY_M_D() + has_update_datas);
        }else if(data_type == Sport_Gps){
            EventBus.getDefault().post(new SyncDataEvent((int) dateUtil.getUnixTimestamp(),false));
        }

        long zeroTime = dateUtil.getZeroTime();
        if (progress_date == zeroTime) {
            KLog.d("progress_date same no post progress");
        } else {
            setProgress_date((int) zeroTime);
            EventBus.getDefault().post(new SyncDataEvent((int) dateUtil.getUnixTimestamp(), false));
        }

        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                KLog.e("38S not updated as synchronization completed");
                postSyncDataEventZg(0, 0, 0, 0);
            }
        }, 38000);

    }

    private static void setProgress_date(int progress_date) {
        ZGBaseUtils.progress_date = progress_date;
    }

    /**
     * Clear the extra alarm clock and schedule
     */
    public static void clearExtraAlarmSchedule() {
        List<TB_Alarmstatue> alarmList = getAlarms();
        if (alarmList.size() > 4) {
            for (int i = 4; i < alarmList.size(); i++) {
                TB_Alarmstatue tb_alarmstatue = alarmList.get(i);
                tb_alarmstatue.delete();
            }
        }

        List<TB_schedulestatue> schedules = getSchedules();

        if (schedules.size() > 4) {
            for (int i = 4; i < schedules.size(); i++) {
                schedules.get(i).delete();
            }
        }

        KLog.e("clearExtraAlarmSchedule ok ");
    }

    public static List<TB_Alarmstatue> getAlarms() {
        List<TB_Alarmstatue> alarmList = DataSupport.where("device_name=?", PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_NAME) + "").order("Ac_Idx asc").find(TB_Alarmstatue.class);
        return alarmList;
    }


    public static List<TB_schedulestatue> getSchedules() {
        List<TB_schedulestatue> schedulestatues =
                DataSupport.where("device_name=?", PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_NAME)).find(TB_schedulestatue.class);
        return schedulestatues;
    }

    public static int getSchedulesCount() {

        return DataSupport.where("device_name=?", PrefUtil.getString(BleApplication.getContext(), BaseActionUtils.ACTION_DEVICE_NAME)).count(TB_schedulestatue.class);

    }

    /**
     * Convert to color screen bracelet  by iwown weekRepeat Required
     *
     * @param iwownWeekRepeat
     */
    public static String getWeekRepeatByIwown(byte iwownWeekRepeat) {

        int[] weeks = new int[8];
        //00000000  -> Is valid Saturday Friday> Monday> Sunday high -> low
        if (iwownWeekRepeat == 0) {
            //Only once
            return "10000000";
        }
        weeks[0] = 1;
        updateWeeksValue(weeks, iwownWeekRepeat, ScheduleUtil.WEEK_6, 1);
        updateWeeksValue(weeks, iwownWeekRepeat, ScheduleUtil.WEEK_5, 2);
        updateWeeksValue(weeks, iwownWeekRepeat, ScheduleUtil.WEEK_4, 3);
        updateWeeksValue(weeks, iwownWeekRepeat, ScheduleUtil.WEEK_3, 4);
        updateWeeksValue(weeks, iwownWeekRepeat, ScheduleUtil.WEEK_2, 5);
        updateWeeksValue(weeks, iwownWeekRepeat, ScheduleUtil.WEEK_1, 6);
        updateWeeksValue(weeks, iwownWeekRepeat, ScheduleUtil.WEEK_7, 7);

        KLog.e("getWeekRepeatByIwown " + Arrays.toString(weeks));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < weeks.length; i++) {
            sb.append(weeks[i]);
        }
        return sb.toString();
    }

    private static void updateWeeksValue(int[] weeks, byte iwownWeekRepeat, byte weekValue, int index) {
        if ((iwownWeekRepeat & weekValue) > 0) {
            weeks[index] = 1;
        }
    }

    /**
     * Set alarm and schedule
     *
     * @param mContext
     */
    public static void updateAlarmAndSchedule(Context mContext) {


        List<ZGAlarmClockScheduleHandler.ZGAlarmClockBean> alarms = new ArrayList<>();


        //Find out all
        List<TB_Alarmstatue> alarms1 = getAlarms();

        for (TB_Alarmstatue tb_alarmstatue : alarms1) {
            ZGAlarmClockScheduleHandler.ZGAlarmClockBean zgAlarmClockBean = new ZGAlarmClockScheduleHandler.ZGAlarmClockBean();
            zgAlarmClockBean.alarmHour = tb_alarmstatue.getAc_Hour();
            zgAlarmClockBean.alarmMinute = tb_alarmstatue.getAc_Minute();

            zgAlarmClockBean.alarmRingSetting = ZGAlarmClockScheduleHandler.getMode(tb_alarmstatue.getZg_mode(), tb_alarmstatue.getZg_number());
            KLog.e("alarm  " + tb_alarmstatue);
            String weekRepeatByIwown1 = "00000000";

            if (tb_alarmstatue.getOpenState() != 0) {
                int conf = tb_alarmstatue.getAc_Conf();
                weekRepeatByIwown1 = ZGBaseUtils.getWeekRepeatByIwown((byte) conf);
            }

//       KLog.e("updateAlarmAndSchedule " + tb_alarmstatue.getAc_Idx() + "  " + weekRepeatByIwown1);

            zgAlarmClockBean.alarmSet = Integer.parseInt(weekRepeatByIwown1, 2);
            KLog.e("weekRepeatByIwown1 " + weekRepeatByIwown1 + " " + com.zeroner.blemidautumn.utils.ByteUtil.bytesToString1(new byte[]{(byte) zgAlarmClockBean.alarmSet}));
            zgAlarmClockBean.text = tb_alarmstatue.getAc_String();
            alarms.add(zgAlarmClockBean);
        }
        KLog.e("alram1s  " + alarms);

//        ZGAlarmClockScheduleHandler.ZGAlarmClockBean zgAlarmClockBean = new ZGAlarmClockScheduleHandler.ZGAlarmClockBean();
//        zgAlarmClockBean.alarmHour = hour;
//        zgAlarmClockBean.alarmMinute = minute;
//        zgAlarmClockBean.alarmSet = Integer.parseInt(weekRepeatByIwown, 2);
//        zgAlarmClockBean.text = text;
//
//        alarms.set(id, zgAlarmClockBean);

        List<TB_schedulestatue> schedules = ZGBaseUtils.getSchedules();
        List<ZGAlarmClockScheduleHandler.ZGSchedule> scheduls = new ArrayList<>();

        scheduls.add(new ZGAlarmClockScheduleHandler.ZGSchedule());
        scheduls.add(new ZGAlarmClockScheduleHandler.ZGSchedule());
        scheduls.add(new ZGAlarmClockScheduleHandler.ZGSchedule());
        scheduls.add(new ZGAlarmClockScheduleHandler.ZGSchedule());

        int index = 0;
        for (int i = 0; i < schedules.size(); i++) {
            TB_schedulestatue tb_schedulestatue = schedules.get(i);
            ZGAlarmClockScheduleHandler.ZGSchedule zgSchedule = new ZGAlarmClockScheduleHandler.ZGSchedule();
            zgSchedule.scheduler_action = 1;
            zgSchedule.scheringSetting = ZGAlarmClockScheduleHandler.getMode(tb_schedulestatue.getZg_mode(), tb_schedulestatue.getZg_number());
            zgSchedule.scheduler_year = tb_schedulestatue.getYear();
            zgSchedule.scheduler_month = tb_schedulestatue.getMonth();
            zgSchedule.scheduler_day = tb_schedulestatue.getDay();
            zgSchedule.scheduler_hour = tb_schedulestatue.getHour();
            zgSchedule.scheduler_minute = tb_schedulestatue.getMinute();
            zgSchedule.text = tb_schedulestatue.getText();
            scheduls.set(index, zgSchedule);
            index++;
        }


        SuperBleSDK.getSDKSendBluetoothCmdImpl(mContext)
                .setAlarmClockAndSchedule(BleApplication.getContext(), alarms, scheduls);


        KLog.d(TAG, "writeAlarm Zg " + schedules);

    }

    public static void setNotifyMsgTime(Context context, int startHour, int endHour) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setComingMessageHours(context, startHour, endHour);

        updateSendKeyTime(context, ZG_BaseInfo.key_push_alert_time, startHour, endHour);

    }

    public static void setPhoneAlertTime(Context context, int startHour, int endHour) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setComingCallHours(context, startHour, endHour);

        updateSendKeyTime(context, ZG_BaseInfo.key_phone_call_time, startHour, endHour);
    }


    /**
     * Update push period by key
     *
     * @param context
     * @param key
     * @param startHour
     * @param endHour
     */
    public static void updateSendKeyTime(Context context, String key, int startHour, int endHour) {
        String start = startHour + ":00";
        if (startHour < 10) {
            start = "0" + startHour + ":00";
        }

        String end = endHour + ":00";
        if (endHour < 10) {
            end = "0" + endHour + ":00";
        }
        ZGDataParsePresenter.updateZGBaseInfo(key, start + "-" + end);
    }

    public static boolean PushOrPhoneTimeisFilter(String key) {
        DateUtil dateUtil = new DateUtil();

        ZG_BaseInfo zgBaseInfoByKey = ZGDataParsePresenter.getZGBaseInfoByKey(key);
        if (zgBaseInfoByKey == null || TextUtils.isEmpty(zgBaseInfoByKey.getContent())) {
            return false;
        }

        try {
            String[] split = zgBaseInfoByKey.getContent().split("-");
            String start = split[0];
            String end = split[1];

            String[] split1 = start.split(":");
            String[] split2 = end.split(":");
            int real_start = Integer.parseInt(split1[0]);
            int real_end = Integer.parseInt(split2[0]);

            KLog.e("PushOrPhoneTimeisFilter " + key + "  " + real_start + " > " + real_end + "  " + dateUtil.getHour());
            if (dateUtil.getHour() < real_start || dateUtil.getHour() > real_end) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Get the specified stored push time EndHour
     *
     * @param key
     * @return
     */
    public static int getEndHourByKey(String key) {
        ZG_BaseInfo zgBaseInfoByKey = ZGDataParsePresenter.getZGBaseInfoByKey(key);
        if (zgBaseInfoByKey == null || TextUtils.isEmpty(zgBaseInfoByKey.getContent())) {
            return -1;
        }
        try {
            String[] split = zgBaseInfoByKey.getContent().split("-");
            String end = split[1];

            String[] split2 = end.split(":");

            int real_end = Integer.parseInt(split2[0]);
            return real_end;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get the specified stored push time Starthour
     *
     * @param key
     * @return
     */
    public static int getStartHourByKey(String key) {
        ZG_BaseInfo zgBaseInfoByKey = ZGDataParsePresenter.getZGBaseInfoByKey(key);
        if (zgBaseInfoByKey == null || TextUtils.isEmpty(zgBaseInfoByKey.getContent())) {
            return -1;
        }
        try {
            String[] split = zgBaseInfoByKey.getContent().split("-");
            String start = split[0];

            String[] split1 = start.split(":");
            int real_start = Integer.parseInt(split1[0]);
            return real_start;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }


    public static boolean isSysnc() {
        int progress = (int) SyncData.getInstance().getProgress();
        if (progress != 0) {
            KLog.e("synchronizing.....");
            return true;
        }
        return false;
    }

    public static void syncinitDataInfo(Context applicationContext) {
        boolean connected = BluetoothUtil.isConnected();
        KLog.e("syncinitDataInfo  connected " + connected);
        if (!connected) {
            return;
        }
        KLog.e("syncinitDataInfo " + isSysnc());

        if (isSysnc()) {
            return;
        }

//        byte[] bytes = {0x06, 00, (byte) 0x81, 0x10, 00, 00, 00, 00, 00, 00, 0x26, 00, 00, 00, 00, 00, 00, 00, 00, 00};
//        BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes);

        if(hasGps()) {
            byte[] gpsTotalData = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getGpsTotalData();
            BackgroundThreadManager.getInstance().addWriteData(applicationContext, gpsTotalData);
        }
        byte[] time = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).setTimeAndWeather();
        BackgroundThreadManager.getInstance().addWriteData(applicationContext, time);

        byte[] dataDate = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getDataDate();
        BackgroundThreadManager.getInstance().addWriteData(applicationContext, dataDate);

        ZGBaseUtils.getBP();

        ExecutorUtils.getExecutorService().execute(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(1500);
                DateUtil dateUtil = new DateUtil();
                ZGBaseUtils.postSyncDataEventZg(ZGBaseUtils.Sport, dateUtil.getYear(), dateUtil.getMonth(), dateUtil.getDay());
            }
        });
    }

    public static boolean hasGps(){
        boolean has = false;
        String deviceName=PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
        if (TextUtils.isEmpty(deviceName)) {
            has = false;
        }else if(deviceName.contains("Band23")){
            has = true;
        }
        com.socks.library.KLog.d("no2855--> 是否需要同步gps："+deviceName+" -- "+has);
        return has;
    }


    public static boolean isZG() {
        Context applicationContext = BleApplication.getInstance().getApplicationContext();
        if (applicationContext != null && SuperBleSDK.readSdkType(applicationContext) == SuperBleSDK.SDK_Zg) {
            return true;
        }
        return false;
    }

    public static boolean isIwown() {
        Context applicationContext = BleApplication.getInstance().getApplicationContext();
        if (applicationContext != null && SuperBleSDK.readSdkType(applicationContext) == SuperBleSDK.SDK_Zeroner) {
            return true;
        }
        return false;
    }

    public static boolean isMtk() {
        Context applicationContext = BleApplication.getInstance().getApplicationContext();
        if (applicationContext != null && SuperBleSDK.readSdkType(applicationContext) == SuperBleSDK.SDK_Mtk) {
            return true;
        }
        return false;
    }

    public static boolean isProtoBuf() {
        Context applicationContext = BleApplication.getInstance().getApplicationContext();
        if (applicationContext != null && SuperBleSDK.readSdkType(applicationContext) == SuperBleSDK.SDK_ProtoBuf) {
            return true;
        }
        return false;
    }


    public static void setPhoneCallStatus(Context context, int status) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setCallNotificationSwitch(context, status);
    }

    public static void setMsgNotificationSwitch(Context context, int status) {
        KLog.e("setMsgNotificationSwitch " + status);
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setMsgNotificationSwitch(context, status);
    }

    public static void getFirmwareInformation(Context context) {
        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getFirmwareInformation();
        BackgroundThreadManager.getInstance().addWriteData(context, bytes);
    }

    public static void setShakeModel(Context context) {
//        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setShake(context, UserConfig.getInstance().getZg_phoneShakeMode(), UserConfig.getInstance().getZg_phoneShakeNum(), UserConfig.getInstance().getZg_smsShakeMode(), UserConfig.getInstance().getZg_smsShakeNum()
//                , UserConfig.getInstance().getZg_sedentaryShakeMode(), UserConfig.getInstance().getZg_sedentaryShakeNum(), UserConfig.getInstance().getZg_heartGuideShakeMode(),  UserConfig.getInstance().getZg_heartGuideShakeNum());

    }

    public static void setTimeDisplay(Context context, int display) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTimeDisplay(context, display);
    }

    public static void setAlarmScheduleModeNumber(int alarm_mode, int alarm_number) {
        alarm_mode1 = alarm_mode;
        alarm_number1 = alarm_number;
    }

    public static void setGesture(Context context, int gestures, int startHour, int endHour) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setGesture(context, gestures, startHour, endHour);
    }

    public static void setAutoHeart(Context context, int heartOn, int startHour, int endHour) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).heartDetection(context, heartOn, startHour, endHour);
    }

    public static void setLanguage(Context context, int language) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setLanguage(context, language);
    }

    public static void setHeartAlarm(Context context, int warmingOn, int heartHighAlarm, int heartLowAlarm) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setHeartAlarm(context, warmingOn, heartHighAlarm, heartLowAlarm);
    }

    public static void setUnit(Context context, int type) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setUnitSwitch(context, type);
    }

    public static void setTemperatureUnit(Context context, int type) {
        SuperBleSDK.getSDKSendBluetoothCmdImpl(context).setTemperatureUnitSwitch(context, type);
    }

    public static int getMsgStatus() {
        try {
            return Integer.parseInt(ZGDataParsePresenter.getZGBaseInfoByKey(ZG_BaseInfo.key_message_notification).getContent());
        } catch (Exception e) {
            return 0;
        }
    }

    public static float geKcal(int total_time_distance, float weight) {
        if (weight == 0) {
            weight = 60;
        }
        return (total_time_distance * weight * 1.036f / 1000);
    }


    public static void writeAgps(Context context,String path) {
        try {
            InputStream open = context.getResources().getAssets().open("cep_pak.bin");
            allAgps = FileIOUtils.readFile2BytesByStream(path);
//            allAgps = Util.input2byte(open);
        } catch (IOException e) {
        }
        allPoint = allAgps.length;

        if (allAgps == null) {
            return;
        }
        progress = 0;
        agpsCount = 0;
        nowPoint = 0;
        count2048 = 0;
        writeAgps2048(context);
//        BleDataOrderHandler.getInstance().startAgps();
    }



    public static void writeAgps2048(Context context) {
        com.socks.library.KLog.e("no2855下一个2048 --> " + allAgps.length + " === " + 2048 * count2048 + " === " + (2048 * count2048 + 2048));
        if (!isEnd()) {
            byte[] data2048 = Arrays.copyOfRange(allAgps, 2048 * count2048, 2048 * count2048 + 2048);
            writeAgps256(context, data2048);
        } else {
            //发送校验
//            byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).agpsOperation(2);
//            BackgroundThreadManager.getInstance().addWriteData(context, bytes);
        }
    }

    public static boolean isEnd() {
        if (allAgps == null) {
            return true;
        }
        return 2048 * count2048 >= allAgps.length;
    }

    private static int sum2048;
    private static int progress = 0;
    private static int allPoint = 1;
    private static int nowPoint = 0;

    public static void writeAgps256(Context context, byte[] data2048) {
        int add = 256;
        com.socks.library.KLog.d("no2855--> 继续下发另一个256:" + agpsCount);
//        int sum256 = data2048.length%16==0 ? data2048.length/256 : data2048.length/256+1;
        sum2048 = data2048.length;
        byte[] datas = Arrays.copyOfRange(data2048, 256 * agpsCount, 256 * agpsCount + 256);
        int sum = datas.length % 16 == 0 ? datas.length / 16 : datas.length / 16 + 1;
        com.socks.library.KLog.e("no2855 需要发送的单独总数量: " + datas.length + " -- " + sum);
        com.socks.library.KLog.d("no2855 需要发送的单独总数量内容: " + ByteUtil.bytesToHex(data2048));
        BackgroundThreadManager.getInstance().clearQueue();
        for (int i = 0; i < sum; i++) {
            byte[] sends = Arrays.copyOfRange(datas, 16 * i, 16 * (i + 1));
            SuperBleSDK.getSDKSendBluetoothCmdImpl(context).writeAgps(context, sum2048, count2048 + 1, agpsCount + 1, i + 1, sends);
        }
        progress = (datas.length + nowPoint) * 100 / allPoint;
        nowPoint += datas.length;
//        writeAgps256Next(false);
        com.socks.library.KLog.e("no2855 当前的progress: " + progress);
        EventBus.getDefault().post("i7g-apgs-progress:"+ progress);
    }

    public static void writeAgps256Next(Context context, boolean isOk) {
        boolean isEnd = false;
        if (isOk) {
            if (256 * (agpsCount + 1) >= sum2048) {
                com.socks.library.KLog.e("no2855--> 继续下发另一个256: is End");
                count2048++;
                isEnd = true;
            }
            if (isEnd) {
                agpsCount = 0;
            } else {
                agpsCount++;
            }
        }
        if (!isEnd) {
            writeAgps2048(context);
        }
    }

    public static void initAgpsData(boolean isOk) {
        if(isOk){
            if (256 * (agpsCount + 1) >= sum2048) {
                com.socks.library.KLog.e("no2855--> 继续下发另一个256: is End");
                count2048++;
            }
        }
        agpsCount = 0;
    }

    public static int getAgpsLength(){
        allAgps = FileIOUtils.readFile2BytesByStream(ZGDataParsePresenter.path);
        if(allAgps==null){
            return 0;
        }else{
            return allAgps.length/2048;
        }
    }

    public static void setWelcomePageContent(String text, int timeZone,int height,int sex){
        String deviceName= PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
        ZG_BaseInfo baseInfo = DataSupport.where("key=? and data_form=?", ZG_BaseInfo.key_welcome_blood, deviceName+"").findFirst(ZG_BaseInfo.class);
        int[] bloods = new int[8];
        boolean isOld = false;
        if(baseInfo!=null){
            WelcomeBloodData bloodData = null;
            if(!TextUtils.isEmpty(baseInfo.getContent())) {
                bloodData = JsonUtils.fromJson(baseInfo.getContent(), WelcomeBloodData.class);
            }
            if(bloodData==null){
                bloodData = new WelcomeBloodData();
            }
            if(bloodData.getOld8D()==1){
                isOld=true;
            }
            if(bloodData.getBlood()!=null){
                bloods[0]=bloodData.getBlood().getSrcSbp_LB();
                bloods[1]=bloodData.getBlood().getSrcSbp_HB();
                bloods[2]=bloodData.getBlood().getSrcDbp_LB();
                bloods[3]=bloodData.getBlood().getSrcDbp_HB();
                bloods[4]=bloodData.getBlood().getDstSbp_LB();
                bloods[5]=bloodData.getBlood().getDstSbp_HB();
                bloods[6]=bloodData.getBlood().getDstDbp_HB();
                bloods[7]=bloodData.getBlood().getDstDbp_LB();
            }
            bloodData.setTimeZone(timeZone);
            bloodData.setWelcome(text);
            bloodData.setGender(sex);
            bloodData.setHeight(height);
            String newStr = JsonUtils.toJson(bloodData);
            if(!newStr.equals(baseInfo.getContent())){
                baseInfo.setContent(newStr);
                baseInfo.updateAll("key=? and data_form=?", ZG_BaseInfo.key_welcome_blood, deviceName+"");
            }
        }else{
            baseInfo = new ZG_BaseInfo();
            baseInfo.setData_form(deviceName);
            baseInfo.setKey(ZG_BaseInfo.key_welcome_blood);
            WelcomeBloodData bloodData = new WelcomeBloodData();
            bloodData.setTimeZone(timeZone);
            bloodData.setWelcome(text);
            bloodData.setGender(sex);
            bloodData.setHeight(height);
            bloodData.setOld8D(0);
            baseInfo.setContent(JsonUtils.toJson(bloodData));
            baseInfo.save();
        }

        if(isOld){
            SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeWelcomePageText(BleApplication.getInstance(),text, timeZone, height, sex);
        }else{
            SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeWelcomePageText(BleApplication.getInstance(),text, timeZone, height, sex,bloods);
        }
    }

    public static void  getBP(){
        byte[] bp= SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).getBP();
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bp);
    }

    public static void startAgps(){
        if(getAgpsLength()>0){
            SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).writeAgpsLength(BleApplication.getInstance(),getAgpsLength());
            byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).agpsOperation(Constants.AgpsMode.START);
            BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes);
        }
    }

    public static void endAgps(){
        byte[] bytes = SuperBleSDK.getSDKSendBluetoothCmdImpl(BleApplication.getInstance()).agpsOperation(Constants.AgpsMode.END);
        BackgroundThreadManager.getInstance().addWriteData(BleApplication.getInstance(),bytes);
    }


}
