package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * Created by nokey on 2017/11/3.
 */

public class TB_f1_index extends DataSupport {

    private long uid;
    private String date;
    private long time;
    private int start_seq;
    private int end_seq;
    private String data_from;
    private int ok;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getOk() {
        return ok;
    }

    public void setOk(int ok) {
        this.ok = ok;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStart_seq() {
        return start_seq;
    }

    public void setStart_seq(int start_seq) {
        this.start_seq = start_seq;
    }

    public int getEnd_seq() {
        return end_seq;
    }

    public void setEnd_seq(int end_seq) {
        this.end_seq = end_seq;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }
}
