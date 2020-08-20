package com.zeroner.bledemo.eventbus;

/**
 * Created by cindy on 17/9/28.
 */

public class EpoEvent {
    public static final int STATE_END = 1;
    public static final int STATE_FAIL = 2;
    public static final int STATE_SENDING = 3;
    public static final int STATE_LOW_BATTERY = 4;
    public static final int STATE_INIT = 5;
    //download file fail
    public static final int STATE_DOWNLOAD_FILE_FAIL = 6;
    //EPO彻底失败
    public static int STATE_NEED_RETRY_TIP = 7;
    private int progress = 0;
    private int state = -1;

    public EpoEvent(int state, int progress) {
        this.progress = progress;
        this.state = state;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
