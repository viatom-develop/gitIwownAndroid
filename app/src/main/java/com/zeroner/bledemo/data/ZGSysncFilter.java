package com.zeroner.bledemo.data;

import android.content.Context;
import android.text.TextUtils;

import com.zeroner.bledemo.bean.sql.ZG_BaseInfo;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.ExecutorUtils;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.bluetooth.model.TDay;
import com.zeroner.blemidautumn.bluetooth.model.ZgGpsTotalInfo;
import com.zeroner.blemidautumn.bluetooth.model.bh_totalinfo;
import com.zeroner.blemidautumn.heart.model.zg.EveryDayInfo;
import com.zeroner.blemidautumn.heart.model.zg.SevenDayStore;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.task.BackgroundThreadManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Daemon on 2017/11/30 10:18.
 */

public class ZGSysncFilter {
    private static int totalDays = 7;
    //today total data
    public static bh_totalinfo totalinfo = new bh_totalinfo();
    public static Map<String,Integer> gpsMap;

    public static void setGpsTotalDay(String gpsTotalDay){
        if(gpsMap==null){
            gpsMap = new HashMap<>();
        }
        com.socks.library.KLog.e("no2855-> "+gpsTotalDay);
        gpsMap.clear();
        ZgGpsTotalInfo zgGpsDay = JsonUtils.fromJson(gpsTotalDay, ZgGpsTotalInfo.class);
        if (zgGpsDay != null && zgGpsDay.getGpsTotalLists() != null) {
            for (ZgGpsTotalInfo.GpsTotalList gpsDay : zgGpsDay.getGpsTotalLists()) {
                gpsMap.put(getGpsMapKey(gpsDay.getGpsYear(),gpsDay.getGpsMonth(),gpsDay.getGpsDay()), gpsDay.getPosition());
            }
        }else{
            com.socks.library.KLog.e("no2855->gson 解析也出错？？");
        }
    }


    public static void initSyncCondition(final Context context, final SevenDayStore sevenDayStore) {
        Date date = null;
        totalDays = sevenDayStore.totalDays;
        try {
            ZG_BaseInfo zgBaseInfoByKey = ZGDataParsePresenter.getZGBaseInfoByKey(ZG_BaseInfo.key_data_last_day);
            if (zgBaseInfoByKey != null && !TextUtils.isEmpty(zgBaseInfoByKey.getContent())) {
                date = DateUtil.String2Date(DateUtil.dFyyyyMMdd1, zgBaseInfoByKey.getContent());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (date != null) {
            totalDays = DateUtil.differentDaysByMillisecond(date, new Date());
            if (totalDays > 7) {
                totalDays = 7;
            }
        }


        KLog.e("initSyncCondition " + totalDays);
        //Officially started syncing total
        if (totalDays == 0) {
            KLog.d("today sync  first sync total data");
            //total data

            //Get the latest local total first
            ZG_BaseInfo zgBaseInfoByKey = ZGDataParsePresenter.getZGBaseInfoByKey(ZG_BaseInfo.key_last_totaldata);
            if (zgBaseInfoByKey != null && !TextUtils.isEmpty(zgBaseInfoByKey.getContent())) {
                totalinfo = JsonUtils.fromJson(zgBaseInfoByKey.getContent(), bh_totalinfo.class);
                KLog.d("old total data "+JsonUtils.toJson(totalinfo));
            }

            byte[] bytes1 = SuperBleSDK.getSDKSendBluetoothCmdImpl(context).getTotalData(TDay.Today);
            BackgroundThreadManager.getInstance().addWriteData(context, bytes1);
        } else {
            KLog.d("total Day Sync" + totalDays);
            ExecutorUtils.getExecutorService().execute(new Runnable() {
                @Override
                public void run() {
                    DateUtil dateToday = new DateUtil();
                    ZGSysncFilter.syncData(context,0,dateToday.getYear(),dateToday.getMonth(),dateToday.getDay());
                    for (int i = 0; i < totalDays; i++) {
//                    SystemClock.sleep(200);
                        if (i > 0 && sevenDayStore.storeDateObject != null && i < sevenDayStore.storeDateObject.size()) {
                            EveryDayInfo everyDayInfo = sevenDayStore.storeDateObject.get(i);
                            DateUtil dateUtil = new DateUtil((int) everyDayInfo.year, everyDayInfo.month, everyDayInfo.day);
                            Set<String> hashUpdatDateSets = ZGBaseUtils.getHashUpdatDateSets();
                            if (hashUpdatDateSets != null && hashUpdatDateSets.contains(dateUtil.getY_M_D())) {
                                KLog.e("------Has been updated-----" + dateUtil.getY_M_D());
                                continue;
                            }
                            ZGSysncFilter.syncData(context,i+1,(int)everyDayInfo.year,everyDayInfo.month,everyDayInfo.day);
                        }

                    }

//                SystemClock.sleep(200);
                    KLog.e("Send the last received instruction");
                    SuperBleSDK.getSDKSendBluetoothCmdImpl(context).syncDataOver(context);
                }
            });

        }
    }


    public static void syncTodayData(Context applicationContext, bh_totalinfo newTotalInfo) {
        if (totalDays != 0) {
            KLog.e("no Today");
            return;
        }

        if (newTotalInfo.getCalorie() != 0 && totalinfo.getCalorie() != newTotalInfo.getCalorie()) {
            KLog.d("syncTodayData Sport");
            //sport
            byte[] bytes2 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getDetailSport(totalDays);
            BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes2);
        }

        if (newTotalInfo.getSleepMinutes() != 0 && totalinfo.getSleepMinutes() != newTotalInfo.getSleepMinutes()) {
            KLog.d("syncTodayData Sleep");
            //sleep
            byte[] bytes0 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getDetailSleep(totalDays);
            BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes0);
        }

        if(gpsMap!=null){
            StringBuffer buffer = new StringBuffer().append(newTotalInfo.getYear()).append("/")
                    .append(newTotalInfo.getMonth()).append("/").append(newTotalInfo.getDay());
            com.socks.library.KLog.e("no2855-> 徐亚同步的8c "+buffer.toString());
            if(gpsMap.get(buffer.toString())!=null){
                com.socks.library.KLog.e("no2855-> 发送的8C数据 "+gpsMap.get(buffer.toString()));
                byte[] bytes6 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getGpsDetailData(gpsMap.get(buffer.toString()));
                BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes6);
            }
        }


        if (totalinfo.getLatestHeart() != newTotalInfo.getLatestHeart()) {
            //heart
            KLog.d("syncTodayData Heart");
            byte[] bytes4 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).readHeartData(totalDays);
            BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes4);
        }

        if (newTotalInfo.getStep() != 0 && totalinfo.getStep() != newTotalInfo.getStep()) {
            //day
            KLog.d("syncTodayData Step");
            byte[] bytes5 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getDetailWalk(totalDays);
            BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes5);
        }


        KLog.e("Send the last received instruction");
        SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).syncDataOver(applicationContext);

        totalinfo = newTotalInfo;
    }


    public static void syncData(Context applicationContext, int day,int timeYear,int timeMonthy,int timeDay) {
        KLog.e("ZG syncinitDataInfo " + day);

        TDay day1 = null;
        if (day == 0) {
            day1 = TDay.Today;
        } else if (day == 1) {
            day1 = TDay.T_1;
        } else if (day == 2) {
            day1 = TDay.T_2;
        } else if (day == 3) {
            day1 = TDay.T_3;
        } else if (day == 4) {
            day1 = TDay.T_4;
        } else if (day == 5) {
            day1 = TDay.T_5;
        } else if (day == 6) {
            day1 = TDay.T_6;
        } else if (day == 7) {
            day1 = TDay.T_7;
        }

        //total data
        if (day1 != null) {
            byte[] bytes1 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getTotalData(day1);
            BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes1);
        }


        //sport
        byte[] bytes2 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getDetailSport(day);
        BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes2);


        //sleep
        byte[] bytes0 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getDetailSleep(day);
        BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes0);

        //heart
        byte[] bytes4 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).readHeartData(day);
        BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes4);

        //day
        byte[] bytes5 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getDetailWalk(day);
        BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes5);

        //gps
        if(gpsMap!=null && timeYear !=0 && timeMonthy!=0 && timeDay!=0){
            String keyDay = getGpsMapKey(timeYear,timeMonthy,timeDay);
            if(gpsMap.get(keyDay)!=null){
                byte[] bytes6 = SuperBleSDK.getSDKSendBluetoothCmdImpl(applicationContext).getGpsDetailData(day);
                BackgroundThreadManager.getInstance().addWriteData(applicationContext, bytes6);
            }
        }



    }

    private static String getGpsMapKey(int timeYear,int timeMonth,int timeDay){
        StringBuffer buffer = new StringBuffer().append(timeYear).append("/")
                .append(timeMonth).append("/").append(timeDay);
        return buffer.toString();
    }
}
