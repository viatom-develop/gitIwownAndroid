package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * Created by nokey on 2017/9/13.
 */

public class TB_sum_61_62_64 extends DataSupport {
    private String date;
//    private String send_date;
//    private String send_61;
//    private String send_62;
//    private String send_64;
    private long date_time;
//    private int sum_61=0;
//    private int sum_62=0;
//    private int sum_64=0;
    private int year;
    private int month;
    private int day;
    private String send_cmd;
    private int sum;
    private int type;
    private String type_str;

    public String getSend_cmd() {
        return send_cmd;
    }

    public void setSend_cmd(String send_cmd) {
        this.send_cmd = send_cmd;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getType_str() {
        return type_str;
    }

    public void setType_str(String type_str) {
        this.type_str = type_str;
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

//    public int getSum_61() {
//        return sum_61;
//    }
//
//    public void setSum_61(int sum_61) {
//        this.sum_61 = sum_61;
//    }
//
//    public int getSum_62() {
//        return sum_62;
//    }
//
//    public void setSum_62(int sum_62) {
//        this.sum_62 = sum_62;
//    }
//
//    public int getSum_64() {
//        return sum_64;
//    }
//
//    public void setSum_64(int sum_64) {
//        this.sum_64 = sum_64;
//    }

    public long getDate_time() {
        return date_time;
    }

    public void setDate_time(long date_time) {
        this.date_time = date_time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

//    public String getSend_date() {
//        return send_date;
//    }
//
//    public void setSend_date(String send_date) {
//        this.send_date = send_date;
//    }
//
//    public String getSend_61() {
//        return send_61;
//    }
//
//    public void setSend_61(String send_61) {
//        this.send_61 = send_61;
//    }
//
//    public String getSend_62() {
//        return send_62;
//    }
//
//    public void setSend_62(String send_62) {
//        this.send_62 = send_62;
//    }
//
//    public String getSend_64() {
//        return send_64;
//    }
//
//    public void setSend_64(String send_64) {
//        this.send_64 = send_64;
//    }
}
