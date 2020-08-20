package com.zeroner.bledemo.sleep;

public class SleepSegment {

    private int et;
    private int st;
    //(睡眠type)1开始时间 2结束时间 3深睡时间 4浅睡时间 5静置 6清醒时间
    private int type;

    public SleepSegment(){
    }

    public SleepSegment(int et, int st, int type){
        this.et = et;
        this.st = st;
        this.type = type;
    }

    public int getEt() {
        return et;
    }

    public void setEt(int et) {
        this.et = et;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "SleepDownData2{" +
                "et=" + et +
                ", st=" + st +
                ", type=" + type +
                '}';
    }
}