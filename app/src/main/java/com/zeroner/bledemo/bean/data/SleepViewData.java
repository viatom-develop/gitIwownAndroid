package com.zeroner.bledemo.bean.data;

/**
 * 作者：hzy on 2018/1/8 19:54
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class SleepViewData {
    private String title;
    private String total;
    private String light;
    private String deep;
    private String weak;

    public SleepViewData(String title, String total, String light, String deep) {
        this.title = title;
        this.total = total;
        this.light = light;
        this.deep = deep;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getLight() {
        return light;
    }

    public void setLight(String light) {
        this.light = light;
    }

    public String getDeep() {
        return deep;
    }

    public void setDeep(String deep) {
        this.deep = deep;
    }

    public String getWeak() {
        return weak;
    }

    public void setWeak(String weak) {
        this.weak = weak;
    }
}
