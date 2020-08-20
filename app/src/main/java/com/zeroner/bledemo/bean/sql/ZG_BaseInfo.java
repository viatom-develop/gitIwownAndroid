package com.zeroner.bledemo.bean.sql;
import org.litepal.crud.DataSupport;

/**
 * Created by Daemon on 2017/11/2 14:56.
 */

public class ZG_BaseInfo extends DataSupport {

    public static final String key_hardinfo="key_hardinfo";
    public static final String key_bleSpeed="key_bleSpeed";
    public static final String key_deviceset="key_deviceset";
    public static final String key_devicetime="key_devicetime";
    public static final String key_message_notification="key_message_notification";

    public static final String key_push_alert_time="key_push_alert_time";
    public static final String key_phone_call_time="key_phone_call_time";

    //Each update updates only data that is larger than this
    public static final String key_data_last_day="key_data_last_day";
    public static final String key_sit_long_time="key_sit_long_time";


    public static final String key_last_totaldata="key_last_totaldata";

    public static final String key_welcome_blood="key_welcome_blood";


    private String data_form;
    private String key;
    private String content;
    private String uid;

    public static String getKey_hardinfo() {
        return key_hardinfo;
    }

    public String getData_form() {
        return data_form;
    }

    public void setData_form(String data_form) {
        this.data_form = data_form;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
