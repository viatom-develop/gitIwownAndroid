package com.zeroner.bledemo.data.sync;

import android.os.Environment;


import com.iwown.app.nativeinvoke.NativeInvoker;
import com.iwown.app.nativeinvoke.SA_SleepBufInfo;
import com.iwown.app.nativeinvoke.SA_SleepDataInfo;
import com.socks.library.KLog;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.Detail_data;
import com.zeroner.bledemo.bean.sql.TB_61_data;
import com.zeroner.bledemo.bean.sql.TB_v3_sport_data;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.FileUtils;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.blemidautumn.utils.Util;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：hzy on 2018/3/23 16:23
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class MtkToIvHandler {


    private static String filename;


    public static void p161DataToIvSport(int year, int month, int day){
        int mSize=0;
        float distance=0;
        long startUTime=0;
        long endUTime=0;
        int stTime=0;
        int edTime=0;
        float calorie=0;
        int activity=0;
        int step=0;

        String dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
        List<TB_61_data> datas = sort61DataBySeq(year, month, day, dataFrom);

        if(datas!=null && datas.size()>0){
            mSize=datas.size();
        }
        //走路数据
        List<TB_61_data> walkData=new ArrayList<TB_61_data>();
        //除走路后的其它数据
        List<TB_61_data> sportData=new ArrayList<TB_61_data>();
        //自动进入运动的影响 需要剔除的走路中相应的数据
        List<TB_61_data> noAddWalkData=new ArrayList<TB_61_data>();
        for (int i = 0; i < mSize; i++) {
            // <32  二进制睡眠数据:100000
            if(datas.get(i).getSport_type()==1 && datas.get(i).getData_type()>=32){
                //走路数据
                walkData.add(datas.get(i));
            }
            //除去走路其它运动
            else if(datas.get(i).getSport_type()!=1){
                //添加其它运动
                sportData.add(datas.get(i));

                //state_type :1  一段运动开始
                //state_type :2  一段运动结束
                //state_type :3  一段运动暂停
                //state_type :4  一段运动运动中
                //datas.get(i).getAutomatic()  >0 大于自动识别
                if(datas.get(i).getState_type()==1 && datas.get(i).getAutomatic()>0){
                    for (int j = 0; j <= datas.get(i).getAutomatic(); j++) {
                        long uxTime = datas.get(i).getTime()/1000-60*j;
                        DateUtil date = new DateUtil(uxTime,true);

                        List<TB_61_data> noAdds=DataSupport.where("and year=? and month=? and day=? and hour=? and min=? and sport_type=? and data_from=? and data_type>=?",
                                year+"",month+"",day+"",date.getHour()+"",date.getMinute()+"","1",dataFrom,"32").find(TB_61_data.class);
                        if(noAdds!=null && noAdds.size()>0) {
                            noAddWalkData.addAll(noAdds);
                        }
                    }
                }
            }
        }

        //新的解析 28表
        List<TB_v3_sport_data> sport_data1=new ArrayList<TB_v3_sport_data>();
        if(walkData.size()>0) {
            distance=walkData.get(0).getDistance();
            startUTime= Util.date2TimeStamp(year,month,day,walkData.get(0).getHour(),walkData.get(0).getMin());
            endUTime=Util.date2TimeStamp(year,month,day,walkData.get(0).getHour(),walkData.get(0).getMin());
            stTime=walkData.get(0).getHour()*60+walkData.get(0).getMin();
            edTime=walkData.get(0).getHour()*60+walkData.get(0).getMin();
            calorie=walkData.get(0).getCalorie();
            activity=1;
            step=walkData.get(0).getStep();
            for (int i = 0; i < walkData.size(); i++) {
                if (i > 0 && !noAddWalkData.contains(walkData.get(i))) {
                    int nowT=walkData.get(i).getHour()*60+walkData.get(i).getMin();
                    if(nowT-edTime>3){
                        if(step>0 || distance>0) {
                            String detail = JsonUtils.toJson(getDetail(activity, step, distance));
                            sport_data1.add(getTbSport(1, year, month, day, stTime, startUTime, edTime, endUTime, calorie, detail, dataFrom,0));
                        }
                        distance=walkData.get(i).getDistance();
                        startUTime=Util.date2TimeStamp(year,month,day,walkData.get(i).getHour(),walkData.get(i).getMin());
                        endUTime=Util.date2TimeStamp(year,month,day,walkData.get(i).getHour(),walkData.get(i).getMin());
                        stTime=walkData.get(i).getHour()*60+walkData.get(i).getMin();
                        edTime=walkData.get(i).getHour()*60+walkData.get(i).getMin();
                        calorie=walkData.get(i).getCalorie();
                        activity=1;
                        step=walkData.get(i).getStep();
                    }else{
                        endUTime=Util.date2TimeStamp(year,month,day,walkData.get(i).getHour(),walkData.get(i).getMin());
                        edTime=walkData.get(i).getHour()*60+walkData.get(i).getMin();
                        distance+=walkData.get(i).getDistance();
                        calorie+=walkData.get(i).getCalorie();
                        activity++;
                        step+=walkData.get(i).getStep();
                    }
                }
                if(i==walkData.size()-1){
                    if(step>0 || distance>0) {
                        String detail = JsonUtils.toJson(getDetail(activity, step, distance));
                        sport_data1.add(getTbSport(1, year, month, day, stTime, startUTime, edTime, endUTime, calorie, detail, dataFrom,0));
                    }
                }
            }
        }


        if(sport_data1.size()>0){
            long[] times=new long[sport_data1.size()];
            int[] tr1=new int[sport_data1.size()];
            for (int i = 0; i < sport_data1.size(); i++) {
                tr1[i]=i;
                times[i]=sport_data1.get(i).getStart_uxtime();
            }

            for (int i = 0; i < times.length -1; i++) {
                for (int j = 0; j < times.length - i - 1; j++) {
                    if (times[j] < times[j+1]) {
                        long temp = times[j];
                        times[j] = times[j+1];
                        times[j+1] = temp;
                        int tr2=tr1[j];
                        tr1[j]=tr1[j+1];
                        tr1[j+1] = tr2;
                    }
                }
            }
            try{
                for (int i = 0; i < sport_data1.size(); i++) {
                    sport_data1.get(tr1[i]).saveOrUpdate("start_uxtime=? and data_from=? and sport_type=?",sport_data1.get(tr1[i]).getStart_uxtime()+"",dataFrom+"",sport_data1.get(tr1[i]).getSport_type()+"");
                }
            }catch (Exception e){
                e.printStackTrace();
                KLog.e("解析61运动异常");
            }

        }

    }


    private static Detail_data getDetail(int activity, int step, float distance){
        Detail_data d=new Detail_data();
        d.setActivity(activity);
        d.setCount(0);
        d.setStep(step);
        d.setDistance(distance);
        return d;
    }



    public static void mtk61DataToHeart(int year, int month, int day, String dataFrom, List<TB_61_data> datas) {
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
                        for (int j = count53; j <= datas.get(i).getMin() && j < 60; j++) {
                            int heart=datas.get(i).getAvg_bpm();
                            if(heart!=0){
                                heart53.add(heart);
                                heartPre=heart;
                            }else {
                                heart53.add(heartPre);
                            }
                        }
                        count53 = datas.get(i).getMin() + 1;
                    }
                } else {
                    for (int j = count53; j <= datas.get(i).getMin() && j < 60; j++) {
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



    public static List<TB_61_data> sort61DataBySeq(int year, int month, int day, String dataFrom) {
        if (dataFrom == null) {
            dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        }
        KLog.d("test61数据：", "某一天的数据--year:  " + year + "-" + month + "-" + day + " " + dataFrom);
        List<TB_61_data> allDatas = DataSupport.where("year=? and month=? and day=? and data_from=?", year + "", month + "", day + "", dataFrom).order("seq asc").find(TB_61_data.class);
        List<TB_61_data> datas = new ArrayList<TB_61_data>();
        if (allDatas.size() > 1) {
            //一头一尾seq差距超过4000认为seq从峰值跳为1
            if (allDatas.get(allDatas.size() - 1).getSeq() - allDatas.get(0).getSeq() >= 4000) {
                int cut = 0;
                for (int i = 1; i < allDatas.size(); i++) {
                    if (allDatas.get(i).getSeq() - allDatas.get(i - 1).getSeq() >= 2000) {
                        cut = i;
                        break;
                    }
                }

                List<TB_61_data> firstDatas = new ArrayList<TB_61_data>();
                List<TB_61_data> twoDatas = new ArrayList<TB_61_data>();
                for (int i = 0; i < allDatas.size(); i++) {
                    if (i < cut) {
                        firstDatas.add(allDatas.get(i));
                    } else {
                        twoDatas.add(allDatas.get(i));
                    }
                }
                twoDatas.addAll(firstDatas);
                datas.addAll(twoDatas);
            } else {
                datas.addAll(allDatas);
            }
        } else {
            datas.addAll(allDatas);
        }
        return datas;
    }


    public static TB_v3_sport_data getTbSport(int sportType,int year,int month,int day,int sTime,long sUTime,int eTime,long eUTime,float calorie,String detail,String dataFrom,int automatic){
        TB_v3_sport_data sport = new TB_v3_sport_data();
        sport.setYear(year);
        sport.setMonth(month);
        sport.setDay(day);
//        if(sTime-automatic<0){
//            sport.setStart_time(sTime-automatic+1440);
//        }else{
//            sport.setStart_time(sTime-automatic);
//        }
        sport.setStart_uxtime(sUTime-automatic*60);
        if(sportType==1){
//            sport.setEnd_time(eTime+1);
            sport.setEnd_uxtime(eUTime+60);
        }else {
//            if(eUTime%60==0) {
//                sport.setEnd_time(eTime);
//            }else{
//                sport.setEnd_time(eTime+1);
//            }
            sport.setEnd_uxtime(eUTime);
        }
        sport.setCalorie(calorie);
        sport.setSport_type(sportType);
//        sport.setDetail_data(detail);
        sport.setData_from(dataFrom);
        return sport;
    }


    public static SA_SleepBufInfo getP1Sleep(String  date) {
        try {
            SA_SleepBufInfo retData = new SA_SleepBufInfo();
            String dev_name = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
            String path =BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir + date+ "/" + BleApplication.getInstance().getString(R.string.file_61_name_format, MtkDataToServer.Fictitious_Uid+"", date,
                    dev_name);
            KLog.d("testSleep", "testSleep睡眠：" + path + "   存在？" + FileUtils.checkFileExists(path));
            if (FileUtils.checkFileExists(path)) {
                File rootDir = Environment.getExternalStorageDirectory();
                NativeInvoker jni = new NativeInvoker();
                int status = jni.calculateSleep(rootDir.getAbsolutePath() + BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir , MtkDataToServer.Fictitious_Uid, date, dev_name, 1, retData);
                retData.datastatus = status;
                KLog.d("testSleep","testSleep: "+ JsonUtils.toJson(retData));
                return retData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static SA_SleepBufInfo getP1Sleep2(long uid, String dev_name, String date) {
        try {
            SA_SleepBufInfo retData = new SA_SleepBufInfo();
            String path =BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir + date+ "/" + BleApplication.getInstance().getString(R.string.file_61_name_format, uid+"", date,
                    dev_name);
            KLog.d("testSleep", "testSleep睡眠：" + path + "   存在？" + FileUtils.checkFileExists(path));
            if (FileUtils.checkFileExists(path)) {
                File rootDir = Environment.getExternalStorageDirectory();
                NativeInvoker jni = new NativeInvoker();
                int status = jni.calculateSleep(rootDir.getAbsolutePath() + BaseActionUtils.FilePath.Mtk_Ble_61_Sleep_Dir , uid, date, dev_name, 1, retData);
                retData.datastatus = status;
                KLog.d("testSleep","testSleep: "+ JsonUtils.toJson(retData));
                return retData;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 时间戳转日期格式
     */
    public static String year2DateStr(int year, int month, int day){
        return year+ String.format("%02d",month)+ String.format("%02d",day);
    }



}
