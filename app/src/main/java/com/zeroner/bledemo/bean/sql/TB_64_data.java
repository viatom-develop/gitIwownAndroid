package com.zeroner.bledemo.bean.sql;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

/**
 * 作者：hzy on 2017/8/10 20:04
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class TB_64_data extends DataSupport implements Comparable<TB_64_data> {
    private String data_from;
    private int seq;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int second;
    private String ecg;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    private String cmd;

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }


    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
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

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public String getEcg() {
        return ecg;
    }

    public void setEcg(String ecg) {
        this.ecg = ecg;
    }

    /**
     * 返回Json字符串
     * @return
     */
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }


    @Override
    public int compareTo(@NonNull TB_64_data tb_64_data) {
        if(this.seq > tb_64_data.getSeq()){
            return 1;
        }
        return -1;
    }
}
