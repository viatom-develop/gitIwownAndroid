package com.zeroner.bledemo.bean.zg;

/**
 * author：hzy on 2017/11/17 17:04
 * <p>
 * email：hezhiyuan@iwown.com
 */

public class ZGFirmwareUpgrade {
    private int success;

    public ZGFirmwareUpgrade() {
    }

    public ZGFirmwareUpgrade(int success) {
        this.success = success;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }
}
