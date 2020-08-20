package com.zeroner.bledemo.data.sync;

/**
 * 作者：hzy on 2017/7/4 11:36
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class GnssData {
    private int longitude_degree;
    private int longitude_minute;
    private int longitude_second;
    private int longitude_direction;
    private int longitude_preci;
    private int latitude_degree;
    private int latitude_minute;
    private int latitude_second;
    private int latitude_direction;
    private int latitude_preci;
    private int gps_speed;
    private int altitude;

    public int getLongitude_degree() {
        return longitude_degree;
    }

    public void setLongitude_degree(int longitude_degree) {
        this.longitude_degree = longitude_degree;
    }

    public int getLongitude_minute() {
        return longitude_minute;
    }

    public void setLongitude_minute(int longitude_minute) {
        this.longitude_minute = longitude_minute;
    }

    public int getLongitude_second() {
        return longitude_second;
    }

    public void setLongitude_second(int longitude_second) {
        this.longitude_second = longitude_second;
    }

    public int getLongitude_direction() {
        return longitude_direction;
    }

    public void setLongitude_direction(int longitude_direction) {
        this.longitude_direction = longitude_direction;
    }

    public int getLatitude_degree() {
        return latitude_degree;
    }

    public void setLatitude_degree(int latitude_degree) {
        this.latitude_degree = latitude_degree;
    }

    public int getLatitude_minute() {
        return latitude_minute;
    }

    public void setLatitude_minute(int latitude_minute) {
        this.latitude_minute = latitude_minute;
    }

    public int getLatitude_second() {
        return latitude_second;
    }

    public void setLatitude_second(int latitude_second) {
        this.latitude_second = latitude_second;
    }

    public int getLatitude_direction() {
        return latitude_direction;
    }

    public void setLatitude_direction(int latitude_direction) {
        this.latitude_direction = latitude_direction;
    }

    public int getLongitude_preci() {
        return longitude_preci;
    }

    public void setLongitude_preci(int longitude_preci) {
        this.longitude_preci = longitude_preci;
    }

    public int getLatitude_preci() {
        return latitude_preci;
    }

    public void setLatitude_preci(int latitude_preci) {
        this.latitude_preci = latitude_preci;
    }

    public int getGps_speed() {
        return gps_speed;
    }

    public void setGps_speed(int gps_speed) {
        this.gps_speed = gps_speed;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }
}
