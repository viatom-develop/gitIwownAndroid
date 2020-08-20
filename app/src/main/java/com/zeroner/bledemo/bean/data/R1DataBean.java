package com.zeroner.bledemo.bean.data;


import java.util.List;

/**
 * Created by Administrator on 2018/6/12.
 */

public class R1DataBean {
    //平均步频
    private String rate_avg;

    private int maxRate;

    //平均触地时间
    private String earth_time_avg;

    private int max_earth_time;

    //平均腾空时间
    private String sky_time_avg;

    //垂直幅度
    private String vertical_avg;

    //触地平衡
    private String earth_balance;

    //最大垂直幅度
    private int max_vertical;

    //平均配速
    private float speed_avg;

    //最佳配速
    private float speed_min;

    private float speed_max;

    private int avg_hr;
    private int max_hr;
    private int min_hr;
    private List<Integer> hrLists;

    //垂直幅度
    private List<Float> verticalLists;

    //触地时间
    private List<Float> earthTimeLists;
    //步频
    private List<Float> stepRateLists;
    //腾空时间
    private List<Float> skyTimeLists;

    private List<Float> speedLists;


    public String getRate_avg() {
        return rate_avg;
    }

    public void setRate_avg(String rate_avg) {
        this.rate_avg = rate_avg;
    }

    public String getEarth_time_avg() {
        return earth_time_avg;
    }

    public void setEarth_time_avg(String earth_time_avg) {
        this.earth_time_avg = earth_time_avg;
    }

    public String getSky_time_avg() {
        return sky_time_avg;
    }

    public void setSky_time_avg(String sky_time_avg) {
        this.sky_time_avg = sky_time_avg;
    }

    public String getVertical_avg() {
        return vertical_avg;
    }

    public void setVertical_avg(String vertical_avg) {
        this.vertical_avg = vertical_avg;
    }

    public String getEarth_balance() {
        return earth_balance;
    }

    public void setEarth_balance(String earth_balance) {
        this.earth_balance = earth_balance;
    }

    public int getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(int maxRate) {
        this.maxRate = maxRate;
    }

    public int getMax_earth_time() {
        return max_earth_time;
    }

    public void setMax_earth_time(int max_earth_time) {
        this.max_earth_time = max_earth_time;
    }

    public int getMax_vertical() {
        return max_vertical;
    }

    public void setMax_vertical(int max_vertical) {
        this.max_vertical = max_vertical;
    }

    public List<Float> getVerticalLists() {
        return verticalLists;
    }

    public void setVerticalLists(List<Float> verticalLists) {
        this.verticalLists = verticalLists;
    }

    public List<Float> getEarthTimeLists() {
        return earthTimeLists;
    }

    public void setEarthTimeLists(List<Float> earthTimeLists) {
        this.earthTimeLists = earthTimeLists;
    }

    public List<Float> getStepRateLists() {
        return stepRateLists;
    }

    public void setStepRateLists(List<Float> stepRateLists) {
        this.stepRateLists = stepRateLists;
    }

    public List<Float> getSkyTimeLists() {
        return skyTimeLists;
    }

    public void setSkyTimeLists(List<Float> skyTimeLists) {
        this.skyTimeLists = skyTimeLists;
    }

    public float getSpeed_avg() {
        return speed_avg;
    }

    public void setSpeed_avg(float speed_avg) {
        this.speed_avg = speed_avg;
    }

    public float getSpeed_min() {
        return speed_min;
    }

    public void setSpeed_min(float speed_min) {
        this.speed_min = speed_min;
    }

    public List<Float> getSpeedLists() {
        return speedLists;
    }

    public void setSpeedLists(List<Float> speedLists) {
        this.speedLists = speedLists;
    }

    public float getSpeed_max() {
        return speed_max;
    }

    public void setSpeed_max(float speed_max) {
        this.speed_max = speed_max;
    }

    public int getAvg_hr() {
        return avg_hr;
    }

    public void setAvg_hr(int avg_hr) {
        this.avg_hr = avg_hr;
    }

    public int getMax_hr() {
        return max_hr;
    }

    public void setMax_hr(int max_hr) {
        this.max_hr = max_hr;
    }

    public int getMin_hr() {
        return min_hr;
    }

    public void setMin_hr(int min_hr) {
        this.min_hr = min_hr;
    }

    public List<Integer> getHrLists() {
        return hrLists;
    }

    public void setHrLists(List<Integer> hrLists) {
        this.hrLists = hrLists;
    }
}
