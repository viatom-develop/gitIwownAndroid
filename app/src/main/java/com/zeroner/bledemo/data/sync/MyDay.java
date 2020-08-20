package com.zeroner.bledemo.data.sync;

/**
 * Created by nokey on 2017/12/23.
 */

public class MyDay {
    private int year;
    private int month;
    private int day;
    private String date;

    public MyDay(int year, int month, int day, String date){
        this.year=year;
        this.month=month;
        this.day=day;
        this.date=date;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
