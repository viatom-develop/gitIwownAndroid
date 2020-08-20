package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

public class TB_mtk_statue extends DataSupport {

    private int year;
    private int month;
    private int day;
    private long date;

    /** 61 62 64 80*/
    private int type;

    private String data_from;

    private int has_file;

    private int has_up;

    private int has_tb;

    public long getId() {
        return getBaseObjId();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public int getHas_file() {
        return has_file;
    }

    public void setHas_file(int has_file) {
        this.has_file = has_file;
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

    public int getHas_up() {
        return has_up;
    }

    public void setHas_up(int has_up) {
        this.has_up = has_up;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getHas_tb() {
        return has_tb;
    }

    public void setHas_tb(int has_tb) {
        this.has_tb = has_tb;
    }
}