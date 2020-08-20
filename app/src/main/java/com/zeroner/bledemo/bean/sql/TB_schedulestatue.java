package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2015/12/5.
 */
public class TB_schedulestatue extends DataSupport {
    //Database-specific data, does not correspond to the firmware data
    //id
    private int id;
    //Remarks information, can be empty, empty with "" instead
    private String remind;
    //Year * 10000 + month * 100 + day
    private int dates;
    //hour * 100 + min
    private int times;

    //Data corresponding to the firmware
    //year 0 - 255
    private int year;
    //month 0 - 11
    private int month;
    //day 0 - 30
    private int day;
    //hour 0 - 23
    private int hour;
    //minute 0 - 59
    private int minute;
    //Title up to 18 characters
    private String text;

    private String device_name;

    private int zg_mode=1;
    private int zg_number=6;

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public int getZg_mode() {
        return zg_mode;
    }

    public void setZg_mode(int zg_mode) {
        this.zg_mode = zg_mode;
    }

    public int getZg_number() {
        return zg_number;
    }

    public void setZg_number(int zg_number) {
        this.zg_number = zg_number;
    }


    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
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

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getDates() {
        return dates;
    }

    public void setDates(int dates) {
        this.dates = dates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public int getProperty() {
//        return property;
//    }
//
//    public void setProperty(int property) {
//        this.property = property;
//    }

    public boolean isSame(TB_schedulestatue data){
        if(year == data.getYear() && month == data.getMonth() && day == data.getDay()
                && hour == data.getHour() && minute == data.getMinute())
            return true;

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TB_schedulestatue)) return false;

        TB_schedulestatue that = (TB_schedulestatue) o;

        if (getId() != that.getId()) return false;
        if (getDates() != that.getDates()) return false;
        if (getTimes() != that.getTimes()) return false;
        if (getYear() != that.getYear()) return false;
        if (getMonth() != that.getMonth()) return false;
        if (getDay() != that.getDay()) return false;
        if (getHour() != that.getHour()) return false;
        if (getMinute() != that.getMinute()) return false;
        if (getRemind() != null ? !getRemind().equals(that.getRemind()) : that.getRemind() != null)
            return false;
        return getText() != null ? getText().equals(that.getText()) : that.getText() == null;

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getRemind() != null ? getRemind().hashCode() : 0);
        result = 31 * result + getDates();
        result = 31 * result + getTimes();
        result = 31 * result + getYear();
        result = 31 * result + getMonth();
        result = 31 * result + getDay();
        result = 31 * result + getHour();
        result = 31 * result + getMinute();
        result = 31 * result + (getText() != null ? getText().hashCode() : 0);
        return result;
    }
}
