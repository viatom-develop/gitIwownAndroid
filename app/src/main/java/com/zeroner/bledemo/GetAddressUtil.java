package com.zeroner.bledemo;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GetAddressUtil {
    Context context;

    public GetAddressUtil(Context context) {
        this.context = context;
    }

    public String getAddress(double lnt, double lat) {

        Geocoder geocoder = new Geocoder(context,Locale.CHINA);
        boolean falg = geocoder.isPresent();
        Log.e("thistt", "the falg is " + falg);
        StringBuilder stringBuilder = new StringBuilder();
        try {

            //根据经纬度获取地理位置信息---这里会获取最近的几组地址信息，具体几组由最后一个参数决定
            List<Address> addresses = geocoder.getFromLocation(lat, lnt, 10);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//                    //每一组地址里面还会有许多地址。这里我取的前2个地址。xxx街道-xxx位置
//                    if (i == 0) {
//                        stringBuilder.append(address.getAddressLine(i)).append("-");
//                    }
//
//                    if (i == 1) {
//                        stringBuilder.append(address.getAddressLine(i));
//                        break;
//                    }
                }
                stringBuilder.append(address.getCountryName()).append("_");//国家
//                stringBuilder.append(address.getFeatureName()).append("_");//周边地址
                stringBuilder.append(address.getSubAdminArea()).append("_");//市
//                stringBuilder.append(address.getPostalCode()).append("_");
//                stringBuilder.append(address.getCountryCode()).append("_");//国家编码
                stringBuilder.append(address.getAdminArea()).append("_");//省份
//                stringBuilder.append(address.getSubAdminArea()).append("_");
//                stringBuilder.append(address.getThoroughfare()).append("_");//道路
//                stringBuilder.append(address.getSubLocality()).append("_");//香洲区
//                stringBuilder.append(address.getLatitude()).append("_");//经度
//                stringBuilder.append(address.getLongitude());//维度
                Log.d("thistt", "地址信息--->" + stringBuilder);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Toast.makeText(context, "报错", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return stringBuilder.toString();

    }
}