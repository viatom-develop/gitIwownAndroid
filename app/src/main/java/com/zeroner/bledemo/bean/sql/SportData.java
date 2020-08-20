package com.zeroner.bledemo.bean.sql;

import android.content.Context;

import com.zeroner.bledemo.bean.data.Detail_data;
import com.zeroner.bledemo.utils.BaseActionUtils;
import com.zeroner.bledemo.utils.JsonUtils;
import com.zeroner.bledemo.utils.PrefUtil;
import com.zeroner.blemidautumn.library.KLog;
import com.zeroner.blemidautumn.output.detail_sport.model.ZeronerDetailSportData;
import com.zeroner.blemidautumn.utils.Util;

import org.litepal.crud.DataSupport;

/**
 * author：hzy on 2017/12/29 09:01
 * <p>
 * email：hezhiyuan@iwown.com
 */

public class SportData extends DataSupport{
    // sport type
    private int sport_type;
    // data from
    private String data_from;
    // kcal
    private double calorie;
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
    // detail data
    private String detail_data;
    //start unix time
    private long start_unixTime;

    private long end_unixTime;
    private String sportCode;

    public int getSport_type() {
        return sport_type;
    }

    public void setSport_type(int sport_type) {
        this.sport_type = sport_type;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public double getCalorie() {
        return calorie;
    }

    public void setCalorie(double calorie) {
        this.calorie = calorie;
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

    public String getDetail_data() {
        return detail_data;
    }

    public void setDetail_data(String detail_data) {
        this.detail_data = detail_data;
    }

    public long getStart_unixTime() {
        return start_unixTime;
    }

    public void setStart_unixTime(long start_unixTime) {
        this.start_unixTime = start_unixTime;
    }

    public long getEnd_unixTime() {
        return end_unixTime;
    }

    public void setEnd_unixTime(long end_unixTime) {
        this.end_unixTime = end_unixTime;
    }

    public String getSportCode() {
        return sportCode;
    }

    public void setSportCode(String sportCode) {
        this.sportCode = sportCode;
    }

    /**
     * 当前数据是否为实时数据
     *
     * @return
     */
    public boolean isLive() {
        // 日期为0xff为实时数据
        if (year == 0xff && month == 0xff && day == 0xff) {
            return true;
        }
        return false;
    }

    /**
     * data parse
     * @param datas
     * @param context
     * @return
     */
    public static SportData parse(ZeronerDetailSportData datas, Context context) {
        SportData nd = new SportData();
        // 年
        int year = datas.getYear();
        nd.setYear(year);
        // 月
        int month = datas.getMonth();
        nd.setMonth(month);
        // 日
        int day = datas.getDay();
        nd.setDay(day);

        // 日期为0xff为实时数据
        if ((nd.getYear() - 2000) == 0xff && (nd.getMonth() - 1) == 0xff && (nd.getDay() - 1) == 0xff) {
            nd.setYear(0xff);
            nd.setMonth(0xff);
            nd.setDay(0xff);
        }

        // 数据来源 手机和手环
        nd.setData_from(PrefUtil.getString(context, BaseActionUtils.ACTION_DEVICE_NAME));
        // 运动类型
        int sport_type = datas.getSport_type();
        nd.setSport_type(sport_type);
        // 开始时间
        nd.setStart_time(datas.getStartMin());
        // 结束时间
        nd.setEnd_time(datas.getEndMin());
        if(nd.getEnd_time()-nd.getStart_time()<0){
            nd.setEnd_unixTime(Util.date2TimeStamp(year,month,day+1,nd.getEnd_time()/60,nd.getEnd_time()%60));
        }else{
            nd.setEnd_unixTime(Util.date2TimeStamp(year,month,day,nd.getEnd_time()/60,nd.getEnd_time()%60));
        }
        nd.setStart_unixTime(Util.date2TimeStamp(year,month,day,nd.getStart_time()/60,nd.getStart_time()%60));
        float a1=datas.getCalories();
        nd.setCalorie(a1);
        if (sport_type == 0x01 || sport_type==0x07) {
            Detail_data d = new Detail_data();
            d.setStep(datas.getSteps());
            d.setDistance(datas.getDistance());
            d.setActivity(datas.getActivity());
            // date_detail
            nd.setDetail_data(JsonUtils.toJson(d));
        }else if(sport_type<0x80&&sport_type!=0x01){
            //detail  json 实体
            Detail_data d=new Detail_data();
            d.setActivity(datas.getActivity());
            d.setCount(datas.getOtherCount());
            nd.setDetail_data(JsonUtils.toJson(d));
        }else if(sport_type>=0x80){
            Detail_data d=new Detail_data();
            d.setActivity(datas.getActivity());
            nd.setDetail_data(JsonUtils.toJson(d));
        }
        return nd;
    }

}
