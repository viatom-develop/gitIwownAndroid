package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 获取R1有效数据运动数据
 */

public class R1_effective_data extends DataSupport implements Serializable {
    //开始时间(s)
    private long time_id;
    //运动时长(减去暂停时长)(s)
    private int time;
    //距离
    private float distance;
    //卡路里
    private float calorie;
    //结束时间
    private long end_time;
    //设置信息
    private String data_from;

    private String year_month_day;

    private String rateOfStride_avg;
    private String flight_avg;
    private String touchDown_avg;
    private int touchDownPower_balance;
    private String speedList;

    private String avg_hr;

//    //1心率 2 运动 3 两者都有
//    private int hr_sport;


    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }


    public long getTime_id() {
        return time_id;
    }

    public void setTime_id(long time_id) {
        this.time_id = time_id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }


    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public String getYear_month_day() {
        return year_month_day;
    }

    public void setYear_month_day(String year_month_day) {
        this.year_month_day = year_month_day;
    }


    public String getAvg_hr() {
        return avg_hr;
    }

    public void setAvg_hr(String avg_hr) {
        this.avg_hr = avg_hr;
    }

    public String getRateOfStride_avg() {
        return rateOfStride_avg;
    }

    public void setRateOfStride_avg(String rateOfStride_avg) {
        this.rateOfStride_avg = rateOfStride_avg;
    }

    public String getFlight_avg() {
        return flight_avg;
    }

    public void setFlight_avg(String flight_avg) {
        this.flight_avg = flight_avg;
    }

    public String getTouchDown_avg() {
        return touchDown_avg;
    }

    public void setTouchDown_avg(String touchDown_avg) {
        this.touchDown_avg = touchDown_avg;
    }

    public int getTouchDownPower_balance() {
        return touchDownPower_balance;
    }


    public String getSpeedList() {
        return speedList;
    }

    public void setSpeedList(String speedList) {
        this.speedList = speedList;
    }

    public void setTouchDownPower_balance(int touchDownPower_balance) {
        this.touchDownPower_balance = touchDownPower_balance;
    }

}
