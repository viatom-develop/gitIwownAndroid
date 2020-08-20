package com.zeroner.bledemo.data;
import android.content.Context;
import com.google.gson.Gson;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.data.Detail_data;
import com.zeroner.bledemo.bean.sql.DailyData;
import com.zeroner.bledemo.bean.sql.HeartRateHour;
import com.zeroner.bledemo.bean.sql.SleepData;
import com.zeroner.bledemo.bean.sql.SportData;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.model.bh_totalinfo;
import com.zeroner.blemidautumn.heart.model.zg.ZGHeartData;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.output.detail_sport.model.ZgDetailSportData;
import com.zeroner.blemidautumn.output.detail_sport.model.ZgDetailWalkData;
import com.zeroner.blemidautumn.output.sleep.model.ZgSleepData;
import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Daemon on 2017/11/1 16:25.
 */

public class Zg2IwownHandler {

    public static void converter2HourHeart(Context context, int dataType, ZGHeartData zgHeartData) {

        // year
        int y = zgHeartData.getYear();
        // month
        int m = zgHeartData.getMonth();
        // day
        int d = zgHeartData.getDay();

        int[] old_rates = zgHeartData.getStaticHeart();
        if (old_rates.length < 144) {
            KLog.e("converter2HourHeart old_rates length < 144");
            return;
        }

        // hour
        //144 points every 10 minutes a point for each updated 24-hour heart rate data
        //Delete the old ones
        DataSupport.deleteAll(HeartRateHour.class, " data_from=? and year=? and month=? and day=?",
                PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "", y + "", m + "", d + "");

        int[] rates = new int[1440];

        for (int rate_index = 0; rate_index < 1440; rate_index++) {
            int i = rate_index / 10;
            rates[rate_index] = old_rates[i];
        }

        DateUtil dateUtil1 = new DateUtil(y, m, d);
        DateUtil dateUtil = new DateUtil();

        for (int i = 0; i < 24; i++) {

            if (DateUtil.isSameDay(new Date(), new Date(dateUtil1.getTimestamp())) && (i > dateUtil.getHour())) {
                KLog.d("53 last ffff");
//                EventBus.getDefault().post(new ViewRefresh(false, 0x53));
                break;
            }

            HeartRateHour hoursData = new HeartRateHour();

            hoursData.setData_from(PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME));

            int hour = i;

            hoursData.setYear(y);
            hoursData.setMonth(m);
            hoursData.setDay(d);
            hoursData.setHours(hour);
            hoursData.setTime_stamp(new DateUtil(y, m, d, hour, 0).getUnixTimestamp());
            hoursData.setRecord_date(new DateUtil(y, m, d, hour, 0).getYyyyMMdd_HHmmDate());
            //每次 1个变10个
            int[] time = new int[60];

            System.arraycopy(rates, hour * 60, time, 0, 60);

            Gson gson = new Gson();

            hoursData.setDetail_data(gson.toJson(time));
            hoursData.save();

        }


        if (y > 0 && m > 0 && d > 0) {
            ZGBaseUtils.postSyncDataEventZg(ZGBaseUtils.Heart, zgHeartData.getYear(), zgHeartData.getMonth(), zgHeartData.getDay());
        }
        KLog.e(" converter2HourHeart ok");
    }

    public static void zgSleepToV3(ZgSleepData zgSleepData) {
        if(zgSleepData!=null && zgSleepData.getStartTime()>0 && zgSleepData.getData()!=null){
            String dataFrom=PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME)+"";
            DateUtil startDay=new DateUtil(zgSleepData.getStartTime(),true);
            int stNum=startDay.getHour()*60+startDay.getMinute();
            List<ZgSleepData.Sleep> sleeps = zgSleepData.getData();
            saveSleepV3(dataFrom,startDay.getYear(),startDay.getMonth(),startDay.getDay(),stNum,stNum,1);
            int stTime=stNum;
            for (int i = 0; i < sleeps.size(); i++) {
                if(i==0 && sleeps.get(i).getType()==6)
                    continue;
                int mSt=stNum+sleeps.get(i).getSt();
                int mEt=stNum+sleeps.get(i).getEt();
                if(stNum>=1200 && mSt>=1440){
                    mSt=mSt-1440;
                }
                if(stNum>=1200 &&mEt>=1440){
                    mEt=mEt-1440;
                }
                if(sleeps.get(i).getType()==3 || sleeps.get(i).getType()==4){
                    if(mSt>=1200){
                        saveSleepV3(dataFrom,startDay.getYear(),startDay.getMonth(),startDay.getDay(),mSt,mEt,sleeps.get(i).getType());
                    }else{
                        saveSleepV3(dataFrom,zgSleepData.getYear(),zgSleepData.getMonth(),zgSleepData.getDay(),mSt,mEt,sleeps.get(i).getType());
                    }
                }else if(sleeps.get(i).getType()==6){
                    if(stTime>=1200) {
                        saveSleepV3(dataFrom, startDay.getYear(), startDay.getMonth(), startDay.getDay(),stTime, mSt, 2);
                    }else{
                        saveSleepV3(dataFrom,zgSleepData.getYear(),zgSleepData.getMonth(),zgSleepData.getDay(),stTime,mSt,2);
                    }
                    if(mEt>=1200){
                        saveSleepV3(dataFrom, startDay.getYear(), startDay.getMonth(), startDay.getDay(),mEt, mEt, 1);
                    }else{
                        saveSleepV3(dataFrom,zgSleepData.getYear(),zgSleepData.getMonth(),zgSleepData.getDay(),mEt,mEt,1);
                    }
                    stTime=mEt;
                }
            }
            DateUtil endDay=new DateUtil(zgSleepData.getEndTime(),true);
            int etNum=endDay.getHour()*60+endDay.getMinute()-1;
            if(stTime>=1200) {
                saveSleepV3(dataFrom, startDay.getYear(), startDay.getMonth(), startDay.getDay(),stTime, etNum, 2);
            }else{
                saveSleepV3(dataFrom,zgSleepData.getYear(),zgSleepData.getMonth(),zgSleepData.getDay(),stTime,etNum,2);
            }
        }
//        EventBus.getDefault().post(new ViewRefresh(false, 0x28));
    }

    public static void saveSleepV3(String dataFrom,int year,int month,int day,int st,int et,int sleepType){
        SleepData data1 =new SleepData();
        data1.setYear(year);
        data1.setMonth(month);
        data1.setDay(day);
        data1.setData_from(dataFrom);
        if(et-st<0){
            data1.setActivity(1440-st+et);
        }else{
            data1.setActivity(et-st);
        }
        data1.setStart_time(st);
        data1.setEnd_time(et);
        data1.setSleep_type(sleepType);
        data1.saveOrUpdate(" data_from=? and year=? and month=? and day=? and start_time=? and sleep_type=?"
               ,dataFrom,year+"",month+"",day+"",st+"",sleepType+"");
    }

    public static void converter2Walking(Context context, int dataType, bh_totalinfo info) {

        DateUtil dateUtil = new DateUtil(info.getYear(), info.getMonth(), info.getDay());
        DataSupport.deleteAll(DailyData.class, "date=? and data_from=?",
                "" + String.valueOf(dateUtil.getSyyyyMMddDate()),PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME)+ "");
        DailyData rn_walking_data = new DailyData();
        rn_walking_data.setData_from(PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME));
        rn_walking_data.setTimeStamp((int) dateUtil.getUnixTimestamp());
        rn_walking_data.setDistance(info.getDistance());
        rn_walking_data.setCalories(info.getCalorie());
        rn_walking_data.setSteps(info.getStep());
        rn_walking_data.setDate(dateUtil.getSyyyyMMddDate());
        rn_walking_data.save();

        if (dateUtil.isToday()) {
            DailyData dailyData=new DailyData();
            DateUtil date=new DateUtil(info.getYear(),info.getMonth(),info.getDay());
            dailyData.setTimeStamp((int) date.getUnixTimestamp());
            dailyData.setData_from(PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_NAME));
            dailyData.setDate(date.getSyyyyMMddDate());
            dailyData.setSteps(info.getStep());
            dailyData.setCalories(info.getCalorie());
            dailyData.setDistance(info.getDistance());
            dailyData.saveOrUpdate("timeStamp=? and data_from=?",String.valueOf(date.getUnixTimestamp()),PrefUtil.getString(context,BaseActionUtils.ACTION_DEVICE_ADDRESS));
        }
        ZGBaseUtils.postSyncDataEventZg(ZGBaseUtils.Walking, info.getYear(), info.getMonth(), info.getDay());
    }

    /**
     * convert Sport
     *
     * @param context
     * @param dataType
     * @param zgDetailSportData
     */
    public static void converter2sport(Context context, int dataType, ZgDetailSportData zgDetailSportData) {

//
        List<ZgDetailSportData.Sport> sports = zgDetailSportData.getSports();
        if (sports == null || sports.isEmpty()) {
            return;
        }
        KLog.e("sports pre size " + sports.size());

        DateUtil today = new DateUtil(zgDetailSportData.getYear(), zgDetailSportData.getMonth(), zgDetailSportData.getDay());
        today.setHour(0);
        today.setMinute(0);
        today.setSecond(0);


        List<SportData> tb_v3_sport_dataList =
                DataSupport.where(" year=? and month=? and day=? and data_from=?",
                        today.getYear() + "", today.getMonth() + "", today.getDay() + "",PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME) + "").find(SportData.class);

        int zeroTime_s = (int) today.getZeroTime();
        Iterator<ZgDetailSportData.Sport> iterator = sports.iterator();
        while (iterator.hasNext()) {
            ZgDetailSportData.Sport new_sport = iterator.next();
            int startTime_S = zeroTime_s + new_sport.getStartMin() * 60;
            int endTime_S = zeroTime_s + new_sport.getEndMin() * 60;

            DateUtil startDate = new DateUtil(startTime_S, true);
            DateUtil endDate = new DateUtil(endTime_S, true);

            long y_m_d_h_m_s = startDate.getUnixTimestamp();
            long  y_m_d_h_m_s1 = endDate.getUnixTimestamp();

            boolean isExist = false;
            //重复的不保存
            for (SportData sport : tb_v3_sport_dataList) {
                if (sport.getStart_unixTime() == startTime_S &&
                        sport.getEnd_unixTime() == endTime_S) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                SportData rn_sport_data = new SportData();
                rn_sport_data.setCalorie(new_sport.getCalories());
                rn_sport_data.setData_from(PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME));
                rn_sport_data.setStart_time(new_sport.getStartMin());
                rn_sport_data.setEnd_time(new_sport.getEndMin());
                rn_sport_data.setYear(zgDetailSportData.getYear());
                rn_sport_data.setMonth(zgDetailSportData.getMonth());
                rn_sport_data.setDay(zgDetailSportData.getDay());
                rn_sport_data.setStart_unixTime(y_m_d_h_m_s);
                rn_sport_data.setEnd_unixTime(y_m_d_h_m_s1);

                rn_sport_data.setSport_type(new_sport.getSportType());

                sportAddDetail(new_sport.getTotalMin(), new_sport.getSteps(), new_sport.getDistance(), rn_sport_data, true);

                KLog.e("rn_sport_data0 " + rn_sport_data.toString());
            }
        }

//        EventBus.getDefault().post(new ViewRefresh(true, 0x00));

        if (today.getUnixTimestamp() > 1000) {
            ZGBaseUtils.postSyncDataEventZg(ZGBaseUtils.Sport, zgDetailSportData.getYear(), zgDetailSportData.getMonth(), zgDetailSportData.getDay());
        }


    }

    /**
     * 提取 walking 1440  -> 28 sport
     *
     * @param context
     * @param zgDetailWalkData
     */
    public static void getWalkingSport(Context context, ZgDetailWalkData zgDetailWalkData) {

        DateUtil today = new DateUtil(zgDetailWalkData.getYear(), zgDetailWalkData.getMonth(), zgDetailWalkData.getDay());
        today.setHour(0);
        today.setMinute(0);
        today.setSecond(0);


        //删除今天所有的
        DataSupport.deleteAll(SportData.class, "year=? and month=? and day=? and data_from=? and  sport_type=1",
                today.getYear() + "", today.getMonth() + "", today.getDay() + "", PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME) + "");


        List<SportData> rn_sport_datas =
                DataSupport.where(" year=? and month=? and day=? and data_from=?",
                         today.getYear() + "", today.getMonth() + "", today.getDay() + "",  PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME)  + "").find(SportData.class);

        List<Integer> walking_steps = zgDetailWalkData.getData();
//
        int[] walking_arrys = new int[walking_steps.size()];
        for (int i = 0; i < walking_steps.size(); i++) {
            walking_arrys[i] = walking_steps.get(i);
        }

        for (SportData data : rn_sport_datas) {
            try {
                Date start = new Date(data.getStart_unixTime() * 1000);
                Date end = new Date(data.getEnd_unixTime() * 1000);

                int start_time = new DateUtil(start).getTodayMin();
                int end_time = new DateUtil(end).getTodayMin();

                KLog.e(start_time + "  > " + end_time + " > " + data.getSport_type());

                int[] zero_values = new int[end_time - start_time + 1];

                System.arraycopy(zero_values, 0, walking_arrys, start_time, zero_values.length);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<SportData> new_spors = new ArrayList<>();
        long zeroTime_s = today.getZeroTime();

        int start_index = 0;
        int total_time_steps = 0;
        float total_time_distance = 0;
        int activity = 0;

        for (int i = 0; i < walking_arrys.length; i++) {

            if (start_index == 0 && walking_arrys[i] != 0) {
                start_index = i;
            } else if (start_index != 0 && walking_arrys[i] == 0) {
//                KLog.e("start_index " + start_index + "  > " + i + "  " + total_time_steps);
                long startTime_S = zeroTime_s + start_index * 60;
                long endTime_S = zeroTime_s + i * 60;

                DateUtil startDate = new DateUtil(startTime_S, true);
                DateUtil endDate = new DateUtil(endTime_S, true);


                SportData rn_sport_data = new SportData();
                rn_sport_data.setCalorie(ZGBaseUtils.geKcal(Math.round(total_time_distance),0));
                rn_sport_data.setData_from(PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME));
                rn_sport_data.setStart_time(startDate.getTodayMin());
                rn_sport_data.setEnd_time(endDate.getTodayMin());
                rn_sport_data.setYear(startDate.getYear());
                rn_sport_data.setMonth(startDate.getMonth());
                rn_sport_data.setDay(startDate.getDay());
                rn_sport_data.setStart_unixTime(startDate.getUnixTimestamp());
                rn_sport_data.setEnd_unixTime(endDate.getUnixTimestamp());
                rn_sport_data.setSport_type(1);
                sportAddDetail(i - start_index + 1, total_time_steps, total_time_distance, rn_sport_data, false);

                new_spors.add(rn_sport_data);

                start_index = 0;
                total_time_steps = 0;
                total_time_distance = 0;
            }

            float pace = 0.55f;
            if (walking_arrys[i] > 120) {
                pace = 0.85f;
            }
            total_time_distance += walking_arrys[i] * pace;
            total_time_steps += walking_arrys[i];
        }

        Collections.sort(new_spors, new ZGRn_sportCompartor());

        for (SportData data : new_spors) {
            KLog.e("sort after " + data.getStart_time() + "  " + data.getEnd_time() + "  ");
        }
        List<SportData> datas = zg1440WalkingFilter(context, new_spors);


        for (SportData sport_data : datas) {
            KLog.e("merge after " + sport_data.getStart_time() + "  " + sport_data.getEnd_time() + " ");
            sport_data.save();
        }

//        EventBus.getDefault().post(new HomeMoveUpdateHealthyScore(HomeMoveUpdateHealthyScore.UPDATESPORT))

        if (today.getUnixTimestamp() > 1000) {
            ZGBaseUtils.postSyncDataEventZg(ZGBaseUtils.Walking_2_Sport, zgDetailWalkData.getYear(), zgDetailWalkData.getMonth(), zgDetailWalkData.getDay());
        }
    }

    private static void sportAddDetail(int activity, int total_time_steps, float total_time_distance, SportData rn_sport_data, boolean isSave) {
        Detail_data d = new Detail_data();
        d.setStep(total_time_steps);
        d.setDistance(Math.round(total_time_distance));
        d.setActivity(activity);
        rn_sport_data.setDetail_data(JsonUtils.toJson(d));
        if (isSave) {
            rn_sport_data.save();
        }
    }

    /**
     * 1440 Cutting walking combined by rules
     *
     * @param context
     * @param new_spors
     * @return
     */
    private static List<SportData> zg1440WalkingFilter(Context context, List<SportData> new_spors) {

        List<SportData> result_datas = new ArrayList<>();
        for (int i = 0; i < new_spors.size(); i++) {
            SportData sport_data = new_spors.get(i);

            Detail_data detail_data = JsonUtils.fromJson(sport_data.getDetail_data(), Detail_data.class);

            //当前<=5
            if (detail_data.getActivity() <= 5) {
                SportData data = null;
                if (result_datas.size() != 0) {
                    data = result_datas.get(result_datas.size() - 1);
                }
                DateUtil dateUtil=new DateUtil(sport_data.getYear(),sport_data.getMonth(),sport_data.getDay());
                long zeroTime=dateUtil.getZeroTime();
                long startTime_S = zeroTime + sport_data.getStart_time() * 60;
                long endTime_S = zeroTime + sport_data.getEnd_time() * 60;

                DateUtil startDate = new DateUtil(startTime_S, true);
                DateUtil endDate = new DateUtil(endTime_S, true);

                if (data == null) {
                    data = new SportData();
                    data.setCalorie(ZGBaseUtils.geKcal(Math.round(detail_data.getDistance()),0));
                    data.setData_from(PrefUtil.getString(BleApplication.getInstance(),BaseActionUtils.ACTION_DEVICE_NAME));
                    data.setStart_time(sport_data.getStart_time());

                    data.setYear(sport_data.getYear());
                    data.setMonth(sport_data.getMonth());
                    data.setDay(sport_data.getDay());

                    data.setEnd_time(sport_data.getEnd_time());
                    data.setSport_type(sport_data.getSport_type());
                    data.setSport_type(1);
                    data.setStart_unixTime(startDate.getUnixTimestamp());
                    data.setEnd_unixTime(endDate.getUnixTimestamp());
                    sportAddDetail(detail_data.getActivity(), detail_data.getStep(), Math.round(detail_data.getDistance()), data, false);
                    result_datas.add(data);

                    continue;
                } else {

                    if (sport_data.getStart_time() - data.getEnd_time() <= (60)
                            && sport_data.getStart_time() > data.getEnd_time()) {

                        Detail_data detail_data1 = JsonUtils.fromJson(data.getDetail_data(), Detail_data.class);
                        data.setEnd_time(sport_data.getEnd_time());
                        int activity = detail_data.getActivity() + detail_data1.getActivity();
                        data.setStart_unixTime(startDate.getUnixTimestamp());
                        data.setEnd_unixTime(endDate.getUnixTimestamp());
                        data.setCalorie(ZGBaseUtils.geKcal(Math.round(detail_data.getDistance() + detail_data1.getDistance()),0));
                        data.setSport_type(1);
                        sportAddDetail(activity, detail_data.getStep() + detail_data1.getStep(),
                                detail_data.getDistance() + detail_data1.getDistance(), data, false);

                        KLog.e("merge " + data.getStart_time() + " " + data.getEnd_time() + " " +
                                activity + " " + detail_data.getStep() + detail_data1.getStep());

                        continue;
                    }
                }
            }
            result_datas.add(sport_data);
        }

        return result_datas;
    }

}
