package com.zeroner.bledemo.bean.data;

/**
 * 作者：hzy on 2017/12/25 17:04
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class BraceletData {
    private String title;
    private String  steps;
    private String  distance;
    private String  calorie;

    public BraceletData(String title, String steps, String distance, String calorie) {
        this.title = title;
        this.steps = steps;
        this.distance = distance;
        this.calorie = calorie;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }
}
