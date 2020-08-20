package com.zeroner.bledemo.data.sync;

import com.zeroner.bledemo.BleApplication;
import com.zeroner.bledemo.bean.sql.R1_68_data;
import com.zeroner.bledemo.bean.sql.R1_effective_data;
import com.zeroner.bledemo.bean.sql.TB_68_data;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.bledemo.utils.Util;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.utils.JsonTool;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018/7/30.
 */

public class R1ConvertHandler {

    public static void tb68ToConvertHistory(R1Tag r1DataBean) {
        KLog.e("TB68DATA", "开始同步68history数据.............");
        if (r1DataBean == null) {
            return;
        }
        if (r1DataBean.getTag().equals("R1TableConvert")) {
            try {
                String dataFrom = PrefUtil.getString(BleApplication.getInstance(), BaseActionUtils.ACTION_DEVICE_NAME);
                Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(1528732800000L);
                calendar.setTimeInMillis(System.currentTimeMillis());
                int mCurrentYear = calendar.get(Calendar.YEAR);
                int mCurrentMonth = calendar.get(Calendar.MONTH) + 1;
                int mCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
                StringBuilder currentSb = new StringBuilder();
                currentSb.append(mCurrentYear).append(mCurrentMonth).append(mCurrentDay);
                for (int i = 0; i < r1DataBean.getYear().size(); i++) {

                    StringBuilder sb = new StringBuilder();
                    sb.append(r1DataBean.getYear().get(i)).append(r1DataBean.getMonth().get(i)).append(r1DataBean.getDay().get(i));

//                    if (currentSb.toString().equals(sb.toString())) {
                        DataSupport.deleteAll(R1_effective_data.class, "year_month_day=?", sb.toString());
//                    }


                    //先查询数据
                    List<R1_effective_data> histories1 = DataSupport.where("year_month_day=?", sb.toString()).find(R1_effective_data.class);
                    if (histories1 == null || histories1.size() == 0) {
                        List<R1_68_data> r1_68_datas = get68Data(dataFrom,r1DataBean.getYear().get(i), r1DataBean.getMonth().get(i), r1DataBean.getDay().get(i));
                        List<Float> stepLists = new ArrayList<>();
                        List<Float> earthLists = new ArrayList<>();
                        List<Float> skyLists = new ArrayList<>();
                        List<Float> speedLists = new ArrayList<>();
                        List<Integer> heartLists = new ArrayList<>();
                        if (r1_68_datas != null && r1_68_datas.size() > 0) {
                            List<R1_effective_data> histories = new ArrayList<>();
                            long startTime = 0;
                            long endTime = 0;
                            float distance = 0;
                            int calorie = 0;
                            int touchdown_b = 0;
                            boolean isSport = false;
                            for (R1_68_data r1_68_data : r1_68_datas) {

                                R1_effective_data history = new R1_effective_data();
                                if (r1_68_data.getState_type() == 1) {//开始
                                    startTime = r1_68_data.getTime();
                                    distance += r1_68_data.getDistance();
                                    calorie += r1_68_data.getCalorie();
                                } else if (r1_68_data.getState_type() == 4) {//运动
                                    isSport = true;
                                    distance += r1_68_data.getDistance();
                                    calorie += r1_68_data.getCalorie();
                                    stepLists.add((float) r1_68_data.getRateOfStride_avg());
                                    earthLists.add((float) r1_68_data.getTouchDown_avg());
                                    skyLists.add(Util.doubleToFloat(1, r1_68_data.getFlight_avg()));
                                    if (r1_68_data.getDistance() > 0) {
                                        //配速 时间/距离
                                        //每条数据没分钟
                                        speedLists.add(Util.doubleToFloat(2, 1.0f / (r1_68_data.getDistance() / 1000f)));
                                    }
                                    touchdown_b += r1_68_data.getTouchDownPower_balance();
                                } else if (r1_68_data.getState_type() == 2) {//结束
                                    endTime = r1_68_data.getTime();
                                    distance += r1_68_data.getDistance();
                                    calorie += r1_68_data.getCalorie();
                                    //结束一条数据
                                    if (endTime >= startTime && isSport) {
                                        isSport = false;
                                        history.setTime((int) ((endTime - startTime) / 1000));
                                        history.setDistance(distance);
                                        history.setCalorie(calorie);
                                        history.setTime_id(startTime / 1000);
                                        history.setEnd_time(endTime / 1000);
                                        history.setData_from(r1_68_data.getData_from());
                                        history.setYear_month_day(sb.toString());
                                        //0跑步,1骑行 ，2健走 ,3 其他数据
                                        String hearts = JsonTool.toJson(heartLists);
                                        String steps = JsonTool.toJson(stepLists);
                                        String earths = JsonTool.toJson(earthLists);
                                        String skys = JsonTool.toJson(skyLists);
                                        String speeds = JsonTool.toJson(speedLists);

                                        history.setAvg_hr(hearts);
                                        history.setRateOfStride_avg(steps);
                                        history.setTouchDown_avg(earths);
                                        history.setFlight_avg(skys);
                                        history.setSpeedList(speeds);
                                        history.setTouchDownPower_balance(touchdown_b / stepLists.size());
                                        histories.add(history);
                                        //将数据置零
                                        startTime = 0;
                                        endTime = 0;
                                        distance = 0;
                                        calorie = 0;
                                        touchdown_b = 0;
                                        stepLists.clear();
                                        earthLists.clear();
                                        skyLists.clear();
                                        speedLists.clear();
                                        heartLists.clear();
                                    } else {
                                        //错误数据丢弃
                                    }
                                }

                            }


                            DataSupport.saveAll(histories);


                        }
                    }
                }

            } catch (Exception e) {

            }

        }
    }

    public static List<R1_68_data> get68Data(String data_from, int year, int month, int day) {
        List<TB_68_data> dataList = DataSupport.where("year=? and month=? and day=? and data_from=?",
                year+"", month+"",day+"",data_from+"")
                .order("time asc")
                .find(TB_68_data.class);

        List<R1_68_data> list = new ArrayList<>();

        if (null==dataList || dataList.size()==0) {

        }else {
            for (TB_68_data data : dataList) {
                R1_68_data r1_68_data = new R1_68_data();
                r1_68_data.setData_from(data_from);
                r1_68_data.setCtrl(data.getCtrl());
                r1_68_data.setSeq(data.getSeq());
                r1_68_data.setYear(data.getYear());
                r1_68_data.setMonth(data.getMonth());
                r1_68_data.setDay(data.getDay());
                r1_68_data.setHour(data.getHour());
                r1_68_data.setMin(data.getMin());
                r1_68_data.setSeconds(data.getSeconds());
                r1_68_data.setData_type(data.getData_type());
                r1_68_data.setSport_type(data.getSport_type());
                r1_68_data.setState_type(data.getState_type());
                r1_68_data.setStep(data.getStep());
                r1_68_data.setDistance(data.getDistance());
                r1_68_data.setCalorie(data.getCalorie());
                r1_68_data.setRateOfStride_min(data.getRateOfStride_min());
                r1_68_data.setRateOfStride_max(data.getRateOfStride_max());
                r1_68_data.setRateOfStride_avg(data.getRateOfStride_avg());
                r1_68_data.setFlight_min(data.getFlight_min());
                r1_68_data.setFlight_max(data.getFlight_max());
                r1_68_data.setFlight_avg(data.getFlight_avg());
                r1_68_data.setTouchDown_min(data.getTouchDown_min());
                r1_68_data.setTouchDown_max(data.getTouchDown_max());
                r1_68_data.setTouchDown_avg(data.getTouchDown_avg());
                r1_68_data.setTouchDownPower_balance(data.getTouchDownPower_balance());
                r1_68_data.setTouchDownPower_stop(data.getTouchDownPower_stop());
                r1_68_data.setMin_hr(data.getMin_hr());
                r1_68_data.setMax_hr(data.getMax_hr());
                r1_68_data.setAvg_hr(data.getAvg_hr());
                r1_68_data.setTime(data.getTime());
                r1_68_data.setCmd(data.getCmd());

                list.add(r1_68_data);
            }
        }
        return list;
    }

}
