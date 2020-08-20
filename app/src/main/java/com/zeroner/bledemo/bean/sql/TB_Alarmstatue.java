package com.zeroner.bledemo.bean.sql;
import com.zeroner.bledemo.bean.data.ScheduleType;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2015/12/16.
 */
public class TB_Alarmstatue extends DataSupport implements ScheduleType {
    //Database primary key to ensure id and Ac_Idx are synchronized
    private int id;
    //Alarm clock serial number
    private int Ac_Idx;
    //Alarm clock repeat mark
    private int Ac_Conf;
    //alarm clock hour
    private int Ac_Hour;
    //alarm clock minute
    private int Ac_Minute;
    //Alarm alarm message
    private String Ac_String;
    //Alarm clock opening sign
    private int openState;
    private String date;

    private int zg_mode=1;
    private int zg_number=6;

    private String device_name;
    private String remind;

    public TB_Alarmstatue(){

    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public int getZg_mode() {
        return zg_mode;
    }

    public void setZg_mode(int zg_mode) {
        this.zg_mode = zg_mode;
    }

    public int getZg_number() {
        return zg_number;
    }

    public void setZg_number(int zg_number) {
        this.zg_number = zg_number;
    }

    public int getAc_Idx() {
        return Ac_Idx;
    }

    public void setAc_Idx(int ac_Idx) {
        Ac_Idx = ac_Idx;
    }

    public int getAc_Conf() {
        return Ac_Conf;
    }

    public void setAc_Conf(int ac_Conf) {
        Ac_Conf = ac_Conf;
    }

    public int getAc_Hour() {
        return Ac_Hour;
    }

    public void setAc_Hour(int ac_Hour) {
        Ac_Hour = ac_Hour;
    }

    public int getAc_Minute() {
        return Ac_Minute;
    }

    public void setAc_Minute(int ac_Minute) {
        Ac_Minute = ac_Minute;
    }

    public String getAc_String() {
        return Ac_String;
    }

    public void setAc_String(String ac_String) {
        Ac_String = ac_String;
    }

    public int getOpenState(){
        return openState;
    }

    public void setOpenState(int openState) {
        this.openState = openState;
    }


    public String getRemind() {
        return remind;
    }

    public void setRemind(String remind) {
        this.remind = remind;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "TB_Alarmstatue{" +
                "id=" + id +
                ", Ac_Idx=" + Ac_Idx +
                ", Ac_Conf=" + Ac_Conf +
                ", Ac_Hour=" + Ac_Hour +
                ", Ac_Minute=" + Ac_Minute +
                ", Ac_String='" + Ac_String + '\'' +
                ", openState=" + openState +
                ", date='" + date + '\'' +
                ", zg_mode=" + zg_mode +
                ", zg_number=" + zg_number +
                ", device_name='" + device_name + '\'' +
                ", remind='" + remind + '\'' +
                '}';
    }

    @Override
    public int getItemType() {
        return TYPE_ALARM;
    }
}
