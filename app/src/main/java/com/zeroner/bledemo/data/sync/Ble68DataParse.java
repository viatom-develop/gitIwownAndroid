package com.zeroner.bledemo.data.sync;

import com.google.gson.Gson;
import com.socks.library.KLog;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.DataIndex_68;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.model.IndexTable;
import com.zeroner.blemidautumn.utils.ByteUtil;

public class Ble68DataParse {
    private int ctrl;
    private int seq;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int seconds;
    private int data_type;

    private int sport_type;
    private int state_type;
    private int step;
    private int distance;
    private int calorie;
    private int rateOfStride_min;
    private int rateOfStride_max;
    private int rateOfStride_avg;
    private int flight_min;
    private int flight_max;
    private int flight_avg;
    private int touchDown_min;
    private int touchDown_max;
    private int touchDown_avg;
    private int touchDownPower_min;
    private int touchDownPower_max;
    private int touchDownPower_avg;
    private int touchDownPower_balance;
    private int touchDownPower_stop;

    private int min_hr;
    private int max_hr;
    private int avg_hr;

    public static DateUtil date = new DateUtil();

    public static List<DataIndex_68> parseCtrl0(String result){
        String data_from= PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";

        IndexTable indexTable=new Gson().fromJson(result,IndexTable.class);
        if(indexTable==null || indexTable.getmTableItems() == null)
            return null;

        List<DataIndex_68> dataIndexList = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-60);
        Date benchmarkDate = cal.getTime();

        KLog.e(String.format("---device to proceed 68:%s, index count:%d", data_from,
                indexTable.getmTableItems().size()));
        for (IndexTable.TableItem tableItem : indexTable.getmTableItems()){

            KLog.e(String.format("----index date:%d-%d-%d",
                    tableItem.getYear(),
                    tableItem.getMonth(),
                    tableItem.getDay()));

            Calendar indexCal = Calendar.getInstance();
            indexCal.set(Calendar.YEAR, tableItem.getYear());
            indexCal.set(Calendar.MONTH, tableItem.getMonth());
            indexCal.set(Calendar.DAY_OF_MONTH, tableItem.getDay());
            Date indexDate = indexCal.getTime();
            if(indexDate.before(benchmarkDate)) {
                continue;
            }

            if(tableItem.getStart_index()==tableItem.getEnd_index()){
                continue;
            }
            //check whether index already in db
            List<DataIndex_68> dbIndexList = DataSupport.where("device_name=? and year=? " +
                            "and month=? and day=? and start_idx=? and end_idx=?",
                    data_from,
                    String.valueOf(tableItem.getYear()),
                    String.valueOf(tableItem.getMonth()),
                    String.valueOf(tableItem.getDay()),
                    String.valueOf(tableItem.getStart_index()),
                    String.valueOf(tableItem.getEnd_index())).find(DataIndex_68.class);
            if(dbIndexList != null && dbIndexList.size()>0) {
                KLog.e("---index proceed before");
                continue;
            }

            DataIndex_68 dataIndex = new DataIndex_68();
            dataIndex.setDevice_name(data_from);
            dataIndex.setYear(tableItem.getYear());
            dataIndex.setMonth(tableItem.getMonth());
            dataIndex.setDay(tableItem.getDay());
            dataIndex.setStart_idx(tableItem.getStart_index());
            dataIndex.setEnd_idx(tableItem.getEnd_index());
            dataIndex.setProcessed(0);

            byte[] cmdBytes = new byte[4];
            cmdBytes[0] = (byte) (tableItem.getStart_index() & 0xff);
            cmdBytes[1] = (byte) (tableItem.getStart_index() >>> 8);
            cmdBytes[2] = (byte) (tableItem.getEnd_index() & 0xff);
            cmdBytes[3] = (byte) (tableItem.getEnd_index() >>> 8);
            String sendCmd= ByteUtil.byteArrayToString(cmdBytes);
            dataIndex.setSend_cmd(sendCmd);
            dataIndexList.add(dataIndex);
            dataIndex.saveOrUpdate("device_name=? and year=? and month=? and day=? " +
                    "and start_idx=? and end_idx=?", data_from,
                    String.valueOf(dataIndex.getYear()), String.valueOf(dataIndex.getMonth()),
                    String.valueOf(dataIndex.getDay()),
                    String.valueOf(dataIndex.getStart_idx()),
                    String.valueOf(dataIndex.getEnd_idx()));
        }

        return dataIndexList;
    }


    public static Ble68DataParse parse(byte[] datas) {

        Ble68DataParse ble68DataParse = new Ble68DataParse();
        int ctrl = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 4, 5));
        ble68DataParse.setCtrl(ctrl);
        int seq = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 5, 7));
        ble68DataParse.setSeq(seq);
        int year = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 7, 8)) + 2000;
        ble68DataParse.setYear(year);
        int month = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 8, 9)) + 1;
        ble68DataParse.setMonth(month);
        int day = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 9, 10)) + 1;
        ble68DataParse.setDay(day);
        int hour = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 10, 11));
        ble68DataParse.setHour(hour);
        int min = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 11, 12));
        ble68DataParse.setMin(min);
        int seconds = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 12, 13));
        ble68DataParse.setSeconds(seconds);
        int data_type = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 13, 14));
        ble68DataParse.setData_type(data_type);
        int index=14;
        byte [] data= ByteUtil.byteToBitArray(data_type);

        if(data[2]==1 && data.length>=51){
            byte[] walkDataBytes= Arrays.copyOfRange(datas, index, index+37);
            int sport_type = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 0, 2));
            ble68DataParse.setSport_type(sport_type);
            int state_type = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 2, 3));
            ble68DataParse.setState_type(state_type);
            int step = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 3, 5));
            ble68DataParse.setStep(step);
            int distance = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 5, 7));
            ble68DataParse.setDistance(distance);
            int calorie = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 7, 9));
            ble68DataParse.setCalorie(calorie);
            int rateOfStride_min = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 9, 11));
            ble68DataParse.setRateOfStride_min(rateOfStride_min);
            int rateOfStride_max = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 11, 13));
            ble68DataParse.setRateOfStride_max(rateOfStride_max);
            int rateOfStride_avg = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 13, 15));
            ble68DataParse.setRateOfStride_avg(rateOfStride_avg);
            int flight_min = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 15, 17));
            ble68DataParse.setFlight_min(flight_min);
            int flight_max = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 17, 19));
            ble68DataParse.setFlight_max(flight_max);
            int flight_avg = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 19, 21));
            ble68DataParse.setFlight_avg(flight_avg);
            int touchdown_min = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 21, 23));
            ble68DataParse.setTouchDown_min(touchdown_min);
            int touchdown_max = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 23, 25));
            ble68DataParse.setTouchDown_max(touchdown_max);
            int touchdown_avg = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 25, 27));
            ble68DataParse.setTouchDown_avg(touchdown_avg);
            int touchdownpower_min = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 27, 29));
            ble68DataParse.setTouchDownPower_min(touchdownpower_min);
            int touchdownpower_max = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 29, 31));
            ble68DataParse.setTouchDownPower_max(touchdownpower_max);
            int touchdownpower_avg = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 31, 33));
            ble68DataParse.setTouchDownPower_avg(touchdownpower_avg);
            int touchdownpower_balance = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 33, 35));
            ble68DataParse.setTouchDownPower_balance(touchdownpower_balance);
            int touchdownpower_stop = ByteUtil.bytesToInt(Arrays.copyOfRange(walkDataBytes, 35, 37));
            ble68DataParse.setTouchDownPower_stop(touchdownpower_stop);
        }
        if(data[7]==1){
            //hr
            if(data[2]!=1 || datas.length<51){
                index=14;
            }
            else{
                index=14+37;
            }
            byte[] hrDataBytes= Arrays.copyOfRange(datas, index, index+6);
            int min_hr = ByteUtil.bytesToInt(Arrays.copyOfRange(hrDataBytes, 0, 2));
            int max_hr = ByteUtil.bytesToInt(Arrays.copyOfRange(hrDataBytes, 2, 4));
            int avg_hr = ByteUtil.bytesToInt(Arrays.copyOfRange(hrDataBytes, 4, 6));

            ble68DataParse.setMax_hr(max_hr);
            ble68DataParse.setAvg_hr(avg_hr);
            ble68DataParse.setMin_hr(min_hr);
        }

        return ble68DataParse;
    }

    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getCtrl() {
        return ctrl;
    }

    public void setCtrl(int ctrl) {
        this.ctrl = ctrl;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

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

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getData_type() {
        return data_type;
    }

    public void setData_type(int data_type) {
        this.data_type = data_type;
    }

    public int getSport_type() {
        return sport_type;
    }

    public void setSport_type(int sport_type) {
        this.sport_type = sport_type;
    }

    public int getState_type() {
        return state_type;
    }

    public void setState_type(int state_type) {
        this.state_type = state_type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCalorie() {
        return calorie;
    }

    public void setCalorie(int calorie) {
        this.calorie = calorie;
    }

    public int getRateOfStride_min() {
        return rateOfStride_min;
    }

    public void setRateOfStride_min(int rateOfStride_min) {
        this.rateOfStride_min = rateOfStride_min;
    }

    public int getRateOfStride_max() {
        return rateOfStride_max;
    }

    public void setRateOfStride_max(int rateOfStride_max) {
        this.rateOfStride_max = rateOfStride_max;
    }

    public int getRateOfStride_avg() {
        return rateOfStride_avg;
    }

    public void setRateOfStride_avg(int rateOfStride_avg) {
        this.rateOfStride_avg = rateOfStride_avg;
    }

    public int getFlight_min() {
        return flight_min;
    }

    public void setFlight_min(int flight_min) {
        this.flight_min = flight_min;
    }

    public int getFlight_max() {
        return flight_max;
    }

    public void setFlight_max(int flight_max) {
        this.flight_max = flight_max;
    }

    public int getFlight_avg() {
        return flight_avg;
    }

    public void setFlight_avg(int flight_avg) {
        this.flight_avg = flight_avg;
    }

    public int getTouchDown_min() {
        return touchDown_min;
    }

    public void setTouchDown_min(int touchDown_min) {
        this.touchDown_min = touchDown_min;
    }

    public int getTouchDown_max() {
        return touchDown_max;
    }

    public void setTouchDown_max(int touchDown_max) {
        this.touchDown_max = touchDown_max;
    }

    public int getTouchDown_avg() {
        return touchDown_avg;
    }

    public void setTouchDown_avg(int touchDown_avg) {
        this.touchDown_avg = touchDown_avg;
    }

    public int getTouchDownPower_min() {
        return touchDownPower_min;
    }

    public void setTouchDownPower_min(int touchDownPower_min) {
        this.touchDownPower_min = touchDownPower_min;
    }

    public int getTouchDownPower_max() {
        return touchDownPower_max;
    }

    public void setTouchDownPower_max(int touchDownPower_max) {
        this.touchDownPower_max = touchDownPower_max;
    }

    public int getTouchDownPower_avg() {
        return touchDownPower_avg;
    }

    public void setTouchDownPower_avg(int touchDownPower_avg) {
        this.touchDownPower_avg = touchDownPower_avg;
    }

    public int getTouchDownPower_balance() {
        return touchDownPower_balance;
    }

    public void setTouchDownPower_balance(int touchDownPower_balance) {
        this.touchDownPower_balance = touchDownPower_balance;
    }

    public int getTouchDownPower_stop() {
        return touchDownPower_stop;
    }

    public void setTouchDownPower_stop(int touchDownPower_stop) {
        this.touchDownPower_stop = touchDownPower_stop;
    }

    public int getMin_hr() {
        return min_hr;
    }

    public void setMin_hr(int min_hr) {
        this.min_hr = min_hr;
    }

    public int getMax_hr() {
        return max_hr;
    }

    public void setMax_hr(int max_hr) {
        this.max_hr = max_hr;
    }

    public int getAvg_hr() {
        return avg_hr;
    }

    public void setAvg_hr(int avg_hr) {
        this.avg_hr = avg_hr;
    }

    public static DateUtil getDate() {
        return date;
    }

    public static void setDate(DateUtil date) {
        Ble68DataParse.date = date;
    }

    @Override
    public String toString() {
        return "";
    }
}
