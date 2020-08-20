package com.zeroner.bledemo.data.sync;


import com.zeroner.bledemo.utils.DateUtil;

/**
 * Created by nokey on 2017/12/13.
 */

public class P1SendBleData {
    public int year;
    public int month;
    public int day;
    public int startIndex;
    public int endIndex;
    public int dataType;
    public String date="";

    public P1SendBleData(int year, int month, int day, int startIndex, int endIndex, int dataType){
        this.year=year;
        this.month=month;
        this.day=day;
        this.startIndex=startIndex;
        this.endIndex=endIndex;
        this.dataType=dataType;
        this.date=new DateUtil(year,month,day).getSyyyyMMddDate();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
}
