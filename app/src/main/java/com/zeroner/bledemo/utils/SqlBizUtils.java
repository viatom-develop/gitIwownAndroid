package com.zeroner.bledemo.utils;
import com.zeroner.bledemo.bean.sql.BleLog;
import com.zeroner.bledemo.bean.sql.BraceletSetting;
import com.zeroner.bledemo.bean.sql.DailyData;
import com.zeroner.bledemo.bean.sql.HeartRateHour;
import com.zeroner.bledemo.bean.sql.SleepData;
import com.zeroner.bledemo.bean.sql.SportData;
import com.zeroner.bledemo.bean.sql.ZG_BaseInfo;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：hzy on 2017/12/26 15:07
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class SqlBizUtils {


    public static void saveTb53Heart(int year,int month,int day,int hour,List<Integer> data,String dataFrom){
        if(data.size()<60){
            for (int i = data.size(); i <60; i++) {
                data.add(0);
            }
        }else if(data.size()>60) {
            for (int i = data.size() - 1; i >= 59; i--) {
                data.remove(i);
            }
        }
        HeartRateHour dataHour = new HeartRateHour();
        dataHour.setYear(year);
        dataHour.setMonth(month);
        dataHour.setDay(day);
        dataHour.setHours(hour);
        dataHour.setRecord_date(new DateUtil(year,month,day,hour,0).getUnixTimestamp()+"");
        dataHour.setData_from(dataFrom);
        dataHour.setDetail_data(JsonUtils.toJson(data));
        dataHour.saveOrUpdate("record_date=? and data_from=?",dataHour.getRecord_date()+"",dataFrom+"");
    }


    /**
     *日期格式转为时间戳
     * @return
     */
    public static long date2TimeStamp(int year,int month,int day,int hour,int min){
        try {
            String date_str = year + "-" + month + "-" + day +" " + hour +":"+ min +":00";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return Long.parseLong(String.valueOf(sdf.parse(date_str).getTime()/1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * daily date
     * @param date
     * @param data_from
     * @return
     */
    public static DailyData dailyData(String date,String data_from){
        return  DataSupport.where("date =? and data_from=?" ,date,data_from).findLast(DailyData.class);
    }

    /**
     * save settings
     * @param setting
     */
    public static void saveBraceletSetting(BraceletSetting setting){
        setting.save();
    }

    /**
     * query setting by key
     * @param key
     * @return
     */
    public static BraceletSetting querySetting(String key){
        BraceletSetting setting= DataSupport.where("key=?",key).findFirst(BraceletSetting.class);
        if(setting!=null){
            return setting;
        }else {
            return new BraceletSetting();
        }
    }

    /**
     *
     * @param timeStamp
     * @param startTime
     * @param endTime
     * @param activity
     * @param type
     * @param dataFrom
     * @return
     */
    public static boolean  querySleepDataExists(int timeStamp,int startTime,int endTime,int activity,int type,String dataFrom){
        boolean flag=false;
        int index=DataSupport.where("timeStamp=? and data_from=? and start_time=? and end_time=? and activity=? and sleep_type= ?"
                ,String.valueOf(timeStamp)
                ,dataFrom
                ,String.valueOf(startTime)
                ,String.valueOf(endTime)
                ,String.valueOf(activity)
                ,String.valueOf(type)).count(SleepData.class);
        if(index>0){
            flag=true;
        }
        return flag;
    }


    public static boolean querySportDataExists(long start_unixTime,long end_unixTime,int startTime,int endTime,int sportType,String dataFrom){
        boolean flag=false;
        int index=DataSupport.where("start_unixTime =? and end_unixTime=? and data_from=? and  sport_type=? and start_time=? and end_time= ?"
                ,String.valueOf(start_unixTime)
                ,String.valueOf(end_unixTime)
                ,String.valueOf(dataFrom)
                ,String.valueOf(sportType)
                ,String.valueOf(startTime)
                ,String.valueOf(endTime)).count(SportData.class);
        if(index>0){
            flag=true;
        }
        return flag;
    }

    public static void saveHeartData(HeartRateHour heart){
        HeartRateHour heartRate=DataSupport.where("data_from =? and  record_date=?"
                ,String.valueOf(heart.getData_from())
                ,String.valueOf(heart.getRecord_date())).findFirst(HeartRateHour.class);
        if(heartRate!=null){
            heartRate.setDetail_data(heart.getDetail_data());
            heartRate.save();
        }else {
            heart.save();
        }

    }

    public static HeartRateHour queryHeartAvg(String dataFrom){
        DateUtil d=new DateUtil();
        HeartRateHour heartRate=DataSupport.where("data_from =? and year =? and month=? and day=?"
                ,String.valueOf(dataFrom)
                ,String.valueOf(d.getYear())
                ,String.valueOf(d.getMonth())
                ,String.valueOf(d.getDay()))
                .order("time_stamp desc").findFirst(HeartRateHour.class);
        return heartRate;
    }

    public static List<HeartRateHour> queryHeartByDay(String dataFrom,int year,int month,int day){
        List<HeartRateHour> hearts=new ArrayList<>();
        hearts=DataSupport.where("data_from =? and year =? and month=? and day=?"
                ,String.valueOf(dataFrom)
                ,String.valueOf(year)
                ,String.valueOf(month)
                ,String.valueOf(day))
                .find(HeartRateHour.class);
        return hearts;
    }

    /**
     *
     * @param dataFrom
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static List<SportData> querySportDetail(String dataFrom,int year,int month,int day){
        List<SportData> sports=new ArrayList<>();
        sports=DataSupport.where("data_from =? and year =? and month=? and day=?"
                ,String.valueOf(dataFrom)
                ,String.valueOf(year)
                ,String.valueOf(month)
                ,String.valueOf(day))
                .find(SportData.class);
        return sports;
    }

    /**
     * Sleep data from 18:00 from the previous day to 18:00 in the day
     * Sleep list data contains sleep sober data
     * @param dataFrom
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static   List<SleepData>  querySleepData(String dataFrom, int year, int month, int day){
        List<SleepData> sleeps=new ArrayList<>();
        DateUtil date=new DateUtil(year,month,day);
        DateUtil preDate = new DateUtil(date.getYear(),date.getMonth(),date.getDay());
        preDate.addDay(-1);
        sleeps= DataSupport.where("data_from=? and ((month=? and day=? and start_time>=1080) or (month=? and day=? and start_time<1080))"
                ,dataFrom
                ,String.valueOf(preDate.getMonth())
                ,String.valueOf(preDate.getDay())
                ,String.valueOf(date.getMonth())
                ,String.valueOf(date.getDay())).find(SleepData.class);
        return sleeps;
    }


    public static List<String> queryLog(String dataFrom,int type){
        List<String> list=new ArrayList<>();
        String logText;
        DateUtil date=new DateUtil();
        long time =date.getTimestamp()-3600*1000;
        List<BleLog> bleLogs = DataSupport.where("dataFrom=? and type =? and time>= ?"
                , dataFrom
                , String.valueOf(type)
                , String.valueOf(time))
                .order("time desc")
                .find(BleLog.class);

        for (BleLog log:bleLogs) {
            DateUtil d=new DateUtil(log.getTime(),false);
            String from=log.getDataFrom();
            String cmd=log.getCmd();
            logText=d.getY_M_D_H_M_S()+":"+from+"\n"+cmd;
            list.add(logText);
        }
        return list;
    }

    public static ZG_BaseInfo getBaseInfoByKey(String key_hardinfo, String derviceName) {
        return DataSupport.where(" key=? and data_form=?", key_hardinfo+"", derviceName+"").findFirst(ZG_BaseInfo.class);
    }


}
