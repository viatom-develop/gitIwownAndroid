package com.zeroner.bledemo.data.viewData;

import android.content.Context;
import android.text.TextUtils;

import com.iwown.app.nativeinvoke.SA_SleepBufInfo;
import com.iwown.app.nativeinvoke.SA_SleepDataInfo;
import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.R;
import com.zeroner.bledemo.bean.data.BraceletData;
import com.zeroner.bledemo.bean.data.Detail_data;
import com.zeroner.bledemo.bean.data.DummyItem;
import com.zeroner.bledemo.bean.data.HeartData;
import com.zeroner.bledemo.bean.data.R1Data;
import com.zeroner.bledemo.bean.data.SleepStatusFlag;
import com.zeroner.bledemo.bean.data.SleepTime;
import com.zeroner.bledemo.bean.data.SleepViewData;
import com.zeroner.bledemo.bean.data.SportDetail;
import com.zeroner.bledemo.bean.sql.DailyData;
import com.zeroner.bledemo.bean.sql.HeartRateHour;
import com.zeroner.bledemo.bean.sql.SleepData;
import com.zeroner.bledemo.bean.sql.SportData;
import com.zeroner.bledemo.bean.sql.TB_64_index_table;
import com.zeroner.bledemo.bean.sql.TB_SLEEP_Final_DATA;
import com.zeroner.bledemo.data.sync.MtkToIvHandler;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.BluetoothUtil;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.SqlBizUtils;
import com.zeroner.bledemo.utils.Util;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：hzy on 2018/1/6 09:44
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class ViewData {

    private static final int PB_ECG_TYPE= 1;

    /**
     * get sport data
     *
     * @return list
     */
    public static List<BraceletData> getSportData(Context context) {
        //sport data list
        List<BraceletData> sportData = new ArrayList<>();
        DailyData dailyData = SqlBizUtils.dailyData(new DateUtil().getSyyyyMMddDate(), PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME));
        if (dailyData != null) {
            BraceletData data = new BraceletData(context.getString(R.string.title_device_data)
                    , String.format(context.getString(R.string.index_sport_step), String.valueOf(dailyData.getSteps()))
                    , String.format(context.getString(R.string.index_sport_distance), dailyData.getDistance())
                    , String.format(context.getString(R.string.index_sport_calorie), dailyData.getCalories()));
            sportData.add(data);
        }
        return sportData;
    }

    /**
     * bracelet connect statue ,battery
     *
     * @return
     */
    public static List<DummyItem> getDeviceData(Context context) {
        //device list
        List<DummyItem> items = new ArrayList<>();
        boolean connect;
        String battery;
        String model;
        String version;
        connect = BluetoothUtil.isConnected();
        try {
            battery = PrefUtil.getString(context, BaseActionUtils.Action_device_Battery);
            model = PrefUtil.getString(context, BaseActionUtils.Action_device_Model);
            version  = PrefUtil.getString(context, BaseActionUtils.Action_device_version);
        } catch (Exception e) {
            e.printStackTrace();
            battery = "0";
            model = "0";
            version = "0";
        }
        if (TextUtils.isEmpty(battery)) {
            battery = "0";
            model = "0";
            version = "0";
        }
        String statue = connect ? context.getString(R.string.index_device_status_connect) : context.getString(R.string.index_device_status_disconnect);
        DummyItem item = new DummyItem(context.getString(R.string.title_device_list)
                , String.format(context.getString(R.string.index_device_name), PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME))
                , String.format(context.getString(R.string.index_device_mac), PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_ADDRESS))
                , String.format(context.getString(R.string.index_device_status), statue)
                , String.format(context.getString(R.string.index_device_battery), battery),
                String.format(context.getString(R.string.index_device_model), model),
                String.format(context.getString(R.string.index_device_version), version));
        items.add(item);
        return items;
    }


    public static List<SleepViewData> getSleepData(Context context, String dataFrom, int year, int month, int day) {
        List<SleepViewData> items = null;
        items = new ArrayList<>();
        if (SuperBleSDK.isMtk(context)) {
            SA_SleepBufInfo retData = MtkToIvHandler.getP1Sleep(new DateUtil(year, month, day).getSyyyyMMddDate());
            if (null != retData && retData.completeFlag == 1 && (retData.datastatus == 0 || retData.datastatus == 1)) {
                long end = new DateUtil(retData.outSleepTime.year + 2000, retData.outSleepTime.month, retData.outSleepTime.day, retData.outSleepTime.hour,
                        retData.outSleepTime.minute).getUnixTimestamp();
                long start = new DateUtil(retData.inSleepTime.year + 2000, retData.inSleepTime.month, retData.inSleepTime.day, retData.inSleepTime.hour, retData.inSleepTime.minute).getUnixTimestamp();
                int sleep_total_time_min = (int) ((end - start) / 60);

                int light_time = 0;
                int deep_time = 0;

                for (SA_SleepDataInfo sleepdatum : retData.sleepdata) {
                    long start_time = new DateUtil(sleepdatum.startTime.year + 2000, sleepdatum.startTime.month
                            , sleepdatum.startTime.day, sleepdatum.startTime.hour, sleepdatum.startTime.minute)
                            .getUnixTimestamp();

                    long end_time = new DateUtil(sleepdatum.stopTime.year + 2000, sleepdatum.stopTime.month
                            , sleepdatum.stopTime.day, sleepdatum.stopTime.hour, sleepdatum.stopTime.minute)
                            .getUnixTimestamp();

                    int min = (int) ((end_time - start_time) / 60);

                    if (sleepdatum.sleepMode == 3) {
                        deep_time += min;
                    } else if (sleepdatum.sleepMode == 4) {
                        light_time += min;
                    }
                }

                SleepViewData sleepViewData = new SleepViewData(context.getString(R.string.sleep_title)
                        , context.getString(R.string.sleep_detail_total, Util.minToTimeUnit(sleep_total_time_min))
                        , context.getString(R.string.sleep_detail_light, Util.minToTimeUnit(light_time))
                        , context.getString(R.string.sleep_detail_deep, Util.minToTimeUnit(deep_time)));
                items.add(sleepViewData);
            }
        }else if(SuperBleSDK.isProtoBuf(context)){
            DateUtil dateUtil = new DateUtil(year,month,day);
            TB_SLEEP_Final_DATA sleep_final_data = DataSupport.where("date=? and data_from=?",dateUtil.getSyyyyMMddDate(),dataFrom).findFirst(TB_SLEEP_Final_DATA.class);
            if(sleep_final_data!=null){
                int totalT = (int) ((sleep_final_data.getEnd_time()-sleep_final_data.getStart_time())/60);
                SleepViewData sleepView = new SleepViewData(context.getString(R.string.sleep_title)
                        , context.getString(R.string.sleep_detail_total, String.valueOf(Util.minToTimeUnit(totalT)))
                        , context.getString(R.string.sleep_detail_light, String.valueOf(Util.minToTimeUnit((int) sleep_final_data.getDeepSleepTime())))
                        , context.getString(R.string.sleep_detail_deep, String.valueOf(Util.minToTimeUnit((int) sleep_final_data.getLightSleepTime()))));
                int weak  = (int) (totalT - sleep_final_data.getDeepSleepTime()-sleep_final_data.getLightSleepTime());
                weak = weak>0?weak:0;
                sleepView.setWeak(context.getString(R.string.sleep_detail_weak,Util.minToTimeUnit(weak)));
                items.add(sleepView);
            }else{
                SleepViewData sleepView = new SleepViewData(context.getString(R.string.sleep_title)
                        , context.getString(R.string.sleep_detail_total, Util.minToTimeUnit(0))
                        , context.getString(R.string.sleep_detail_light, Util.minToTimeUnit(0))
                        , context.getString(R.string.sleep_detail_deep,Util.minToTimeUnit(0)));
                sleepView.setWeak(context.getString(R.string.sleep_detail_weak,Util.minToTimeUnit(0)));
                items.add(sleepView);
            }
        }else{
                SleepTime sleep = sleepDetail(deleteSoberSleepData(SqlBizUtils.querySleepData(dataFrom, year, month, day)));
                SleepViewData sleepView = new SleepViewData(context.getString(R.string.sleep_title)
                        , context.getString(R.string.sleep_detail_total, String.valueOf(Util.minToTimeUnit(sleep.getTotalMin())))
                        , context.getString(R.string.sleep_detail_light, String.valueOf(Util.minToTimeUnit(sleep.getLightSleepTime())))
                        , context.getString(R.string.sleep_detail_deep, String.valueOf(Util.minToTimeUnit(sleep.getDeepSleepTime()))));
            sleepView.setWeak(context.getString(R.string.sleep_detail_weak,Util.minToTimeUnit(0)));
                items.add(sleepView);
        }
        return items;
    }

    /**
     * The average heart rate in the last hour of valid heart rate data
     *
     * @param context
     * @return
     */
    public static List<HeartData> getHeartData(Context context) {
        List<HeartData> items = new ArrayList<>();
        List<Integer> effective = new ArrayList<>();
        int sum = 0;
        int avg = 0;
        HeartRateHour heartRateHour = SqlBizUtils.queryHeartAvg(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME));
        if (heartRateHour != null) {
            String detail = heartRateHour.getDetail_data();
            ArrayList<Integer> hearts = JsonUtils.getListJson(detail, Integer.class);
            for (int i : hearts) {
                if (i != 255) {
                    effective.add(i);
                }
            }
            if (effective.size() > 0) {
                for (int i : effective) {
                    sum += i;
                }
                avg = sum / effective.size();
            }
        }
        HeartData data = new HeartData(context.getString(R.string.heart_title)
                , String.format(context.getString(R.string.heart_curr_value), String.valueOf(avg)));
        items.add(data);
        return items;
    }

    public static List<R1Data> getR1data(Context context) {
        List<R1Data> items = new ArrayList<>();
        R1Data r1Data = new R1Data(context.getString(R.string.r1_title));
        items.add(r1Data);
        return items;
    }

    public static List<R1Data> getZgGpsData(Context context) {
        List<R1Data> items = new ArrayList<>();
        R1Data r1Data = new R1Data(context.getString(R.string.zg_gps_data));
        items.add(r1Data);
        return items;
    }

    public static List<R1Data> getZgAGpsData(Context context) {
        List<R1Data> items = new ArrayList<>();
        R1Data r1Data = new R1Data(context.getString(R.string.zg_agps_data));
        items.add(r1Data);
        return items;
    }

    public static List<R1Data> getECGData(Context context) {
        String deviceName = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME) + "";
        int size = DataSupport.where("data_from=? and data_ymd=?", deviceName,new DateUtil().getSyyyyMMddDate()).count(TB_64_index_table.class);

        List<R1Data> items = new ArrayList<>();
        R1Data r1Data = new R1Data(context.getString(R.string.pb_ecg_data));
        r1Data.setType(PB_ECG_TYPE);
        r1Data.setMsgContent(context.getString(R.string.pb_ecg_size,String.valueOf(size)));
        items.add(r1Data);
        return items;
    }


    /**
     * a collection of heart rate values for every minute in 1440 minutes a day
     * 1 day =1440 min
     * heart point 1440
     *
     * @param dataFrom
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static List<Integer> getHeartDetail(String dataFrom, int year, int month, int day) {
        List<Integer> point = new ArrayList<>();
        int[] heartPoint = new int[1440];
        List<HeartRateHour> hearts = SqlBizUtils.queryHeartByDay(dataFrom, year, month, day);
        for (HeartRateHour heart : hearts) {
            int hour = heart.getHours();
            ArrayList<Integer> iHearts = JsonUtils.getListJson(heart.getDetail_data(), Integer.class);
            for (int j = 0; j < iHearts.size(); j++) {
                int k = iHearts.get(j);
                if (k == 255) {
                    //no heart data
                    k = 0;
                }
                heartPoint[60 * hour + j] = k;
            }
        }
        for (int i = 0; i < heartPoint.length; i++) {
            if (heartPoint[i] == 0) {
                //no heart data
                heartPoint[i] = 0;
            }
            point.add(heartPoint[i]);
        }
        return point;
    }

    /**
     * The detailed time, step, distance, calorie of each movement
     *
     * @param context
     * @param dataFrom
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static List<SportDetail> getSportDetail(Context context, String dataFrom, int year, int month, int day) {
        List<SportDetail> data = new ArrayList<>();
        List<SportData> sports = SqlBizUtils.querySportDetail(dataFrom, year, month, day);
        for (int i = 0; i < sports.size(); i++) {
            SportDetail detail = new SportDetail();
            SportData sportData = sports.get(i);
            detail.setSportType(sportData.getSport_type());
            detail.setSportName(context.getString(Util.getSportName(0, sportData.getSport_type())));
            detail.setStartTime(context.getString(R.string.sport_detail_start_time, Util.minToTime(sportData.getStart_time())));
            detail.setEndTime(context.getString(R.string.sport_detail_end_time, Util.minToTime(sportData.getEnd_time())));
            Detail_data detail_data = JsonUtils.fromJson(sportData.getDetail_data(), Detail_data.class);
            detail.setSteps(context.getString(R.string.index_sport_step, String.valueOf(detail_data.getStep())));
            detail.setDistance(context.getString(R.string.index_sport_distance, detail_data.getDistance()));
            detail.setCalorie(context.getString(R.string.index_sport_calorie, sportData.getCalorie()));
            data.add(detail);
        }
        return data;
    }


    /**
     * Delete sleep sober data
     *
     * @param data
     * @return
     */
    public static List<SleepData> deleteSoberSleepData(List<SleepData> data) {
        List<SleepData> sleeps = new ArrayList<>();

        //placement status index
        List<Integer> placedIndexList = new ArrayList<>();
        //Placement Status
        int sleepTypePlaced = 5;
        //sleep start
        int sleepTypeStartSleep = 1;

        for (int i = 0; i < data.size(); i++) {
            //sleep type
            int sleepType = data.get(i).getSleep_type();
            if (sleepType == sleepTypePlaced) {
                placedIndexList.add(i);

                //Take out the data before the state of placement
                for (int j = i - 1; j >= 0; j--) {
                    int preSleepType = data.get(j).getSleep_type();
                    placedIndexList.add(j);
                    //When the start status is reached,break
                    if (preSleepType == sleepTypeStartSleep) {
                        break;
                    }
                }
            }
        }

        //Delete Marker Data
        for (int i = 0; i < data.size(); i++) {
            if (!placedIndexList.contains(i)) {
                sleeps.add(data.get(i));
            }
        }
        return sleeps;
    }

    /**
     * sleep detail data
     *
     * @param sleepList
     * @return
     */
    public static SleepTime sleepDetail(List<SleepData> sleepList) {
        String dataForm = null;
        int sleepTime = 0;
        int totalTime = 0;
        boolean isType2;
        int totalSleepTime = 0;
        int deepSleepTotal = 0;
        int lightSleepTotal = 0;
        int noSleepTotal = 0;
        long startJudge = 0;
        long endJudge = 0;
        int endSize = -2;
        int soberTime = 0;
        int startMin = 0, endMin = 0;
        boolean isStartTimeFound = false;
        ArrayList<SleepStatusFlag> sleepStatus = new ArrayList<>();
        if (sleepList.size() > 0)
            for (int i = 0; i < sleepList.size(); i++) {
                if (i == 0) {
                    dataForm = sleepList.get(0).getData_from();
                } else {
                    if ((dataForm == null && sleepList.get(i).getData_from() != null) || (dataForm != null && !dataForm.equals(sleepList.get(i).getData_from()))) {
                        continue;
                    }
                }
                int newTime = sleepList.get(i).getSleep_exit() - sleepList.get(i).getSleep_enter();
                if (newTime != 0 && newTime != sleepTime) {
                    sleepTime = newTime;
                    int number = newTime >= 0 ? newTime : (1440 - sleepList.get(i).getSleep_enter() + sleepList.get(i).getSleep_exit());
                    totalTime += number;
                }
                int startTime = sleepList.get(i).getStart_time();
                int endTime = sleepList.get(i).getEnd_time();
                int sleepType = sleepList.get(i).getSleep_type();
                int sleepNum = endTime - startTime;
                int activity = sleepNum >= 0 ? sleepNum : (1440 - startTime + endTime);
                int year = sleepList.get(i).getYear();
                int month = sleepList.get(i).getMonth();
                int day = sleepList.get(i).getDay();
                long stUx = new DateUtil(year, month, day, startTime / 60, startTime % 60).getUnixTimestamp();
                DateUtil dateUtil = new DateUtil(year, month, day);
                if (startTime > endTime) {
                    dateUtil.addDay(1);
                }
                long etUx = new DateUtil(dateUtil.getYear(), dateUtil.getMonth(), dateUtil.getDay(), endTime / 60, endTime % 60).getUnixTimestamp();
                if (sleepType == 3) {
                    sleepStatus.add(new SleepStatusFlag(activity, 1, startTime));
                    deepSleepTotal += activity;
                    totalSleepTime += activity;
                } else if (sleepType == 4) {
                    sleepStatus.add(new SleepStatusFlag(activity, 2, startTime));
                    lightSleepTotal += activity;
                    totalSleepTime += activity;
                }

                //first data
                if (i == 0) {
                    startMin = startTime;
                    startJudge = stUx;

                }
                //last data
                if (i == (sleepList.size() - 1)) {
                    if (endJudge < etUx) {
                        endJudge = etUx;
                        endMin = endTime;
                    }
                }

                //wake up
                if (sleepType == 2) {
                    isType2 = true;
                    if (totalSleepTime < totalTime) {
                        sleepStatus.add(new SleepStatusFlag(totalTime - totalSleepTime, 2, startTime));
                        totalSleepTime = totalTime;
                    }
                    if (!isStartTimeFound) {
                        if (startJudge < stUx) {
                            startJudge = stUx;
                            startMin = startTime;
                        }
                        isStartTimeFound = true;
                    }

                    if (endJudge < etUx) {
                        endJudge = etUx;
                        endMin = endTime;
                    }
                    endSize = i;
                    soberTime = endTime;
                } else {
                    isType2 = false;
                }

                if (i == (endSize + 1) && !isType2) {
                    if (startTime < soberTime) {
                        sleepStatus.add(new SleepStatusFlag((startTime - soberTime + 1440), 0, soberTime));
                        noSleepTotal += (startTime - soberTime + 1440);
                    } else {
                        sleepStatus.add(new SleepStatusFlag((startTime - soberTime), 0, soberTime));
                        noSleepTotal += (startTime - soberTime);
                    }
                }

                if (i == sleepList.size() - 1) {
                    if (endTime < startTime) {
                        DateUtil dates = new DateUtil(year, month, day);
                        dates.addDay(1);
                    }
                }
            }
        if (totalTime > 0) {
            if (totalSleepTime < totalTime) {
                totalSleepTime = totalTime;
                sleepStatus.add(new SleepStatusFlag(totalTime - totalSleepTime, 2, endMin));
            }
            startMin = sleepList.get(0).getSleep_enter();
            endMin = sleepList.get(sleepList.size() - 1).getSleep_exit();
            if ((lightSleepTotal + deepSleepTotal) < totalTime) {
                lightSleepTotal = totalTime - deepSleepTotal;
            }
        }

        SleepTime sleepData = new SleepTime();
        totalTime = (endMin - startMin >= 0) ? (endMin - startMin) : (endMin - startMin + 1440);
        sleepData.setTotalMin(totalTime);
        sleepData.setSleepStatus(sleepStatus);
        sleepData.setStartMin(startMin);
        sleepData.setEndMin(endMin);
        sleepData.setTotalTimeIncludeNoSleep(totalTime);
        sleepData.setDeepSleepTime(deepSleepTotal);
        sleepData.setLightSleepTime(lightSleepTotal);
        sleepData.setAwakeSleepTime(noSleepTotal);
        return sleepData;
    }
}
