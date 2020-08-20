package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * create by ：hzy on 2018/1/11 14:51
 * <p>
 * email：hezhiyuan@iwown.com
 */

public class BleLog extends DataSupport {
    private String dataFrom;
    private long time;
    // 1: write 2:notify 3:test command
    private int type;
    private String cmd;

    public String getDataFrom() {
        return dataFrom;
    }

    public void setDataFrom(String dataFrom) {
        this.dataFrom = dataFrom;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}
