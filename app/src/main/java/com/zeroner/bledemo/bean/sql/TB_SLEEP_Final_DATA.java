package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

public class TB_SLEEP_Final_DATA extends DataSupport {

    private int id;

    private int year=0;
//
    private int month=0;
//
//    private int day;

//    private int hour;
//
//    private int minute;

    private float  deepSleepTime;

    private float lightSleepTime;

    private String date;

    private String email;
    private long uid;
    private String sleep_segment;
    //s
    private long end_time;
    private long start_time;
    //1->4
    private int feel_type;

    //1,2,3,4,5
    private String action;

    private int score;
    private int upload;

    private String data_from;

    private int eye_move_time;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
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

    public float getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(float deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public float getLightSleepTime() {
        return lightSleepTime;
    }

    public void setLightSleepTime(float lightSleepTime) {
        this.lightSleepTime = lightSleepTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSleep_segment() {
        return sleep_segment;
    }

    public void setSleep_segment(String sleep_segment) {
        this.sleep_segment = sleep_segment;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public int getFeel_type() {
        return feel_type;
    }

    public void setFeel_type(int feel_type) {
        this.feel_type = feel_type;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public int getUpload() {
        return upload;
    }

    public void setUpload(int upload) {
        this.upload = upload;
    }

    public int getEye_move_time() {
        return eye_move_time;
    }

    public void setEye_move_time(int eye_move_time) {
        this.eye_move_time = eye_move_time;
    }

    @Override
    public String toString() {
        return "TB_SLEEP_Final_DATA{" +
                "year=" + year +
                ", month=" + month +
                ", deepSleepTime=" + deepSleepTime +
                ", lightSleepTime=" + lightSleepTime +
                ", date='" + date + '\'' +
                ", email='" + email + '\'' +
                ", uid=" + uid +
                ", sleep_segment='" + sleep_segment + '\'' +
                ", end_time=" + end_time +
                ", start_time=" + start_time +
                ", feel_type=" + feel_type +
                ", data_from='" + data_from + '\'' +
                '}';
    }
}