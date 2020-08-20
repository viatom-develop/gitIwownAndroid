package com.zeroner.bledemo.data.sync;

/**
 * 作者：hzy on 2017/7/13 15:26
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class LongitudeAndLatitude {
    private double longitude;
    private double latitude;
    private int gps_speed;
    private int altitude;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
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

    @Override
    public String toString() {
        return "LongitudeAndLatitude{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", gps_speed=" + gps_speed +
                ", altitude=" + altitude +
                '}';
    }
}
