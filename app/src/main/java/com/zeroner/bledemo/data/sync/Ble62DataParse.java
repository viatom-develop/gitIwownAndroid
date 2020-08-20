package com.zeroner.bledemo.data.sync;

import android.util.Log;

import com.google.gson.Gson;

import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.TB_62_data;
import com.zeroner.bledemo.bean.sql.TB_sum_61_62_64;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.bluetooth.model.IndexTable;
import com.zeroner.blemidautumn.utils.ByteUtil;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 作者：hzy on 2017/7/4 11:10
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class Ble62DataParse {
    private int ctrl;
    private int seq;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int freq;
    private int num;
    private List<LongitudeAndLatitude> list;
    public static DateUtil date = new DateUtil();
    private String detail="";

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    //    23ff62 16 00 02 11000b 04 08 33 0000 2100 110716 00 00 00 21002800

    public static void parseCtrl0(String result){
        IndexTable indexTable=new Gson().fromJson(result,IndexTable.class);

        List<IndexTable.TableItem> tableItems=indexTable.getmTableItems();
        DateUtil todayDate = new DateUtil();
        if(tableItems.size()>0){
            String from= PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
            for (IndexTable.TableItem tableItem : tableItems) {
                if(tableItem.getYear()!=todayDate.getYear() && (tableItem.getYear()+1)!=todayDate.getYear()) {
                    continue;
                }
                if(tableItem.getStart_index()==tableItem.getEnd_index()){
                    continue;
                }
                TB_62_data data_62 = DataSupport.where("year=? and month=? and day=? and data_from=? "
                        , String.valueOf(tableItem.getYear())
                        , String.valueOf(tableItem.getMonth())
                        , String.valueOf(tableItem.getDay())
                        , from).order("time desc").findFirst(TB_62_data.class);
                int startSeq=tableItem.getStart_index();
                int endSeq=tableItem.getEnd_index();
                int begins=startSeq;
                if(data_62!=null){
                    startSeq = data_62.getSeq();
                }
                if(endSeq>1023 && startSeq<begins)
                    startSeq+=1024;
                KLog.d("testf1shuju","62需要的总数据: "+tableItem.getYear()+"-"+tableItem.getMonth()+"-"+tableItem.getDay()+"  原始: "+begins +" -- "+endSeq+"  已同步到的: "+startSeq);
                if (startSeq >= endSeq-1) {
                    continue;
                }
                KLog.e("testf1shuju","62有需要e同步的？？: ");
                byte[] b1 = new byte[4];
                b1[0] = (byte) (startSeq & 0xff);
                b1[1] = (byte) (startSeq >>> 8);
                b1[2] = (byte) (endSeq & 0xff);
                b1[3] = (byte) (endSeq >>> 8);
                DateUtil dateUtil = new DateUtil(tableItem.getYear(),tableItem.getMonth(),tableItem.getDay());
                String sendS= ByteUtil.byteArrayToString(b1);
                TB_sum_61_62_64 sum616264 = DataSupport.where("date=? and send_cmd=?",dateUtil.getSyyyyMMddDate(),sendS).findFirst(TB_sum_61_62_64.class);
                if(sum616264==null) {
                    sum616264 = new TB_sum_61_62_64();
                    sum616264.setDate(dateUtil.getSyyyyMMddDate());
                    sum616264.setDate_time(dateUtil.getUnixTimestamp());
//                    sum616264.setSend_62(ByteUtil.byteArrayToString(b1));
//                    sum616264.setSum_62(endSeq - startSeq);
                    sum616264.setSend_cmd(sendS);
                    sum616264.setSum(tableItem.getEnd_index() - startSeq);
                    sum616264.setYear(tableItem.getYear());
                    sum616264.setMonth(tableItem.getMonth());
                    sum616264.setDay(tableItem.getDay());
                    sum616264.setType(ByteUtil.bytesToInt(new byte[]{0x62}));
                    sum616264.setType_str("0x62");
                    sum616264.save();
                }else{
                    sum616264.setYear(dateUtil.getYear());
                    sum616264.setMonth(dateUtil.getMonth());
                    sum616264.setDay(dateUtil.getDay());
                    sum616264.setDate(dateUtil.getSyyyyMMddDate());
                    sum616264.setDate_time(dateUtil.getUnixTimestamp());
                    sum616264.setSend_cmd(sendS);
                    sum616264.setSum(tableItem.getEnd_index() - startSeq);
                    sum616264.setType(ByteUtil.bytesToInt(new byte[]{0x62}));
                    sum616264.setType_str("0x62");
                    sum616264.updateAll("date=? and send_cmd=?",dateUtil.getSyyyyMMddDate(),sendS);
                }
            }
        }
    }

    public static Ble62DataParse parse(byte[] datas) {
        Ble62DataParse ble62DataParse = new Ble62DataParse();
        List<LongitudeAndLatitude> list = new ArrayList<>();
        int ctrl = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 4, 5));
        int seq = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 5, 7));
        int year = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 7, 8)) + 2000;
        int month = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 8, 9)) + 1;
        int day = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 9, 10)) + 1;
        int hour = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 10, 11));
        int min = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 11, 12));
        int freq = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 12, 13));
        int num = ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 13, 14));
        for (int i = 0; i < num; i++) {
            GnssData gnssData = new GnssData();
            gnssData.setLongitude_degree(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 14 + 14 * i, 15 + 14 * i)));
            gnssData.setLongitude_minute(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 15 + 14 * i, 16 + 14 * i)));
            gnssData.setLongitude_second(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 16 + 14 * i, 17 + 14 * i)));
            gnssData.setLongitude_preci(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 17 + 14 * i, 18 + 14 * i)));
            gnssData.setLongitude_direction(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 18 + 14 * i, 19 + 14 * i)));
            gnssData.setLatitude_degree(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 19 + 14 * i, 20 + 14 * i)));
            gnssData.setLatitude_minute(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 20 + 14 * i, 21 + 14 * i)));
            gnssData.setLatitude_second(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 21 + 14 * i, 22 + 14 * i)));
            gnssData.setLatitude_preci(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 22 + 14 * i, 23 + 14 * i)));
            gnssData.setLatitude_direction(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 23 + 14 * i, 24 + 14 * i)));
            gnssData.setGps_speed(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 24 + 14 * i, 26 + 14 * i)));
            gnssData.setAltitude(ByteUtil.bytesToInt(Arrays.copyOfRange(datas, 26 + 14 * i, 28 + 14 * i)));
            if (gnssData.getLongitude_direction() == 0) {
                gnssData.setLongitude_direction(1);
            } else if (gnssData.getLongitude_direction() == 1) {
                gnssData.setLongitude_direction(-1);
            }

            if (gnssData.getLatitude_direction() == 0) {
                gnssData.setLatitude_direction(1);
            } else if (gnssData.getLatitude_direction() == 1) {
                gnssData.setLatitude_direction(-1);
            }
            double longitude =
                    gnssData.getLongitude_direction() *
                            (gnssData.getLongitude_degree() + gnssData.getLongitude_minute() / 60.0f + (gnssData.getLongitude_second() + gnssData.getLongitude_preci() / 100.0f) / 3600.0f);
            double latitude = gnssData.getLatitude_direction() * (gnssData.getLatitude_degree() + gnssData.getLatitude_minute() / 60.0f + (gnssData.getLatitude_second() + gnssData.getLatitude_preci() / 100.0f) / 3600.0f);
            Log.d("testgps","longitude: "+longitude+"  latitude: "+latitude);
            LongitudeAndLatitude loLa = new LongitudeAndLatitude();
            loLa.setLongitude(longitude);
            loLa.setLatitude(latitude);
            loLa.setGps_speed(gnssData.getGps_speed());
            loLa.setAltitude(gnssData.getAltitude());
            list.add(loLa);
        }
        String message=new Gson().toJson(list);
        Log.d("testgps","解析后数据: "+message);
        ble62DataParse.setCtrl(ctrl);
        ble62DataParse.setSeq(seq);
        ble62DataParse.setYear(year);
        ble62DataParse.setMonth(month);
        ble62DataParse.setDay(day);
        ble62DataParse.setHour(hour);
        ble62DataParse.setMin(min);
        ble62DataParse.setFreq(freq);
        ble62DataParse.setNum(num);
//        ble62DataParse.setList(list);
        ble62DataParse.setDetail(message);
        return ble62DataParse;
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

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public List<LongitudeAndLatitude> getList() {
        return list;
    }

    public void setList(List<LongitudeAndLatitude> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Ble62DataParse{" +
                "ctrl=" + ctrl +
                ", seq=" + seq +
                ", year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", min=" + min +
                ", freq=" + freq +
                ", num=" + num +
                ", list=" + list +
                '}';
    }
}
