package com.zeroner.bledemo.bean.data;

import java.util.List;

/**
 * Created by Daemon on 2017/11/9 17:02.
 */

public class ModeItems {

    /**
     * retCode : 0
     * data : [{"categoryid":1,"categoryname":"i5 Plus 智能运动手环","classid":1,"sdktype":1,"keyword":"01"},{"categoryid":2,"categoryname":"V6 智能语音手环","classid":1,"sdktype":1,"keyword":"02"},{"categoryid":3,"categoryname":"i7 智能心率手环","classid":1,"sdktype":1,"keyword":"03"},{"categoryid":4,"categoryname":"i5 Pro智能运动手环","classid":1,"sdktype":1,"keyword":"04"},{"categoryid":5,"categoryname":"蛋卷手环","classid":1,"sdktype":1,"keyword":"05,ATC303"},{"categoryid":6,"categoryname":"心率运动耳机R1","classid":4,"sdktype":1,"keyword":"06"},{"categoryid":7,"categoryname":"V6 Pro语音运动手环","classid":1,"sdktype":1,"keyword":"07"},{"categoryid":8,"categoryname":"蛋卷手环 彩屏版","classid":1,"sdktype":3,"keyword":"08"},{"categoryid":9,"categoryname":"I5 HR心率运动手环","classid":1,"sdktype":1,"keyword":"09"},{"categoryid":10,"categoryname":"i6 HR运动心率手环彩屏版","classid":1,"sdktype":3,"keyword":"10"},{"categoryid":11,"categoryname":"I5 A运动手环","classid":1,"sdktype":1,"keyword":"11"},{"categoryid":12,"categoryname":"i6 HR运动心率手环","classid":1,"sdktype":1,"keyword":"13,16,17,I7HR,12"},{"categoryid":13,"categoryname":"i6 智能运动手环","classid":1,"sdktype":1,"keyword":"15,EasyFit Touch 2"},{"categoryid":14,"categoryname":"WiFi体脂秤 S1","classid":3,"sdktype":0,"keyword":"20"},{"categoryid":15,"categoryname":"能量运动手表 P1","classid":2,"sdktype":2,"keyword":"watch-P1"}]
     * data_type : 2
     */

    private int retCode=-1;
    private int data_type;
    private List<DataBean> data;

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public int getData_type() {
        return data_type;
    }

    public void setData_type(int data_type) {
        this.data_type = data_type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * categoryid : 1
         * categoryname : i5 Plus 智能运动手环
         * classid : 1
         * sdktype : 1
         * keyword : 01
         */

        private int categoryid;
        private String categoryname;
        private int classid;
        private int sdktype;
        private String keyword;

        public int getCategoryid() {
            return categoryid;
        }

        public void setCategoryid(int categoryid) {
            this.categoryid = categoryid;
        }

        public String getCategoryname() {
            return categoryname;
        }

        public void setCategoryname(String categoryname) {
            this.categoryname = categoryname;
        }

        public int getClassid() {
            return classid;
        }

        public void setClassid(int classid) {
            this.classid = classid;
        }

        public int getSdktype() {
            return sdktype;
        }

        public void setSdktype(int sdktype) {
            this.sdktype = sdktype;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        @Override
        public String toString() {
            return "DataBean{" +
                    "categoryid=" + categoryid +
                    ", categoryname='" + categoryname + '\'' +
                    ", classid=" + classid +
                    ", sdktype=" + sdktype +
                    ", keyword='" + keyword + '\'' +
                    '}';
        }
    }
}
