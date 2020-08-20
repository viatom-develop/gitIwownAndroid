package com.zeroner.bledemo.bean.data;

/**
 * 作者：hzy on 2018/1/6 09:32
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class HeartData {
    private String title;
    private String heart;

    public HeartData() {
    }

    public HeartData(String title, String heart) {
        this.title = title;
        this.heart = heart;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHeart() {
        return heart;
    }

    public void setHeart(String heart) {
        this.heart = heart;
    }
}
