package com.zeroner.bledemo.bean.sql;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

public class ProtoBuf_80_data extends DataSupport {

    /**
     * data
     */
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    /**
     * 时间戳(单位s)
     */
    private int time;
    /**
     * 排序用的
     */
    private int seq;
    /**
     * 设备名
     */
    private String data_from;
    /**
     * 睡眠数据
     */
    private String sleepData;
    private boolean charge;
    private boolean shutdown;
    /**
     * 健康
     */
    private int type;
    private int state;
    private float calorie;
    private int step;
    private float distance;
    /**
     * 心率
     */
    private int min_bpm;
    private int max_bpm;
    private int avg_bpm;
    /**
     * 疲劳度
     */
    private float SDNN;
    private float RMSSD;
    private float PNN50;
    private float MEAN;
    private float fatigue;


    /**
     * 呼吸训练
     */
    private float mdt_SDNN;
    private  float mdt_RMSSD;
    private  float mdt_PNN50;
    private  float mdt_MEAN;
    private  int mdt_status;
    private  float mdt_RESULT;
    private  float mdt_RELAX;

    /**
     * 血氧数据
     */
    private int avgSpo2;
    private int maxSpo2;
    private int minSpo2;


    /**
     * 温度数据
     */
    private int temperType;
    //环境温度
    private int temperEnv;
    //体表温度
    private int temperBody;
    //预设温度
    private int temperDef;
    //腋下温度
    private int temperArm;



    /**
     * 血压
     *
     */
    private int sbp;
    private int dbp;
    private int bpTime;//bp数据


    /** 不进入数据库,仅用于计算
     * (Do not enter database, only for calculation)
     * */
    @Column(ignore = true)
    public int endStep=0;
    @Column(ignore = true)
    public float endDis=0;
    @Column(ignore = true)
    public float endClo=0;
    @Column(ignore = true)
    public int endMin=0;

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean isCharge() {
        return charge;
    }

    public void setCharge(boolean charge) {
        this.charge = charge;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public float getCalorie() {
        return calorie;
    }

    public void setCalorie(float calorie) {
        this.calorie = calorie;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public int getMin_bpm() {
        return min_bpm;
    }

    public void setMin_bpm(int min_bpm) {
        this.min_bpm = min_bpm;
    }

    public int getMax_bpm() {
        return max_bpm;
    }

    public void setMax_bpm(int max_bpm) {
        this.max_bpm = max_bpm;
    }

    public int getAvg_bpm() {
        return avg_bpm;
    }

    public void setAvg_bpm(int avg_bpm) {
        this.avg_bpm = avg_bpm;
    }

    public float getSDNN() {
        return SDNN;
    }

    public void setSDNN(float SDNN) {
        this.SDNN = SDNN;
    }

    public float getRMSSD() {
        return RMSSD;
    }

    public void setRMSSD(float RMSSD) {
        this.RMSSD = RMSSD;
    }

    public float getPNN50() {
        return PNN50;
    }

    public void setPNN50(float PNN50) {
        this.PNN50 = PNN50;
    }

    public float getMEAN() {
        return MEAN;
    }

    public void setMEAN(float MEAN) {
        this.MEAN = MEAN;
    }

    public float getFatigue() {
        return fatigue;
    }

    public void setFatigue(float fatigue) {
        this.fatigue = fatigue;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public String getSleepData() {
        return sleepData;
    }

    public void setSleepData(String sleepData) {
        this.sleepData = sleepData;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }


    public int getSbp() {
        return sbp;
    }

    public void setSbp(int sbp) {
        this.sbp = sbp;
    }

    public int getDbp() {
        return dbp;
    }

    public void setDbp(int dbp) {
        this.dbp = dbp;
    }

    public int getBpTime() {
        return bpTime;
    }

    public void setBpTime(int bpTime) {
        this.bpTime = bpTime;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public float getMdt_SDNN() {
        return mdt_SDNN;
    }

    public void setMdt_SDNN(float mdt_SDNN) {
        this.mdt_SDNN = mdt_SDNN;
    }

    public float getMdt_RMSSD() {
        return mdt_RMSSD;
    }

    public void setMdt_RMSSD(float mdt_RMSSD) {
        this.mdt_RMSSD = mdt_RMSSD;
    }

    public float getMdt_PNN50() {
        return mdt_PNN50;
    }

    public void setMdt_PNN50(float mdt_PNN50) {
        this.mdt_PNN50 = mdt_PNN50;
    }

    public float getMdt_MEAN() {
        return mdt_MEAN;
    }

    public void setMdt_MEAN(float mdt_MEAN) {
        this.mdt_MEAN = mdt_MEAN;
    }

    public int getMdt_status() {
        return mdt_status;
    }

    public void setMdt_status(int mdt_status) {
        this.mdt_status = mdt_status;
    }

    public float getMdt_RESULT() {
        return mdt_RESULT;
    }

    public void setMdt_RESULT(float mdt_RESULT) {
        this.mdt_RESULT = mdt_RESULT;
    }

    public float getMdt_RELAX() {
        return mdt_RELAX;
    }

    public void setMdt_RELAX(float mdt_RELAX) {
        this.mdt_RELAX = mdt_RELAX;
    }

    public int getAvgSpo2() {
        return avgSpo2;
    }

    public void setAvgSpo2(int avgSpo2) {
        this.avgSpo2 = avgSpo2;
    }

    public int getMaxSpo2() {
        return maxSpo2;
    }

    public void setMaxSpo2(int maxSpo2) {
        this.maxSpo2 = maxSpo2;
    }

    public int getMinSpo2() {
        return minSpo2;
    }

    public void setMinSpo2(int minSpo2) {
        this.minSpo2 = minSpo2;
    }

    public int getTemperType() {
        return temperType;
    }

    public void setTemperType(int temperType) {
        this.temperType = temperType;
    }

    public int getTemperEnv() {
        return temperEnv;
    }

    public void setTemperEnv(int temperEnv) {
        this.temperEnv = temperEnv;
    }

    public int getTemperBody() {
        return temperBody;
    }

    public void setTemperBody(int temperBody) {
        this.temperBody = temperBody;
    }

    public int getTemperDef() {
        return temperDef;
    }

    public void setTemperDef(int temperDef) {
        this.temperDef = temperDef;
    }

    public int getTemperArm() {
        return temperArm;
    }

    public void setTemperArm(int temperArm) {
        this.temperArm = temperArm;
    }
}

