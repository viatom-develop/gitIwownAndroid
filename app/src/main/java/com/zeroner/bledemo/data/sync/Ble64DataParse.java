package com.zeroner.bledemo.data.sync;

import com.google.gson.Gson;

import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.TB_64_data;
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
 * 作者：hzy on 2017/8/10 16:01
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class Ble64DataParse{
    private int seq;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int min;
    private int second;
    private List<Integer> list;
    public static DateUtil date = new DateUtil();

//    23ff642500 05 fffefefa00f401 fffefef401ee02fffefeee02e803fffefee803e204fffefee204e204

//    23FF64 1E 00 04 11070F11052B 0000 0900 11071011142D09006F001107101116046F00



    public static void parseCtrl0(String result){
        String from= PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME)+"";
        IndexTable indexTable=new Gson().fromJson(result,IndexTable.class);
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
                TB_64_data data_64 = DataSupport.where("year=? and month=? and day=? and data_from=? "
                        , String.valueOf(tableItem.getYear())
                        , String.valueOf(tableItem.getMonth())
                        , String.valueOf(tableItem.getDay())
                        , from).order("time desc").findFirst(TB_64_data.class);
                int startSeq=tableItem.getStart_index();
                int endSeq=tableItem.getEnd_index();
                int begins=startSeq;
                if(data_64!=null){
                    startSeq = data_64.getSeq();
                }
                if(endSeq>1023 && startSeq<begins)
                    startSeq+=1024;
                KLog.d("testf1shuju","64需要的总数据: "+tableItem.getYear()+"-"+tableItem.getMonth()+"-"+tableItem.getDay()+"  原始: "+begins +" -- "+endSeq+"  已同步到的: "+startSeq);
                if (startSeq >= endSeq-1) {
                    continue;
                }
                KLog.e("testf1shuju","64有需要e同步的？？: ");
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
                    sum616264.setType(ByteUtil.bytesToInt(new byte[]{0x64}));
                    sum616264.setType_str("0x64");
                    sum616264.save();
                }else{
                    sum616264.setYear(dateUtil.getYear());
                    sum616264.setMonth(dateUtil.getMonth());
                    sum616264.setDay(dateUtil.getDay());
                    sum616264.setDate(dateUtil.getSyyyyMMddDate());
                    sum616264.setDate_time(dateUtil.getUnixTimestamp());
//                    sum616264.setSend_62(ByteUtil.byteArrayToString(b1));
//                    sum616264.setSum_62(endSeq - startSeq);
                    sum616264.setSend_cmd(sendS);
                    sum616264.setSum(tableItem.getEnd_index() - startSeq);
                    sum616264.setType(ByteUtil.bytesToInt(new byte[]{0x64}));
                    sum616264.setType_str("0x64");
                    sum616264.updateAll("date=? and send_cmd=?",dateUtil.getSyyyyMMddDate(),sendS);
                }
            }
        }
    }

//23ff64 f9 018c01110717090f30f952f8ffb56af8ff7182f8ff2d9af8ffe9b1f8ffa5c9f8ff61e1f8ff1df9f8ffd910f9ff9528f9ff5140f9ff0d58f9ffc96ff9ff8587f9ff419ff9fffdb6f9ffb9cef9ff75e6f9ff32fef9ffee15faffaa2dfaff6645faff225dfaffde74faff9a8cfaff56a4faff12bcfaffced3faff8aebfaff4603fbff021bfbffbe32fbff7a4afbff3662fbfff279fbffaf91fbff6ba9fbff27c1fbffe3d8fbff9ff0fbff5b08fcff1720fcffd337fcff8f4ffcff4b67fcff077ffcffc396fcff7faefcff3bc6fcfff7ddfcffb3f5fcff6f0dfdff2b25fdffe83cfdffa454fdff606cfdff1c84fdffd89bfdff94b3fdff50cbfdff

    public static Ble64DataParse parse(byte[] data){
       Ble64DataParse cmd64=new Ble64DataParse();
       List<Integer> list=new ArrayList<>();
        int seq= ByteUtil.bytesToInt(Arrays.copyOfRange(data, 5, 7 ));
        int year = ByteUtil.bytesToInt(Arrays.copyOfRange(data, 7, 8 )) + 2000;
       int month = ByteUtil.bytesToInt(Arrays.copyOfRange(data, 8, 9 ))+1;
       int day = ByteUtil.bytesToInt(Arrays.copyOfRange(data, 9, 10 ))+1;
       int hour = ByteUtil.bytesToInt(Arrays.copyOfRange(data, 10, 11 ))+1;
       int min = ByteUtil.bytesToInt(Arrays.copyOfRange(data, 11, 12 )) ;
       int second = ByteUtil.bytesToInt(Arrays.copyOfRange(data, 12, 13 ));
       for (int i=0;i<60;i++){
           if(data.length<15+2*i){
               continue;
           }
           int ecg= ByteUtil.bytesToInt(Arrays.copyOfRange(data, 13+2*i,15+2*i));
           list.add(ecg);
       }
       cmd64.setSeq(seq);
       cmd64.setYear(year);
       cmd64.setMonth(month);
       cmd64.setDay(day);
       cmd64.setHour(hour);
       cmd64.setMin(min);
       cmd64.setSecond(second);
       cmd64.setList(list);
       return cmd64;
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

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public List<Integer> getList() {
        return list;
    }

    public void setList(List<Integer> list) {
        this.list = list;
    }


}
