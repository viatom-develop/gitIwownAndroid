package com.zeroner.bledemo.bean.data;

/**
 * 作者：hzy on 2018/1/6 09:32
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class R1Data {
    private String title;
    private int type;
    private String msgContent;

    public R1Data() {
    }

    public R1Data(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }
}
