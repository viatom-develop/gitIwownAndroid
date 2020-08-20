package com.zeroner.bledemo.data;

import com.zeroner.bledemo.bean.data.R1DataBean;
import com.zeroner.bledemo.bean.sql.R1_effective_data;
import com.zeroner.blemidautumn.utils.JsonTool;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2018/6/21.
 */

public class R1DataPresenter {

    private R1DataImpl r1Data;
    private R1_effective_data item;

    private DecimalFormat decimal0Format = new DecimalFormat("0", new DecimalFormatSymbols(Locale.US));//构造方法的字符格式这里如果小数不足2位,会以0补足.
    private DecimalFormat decimal1Format = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));//构造方法的字符格式这里如果小数不足2位,会以0补足.

    public interface R1DataImpl {
        void showR1Data(R1DataBean r1DataBean);

    }

    public R1DataPresenter(R1_effective_data item, R1DataImpl r1Data) {
        this.r1Data = r1Data;
        this.item = item;
    }

    public void setR1Data() {
        initHistoryData();
    }





    private void initHistoryData() {

        R1DataBean r1DataBean = new R1DataBean();

        float rateOfStride_avg = 0;//步频
        float touchDown_avg = 0;//触地时间
        float flight_avg = 0;//腾空高度
        float touchDow_balance_avg = 0;//触地平衡
        int maxRate = 0;//最大步频
        int maxEarth_time = 0;//最大触地时间
        int maxFlight_avg = 0;//最大腾空时间
        int maxHr_avg = 0;//最大心率
        int minHr_avg = 0;//最小心率
        int avgHr_avg = 0;//平均心率
        float min_speed = 100;//配速
        float max_speed = 0;//配速

        if (item != null) {
            try {
                List<Float> rateOfStride_avgs = new ArrayList<>();
                List<Float> touchDown_avgs = new ArrayList<>();
                List<Float> flight_avgs = new ArrayList<>();
                List<Float> speeds = new ArrayList<>();
                List<Integer> avgHr_avgs = new ArrayList<>();
                List<Float> rateOfStride = JsonTool.getListJson(item.getRateOfStride_avg(), Float.class);
                List<Float> touchDown = JsonTool.getListJson(item.getTouchDown_avg(), Float.class);
                List<Float> flight = JsonTool.getListJson(item.getFlight_avg(), Float.class);
                List<Float> speed = JsonTool.getListJson(item.getSpeedList(), Float.class);
                List<Integer> heartAvg = JsonTool.getListJson(item.getAvg_hr(), Integer.class);

                rateOfStride_avgs.addAll(rateOfStride);
                touchDown_avgs.addAll(touchDown);
                flight_avgs.addAll(flight);
                speeds.addAll(speed);
                avgHr_avgs.addAll(heartAvg);
                rateOfStride_avg = addAvgData(rateOfStride_avgs);
                touchDown_avg = addAvgData(touchDown_avgs);

                flight_avg = addAvgData(flight_avgs);

                List<Float> list = flightTimeToVerticalLists(flight_avgs);

                touchDow_balance_avg = item.getTouchDownPower_balance() / 10.0f;

                avgHr_avg = addAvgIntData(avgHr_avgs);

                float avg_distance = addAvgData(speeds);

                maxRate = (int) maxValue(rateOfStride_avgs);
                maxEarth_time = (int) maxValue(touchDown_avgs);
                maxFlight_avg = (int) maxValue(flight_avgs);
                min_speed = minValue(speeds);
                max_speed = maxValue(speeds);
                maxHr_avg = (int) maxIntValue(avgHr_avgs);
                minHr_avg = (int) minIntValue(avgHr_avgs);


                r1DataBean.setRate_avg(decimal0Format.format(rateOfStride_avg));
                r1DataBean.setEarth_time_avg(String.valueOf(decimal0Format.format(touchDown_avg)));
                r1DataBean.setSky_time_avg(String.valueOf(decimal0Format.format(flight_avg)));
                r1DataBean.setMaxRate(maxRate);
                r1DataBean.setMax_earth_time(maxEarth_time);
                r1DataBean.setMax_vertical((int) flyTimeToVertical(maxFlight_avg));
                r1DataBean.setVertical_avg(decimal1Format.format(flyTimeToVertical(flight_avg)));
                r1DataBean.setEarth_balance(decimal1Format.format(touchDow_balance_avg) + "% - " + decimal1Format.format(Math.abs(100 - touchDow_balance_avg)) + "%");
                r1DataBean.setSpeed_min(min_speed);
                if (speeds.size() > 0) {
                    r1DataBean.setSpeed_avg(avg_distance);
                }
                r1DataBean.setSpeedLists(speeds);
                r1DataBean.setStepRateLists(rateOfStride_avgs);
                r1DataBean.setEarthTimeLists(touchDown_avgs);
                r1DataBean.setVerticalLists(list);
                r1DataBean.setSpeed_max(max_speed);

                r1DataBean.setMin_hr(minHr_avg);
                r1DataBean.setMax_hr(maxHr_avg);
                r1DataBean.setAvg_hr(avgHr_avg);
                r1DataBean.setHrLists(avgHr_avgs);

                r1Data.showR1Data(r1DataBean);
            } catch (Exception e) {

            }
        }
    }


    private float addAvgData(List<Float> dlineDataBeans) {
        float value = 0;
        float index = 0;
        for (Float bean : dlineDataBeans) {
            if (bean > 0) {
                value += bean;
                index++;
            }
        }
        if (index != 0) {
            return value * 1.0f / index;
        } else {
            return index;
        }
    }

    private int addAvgIntData(List<Integer> integers) {
        int value = 0;
        int index = 0;
        for (float bean : integers) {
            if (bean > 0) {
                value += bean;
                index++;
            }
        }
        if (index != 0) {
            return value / index;
        } else {
            return index;
        }
    }

    private float maxValue(List<Float> dlineDataBeans) {
        float maxValue = 0;
        for (Float bean : dlineDataBeans) {
            maxValue = Math.max(bean, maxValue);
        }
        return maxValue;
    }

    private float minValue(List<Float> dlineDataBeans) {
        float minValue = 1000;
        for (Float bean : dlineDataBeans) {
            if (bean > 0) {
                minValue = Math.min(bean, minValue);
            }

        }
        return minValue;
    }

    private float maxIntValue(List<Integer> dlineDataBeans) {
        float maxValue = 0;
        for (Integer bean : dlineDataBeans) {
            maxValue = Math.max(bean, maxValue);
        }
        return maxValue;
    }

    private float minIntValue(List<Integer> dlineDataBeans) {
        float minValue = 1000;
        for (Integer bean : dlineDataBeans) {
            if (bean > 0) {
                minValue = Math.min(bean, minValue);
            }
        }
        return minValue;
    }

    private List<Float> flightTimeToVerticalLists(List<Float> lists) {
        List<Float> list = new ArrayList<>();
        for (Float bean : lists) {
            Float bean1 = flyTimeToVertical(bean);
            list.add(bean1);
        }
        return list;
    }

    private float flyTimeToVertical(float value) {
        float myValue = 1.0f / 2 * 10 * value * value / 10000;
        int scale = 2;//设置位数
        int roundingMode = 4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
        BigDecimal bd = new BigDecimal(myValue);
        bd = bd.setScale(scale, roundingMode);
        myValue = bd.floatValue();
        return myValue;
    }


}
