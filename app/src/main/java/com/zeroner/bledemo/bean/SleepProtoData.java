package com.zeroner.bledemo.bean;

import com.zeroner.bledemo.bean.sql.ProtoBuf_80_data;
import com.zeroner.bledemo.utils.DateUtil;

import java.util.List;

/**
 * @author Gavin
 * @date 2020-03-31
 */
public class SleepProtoData {

    private List<ProtoBuf_80_data> todayList;
    private List<ProtoBuf_80_data> yesterdayList;
    private DateUtil yesDate;


    public boolean hasData(){
        return todayList != null && todayList.size() > 0;
    }

    public List<ProtoBuf_80_data> getTodayList() {
        return todayList;
    }

    public void setTodayList(List<ProtoBuf_80_data> todayList) {
        this.todayList = todayList;
    }

    public List<ProtoBuf_80_data> getYesterdayList() {
        return yesterdayList;
    }

    public void setYesterdayList(List<ProtoBuf_80_data> yesterdayList) {
        this.yesterdayList = yesterdayList;
    }

    public DateUtil getYesDate() {
        return yesDate;
    }

    public void setYesDate(DateUtil yesDate) {
        this.yesDate = yesDate;
    }
}
