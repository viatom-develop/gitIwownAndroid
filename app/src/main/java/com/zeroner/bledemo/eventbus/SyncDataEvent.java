package com.zeroner.bledemo.eventbus;

/**
 * Created by zm on 2016/10/25.
 */
public class SyncDataEvent {
    private  int progress;
    private boolean isStop;
    private int totalDay;
    private int mDay;
    private String date_str;

    public String getDate_str() {
        return date_str;
    }

    public void setDate_str(String date_str) {
        this.date_str = date_str;
    }

    public int getTotalDay() {
        return totalDay;
    }

    public void setTotalDay(int totalDay) {
        this.totalDay = totalDay;
    }

    public int getmDay() {
        return mDay;
    }

    public void setmDay(int mDay) {
        this.mDay = mDay;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public SyncDataEvent() {
    }

    public SyncDataEvent(int progress, boolean isStop) {
        this.progress = progress;
        this.isStop = isStop;
    }

    public SyncDataEvent(int progress, boolean isStop, int totalDay, int mDay){
        this.progress = progress;
        this.isStop = isStop;
        this.totalDay=totalDay;
        this.mDay=mDay;
    }

    public SyncDataEvent(int progress, boolean isStop, String date){
        this.progress = progress;
        this.isStop = isStop;
        this.date_str = date;
    }

    public SyncDataEvent(int progress, boolean isStop, int totalDay, int mDay, String date){
        this.progress = progress;
        this.isStop = isStop;
        this.totalDay=totalDay;
        this.mDay=mDay;
        this.date_str = date;
    }
}
