package com.zeroner.bledemo.bean.sql;

import android.content.Context;
import android.util.Log;



import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Created by hzy on 2015/10/28.
 */
public class TB_v3_sport_data extends DataSupport implements Serializable {

    // 运动类型
    private int sport_type;
    // 数据来源
    private String data_from;
    // 消耗卡路里
    private double calorie;
    // 年
    private int year;
    // 月
    private int month;
    // 日
    private int day;
    @Column(ignore = true)
    public int index;
    private long start_uxtime;
    private long end_uxtime;
    private String sportCode;
    private String heart;
    //有效运动时间 (单位秒)
    private int duration;
    private float distance;
    //步数 或者仰卧起坐这类运动的个数
    private int step_count;

    public TB_v3_sport_data() {
    }

    public int getSport_type() {
        return sport_type;
    }

    public void setSport_type(int sport_type) {
        this.sport_type = sport_type;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getEnd_uxtime() {
        return end_uxtime;
    }

    public void setEnd_uxtime(long end_uxtime) {
        this.end_uxtime = end_uxtime;
    }

    public String getSportCode() {
        return sportCode;
    }

    public void setSportCode(String sportCode) {
        this.sportCode = sportCode;
    }

    public long getStart_uxtime() {
        return start_uxtime;
    }

    public void setStart_uxtime(long start_uxtime) {
        this.start_uxtime = start_uxtime;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getStep_count() {
        return step_count;
    }

    public void setStep_count(int step_count) {
        this.step_count = step_count;
    }

    @Override
    public String toString() {
        return "TB_v3_sport_data{" +
                ", sport_type=" + sport_type +
                ", data_from='" + data_from + '\'' +
                ", calorie=" + calorie +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", index=" + index +
                ", start_uxtime=" + start_uxtime +
                ", end_uxtime=" + end_uxtime +
                ", sportCode='" + sportCode + '\'' +
                '}';
    }
}
