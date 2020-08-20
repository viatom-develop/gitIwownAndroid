package com.zeroner.bledemo.data;

import com.zeroner.bledemo.bean.sql.ProtoBuf_80_data;
import com.zeroner.bledemo.bean.sql.TB_spo2_data;
import com.zeroner.bledemo.bean.sql.TB_temper;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.blemidautumn.utils.JsonTool;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gavin
 * @date 2020-01-03
 */
public class PbTolvHandler {
    public static void pbDataToHeart( int year, int month, int day, String dataFrom,List<ProtoBuf_80_data> datas) {
        int mSize = 0;
        if(datas!=null){
            mSize=datas.size();
        }
        if (mSize > 0) {
            List<Integer> heart53 = new ArrayList<Integer>();
            int count53 = 0;
            int heartPre=0;
            for (int i = 0; i < mSize; i++) {
                if (i < mSize - 1) {
                    //取出一个小时的点
                    if (datas.get(i).getHour() != datas.get(i + 1).getHour()) {
                        for (int j = count53; j < 60; j++) {
                            int heart=datas.get(i + 1).getAvg_bpm();
                                if(j%10==1){
                                    heartPre=0;
                                }

                            if(heart!=0){
                                heart53.add(heart);
                                heartPre=heart;
                            }else {
                                heart53.add(heartPre);
                            }

                        }
                        SqlBizUtils.saveTb53Heart(year, month, day, datas.get(i).getHour(), heart53, dataFrom);
                        heart53 = new ArrayList<Integer>();
                        count53 = 0;
                    } else {
                        for (int j = count53; j <= datas.get(i).getMinute() && j < 60; j++) {
                            int heart=datas.get(i).getAvg_bpm();
                                if(j%10==1){
                                    heartPre=0;
                                }
                            if(heart!=0){
                                heart53.add(heart);
                                heartPre=heart;
                            }else {
                                heart53.add(heartPre);
                            }
                        }
                        count53 = datas.get(i).getMinute() + 1;
                    }
                } else {
                    for (int j = count53; j <= datas.get(i).getMinute() && j < 60; j++) {
                        int heart=datas.get(i).getAvg_bpm();
                        if(heart!=0){
                            heart53.add(heart);
                            heartPre=heart;
                        }else {
                            heart53.add(heartPre);
                        }
                    }
                    SqlBizUtils.saveTb53Heart(year, month, day, datas.get(i).getHour(), heart53, dataFrom);
                }
            }

        }
    }

    public static void pbToSpo2(List<ProtoBuf_80_data> datas,String dataFrom,long uid){
        if(datas.size() == 0){
            return;
        }
        for (ProtoBuf_80_data data: datas){
            if(data.getAvgSpo2() > 30 && data.getAvgSpo2() < 130){
                TB_spo2_data tbSpo2Data = new TB_spo2_data();
                DateUtil dateUtil = new DateUtil(data.getYear(), data.getMonth(),
                        data.getDay(), data.getHour(), data.getMinute(), data.getSecond());
                tbSpo2Data.setYear(data.getYear());
                tbSpo2Data.setMonth(data.getMonth());
                tbSpo2Data.setDay(data.getDay());
                tbSpo2Data.setHour(data.getHour());
                tbSpo2Data.setMinute(data.getMinute());
                tbSpo2Data.setSecond(data.getSecond());
                tbSpo2Data.setUid(uid);
                tbSpo2Data.setData_from(dataFrom);
                int[] spo2 = new int[1];
                spo2[0] = data.getAvgSpo2();
                tbSpo2Data.setRawData(JsonTool.toJson(spo2));
                tbSpo2Data.setCredibility(JsonTool.toJson(0));
                tbSpo2Data.setSeq(data.getSeq());
                tbSpo2Data.setTimeStamp(data.getTime());
                tbSpo2Data.setDate(dateUtil.getSyyyyMMddDate());
                tbSpo2Data.saveOrUpdate("uid=? and data_from=?  and year=? and month=? and day=? and hour=? and minute=? and second=? and seq=?"
                        , String.valueOf(uid)
                        , String.valueOf(dataFrom)
                        , String.valueOf(tbSpo2Data.getYear())
                        , String.valueOf(tbSpo2Data.getMonth() )
                        , String.valueOf(tbSpo2Data.getDay())
                        , String.valueOf(tbSpo2Data.getHour())
                        , String.valueOf(tbSpo2Data.getMinute())
                        , String.valueOf(tbSpo2Data.getSecond())
                        , String.valueOf(tbSpo2Data.getSeq()));
            }

        }


    }


    public static void pbToTemper(List<ProtoBuf_80_data> data, String dataFrom, long uid) {
        if(data.size() == 0){
            return;
        }
        for (ProtoBuf_80_data protoBuf80Data: data){
            if(protoBuf80Data.getTemperType() !=0) {
                TB_temper temper = new TB_temper();

                temper.setUid(uid);
                temper.setData_from(dataFrom);
                temper.setYear(protoBuf80Data.getYear());
                temper.setMonth(protoBuf80Data.getMonth());
                temper.setDay(protoBuf80Data.getDay());
                temper.setHour(protoBuf80Data.getHour());
                temper.setMinute(protoBuf80Data.getMinute());
                temper.setSecond(protoBuf80Data.getSecond());
                temper.setSeq(protoBuf80Data.getSeq());
                temper.setDate(new DateUtil(protoBuf80Data.getYear(),protoBuf80Data.getMonth(),protoBuf80Data.getDay()).getSyyyyMMddDate());
                temper.setTemperArm(protoBuf80Data.getTemperArm());
                temper.setTemperBody(protoBuf80Data.getTemperBody());
                temper.setTemperDef(protoBuf80Data.getTemperDef());
                temper.setTemperType(protoBuf80Data.getTemperType());
                temper.setTemperEnv(protoBuf80Data.getTemperEnv());
                temper.setTimeStamp(protoBuf80Data.getTime());
                temper.saveOrUpdate("uid=? and data_from=?  and year=? and month=? and day=? and hour=? and minute=? and second=? and seq=? and temperType!=0"
                        , String.valueOf(uid)
                        , String.valueOf(dataFrom)
                        , String.valueOf(temper.getYear())
                        , String.valueOf(temper.getMonth())
                        , String.valueOf(temper.getDay())
                        , String.valueOf(temper.getHour())
                        , String.valueOf(temper.getMinute())
                        , String.valueOf(temper.getSecond())
                        , String.valueOf(temper.getSeq()));
            }
        }
    }


}
