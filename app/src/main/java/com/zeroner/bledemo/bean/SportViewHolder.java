package com.zeroner.bledemo.bean;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zeroner.bledemo.R;
import com.zeroner.bledemo.WatchSportInfoActivity;
import com.zeroner.bledemo.bean.data.BraceletData;
import com.zeroner.bledemo.sport.SportActivity;
import com.zeroner.bledemo.utils.UI;
import com.zeroner.blemidautumn.bluetooth.SuperBleSDK;
import com.zeroner.blemidautumn.library.KLog;

import cn.lemon.view.adapter.BaseViewHolder;

/**
 * 作者：hzy on 2017/12/26 08:44
 * <p>
 * 邮箱：hezhiyuan@iwown.com
 */

public class SportViewHolder extends BaseViewHolder<BraceletData> {
    public  TextView mSteps;
    public  TextView mDistance;
    public  TextView mCal;
    public  TextView mTitle;
    private Context context;
    public SportViewHolder(ViewGroup parent, Context context) {
        super(parent, R.layout.fragment_item_sport);
        this.context=context;
    }

    @Override
    public void onInitializeView() {
        super.onInitializeView();
        mTitle= (TextView) findViewById(R.id.card_title);
        mSteps = (TextView) findViewById(R.id.bracelet_steps);
        mDistance = (TextView) findViewById(R.id.bracelet_distance);
        mCal= (TextView) findViewById(R.id.bracelet_cal);
    }

    @Override
    public void setData(BraceletData data) {
        super.setData(data);
        mTitle.setText(String.valueOf(data.getTitle()));
        mSteps.setText(String.valueOf(data.getSteps()));
        mDistance.setText(String.valueOf(data.getDistance()));
        mCal.setText(String.valueOf(data.getCalorie()));
    }

    @Override
    public void onItemViewClick(BraceletData data) {
        super.onItemViewClick(data);
        KLog.i(data.toString());
        if (!SuperBleSDK.isMtk(context)) {
            UI.startActivity((Activity) context,SportActivity.class);
        }else if(SuperBleSDK.isMtk(context)) {
            UI.startActivity((Activity) context, WatchSportInfoActivity.class);
        }
    }
}
