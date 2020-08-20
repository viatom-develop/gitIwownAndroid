package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * 作者：hzy on 2018/8/22 17:56
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 * CREATE TABLE IF NOT EXISTS P1_64_index_table
 * (id INTEGER PRIMARY KEY AUTOINCREMENT, uid INTEGER, flag INTEGER,data_from TEXT, date DATE, seq_start INTEGER, seq_end INTEGER)
 *
 */

public class TB_64_index_table extends DataSupport {

    private long id;
    private long uid;
    private int flag;
    private String data_from;
    private String date;
    private int seq_start;
    /** seq_end 不会超过1280  1291->11*/
    private int seq_end;
    private long unixTime;
    /** 日期  20180827*/
    private String data_ymd;
    /** 已同步到的seq */
    private int sync_seq;

    public long getUnixTime() {
        return unixTime;
    }

    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSeq_start() {
        return seq_start;
    }

    public void setSeq_start(int seq_start) {
        this.seq_start = seq_start;
    }

    public int getSeq_end() {
        return seq_end;
    }

    public void setSeq_end(int seq_end) {
        this.seq_end = seq_end;
    }

    public String getData_ymd() {
        return data_ymd;
    }

    public void setData_ymd(String data_ymd) {
        this.data_ymd = data_ymd;
    }

    public int getSync_seq() {
        return sync_seq;
    }

    public void setSync_seq(int sync_seq) {
        this.sync_seq = sync_seq;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
