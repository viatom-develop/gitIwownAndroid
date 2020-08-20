package com.zeroner.bledemo.bean.data;

/**
 * author：hzy on 2017/12/25 18:57
 * <p>
 * email：hezhiyuan@iwown.com
 */

public class DummyItem {
    public  String title;
    public  String deviceName;
    public  String deviceAddress;
    public  String deviceStatue;
    public  String battery;
    public String model;
    public String version;

    public DummyItem(String title, String deviceName, String deviceAddress, String deviceStatue, String battery,String model,String version) {
        this.title = title;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceStatue = deviceStatue;
        this.battery = battery;
        this.model = model;
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceStatue() {
        return deviceStatue;
    }

    public void setDeviceStatue(String deviceStatue) {
        this.deviceStatue = deviceStatue;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }



    @Override
    public String toString() {
        return "DummyItem{" +
                "title='" + title + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", deviceAddress='" + deviceAddress + '\'' +
                ", deviceStatue='" + deviceStatue + '\'' +
                ", battery='" + battery + '\'' +
                '}';
    }
}
