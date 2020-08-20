package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * 作者：hzy on 2018/1/2 11:48
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class HeartRateHour extends DataSupport{
    private String data_from;
    //year
    private int year;
    //month
    private int month;
    //day
    private int day;
    //hour
    private int hours;
    //detail data
    private String detail_data;
    //year month day hour
    private String record_date;
    //Data to the second of the hour
    private long time_stamp;

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public String getDetail_data() {
        return detail_data;
    }

    public void setDetail_data(String detail_data) {
        this.detail_data = detail_data;
    }

    public String getRecord_date() {
        return record_date;
    }

    public void setRecord_date(String record_date) {
        this.record_date = record_date;
    }

    public long getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(long time_stamp) {
        this.time_stamp = time_stamp;
    }
}
