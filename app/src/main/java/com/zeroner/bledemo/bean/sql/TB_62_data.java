package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * 作者：hzy on 2017/7/4 15:46
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class TB_62_data extends DataSupport {
    private String data_from;
    private int ctrl;
    private int seq;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int freq;
    private int num;
    private String gnssData;
    private long time;
    private String cmd;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public int getCtrl() {
        return ctrl;
    }

    public void setCtrl(int ctrl) {
        this.ctrl = ctrl;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
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

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getGnssData() {
        return gnssData;
    }

    public void setGnssData(String gnssData) {
        this.gnssData = gnssData;
    }
}
