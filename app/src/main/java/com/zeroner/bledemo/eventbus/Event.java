package com.zeroner.bledemo.eventbus;

import java.util.HashMap;

/**
 * author：hzy on 2017/12/23 11:19
 * <p>
 * email：hezhiyuan@iwown.com
 */

public class Event {
    public static final String Ble_Connect_Statue="com.zeroner.app.BleConnectStatue";
    public static final String Ble_Data_Total="com.zeroner.app.Ble_Data_Total";
    public static final String Ble_Data_Unbind="com.zeroner.app.Ble_Data_Unbind";

    private String action;
    private HashMap<String,Object> dataMap=new HashMap<>();

    public Event() {
    }

    public Event(String action) {
        this.action = action;
    }

    public Event(String action, HashMap<String, Object> dataMap) {
        this.action = action;
        this.dataMap = dataMap;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public HashMap<String, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(HashMap<String, Object> dataMap) {
        this.dataMap = dataMap;
    }
}
