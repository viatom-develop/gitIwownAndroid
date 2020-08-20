package com.zeroner.bledemo.bean.data;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者：hzy on 2017/12/22 09:16
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class SDKType implements Parcelable {
    private int sdkType;

    public SDKType(int sdkType) {
        this.sdkType = sdkType;
    }

    protected SDKType(Parcel in) {
        sdkType = in.readInt();
    }

    public static final Creator<SDKType> CREATOR = new Creator<SDKType>() {
        @Override
        public SDKType createFromParcel(Parcel in) {
            return new SDKType(in);
        }

        @Override
        public SDKType[] newArray(int size) {
            return new SDKType[size];
        }
    };

    public int getSdkType() {
        return sdkType;
    }

    public void setSdkType(int sdkType) {
        this.sdkType = sdkType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(sdkType);
    }
}
