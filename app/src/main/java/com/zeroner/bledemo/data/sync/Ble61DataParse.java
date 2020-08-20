package com.zeroner.bledemo.data.sync;

import android.util.Log;

import com.google.gson.Gson;

import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.TB_61_data;
import com.zeroner.bledemo.bean.sql.TB_f1_index;
import com.zeroner.bledemo.bean.sql.TB_sum_61_62_64;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.model.IndexTable;
import com.zeroner.blemidautumn.utils.ByteUtil;

import org.litepal.crud.DataSupport;

import java.util.Arrays;
import java.util.List;

/**
 * 作者：hzy on 2017/7/3 14:23
 * <p>
 * 邮箱：hezhiyuan@iwown.com.
 * <p>
 * 23FF61 33 01 1900 1107030A10 21 0000 0000 0000 0100 00 00 4300 8400 8300 FF00 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
 * <p>
 * 61取0时
 * 23ff61 64 00 03 11030e 0000 0200 11030f 0300 0400 11030e 0500 8f000 000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000
 * ble61DataParseBle61DataParse
 * {seq=31, year=2017, month=7, day=3, hour=10,
 * min=22, data_type=33, sport_type=0, calorie=0.0,
 * step=0, distance=1.0, state_type=0, reserve=0, min_bpm=109,
 * max_bpm=140, avg_bpm=130, level=255, sdnn=0, lf=0, hf=0, lf_hf=0,
 * bpm_hr=0, sbp=0, dbp=0, bpm=0, data0=0, data1=0, data2=0, data3=0, data4=0}
 *
 * 61最大能存的seq为4095，后面的从0开始
 */

public class Ble61DataParse {
    private int ctrl;
    private int seq;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int data_type;
    private int sport_type;
    private float calorie;
    private int step;
    private float distance;
    private int state_type;
    private int reserve;

    //    心率
    private int min_bpm;
    private int max_bpm;
    private int avg_bpm;
    private int level;

    //    心率变性性
    private int sdnn;
    private int lf;
    private int hf;
    private int lf_hf;
    private int bpm_hr;

    //    血压
    private int sbp;
    private int dbp;
    private int bpm;

//    算法数据

    private int data0;
    private int data1;
    private int data2;
    private int data3;
    private int data4;
    private int data5;
    private int data6;
    private int data7;
    private int data8;
    private int data9;
    private int data10;
    private int automaticMin;

    public static DateUtil date = new DateUtil();

    /**
     * 23ff61 33 00 07 1100019d04bf05 11061ac005b807 11051ab807070d 11051b070d5001 11061c51013b02 11061d3b024d03 11061e4d03ad03
     *
     * 23FF611301110A11070F12111000000100000000000000
     *
     * 23FF611D01090A11070F113B3000002900E9000100000010005200220002000000
     *
     * 23FF611001020A11070F0B140140004000400005
     *
     * 23ff61 3e 00 06 11000000000000005c00 1107100f04125c000401 11071100000004016c02 1107120000006c02ec02 110713000000ec02f803 110714000000f8033005
     *
     */

    public static void parseCtrl0(String result){
        String from= PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        IndexTable indexTable=new Gson().fromJson(result,IndexTable.class);
        Log.e("licl","获取到61 -:"+new Gson().toJson(indexTable));
        List<IndexTable.TableItem> tableItems=indexTable.getmTableItems();
        DateUtil todayDate = new DateUtil();
        if(tableItems.size()>0){
            for (IndexTable.TableItem tableItem : tableItems) {
                if(tableItem.getYear()!=todayDate.getYear() && (tableItem.getYear()+1)!=todayDate.getYear()) {
                    continue;
                }
                if(tableItem.getStart_index()==tableItem.getEnd_index()){
                    continue;
                }
                int mfend=tableItem.getEnd_index();
                if(mfend>4095){
                    mfend=mfend-4096;
                }
                DateUtil dateUtil = new DateUtil(tableItem.getYear(),tableItem.getMonth(),tableItem.getDay());
                long fTime=dateUtil.getUnixTimestamp();
                //TB_f1_index为了防止数据的日期与seq对不上问题，导致每次刷新都有几天数据
                TB_f1_index f1Index = DataSupport.where("start_seq=? and end_seq=? and data_from=?",tableItem.getStart_index()+"",mfend+"",from).findFirst(TB_f1_index.class);
                //防止今天的数据一直从0开始获取
                if(dateUtil.getSyyyyMMddDate().equals(todayDate.getSyyyyMMddDate())){
                    DataSupport.deleteAll(TB_f1_index.class,"data_from=? and date=?",from,todayDate.getSyyyyMMddDate());
                    if(f1Index==null)
                        f1Index=new TB_f1_index();
                    f1Index.setDate(dateUtil.getSyyyyMMddDate());
                    f1Index.setData_from(from);
                    f1Index.setTime(fTime);
                    f1Index.setStart_seq(tableItem.getStart_index());
                    f1Index.setEnd_seq(mfend);
                    f1Index.setOk(0);
                    f1Index.setType("61");
                    f1Index.save();
                }
                int begins = tableItem.getStart_index();
                int startSeq=tableItem.getStart_index();
                if(f1Index==null){
                    f1Index=new TB_f1_index();
                    f1Index.setDate(dateUtil.getSyyyyMMddDate());
                    f1Index.setData_from(from);
                    f1Index.setStart_seq(tableItem.getStart_index());
                    f1Index.setEnd_seq(mfend);
                    f1Index.setOk(0);
                    f1Index.setType("61");
                    f1Index.save();
                }else{
                    if(f1Index.getOk()==1){
                        continue;
                    }else{
                        TB_61_data data_61 = DataSupport.where("year=? and month=? and day=? and data_from=? "
                                , String.valueOf(tableItem.getYear())
                                , String.valueOf(tableItem.getMonth())
                                , String.valueOf(tableItem.getDay())
                                , from).order("time desc").findFirst(TB_61_data.class);

                        if(data_61!=null){
                            startSeq = data_61.getSeq();
                        }
                        if(tableItem.getEnd_index()>4095 && startSeq<begins)
                            startSeq+=4096;
                        if(tableItem.getEnd_index()<4095 && startSeq<begins){
                            KLog.e("testf1shuju11","date与seq存在异常"+startSeq+" - "+begins);
                            startSeq=begins;
                        }
                        if (startSeq >= tableItem.getEnd_index()-1) {
                            continue;
                        }
                    }
                }
                KLog.e("testf1shuju","61有需要e同步的: "+tableItem.getYear()+"-"+tableItem.getMonth()+"-"+tableItem.getDay()+"  原始: "+begins +" -- "+tableItem.getEnd_index()+"  已同步到的: "+startSeq);

                byte[] b = new byte[4];
                b[0] = (byte) (startSeq & 0xff);
                b[1] = (byte) (startSeq >>> 8);
                b[2] = (byte) (tableItem.getEnd_index() & 0xff);
                b[3] = (byte) (tableItem.getEnd_index() >>> 8);
                String sendS= ByteUtil.byteArrayToString(b);
                TB_sum_61_62_64 sum616264 = DataSupport.where("date=? and send_cmd=?",dateUtil.getSyyyyMMddDate(),sendS).findFirst(TB_sum_61_62_64.class);
                if(sum616264==null) {
                    sum616264 = new TB_sum_61_62_64();
                    sum616264.setDate(dateUtil.getSyyyyMMddDate());
                    sum616264.setDate_time(dateUtil.getUnixTimestamp());
//                    sum616264.setSend_61(ByteUtil.byteArrayToString(b));
//                    sum616264.setSum_61(tableItem.getEnd_index() - startSeq);
                    sum616264.setSend_cmd(sendS);
                    sum616264.setSum(tableItem.getEnd_index() - startSeq);
                    sum616264.setYear(dateUtil.getYear());
                    sum616264.setMonth(dateUtil.getMonth());
                    sum616264.setDay(dateUtil.getDay());
                    sum616264.setType(ByteUtil.bytesToInt(new byte[]{0x61}));
                    sum616264.setType_str("0x61");
                    sum616264.save();
                }else{
                    sum616264.setYear(dateUtil.getYear());
                    sum616264.setMonth(dateUtil.getMonth());
                    sum616264.setDay(dateUtil.getDay());
                    sum616264.setDate(dateUtil.getSyyyyMMddDate());
                    sum616264.setDate_time(dateUtil.getUnixTimestamp());
                    sum616264.setSend_cmd(sendS);
                    sum616264.setSum(tableItem.getEnd_index() - startSeq);
                    sum616264.setType(ByteUtil.bytesToInt(new byte[]{0x61}));
                    sum616264.setType_str("0x61");
//                    sum616264.setSend_61(ByteUtil.byteArrayToString(b));
                    sum616264.updateAll("date=? and send_cmd=?",dateUtil.getSyyyyMMddDate(),sendS);
                }
            }
        }

    }

    public static Ble61DataParse parse1(String result){
        Ble61DataParse ble61DataParse = new Ble61DataParse();

        return ble61DataParse;
    }

    public static Ble61DataParse parse(byte[] datas) {
//        23FF611D01090A 11070F 113B 30 00002900E90001000000 10005200220002000000
//        23FF611301110A 11070F 1211 10 0000010000 0000000000
//        23FF611001020A 11070F 0B14 01 40004000400005

        Ble61DataParse ble61DataParse = new Ble61DataParse();
        int ctrl = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 4, 5));
        ble61DataParse.setCtrl(ctrl);
        int seq = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 5, 7));
        ble61DataParse.setSeq(seq);
        int year = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 7, 8)) + 2000;
        ble61DataParse.setYear(year);
        int month = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 8, 9)) + 1;
        ble61DataParse.setMonth(month);
        int day = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 9, 10)) + 1;
        ble61DataParse.setDay(day);
        int hour = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 10, 11));
        ble61DataParse.setHour(hour);
        int min = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 11, 12));
        ble61DataParse.setMin(min);
        int data_type = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 12, 13));
        ble61DataParse.setData_type(data_type);
        int index=13;
        byte [] data= ByteUtil.byteToBitArray(data_type);

        if(data[2]==1){
            byte[] pedoData= Arrays.copyOfRange(datas, index, index+10);
//       pedo
            float calorie = ByteUtil.bytesToInt(Arrays.copyOfRange(pedoData, 0, 2)) * 0.1f;
//            calorie = Math.round(calorie * 10) / 10;
            ble61DataParse.setCalorie(calorie);
            int step = ByteUtil.bytesToInt(Arrays.copyOfRange(pedoData, 2, 4));
            ble61DataParse.setStep(step);
            float distance = ByteUtil.bytesToInt(Arrays.copyOfRange(pedoData, 4, 6)) * 0.1f;
//            distance = Math.round(distance * 100) / 100;
            ble61DataParse.setDistance(distance);
            int sport_type = ByteUtil.bytesToInt(Arrays.copyOfRange(pedoData, 6, 8));
            ble61DataParse.setSport_type(sport_type);
            int automaticMin= ByteUtil.byteToInt((byte)(pedoData[8]>>4));
            int state_type = ByteUtil.byteToInt((byte)(pedoData[8]&0x0f));
            ble61DataParse.setState_type(state_type);
            ble61DataParse.setAutomaticMin(automaticMin);

            int reserve = ByteUtil.bytesToInt(Arrays.copyOfRange(pedoData, 9, 10));
            ble61DataParse.setReserve(reserve);
            index+=10;
        }
        if(data[7]==1){
            //hr
            byte[] hrData= Arrays.copyOfRange(datas, index, index+7);
            int min_bpm = ByteUtil.bytesToInt(Arrays.copyOfRange(hrData, 0, 2));
            ble61DataParse.setMin_bpm(min_bpm);
            int max_bpm = ByteUtil.bytesToInt(Arrays.copyOfRange(hrData, 2, 4));
            ble61DataParse.setMax_bpm(max_bpm);
            int avg_bpm = ByteUtil.bytesToInt(Arrays.copyOfRange(hrData, 4, 6));
            ble61DataParse.setAvg_bpm(avg_bpm);
            int level = ByteUtil.bytesToInt(Arrays.copyOfRange(hrData, 6, 7));
            ble61DataParse.setLevel(level);
            index+=7;
        }
        if(data[6]==1){
            //hrv
            byte[] hrvData= Arrays.copyOfRange(datas, index, index+14);
            int sdnn = ByteUtil.bytesToInt(Arrays.copyOfRange(hrvData, 0, 2));
            ble61DataParse.setSdnn(sdnn);
            int lf = ByteUtil.bytesToInt(Arrays.copyOfRange(hrvData, 2, 6));
            ble61DataParse.setLf(lf);
            int hf = ByteUtil.bytesToInt(Arrays.copyOfRange(hrvData, 6, 10));
            ble61DataParse.setHf(hf);
            int lf_hf = ByteUtil.bytesToInt(Arrays.copyOfRange(hrvData, 10, 12));
            ble61DataParse.setLf_hf(lf_hf);
            int bpm_hr = ByteUtil.bytesToInt(Arrays.copyOfRange(hrvData, 12, 14));
            ble61DataParse.setBpm_hr(bpm_hr);
            index+=14;
        }
        if(data[5]==1){
            //bp
            byte[] bpData= Arrays.copyOfRange(datas, index, index+6);
            int sbp = ByteUtil.bytesToInt(Arrays.copyOfRange(bpData, 0, 2));
            ble61DataParse.setSbp(sbp);
            int dbp = ByteUtil.bytesToInt(Arrays.copyOfRange(bpData, 2, 4));
            ble61DataParse.setDbp(dbp);
            int bpm = ByteUtil.bytesToInt(Arrays.copyOfRange(bpData, 4, 6));
            ble61DataParse.setBpm(bpm);
            index+=6;
        }

        return ble61DataParse;
    }

    public int getData5() {
        return data5;
    }

    public void setData5(int data5) {
        this.data5 = data5;
    }

    public int getData6() {
        return data6;
    }

    public void setData6(int data6) {
        this.data6 = data6;
    }

    public int getData7() {
        return data7;
    }

    public void setData7(int data7) {
        this.data7 = data7;
    }

    public int getData8() {
        return data8;
    }

    public void setData8(int data8) {
        this.data8 = data8;
    }

    public int getData9() {
        return data9;
    }

    public void setData9(int data9) {
        this.data9 = data9;
    }

    public int getData10() {
        return data10;
    }

    public void setData10(int data10) {
        this.data10 = data10;
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

    public int getState_type() {
        return state_type;
    }

    public void setState_type(int state_type) {
        this.state_type = state_type;
    }

    public int getReserve() {
        return reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSdnn() {
        return sdnn;
    }

    public void setSdnn(int sdnn) {
        this.sdnn = sdnn;
    }

    public int getLf() {
        return lf;
    }

    public void setLf(int lf) {
        this.lf = lf;
    }

    public int getHf() {
        return hf;
    }

    public void setHf(int hf) {
        this.hf = hf;
    }

    public int getLf_hf() {
        return lf_hf;
    }

    public void setLf_hf(int lf_hf) {
        this.lf_hf = lf_hf;
    }

    public int getBpm_hr() {
        return bpm_hr;
    }

    public void setBpm_hr(int bpm_hr) {
        this.bpm_hr = bpm_hr;
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

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
    }

    public int getData0() {
        return data0;
    }

    public void setData0(int data0) {
        this.data0 = data0;
    }

    public int getData1() {
        return data1;
    }

    public void setData1(int data1) {
        this.data1 = data1;
    }

    public int getData2() {
        return data2;
    }

    public void setData2(int data2) {
        this.data2 = data2;
    }

    public int getData3() {
        return data3;
    }

    public void setData3(int data3) {
        this.data3 = data3;
    }

    public int getData4() {
        return data4;
    }

    public void setData4(int data4) {
        this.data4 = data4;
    }

    public int getAutomaticMin() {
        return automaticMin;
    }

    public void setAutomaticMin(int automaticMin) {
        this.automaticMin = automaticMin;
    }

    @Override
    public String toString() {
        return "Ble61DataParse{" +
                "seq=" + seq +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", min=" + min +
                ", data_type=" + data_type +
                ", sport_type=" + sport_type +
                ", calorie=" + calorie +
                ", step=" + step +
                ", distance=" + distance +
                ", state_type=" + state_type +
                ", reserve=" + reserve +
                ", min_bpm=" + min_bpm +
                ", max_bpm=" + max_bpm +
                ", avg_bpm=" + avg_bpm +
                ", level=" + level +
                ", sdnn=" + sdnn +
                ", lf=" + lf +
                ", hf=" + hf +
                ", lf_hf=" + lf_hf +
                ", bpm_hr=" + bpm_hr +
                ", sbp=" + sbp +
                ", dbp=" + dbp +
                ", bpm=" + bpm +
                ", data0=" + data0 +
                ", data1=" + data1 +
                ", data2=" + data2 +
                ", data3=" + data3 +
                ", data4=" + data4 +
                ", automaticMin=" + automaticMin +
                '}';
    }
}
