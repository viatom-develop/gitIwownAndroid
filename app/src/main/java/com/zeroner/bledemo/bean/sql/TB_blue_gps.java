package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

public class TB_blue_gps extends DataSupport {

    private double lat;
    private double lon;
    private long time;
    private String data_from;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }
}