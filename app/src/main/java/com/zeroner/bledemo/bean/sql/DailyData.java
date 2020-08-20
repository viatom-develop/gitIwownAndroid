package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * 作者：hzy on 2017/12/26 13:43
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class DailyData extends DataSupport{
    //data unix timeStamp
    private int timeStamp;
    // date string
    private String date;

    private int steps;
    //unit kcal
    private float calories;
    //unit  m
    private float distance;
    //data from
    private String data_from;

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }
}
