package com.zeroner.bledemo.data.sync;

import java.util.List;

/**
 * Created by Administrator on 2018/6/15.
 */

public class R1Tag {

    private String tag;
    private List<Integer> year;
    private List<Integer> month;
    private List<Integer> day;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Integer> getYear() {
        return year;
    }

    public void setYear(List<Integer> year) {
        this.year = year;
    }

    public List<Integer> getMonth() {
        return month;
    }

    public void setMonth(List<Integer> month) {
        this.month = month;
    }

    public List<Integer> getDay() {
        return day;
    }

    public void setDay(List<Integer> day) {
        this.day = day;
    }
}
