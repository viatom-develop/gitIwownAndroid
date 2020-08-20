package com.zeroner.bledemo.bean.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by HP-HP on 05-12-2015.
 */
public class TimeLineModel implements Parcelable {
    private int type;
    private String sleepType;
    private String start;
    private String end;
    private OrderStatus mStatus;

    public TimeLineModel() {
    }

    public TimeLineModel(int type,String sleepType, String start, String end, OrderStatus mStatus) {
        this.type=type;
        this.sleepType = sleepType;
        this.start = start;
        this.end = end;
        this.mStatus = mStatus;
    }

    protected TimeLineModel(Parcel in) {
        type = in.readInt();
        sleepType = in.readString();
        start = in.readString();
        end = in.readString();
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static final Creator<TimeLineModel> CREATOR = new Creator<TimeLineModel>() {
        @Override
        public TimeLineModel createFromParcel(Parcel in) {
            return new TimeLineModel(in);
        }

        @Override
        public TimeLineModel[] newArray(int size) {
            return new TimeLineModel[size];
        }
    };

    public String getSleepType() {
        return sleepType;
    }

    public void setSleepType(String sleepType) {
        this.sleepType = sleepType;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(sleepType);
        dest.writeString(start);
        dest.writeString(end);
    }
}
