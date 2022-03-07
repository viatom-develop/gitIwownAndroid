package com.zeroner.bledemo.bean.data;

import com.blankj.utilcode.util.LogUtils;
import com.zeroner.bledemo.utils.StringUtil;

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
    public String sn;

    public DummyItem(String title, String deviceName, String deviceAddress, String deviceStatue, String battery,String model,String version) {
        this.title = title;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceStatue = deviceStatue;
        this.battery = battery;
        this.model = model;
        this.version = version;
    }

    public DummyItem(String title, String deviceName, String deviceAddress, String deviceStatue, String battery,String model,String version, String codeStr) {
        this.title = title;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.deviceStatue = deviceStatue;
        this.battery = battery;
        this.model = model;
        this.version = version;

        // codeStr = sn + ctei
        String code = codeStr.replace(" ", "");
        String tempSn ="123" /*"i am sn"*/;
//        String tempCtei = "";
        if (code.length() > 32) {
            tempSn = code.substring(0, 32);
        }
//        if (code.length() > 32+30) {
//            tempCtei = code.substring(32, 32+30);
//        }

        this.sn = StringUtil.convertHexToString(tempSn);
//        this.ctei = StringUtil.convertHexToString(tempCtei);

//        LogUtils.d("SN: " + sn, "CTEI: " + ctei);
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
