package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

public class ProtoBuf_index_80 extends DataSupport {

    private long uid;
    private String data_from;
    private int start_idx;
    private int end_idx;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int second;
    private int time;
    private int indexType;

    private int isFinish;

    public long getId(){
        return getBaseObjId();
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

    public int getStart_idx() {
        return start_idx;
    }

    public void setStart_idx(int start_idx) {
        this.start_idx = start_idx;
    }

    public int getEnd_idx() {
        return end_idx;
    }

    public void setEnd_idx(int end_idx) {
        this.end_idx = end_idx;
    }

    public int getIndexType() {
        return indexType;
    }

    public void setIndexType(int indexType) {
        this.indexType = indexType;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
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

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getIsFinish() {
        return isFinish;
    }

    public void setIsFinish(int isFinish) {
        this.isFinish = isFinish;
    }

    @Override
    public String toString() {
        return "ProtoBuf_index_80{" +
                "uid=" + uid +
                ", data_from='" + data_from + '\'' +
                ", start_idx=" + start_idx +
                ", end_idx=" + end_idx +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", min=" + min +
                ", second=" + second +
                ", time=" + time +
                ", indexType=" + indexType +
                '}';
    }
}
