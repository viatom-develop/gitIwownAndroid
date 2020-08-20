package com.zeroner.bledemo.bean.data;

import java.util.ArrayList;

/**
 * 作者：hzy on 2018/1/9 09:07
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class SleepTime {
    private int totalMin;
    private ArrayList<SleepStatusFlag> sleepStatus;
    private int startMin;
    private int endMin;
    private int lightSleepTime;
    private int deepSleepTime;
    private int awakeSleepTime;
    private int totalTimeIncludeNoSleep;

    public int getTotalMin() {
        return totalMin;
    }

    public void setTotalMin(int totalMin) {
        this.totalMin = totalMin;
    }

    public ArrayList<SleepStatusFlag> getSleepStatus() {
        return sleepStatus;
    }

    public void setSleepStatus(ArrayList<SleepStatusFlag> sleepStatus) {
        this.sleepStatus = sleepStatus;
    }

    public int getStartMin() {
        return startMin;
    }

    public void setStartMin(int startMin) {
        this.startMin = startMin;
    }

    public int getEndMin() {
        return endMin;
    }

    public void setEndMin(int endMin) {
        this.endMin = endMin;
    }

    public int getLightSleepTime() {
        return lightSleepTime;
    }

    public void setLightSleepTime(int lightSleepTime) {
        this.lightSleepTime = lightSleepTime;
    }

    public int getDeepSleepTime() {
        return deepSleepTime;
    }

    public void setDeepSleepTime(int deepSleepTime) {
        this.deepSleepTime = deepSleepTime;
    }

    public int getAwakeSleepTime() {
        return awakeSleepTime;
    }

    public void setAwakeSleepTime(int awakeSleepTime) {
        this.awakeSleepTime = awakeSleepTime;
    }

    public int getTotalTimeIncludeNoSleep() {
        return totalTimeIncludeNoSleep;
    }

    public void setTotalTimeIncludeNoSleep(int totalTimeIncludeNoSleep) {
        this.totalTimeIncludeNoSleep = totalTimeIncludeNoSleep;
    }
}
