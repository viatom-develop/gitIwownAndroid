package com.zeroner.bledemo.handler_data;

import com.zeroner.bledemo.bean.data.Detail_data;
import com.zeroner.bledemo.bean.sql.ProtoBuf_80_data;
import com.zeroner.bledemo.bean.sql.TB_v3_sport_data;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.blemidautumn.utils.ByteUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Gavin
 * @date 2020-03-05
 */
public class SportHandler {

    /**
     *日期格式转为时间戳
     * Date format to timestamp
     * @return
     */
    public static long date2TimeStamp(int year,int month,int day,int hour,int min,int sec){
        try {
            String date_str = year + "-" + month + "-" + day +" " + hour +":"+ min +":"+sec;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return Long.parseLong(String.valueOf(sdf.parse(date_str).getTime()/1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     *
     * @param dataFrom 设备的广播名
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static List<TB_v3_sport_data> getOneDaySports(String dataFrom,int year,int month,int day){
        List<TB_v3_sport_data> dataList = DataSupport.where("data_from=? and year=? and month=? and day=?",
                dataFrom,String.valueOf(year),String.valueOf(month),String.valueOf(day)).find(TB_v3_sport_data.class);
        return dataList;
    }

    /**
     * 将一整天的分钟数据转为一段一段的数据,走路和运动数据
     * Turn the minute data of the whole day is transformed into a piece of data,Walking and exercise data
     *
     */
    public static void ble2SportAndWalkData(String dataFrom, List<ProtoBuf_80_data> datas){
        int mSize=0;
        if(datas!=null && datas.size()>0){
            mSize=datas.size();
        }
        // Divide the data into walking and movement

        List<ProtoBuf_80_data> walkData=new ArrayList<>();
        List<ProtoBuf_80_data> sportData=new ArrayList<>();
//        自动进入的运动会和走路步数重合一部分 需要剔除的走路中相应的数据
//        List<ProtoBuf_80_data> noAddWalkData=new ArrayList<ProtoBuf_80_data>();
        int lastWalkI=-1;
        for (int i = 0; i < mSize; i++) {
            if(datas.get(i).getType()==1){
                //走路数据
                walkData.add(datas.get(i));
                lastWalkI = i;
            }else{
                //添加其它运动
                sportData.add(datas.get(i));
                byte mStatue = ByteUtil.int2byte(datas.get(i).getState());
                int state = mStatue&0x0f;
                //自动运动提前的分钟数(Minutes ahead of automatic movement)
                int automatic = mStatue>>4;
                if(state==1 && automatic>0){
                    for(int j=1;j<=automatic+1;j++){
                        if(i-j == lastWalkI && lastWalkI>=0){
                            datas.get(i-1).endStep= datas.get(i).getStep();
                            datas.get(i-1).endDis= datas.get(i).getDistance();
                            datas.get(i-1).endClo= datas.get(i).getCalorie();
                            datas.get(i-1).endMin= automatic;
                            walkData.remove(walkData.size()-1);
                            walkData.add(datas.get(i-1));
                            break;
                        }
                    }
                }
            }
        }
        List<TB_v3_sport_data> walkList = saveWalkData(walkData);
        List<TB_v3_sport_data> sportList = saveSportData(sportData);
        List<TB_v3_sport_data> allList = new ArrayList<>();
        allList.addAll(walkList);
        allList.addAll(sportList);


        if(allList.size()>0){
            //每一段运动的开始时间集合
            long[] times=new long[allList.size()];
            //集合下标数组
            int[] tr1=new int[allList.size()];
            //初始化数组
            for (int i = 0; i < allList.size(); i++) {
                tr1[i]=i;
                times[i]=allList.get(i).getStart_uxtime();
            }
            //按时间轴排序
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

            //这是一天可视化的运动数据，保存进数据库
            for (int i = allList.size()-1; i >=0 ; i--) {
                TB_v3_sport_data v3_sport_data = allList.get(tr1[i]);
                v3_sport_data.saveOrUpdate("data_from=? and start_uxtime=?",dataFrom,v3_sport_data.getStart_uxtime()+"");
            }

        }
    }

    /**
     * 将分散数据整合成分段数据
     * Integrate distributed data into component segment data
     */
    private static List<TB_v3_sport_data> saveWalkData(List<ProtoBuf_80_data> walkData){
        float distance = 0;
        long startUTime = 0;
        long endUTime = 0;
        int stTime = 0;
        int edTime = 0;
        float calorie = 0;
        int activity = 0;
        int step = 0;

        //新的解析 28表
        List<TB_v3_sport_data> walk_data=new ArrayList<TB_v3_sport_data>();
        if(walkData.size()>0) {
            int year = walkData.get(0).getYear();
            int month = walkData.get(0).getMonth();
            int day = walkData.get(0).getDay();
            String dataFrom = walkData.get(0).getData_from();

            distance = walkData.get(0).getDistance();
            startUTime = date2TimeStamp(year,month,day,
                    walkData.get(0).getHour(),walkData.get(0).getMinute(),walkData.get(0).getSecond());
            endUTime = date2TimeStamp(year,month,day,
                    walkData.get(0).getHour(),walkData.get(0).getMinute(),walkData.get(0).getSecond());
            stTime = walkData.get(0).getHour()*60+walkData.get(0).getMinute();
            edTime = walkData.get(0).getHour()*60+walkData.get(0).getMinute();
            calorie = walkData.get(0).getCalorie();
            activity = 1;
            step = walkData.get(0).getStep();
            //是否存在与运动重合的步数(Whether there are steps coincident with the motion)
            boolean hasSame=false;
            for (int i = 0; i < walkData.size(); i++) {
                if (i > 0) {
                    int nowT = walkData.get(i).getHour()*60+walkData.get(i).getMinute();
                    //两条数据时间超过5分钟即可切为一段数据
                    if(nowT-edTime>5 && (!hasSame)){
                        if(step>0 || distance>0) {
                            walk_data.add(getTbSport(1, year, month, day, startUTime, endUTime,calorie,distance,step,activity,dataFrom,null));
                        }
                        distance = walkData.get(i).getDistance();
                        startUTime = date2TimeStamp(year,month,day,walkData.get(i).getHour(),walkData.get(i).getMinute(),walkData.get(i).getSecond());
                        endUTime = date2TimeStamp(year,month,day,walkData.get(i).getHour(),walkData.get(i).getMinute(),walkData.get(i).getSecond());
                        stTime = walkData.get(i).getHour()*60+walkData.get(i).getMinute();
                        edTime = walkData.get(i).getHour()*60+walkData.get(i).getMinute();
                        calorie = walkData.get(i).getCalorie();
                        activity = 1 ;
                        step = walkData.get(i).getStep();
                    }else{
                        if(hasSame){
                            distance = 0;
                            startUTime = date2TimeStamp(year,month,day,walkData.get(i).getHour(),walkData.get(i).getMinute(),walkData.get(i).getSecond());
                            stTime = walkData.get(i).getHour()*60+walkData.get(i).getMinute();
                            calorie =0;
                            activity =0;
                            step =0;
                            hasSame =false;
                        }
                        endUTime = date2TimeStamp(year,month,day,walkData.get(i).getHour(),walkData.get(i).getMinute(),walkData.get(i).getSecond());
                        edTime = walkData.get(i).getHour()*60+walkData.get(i).getMinute();
                        distance += walkData.get(i).getDistance();
                        calorie += walkData.get(i).getCalorie();
                        activity++;
                        step += walkData.get(i).getStep();
                        if(walkData.get(i).endStep>0){
                            hasSame =true;
                            edTime -= walkData.get(i).endMin;
                            endUTime -= walkData.get(i).endMin*60;
                            step -= walkData.get(i).endStep;
                            distance -= walkData.get(i).endDis;
                            calorie -= walkData.get(i).endClo;
                            if(endUTime < startUTime){
                                endUTime = startUTime+59;
                                edTime = stTime;
                            }
                            if(step>0 || distance>0) {
                                walk_data.add(getTbSport(1, year, month, day, startUTime, endUTime, calorie,distance,step,activity,dataFrom,null));
                            }
                        }
                    }
                }
                if(i==walkData.size()-1){
                    if(step>0 || distance>0) {
                        walk_data.add(getTbSport(1, year, month, day, startUTime, endUTime, calorie,distance,step,activity,dataFrom,null));
                    }
                }
            }
        }
        return walk_data;
    }

    private static List<TB_v3_sport_data> saveSportData(List<ProtoBuf_80_data> sportData){
        //新的解析 28表
        List<TB_v3_sport_data> sportList=new ArrayList<TB_v3_sport_data>();
        int sportType=0;
        //运动是否暂停
        boolean pause = false;
        int pauseTime =0;
        long pauseUt =0;
        float calorie =0;
        float distance =0;
        int step =0;
        long lastTime =0;
        boolean isOver =true;
        long startUTime = 0;
        long endUTime = 0;
        int duration = 0;

        LinkedList<Integer> heart53 = new LinkedList<>();

        String lastHeartTime = "";
        int lastHeart=0;
        //人体的最大心率
        int maxHeart = 200;
        int automatic = 0;
        //每分钟的步数或划水次数
        int minStep=0;
//        LinkedList<Integer> stepList = new LinkedList<>();

        // === sportType 为 131 为游泳数据 ，特殊处理  ===

        if(sportData.size()>0){

            int year = sportData.get(0).getYear();
            int month = sportData.get(0).getMonth();
            int day = sportData.get(0).getDay();
            String dataFrom = sportData.get(0).getData_from();

            for (int i = 0; i < sportData.size(); i++) {
                if(sportData.get(i).getType()==0 && sportType==0) {
                    continue;
                }

                byte mStatue = ByteUtil.int2byte(sportData.get(i).getState());
                int state = mStatue&0x0f;
                int mautomatic = mStatue>>4;
                if(!isOver){
                    //此段运动异常，强行结算
                    if(state==1) {
                        //运动时长 秒值
                        duration  = (int) (lastTime - startUTime) - pauseTime;

                        //运动时长 分钟
//                        activity = df % 60 == 0 ? (df / 60) : (df / 60 + 1);
                        if (duration > 0) {
                            sportList.add(getTbSport(sportType,year,month,day,startUTime,endUTime,calorie,distance,step,duration,dataFrom,heart53));
                        }
                        sportType = 0;
                        pauseTime = 0;
                        minStep=0;
                    }
                }
                //运动类型状态
                if(sportType==0){
                    //一段运动开启
                    if(state==1) {
                        calorie=0;
                        distance=0;
                        pauseTime=0;
                        automatic=mautomatic;
                        heart53.clear();
//                        stepList.clear();
                        step=0;
                        isOver=false;
                        //取每一分钟的心率值
                        int heart=sportData.get(i).getAvg_bpm();
                        if(heart<30 || (heart>maxHeart)){
                            heart = 0;
                        }
                        minStep=sportData.get(i).getStep();
                        lastHeartTime="";
                        String nowHeartTime = sportData.get(i).getHour()+"/"+sportData.get(i).getMinute();
                        if(!nowHeartTime.equals(lastHeartTime)){
                            heart53.add(heart);
                            lastHeartTime = nowHeartTime;
                            lastHeart=heart;
                        }else{
                            if(lastHeart==0 && heart>0){
                                if(heart53.size()>0) {
                                    heart53.removeLast();
                                }
                                heart53.add(heart);
                                lastHeart=heart;
                            }
                        }
                        calorie += sportData.get(i).getCalorie();
                        distance += sportData.get(i).getDistance();
                        step += sportData.get(i).getStep();
                        sportType = sportData.get(i).getType();
                        startUTime = date2TimeStamp(year,month,day,sportData.get(i).getHour(),sportData.get(i).getMinute(),sportData.get(i).getSecond());
                        //自动进入的运动开始时间需要提前
                        startUTime = startUTime - automatic*60;
                    } else {
                        continue;
                    }
                }else{
                    if(state != 3){
                        calorie += sportData.get(i).getCalorie();
                        distance += sportData.get(i).getDistance();
                        step += sportData.get(i).getStep();
                    }
                    int heart=sportData.get(i).getAvg_bpm();
                    if(heart<30 || (heart>maxHeart)){
                        heart = 0;
                    }
                    String nowHeartTime = sportData.get(i).getHour()+"/"+sportData.get(i).getMinute();
                    if(!nowHeartTime.equals(lastHeartTime)){
                        heart53.add(heart);
                        lastHeartTime = nowHeartTime;
                        lastHeart=heart;
//                        stepList.add(minStep);
                        minStep=sportData.get(i).getStep();
                    }else{
                        if(lastHeart==0 && heart>0){
                            if(heart53.size()>0) {
                                heart53.removeLast();
                            }
                            heart53.add(heart);
                            lastHeart=heart;
                        }
                        minStep+=sportData.get(i).getStep();
                    }
                    lastTime = date2TimeStamp(year, month, day, sportData.get(i).getHour(), sportData.get(i).getMinute(),sportData.get(i).getSecond());

                    //手表第一个暂停状态
                    if(state==3 && !pause){
                        pauseUt=lastTime;
                        pause=true;
                    }
                    //全部在暂停状态下 0：是代表在休息，如果出现其它状态就是代表暂停结束
                    if(pause && state!=3 && state!=0){
                        long time1=lastTime;
                        //算运动暂停时间
                        pauseTime+= (int) (time1-pauseUt);
                        pause=false;
                    }
                    //一段运动结束
                    if(state==2){
                        isOver=true;
                        if(sportType==0) {
                            sportType = sportData.get(i).getType();
                        }
                        endUTime=lastTime;
                        duration =(int) (lastTime-startUTime)-pauseTime;
//                        stepList.add(minStep);
                            //游泳特殊处理
                        if(sportType==131){
                            //趟数
                            int laps = sportData.get(i).getStep();
                            //泳道长度
                            int poolLength = (int) (sportData.get(i).getDistance()*10);
                            step = step-sportData.get(i).getStep();
                            distance = laps*poolLength;
//                            heart53.clear();
//                            heart53.add(poolLength);
//                            heart53.add((int) (sportData.get(i).getCalorie()*10));
                        }
                        sportList.add(getTbSport(sportType,year,month,day,startUTime,endUTime,0,distance,step,duration,dataFrom,heart53));

                        step=0;
                        sportType=0;
                        calorie=0;
                        distance=0;
                        pauseTime=0;
                        automatic=0;
                        heart53.clear();
                    }
                }

                if(i==sportData.size()-1 && !isOver){
                    duration =(int) (lastTime-startUTime)-pauseTime;
                    if(duration > 0) {
                        sportList.add(getTbSport(sportType,year,month,day,startUTime,endUTime,calorie,distance,step,duration,dataFrom,heart53));
                        sportType=0;
                        pauseTime=0;
                    }
                }
            }
        }

        return sportList;
    }

    private static Detail_data getDetail(int activity,int step,float distance){
        Detail_data d=new Detail_data();
        d.setActivity(activity);
        d.setCount(0);
        d.setStep(step);
        d.setDistance(distance);
        return d;
    }

    /**
     * 一段可视化数据
     * A piece of visual data
     */
    public static TB_v3_sport_data getTbSport(int sportType,int year,int month,int day,long sUTime,long eUTime,
                                              float calorie,float distance,int count,int duration,String dataFrom,List<Integer> heartList){
        TB_v3_sport_data sport = new TB_v3_sport_data();
        sport.setYear(year);
        sport.setMonth(month);
        sport.setDay(day);
        sport.setStart_uxtime(sUTime);
        if(sportType==1){
            sport.setEnd_uxtime(eUTime+60);
        }else {
            sport.setEnd_uxtime(eUTime);
        }
        sport.setCalorie(calorie);
        sport.setSport_type(sportType);
        sport.setDistance(distance);
        sport.setStep_count(count);
        sport.setDuration(duration);
        if(heartList!=null) {
            sport.setHeart(JsonUtils.toJson(heartList));
        }
        sport.setData_from(dataFrom);
        return sport;
    }

}
