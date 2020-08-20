package com.zeroner.bledemo.bean.sql;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * 作者：hzy on 2017/12/28 08:57
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class BraceletSetting extends DataSupport{
    @Column(unique = true, defaultValue = "unknown")
    private String key;
    private int value;
    private int startTime;
    private int endTime;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
}
