package com.zeroner.bledemo.bean.data;

public class ProtobufSyncSeq {

    private int totalSeq;
    private int startSeq;
    private int currentDay;
    private int endSeq;
    private int type;

    public int getTotalSeq() {
        return totalSeq;
    }

    public void setTotalSeq(int totalSeq) {
        this.totalSeq = totalSeq;
    }

    public int getStartSeq() {
        return startSeq;
    }

    public void setStartSeq(int startSeq) {
        this.startSeq = startSeq;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public int getEndSeq() {
        return endSeq;
    }

    public void setEndSeq(int endSeq) {
        this.endSeq = endSeq;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public ProtobufSyncSeq(int totalSeq, int startSeq, int currentDay, int endSeq, int type) {
        this.totalSeq = totalSeq;
        this.startSeq = startSeq;
        this.currentDay = currentDay;
        this.endSeq = endSeq;
        this.type = type;
    }

    @Override
    public String toString() {
        return "ProtobufSyncSeq{" +
                "totalSeq=" + totalSeq +
                ", startSeq=" + startSeq +
                ", currentDay=" + currentDay +
                ", endSeq=" + endSeq +
                ", type=" + type +
                '}';
    }
}
