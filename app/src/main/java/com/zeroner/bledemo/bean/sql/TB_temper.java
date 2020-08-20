package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * @author yx
 * @date 2020/5/19
 */
public class TB_temper extends DataSupport {

    private long uid;
    private String data_from;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;
    private int timeStamp;//s
    private String date;//yyyyMMdd
    private int seq;
    private int temperType;
    //环境温度
    private int temperEnv;
    //体表温度
    private int temperBody;
    //预设温度
    private int temperDef;
    //腋下温度
    private int temperArm;

    public int getTemperType() {
        return temperType;
    }

    public void setTemperType(int temperType) {
        this.temperType = temperType;
    }

    public int getTemperEnv() {
        return temperEnv;
    }

    public void setTemperEnv(int temperEnv) {
        this.temperEnv = temperEnv;
    }

    public int getTemperBody() {
        return temperBody;
    }

    public void setTemperBody(int temperBody) {
        this.temperBody = temperBody;
    }

    public int getTemperDef() {
        return temperDef;
    }

    public void setTemperDef(int temperDef) {
        this.temperDef = temperDef;
    }

    public int getTemperArm() {
        return temperArm;
    }

    public void setTemperArm(int temperArm) {
        this.temperArm = temperArm;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

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

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
