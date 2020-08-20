package com.zeroner.bledemo.bean.sql;

import android.content.Context;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.DateUtil;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.output.sleep.model.ZeronerSleepData;
import org.litepal.crud.DataSupport;

/**
 * 作者：hzy on 2017/12/28 19:28
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class SleepData extends DataSupport{
    // sleep type
    private int sleep_type;
    // data from
    private String data_from;
    // year
    private int year;
    // month
    private int month;
    // day
    private int day;
    // start time
    private int start_time;
    // end time
    private int end_time;
    // during
    private int activity;

    //time stamp
    private int timeStamp;
    //sleep enter time
    private int sleep_enter;
    //sleep exit time
    private int sleep_exit;



    public static SleepData parse(ZeronerSleepData data, Context context) {
        SleepData ne = new SleepData();
        // year
        int year = data.getYear();
        ne.setYear(year);
        // month
        int month = data.getMonth();
        ne.setMonth(month);
        // day
        int day = data.getDay();

        DateUtil date=new DateUtil(year,month,day);
        ne.setTimeStamp((int) date.getUnixTimestamp());

        ne.setDay(day);
        // Date 0xff for real-time data
        if ((ne.getYear() - 2000) == 0xff && (ne.getMonth() - 1) == 0xff && (ne.getDay() - 1) == 0xff) {
            ne.setYear(0xff);
            ne.setMonth(0xff);
            ne.setDay(0xff);
        }

        // start time
        ne.setStart_time(data.getStart());
        // end time
        ne.setEnd_time(data.getEnd());
        // during
        ne.setActivity(data.getTimes());
        // sleep type
        ne.setSleep_type(data.getType());

        ne.setSleep_enter(data.getSleep_enter());

        ne.setSleep_exit(data.getSleep_exit());

        ne.setData_from(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME));
        return ne;
    }

    public int getSleep_enter() {
        return sleep_enter;
    }

    public void setSleep_enter(int sleep_enter) {
        this.sleep_enter = sleep_enter;
    }

    public int getSleep_exit() {
        return sleep_exit;
    }

    public void setSleep_exit(int sleep_exit) {
        this.sleep_exit = sleep_exit;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getSleep_type() {
        return sleep_type;
    }

    public void setSleep_type(int sleep_type) {
        this.sleep_type = sleep_type;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
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

    public int getStart_time() {
        return start_time;
    }

    public void setStart_time(int start_time) {
        this.start_time = start_time;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public int getActivity() {
        return activity;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }
}
