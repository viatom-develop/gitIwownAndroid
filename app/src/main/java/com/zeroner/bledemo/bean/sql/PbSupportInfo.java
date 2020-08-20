package com.zeroner.bledemo.bean.sql;

import org.litepal.crud.DataSupport;

/**
 * @author yanxi
 * @data 2018/12/12
 */
public class PbSupportInfo extends DataSupport {

    private boolean  support_health;
    private boolean  support_gnss;
    private boolean  support_ecg;
    private boolean  support_ppg;
    private boolean  support_rri;
    private boolean  support_medic;
    private boolean  support_spo2 ;
    private boolean  support_swim  ;
    private String data_from;

    public boolean isSupport_health() {
        return support_health;
    }

    public void setSupport_health(boolean support_health) {
        this.support_health = support_health;
    }

    public boolean isSupport_gnss() {
        return support_gnss;
    }

    public void setSupport_gnss(boolean support_gnss) {
        this.support_gnss = support_gnss;
    }

    public boolean isSupport_ecg() {
        return support_ecg;
    }

    public void setSupport_ecg(boolean support_ecg) {
        this.support_ecg = support_ecg;
    }

    public boolean isSupport_ppg() {
        return support_ppg;
    }

    public void setSupport_ppg(boolean support_ppg) {
        this.support_ppg = support_ppg;
    }

    public boolean isSupport_rri() {
        return support_rri;
    }

    public void setSupport_rri(boolean support_rri) {
        this.support_rri = support_rri;
    }

    public String getData_from() {
        return data_from;
    }

    public void setData_from(String data_from) {
        this.data_from = data_from;
    }

    public boolean isSupport_medic() {
        return support_medic;
    }

    public void setSupport_medic(boolean support_medic) {
        this.support_medic = support_medic;
    }

    public boolean isSupport_spo2() {
        return support_spo2;
    }

    public void setSupport_spo2(boolean support_spo2) {
        this.support_spo2 = support_spo2;
    }

    public boolean isSupport_swim() {
        return support_swim;
    }

    public void setSupport_swim(boolean support_swim) {
        this.support_swim = support_swim;
    }
}
