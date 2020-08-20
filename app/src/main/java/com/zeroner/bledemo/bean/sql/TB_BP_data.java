package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * 获取
 */
public class TB_BP_data extends DataSupport {

    private String dataFrom;
    /**高压值**/
    private int sbp;
    /**低压值**/
    private int dbp;
    private long bpTime;

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public int getSbp() {
        return sbp;
    }

    public void setSbp(int sbp) {
        this.sbp = sbp;
    }

    public int getDbp() {
        return dbp;
    }

    public void setDbp(int dbp) {
        this.dbp = dbp;
    }

    public long getBpTime() {
        return bpTime;
    }

    public void setBpTime(long bpTime) {
        this.bpTime = bpTime;
    }
}