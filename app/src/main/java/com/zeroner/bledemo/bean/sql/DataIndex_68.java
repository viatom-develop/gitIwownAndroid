package com.zeroner.bledemo.bean.sql;

import androidx.annotation.NonNull;

import org.litepal.crud.DataSupport;

public class DataIndex_68 extends DataSupport implements Comparable<DataIndex_68> {
    private long uid;
    private String device_name;
    private int year;
    private int month;
    private int day;
    private int start_idx;
    private int end_idx;
    private String send_cmd;
    private int processed;

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

    public String getSend_cmd() {
        return send_cmd;
    }

    public void setSend_cmd(String send_cmd) {
        this.send_cmd = send_cmd;
    }

    public int getProcessed() {
        return processed;
    }

    public void setProcessed(int processed) {
        this.processed = processed;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    @Override
    public int compareTo(@NonNull DataIndex_68 obj) {
        if(year>obj.year)
            return 1;
        else if(year<obj.year)
            return  -1;
        else{
            if(month>obj.month)
                return 1;
            else if(month<obj.month)
                return -1;
            else {
                if(day>obj.day)
                    return 1;
                else if(day<obj.day)
                    return -1;
                else{
                    //it's possible return several records for same day, but no matter compare them by index
                    return 0;
                }
            }
        }
    }
}
